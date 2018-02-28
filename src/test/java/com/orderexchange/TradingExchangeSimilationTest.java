package com.orderexchange;


import static com.orderexchange.Order.Direction.BUY;
import static com.orderexchange.Order.Direction.SELL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.orderexchange.Order.Direction;

/**
 * 
 * Test to simulate buy/sell orders added by multiple threads
 * 
 * @author Keith
 *
 */
public class TradingExchangeSimilationTest {
	
	private static final int NO_OF_BUY_ORDERS = 50;
	private static final int NO_OF_SELL_ORDERS = NO_OF_BUY_ORDERS;
	
	private final Logger logger = Logger.getLogger(this.getClass());
	private static final String RIC = "VOD.L";
	private static final int QUANTITY = 1000;
	private static final double PRICE = 100;
	private static final String USER_1 = "USER 1";
	private static final String USER_2 = "USER 2";
	
	private CyclicBarrier startBarrier = new CyclicBarrier(NO_OF_BUY_ORDERS + NO_OF_SELL_ORDERS);
	private CountDownLatch finishLatch = new CountDownLatch(NO_OF_BUY_ORDERS + NO_OF_SELL_ORDERS);

	private TradingExchange tradingExchange;
	
	@Before
	public void setup(){
		tradingExchange = new TradingExchangeImpl(new OrderCacheStore());
	}
	
	@Test
	public void testTradingExchange(){
		
		addOrders(NO_OF_BUY_ORDERS, RIC, QUANTITY, PRICE, USER_1, BUY);
		addOrders(NO_OF_SELL_ORDERS, RIC, QUANTITY, PRICE, USER_2, SELL);
		
		try {
			finishLatch.await(10,TimeUnit.SECONDS);
		
			List<Order> openBuyOrders = tradingExchange.getOpenInterest(RIC, BUY);
			assertEquals(-0, openBuyOrders.size());
			
			List<Order> opeSellOrders = tradingExchange.getOpenInterest(RIC, SELL);
			assertEquals(0, opeSellOrders.size());
			
			int user1Quantity = tradingExchange.getExecutedQuantity(RIC, USER_1);
			assertEquals(NO_OF_BUY_ORDERS * QUANTITY, user1Quantity);
			int user2Quantity = tradingExchange.getExecutedQuantity(RIC, USER_2);
			assertEquals(-1 * NO_OF_SELL_ORDERS * QUANTITY, user2Quantity);
			
			double averagePrice = tradingExchange.getAverageExecutedPrice(RIC);
			assertEquals(Double.valueOf(PRICE), Double.valueOf(averagePrice));
			
		} catch (InterruptedException e) {
			fail("Failed to add order" + e);
		};
		
	}

	
	private void addOrders(int noOfOrders, String ric, int quantity, double price, String user, Direction direction){
		
		for(int i=0; i < noOfOrders; i++){
			Order order = new Order(ric, quantity, price, user, direction);
			addOrderOnSeparateThread(order);
		}
	}

	private void addOrderOnSeparateThread(Order order){
		Thread workThread = new Thread(() -> {
			try {
				startBarrier.await(); //make sure all thread start adding orders at same time
				tradingExchange.addOrder(order);
				finishLatch.countDown();
			} catch (Exception e) {
				logger.error("Error when adding order", e);
			}

		});
		
		workThread.start();
	}

}
