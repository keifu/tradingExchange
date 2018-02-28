package com.orderexchange;

import static com.orderexchange.Order.Direction.BUY;
import static com.orderexchange.Order.Direction.SELL;
import static com.orderexchange.Order.Status.EXECUTED;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.orderexchange.Order.Direction;

public class TradingExchangeImpl implements TradingExchange {
	
	private final Logger logger = Logger.getLogger(this.getClass());
	
	private OrderStore orderStore;
	
	public TradingExchangeImpl(OrderStore orderStore) {
		this.orderStore = orderStore;
	}
	
	/**
	 *
	 * This needs to be synchronized to prevent the 'check-then-act' race condition.
	 * For example, 2 BUY orders can arrive at the same time matching the same SELL order.
	 * Without synchronization, both the BUY orders might be matched
	 * 
	 * Run {@link TradingExchangeSimilationTest} to simulate multi-threaded behaviour
	 * 
	 */
	@Override
	public synchronized void addOrder(Order newOrder) {
		logger.info("Adding new order " + newOrder);
		
		Optional<Order> matchingOrder = getMatchingOrder(newOrder);
		if(matchingOrder.isPresent()){
			Order openMatchingOrder = matchingOrder.get();
			logger.info("Order matched.  Matched order " + openMatchingOrder);
			
			newOrder.setStatus(EXECUTED);
			newOrder.setExecutedPrice(newOrder.getPrice());
			this.orderStore.addExecutedOrder(newOrder);

			openMatchingOrder.setStatus(EXECUTED);
			openMatchingOrder.setExecutedPrice(newOrder.getPrice());
			this.orderStore.removeOpenOrder(openMatchingOrder);
			this.orderStore.addExecutedOrder(openMatchingOrder);
			
			MatchedOrder matchedOrder = new MatchedOrder(newOrder.getRic(), newOrder.getPrice(), newOrder.getQuantity(), newOrder, openMatchingOrder);
			this.orderStore.addMatchedOrder(matchedOrder);
		}else{
			logger.info("No matching order");
			this.orderStore.addOpenOrder(newOrder);
		}
	}
	
	Optional<Order> getMatchingOrder(Order newOrder){
		
		Direction direction = newOrder.getDirection() == BUY ? SELL : BUY;
		List<Order> openOrders = this.orderStore.getOpenOrders(newOrder.getRic(), direction);
		
		List<Order> orderMatches = openOrders.stream()
										.filter(orderMatch(newOrder))
										.collect(Collectors.toList());
		
		if(orderMatches.size() == 0) return Optional.empty();
		
		List<Order> orderMatchingSamePrice = orderMatches.stream()
												.filter(order -> order.getPrice() == newOrder.getPrice())
												.collect(Collectors.toList());
		
		if(orderMatchingSamePrice.size() > 0){
			//get the first order. The list will be in order of insertion and so the first element is the earliest one
			return Optional.of(orderMatchingSamePrice.get(0));
		}else{
			//sort by price
			orderMatches.sort((a,b) -> Double.valueOf(a.getPrice()).compareTo(Double.valueOf(b.getPrice())));
			//If SELL, get highest buying price.  If BUY, get lowest selling price
			Order matchedOrder =  newOrder.getDirection() == SELL ? orderMatches.get(orderMatches.size() - 1) : orderMatches.get(0);
			return Optional.of(matchedOrder);
			
		}
	}
	
	/**
	 * Test to see whether 2 orders can be matched
	 * Orders are matched if:
	 *   - Sell price is less than or equal to buy price
	 *   - Different directions
	 *   - Same RIC
	 *   - Same uantity
	 * 
	 * @param newOrder
	 * @return
	 */
	Predicate<Order> orderMatch(Order newOrder){
		return (openOrder) -> {
			double buyPrice = newOrder.getDirection() == BUY ? newOrder.getPrice() : openOrder.getPrice();
			double sellPrice = newOrder.getDirection() == SELL ? newOrder.getPrice() : openOrder.getPrice();
			return sellPrice <= buyPrice 
					&& newOrder.getDirection() != openOrder.getDirection()
					&& newOrder.getRic().equals(openOrder.getRic())
					&& newOrder.getQuantity() == openOrder.getQuantity();
			
		};	
	}

	@Override
	public List<Order> getOpenInterest(String ric, Direction direction) {
		return this.orderStore.getOpenOrders(ric, direction);
	}

	@Override
	public double getAverageExecutedPrice(String ric) {
		List<MatchedOrder> matchedOrders = this.orderStore.getMatchedOrders(ric);
		 
		BigDecimal sum = BigDecimal.ZERO;
		BigDecimal noOfUnits = BigDecimal.ZERO;
		
		for(MatchedOrder matchedOrder: matchedOrders){
			 sum = sum.add(BigDecimal.valueOf(matchedOrder.getExecutedPrice()).multiply(BigDecimal.valueOf(matchedOrder.getQuantity())));
			 noOfUnits = noOfUnits.add(BigDecimal.valueOf(matchedOrder.getQuantity()));
		}
		
		return noOfUnits ==  BigDecimal.ZERO ?  0  : sum.divide(noOfUnits, 4, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	@Override
	public int getExecutedQuantity(String ric, String user) {
		
		 List<Order> executedOrders = this.orderStore.getExecutedOrders(ric, user);
		 int executedQuantity = executedOrders.stream()
		 							.mapToInt(order -> (order.getDirection() == BUY ? order.getQuantity() :  -1 * order.getQuantity() ))
		 							.sum();
		 return executedQuantity;
		 
	}
		
}
