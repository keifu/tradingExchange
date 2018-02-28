package com.orderexchange;

import java.util.List;
import java.util.Objects;

import static com.orderexchange.Order.Status.*;

import com.orderexchange.Order.Direction;
import com.orderexchange.Order.Status;

public class OrderCacheStore implements OrderStore {
	
	//(RIC/Direction -> open orders) 
	public MultiValueMap<String, Order> openOrders = new MultiValueMap<>();
	//(RIC/User -> executed orders) 
	public MultiValueMap<String, Order> executedOrders = new MultiValueMap<>();
	//(RIC -> matched orders) 
	public MultiValueMap<String, MatchedOrder> matchedOrders = new MultiValueMap<>();

	@Override
	public void addOpenOrder(Order order) {
		Objects.requireNonNull(order, "Order cannot be null");
		if(order.getStatus() != OPEN){
			throw new IllegalArgumentException("Order must be in 'new' status");
		}
		
		this.openOrders.put(getKeyForOpenOrders(order), order);
	}
	
	private String getKeyForOpenOrders(Order order){
		return getKeyForOpenOrders(order.getRic(), order.getDirection());
	}
	
	private String getKeyForOpenOrders(String ric, Direction direction){
		return ric  + "/" + direction;
	}
	
	@Override
	public void removeOpenOrder(Order order) {
		Objects.requireNonNull(order, "Order cannot be null");
		this.openOrders.removeValue(getKeyForOpenOrders(order), order);
	}
	
	@Override
	public void addExecutedOrder(Order order) {
		Objects.requireNonNull(order, "Order cannot be null");
		if(order.getStatus() != EXECUTED){
			throw new IllegalArgumentException("Order must be in 'executed' status");
		}
		
		this.executedOrders.put(getKeyForExexutedOrders(order), order);
	}
	
	private String getKeyForExexutedOrders(Order order){
		return getKeyForExexutedOrders(order.getRic(), order.getUser());
	}
	
	private String getKeyForExexutedOrders(String ric, String user){
		return ric  + "/" + user;
	}
	
	@Override
	public void addMatchedOrder(MatchedOrder matchedOrder) {
		Objects.requireNonNull(matchedOrder, "Matched order cannot be null");	
		this.matchedOrders.put(matchedOrder.getRic(), matchedOrder);		
	}
	
	@Override
	public List<MatchedOrder> getMatchedOrders(String ric) {
		Objects.requireNonNull(ric, "RIC cannot be null");	
		return this.matchedOrders.get(ric);
	}

	@Override
	public List<Order> getOpenOrders(String ric, Direction direction){
		List<Order> openOrders = this.openOrders.get(getKeyForOpenOrders(ric, direction));		
		return openOrders;
	}
	
	@Override
	public List<Order> getExecutedOrders(String ric, String user) {
		List<Order> executedOrders = this.executedOrders.get(getKeyForExexutedOrders(ric, user));		
		return executedOrders;
	}
}
