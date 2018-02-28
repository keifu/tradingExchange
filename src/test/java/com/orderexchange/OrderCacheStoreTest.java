package com.orderexchange;


import static com.orderexchange.Order.Direction.BUY;
import static com.orderexchange.Order.Direction.SELL;
import static com.orderexchange.Order.Status.EXECUTED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Before;
import org.junit.Test;


public class OrderCacheStoreTest {
	
	private OrderStore orderStore;
	
	private Order order_1 = new Order("VOD.L", 1000, 98, "user 1" , SELL);
	private Order order_2 = new Order("VOD.L", 1000, 100.2, "user 2" , BUY);
	private Order order_3 = new Order("VOD.L", 1000, 99, "user 1" , BUY);
	private Order order_4 = new Order("GOOGL.OQ", 1000, 99, "user 1" , BUY);
	
	@Before
	public void setup(){
		orderStore =  new OrderCacheStore();
	}
	
	@Test
	public void testGetOpenOrders(){
		orderStore.addOpenOrder(order_1);
		orderStore.addOpenOrder(order_2);
		orderStore.addOpenOrder(order_3);
		
		List<Order> buyOrders = orderStore.getOpenOrders("VOD.L", BUY);
		assertEquals(2, buyOrders.size());
		assertTrue(buyOrders.contains(order_2));
		assertTrue(buyOrders.contains(order_3));
		
		List<Order> sellOrders = orderStore.getOpenOrders("VOD.L", SELL);
		assertEquals(1, sellOrders.size());
		assertTrue(sellOrders.contains(order_1));
		
	}
	
	@Test
	public void testGetOpenOrders_Different_RICS(){
		orderStore.addOpenOrder(order_1);
		orderStore.addOpenOrder(order_2);
		orderStore.addOpenOrder(order_3);
		orderStore.addOpenOrder(order_4);
		
		List<Order> buyOrders = orderStore.getOpenOrders("VOD.L", BUY);
		assertEquals(2, buyOrders.size());
		assertTrue(buyOrders.contains(order_2));
		assertTrue(buyOrders.contains(order_3));
		
		List<Order> sellOrders = orderStore.getOpenOrders("VOD.L", SELL);
		assertEquals(1, sellOrders.size());
		assertTrue(sellOrders.contains(order_1));
		
		buyOrders = orderStore.getOpenOrders("GOOGL.OQ", BUY);
		assertEquals(1, buyOrders.size());
		assertTrue(buyOrders.contains(order_4));	
	}
	
	@Test
	public void testGetExecutedOrders(){
		order_1.setStatus(EXECUTED);
		order_3.setStatus(EXECUTED);
		orderStore.addExecutedOrder(order_1);
		orderStore.addExecutedOrder(order_3);
		
		List<Order> executedyOrders = orderStore.getExecutedOrders("VOD.L", "user 1");
		assertEquals(2, executedyOrders.size());
		assertTrue(executedyOrders.contains(order_1));
		assertTrue(executedyOrders.contains(order_3));
	}

	
	@Test
	public void testGetExecutedOrders_Different_RICS(){
		
		order_1.setStatus(EXECUTED);
		order_3.setStatus(EXECUTED);
		order_4.setStatus(EXECUTED);
		orderStore.addExecutedOrder(order_1);
		orderStore.addExecutedOrder(order_3);
		orderStore.addExecutedOrder(order_4);
		
		List<Order> executedyOrders = orderStore.getExecutedOrders("VOD.L", "user 1");
		assertEquals(2, executedyOrders.size());
		assertTrue(executedyOrders.contains(order_1));
		assertTrue(executedyOrders.contains(order_3));
		
		executedyOrders = orderStore.getExecutedOrders("GOOGL.OQ", "user 1");
		assertEquals(1, executedyOrders.size());
		assertTrue(executedyOrders.contains(order_4));
		

	}
}
