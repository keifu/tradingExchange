package com.orderexchange;

import static com.orderexchange.Order.Direction.BUY;
import static com.orderexchange.Order.Direction.SELL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

public class TradingExchangeImplTest {
	
	private TradingExchangeImpl tradingExchange;
	
	//orders as per example
	private Order order_1_user_1_sell_1000_AT_100_2 = new Order("VOD.L", 1000, 100.2, "user 1" , SELL);
	private Order order_2_user_2_buy_1000_AT_100_2 = new Order("VOD.L", 1000, 100.2, "user 2" , BUY);
	private Order order_3_user_1_buy_1000_AT_99 = new Order("VOD.L", 1000, 99, "user 1" , BUY);
	private Order order_4_user_1_buy_1000_AT_101 = new Order("VOD.L", 1000, 101, "user 1" , BUY);
	private Order order_5_user_2_sell_500_102 = new Order("VOD.L", 500, 102, "user 2" , SELL);
	private Order order_6_user_1_buy_500_103 = new Order("VOD.L", 500, 103, "user 1" , BUY);
	private Order order_7_user_2_sell_1000_98 = new Order("VOD.L", 1000, 98, "user 2" , SELL);
	
	@Before
	public void setup(){	
		tradingExchange = new TradingExchangeImpl(new OrderCacheStore());
	}
	
	@Test
	public void testOpenOrders_Empty(){
		List<Order> buyOrders = tradingExchange.getOpenInterest("VOD.L", BUY);
		assertTrue(buyOrders.isEmpty());
		
		List<Order> sellOrders = tradingExchange.getOpenInterest("VOD.L", SELL);
		assertTrue(sellOrders.isEmpty());
		
	}
	
	@Test
	public void testOpenOrders_Non_Empty(){
				
		tradingExchange.addOrder(order_1_user_1_sell_1000_AT_100_2);
		tradingExchange.addOrder(order_5_user_2_sell_500_102);
		tradingExchange.addOrder(order_7_user_2_sell_1000_98);
		
		List<Order> sellOrders = tradingExchange.getOpenInterest("VOD.L", SELL);
		assertEquals(3, sellOrders.size());
	}
	
	@Test
	public void testExampleScenario(){
		
		tradingExchange.addOrder(order_1_user_1_sell_1000_AT_100_2);
		tradingExchange.addOrder(order_2_user_2_buy_1000_AT_100_2);
		
		double averagePrice = tradingExchange.getAverageExecutedPrice("VOD.L");
		assertEquals(Double.valueOf(100.2), Double.valueOf(averagePrice));
		
		int executedQuantityUser1 = tradingExchange.getExecutedQuantity("VOD.L", "user 1");
		assertEquals(-1000, executedQuantityUser1);	
		
		int executedQuantityUser2 = tradingExchange.getExecutedQuantity("VOD.L", "user 2");
		assertEquals(1000, executedQuantityUser2);	
		
		tradingExchange.addOrder(order_3_user_1_buy_1000_AT_99);
		tradingExchange.addOrder(order_4_user_1_buy_1000_AT_101);
		tradingExchange.addOrder(order_5_user_2_sell_500_102);
		
		List<Order> openInterestOrdersBuy = tradingExchange.getOpenInterest("VOD.L", BUY);
		assertEquals(2, openInterestOrdersBuy.size());
		assertEquals(order_3_user_1_buy_1000_AT_99, openInterestOrdersBuy.get(0));
		assertEquals(order_4_user_1_buy_1000_AT_101, openInterestOrdersBuy.get(1));
		
		List<Order> openInterestOrdersSell = tradingExchange.getOpenInterest("VOD.L", SELL);
		assertEquals(1, openInterestOrdersSell.size());
		assertEquals(order_5_user_2_sell_500_102, openInterestOrdersSell.get(0));
		
		tradingExchange.addOrder(order_6_user_1_buy_500_103);
		
		averagePrice = tradingExchange.getAverageExecutedPrice("VOD.L");
		assertEquals(Double.valueOf(101.1333), Double.valueOf(averagePrice));
		
		executedQuantityUser1 = tradingExchange.getExecutedQuantity("VOD.L", "user 1");
		assertEquals(-500, executedQuantityUser1);	
		
	    executedQuantityUser2 = tradingExchange.getExecutedQuantity("VOD.L", "user 2");
		assertEquals(500, executedQuantityUser2);
		
		
		tradingExchange.addOrder(order_7_user_2_sell_1000_98);
		
		openInterestOrdersBuy = tradingExchange.getOpenInterest("VOD.L", BUY);
		assertEquals(1, openInterestOrdersBuy.size());
		assertEquals(order_3_user_1_buy_1000_AT_99, openInterestOrdersBuy.get(0));

		openInterestOrdersSell = tradingExchange.getOpenInterest("VOD.L", SELL);
		assertTrue(openInterestOrdersSell.isEmpty());
		
		averagePrice = tradingExchange.getAverageExecutedPrice("VOD.L");
		assertEquals(Double.valueOf(99.8800), Double.valueOf(averagePrice));
		
		executedQuantityUser1 = tradingExchange.getExecutedQuantity("VOD.L", "user 1");
		assertEquals(500, executedQuantityUser1);	
		
	    executedQuantityUser2 = tradingExchange.getExecutedQuantity("VOD.L", "user 2");
		assertEquals(-500, executedQuantityUser2);	
		
	}
    
	@Test
	public void testMatchingOrder_Order_With_Same_Price(){
		
		Order order1 = new Order("VOD.L", 1000, 100.2, "user 1" , SELL);
		Order order2 = new Order("VOD.L", 1000, 100.2, "user 2" , SELL);
		Order order3 = new Order("VOD.L", 1000, 100.2, "user 3" , SELL);
		tradingExchange.addOrder(order1);
		tradingExchange.addOrder(order2);
		tradingExchange.addOrder(order3);
		
		Order newOrder = new Order("VOD.L", 1000, 100.2, "user 3" , BUY);
		Optional<Order> matchingOrder = tradingExchange.getMatchingOrder(newOrder);
		
		assertEquals(order1, matchingOrder.get());
		
	}
	
	@Test
	public void testMatchingOrder_Sell_Order_Matching_Highest_Buying_Price(){
		
		Order order1 = new Order("VOD.L", 1000, 100.6, "user 1" , BUY);
		Order order2 = new Order("VOD.L", 1000, 100.8, "user 2" , BUY);
		Order order3 = new Order("VOD.L", 1000, 100.4, "user 3" , BUY);
		tradingExchange.addOrder(order1);
		tradingExchange.addOrder(order2);
		tradingExchange.addOrder(order3);

		Order newOrder = new Order("VOD.L", 1000, 100.2, "user 3" , SELL);
		Optional<Order> matchingOrder = tradingExchange.getMatchingOrder(newOrder);
		assertEquals(order2, matchingOrder.get());
		
	}
	
	@Test
	public void testMatchingOrder_Buy_Order_Matching_Lowest_Selling_Price(){
		
		Order order1 = new Order("VOD.L", 1000, 100.4, "user 1" , SELL);
		Order order2 = new Order("VOD.L", 1000, 100.3, "user 2" , SELL);
		Order order3 = new Order("VOD.L", 1000, 100.6, "user 3" , SELL);
		tradingExchange.addOrder(order1);
		tradingExchange.addOrder(order2);
		tradingExchange.addOrder(order3);
		
		Order newOrder = new Order("VOD.L", 1000, 100.8, "user 3" , BUY);
		Optional<Order> matchingOrder = tradingExchange.getMatchingOrder(newOrder);
		
		assertEquals(order2, matchingOrder.get());
	
	}
	
	@Test
	public void testMatchingOrder_No_Match(){
		Order order1 = new Order("VOD.L", 1000, 100.4, "user 1" , SELL);
		Order order2 = new Order("VOD.L", 1000, 100.3, "user 2" , SELL);
		Order order3 = new Order("VOD.L", 1000, 100.6, "user 3" , SELL);		
		tradingExchange.addOrder(order1);
		tradingExchange.addOrder(order2);
		tradingExchange.addOrder(order3);
		
		Order newOrder = new Order("VOD.L", 1000, 100.2, "user 3" , BUY);
		Optional<Order> matchingOrder = tradingExchange.getMatchingOrder(newOrder);
		assertTrue(!matchingOrder.isPresent());
	}
	
	@Test
	public void testAverageExecutedPrice_No_Executed_Orders(){
		double averagePrice= tradingExchange.getAverageExecutedPrice("VOD.L");
		assertEquals(Double.valueOf(0), Double.valueOf(averagePrice));
	}
	
	@Test
	public void testAverageExecutedPrice(){
		
		tradingExchange.addOrder(order_1_user_1_sell_1000_AT_100_2);
		tradingExchange.addOrder(order_2_user_2_buy_1000_AT_100_2);
		
		tradingExchange.addOrder(order_6_user_1_buy_500_103);
		tradingExchange.addOrder(order_5_user_2_sell_500_102);
		
		double averagePrice= tradingExchange.getAverageExecutedPrice("VOD.L");
		assertEquals(Double.valueOf(100.8), Double.valueOf(averagePrice));
	}
	
	@Test
	public void testExecutedQuantity_No_Executed_Orders(){
		int quantity= tradingExchange.getExecutedQuantity("VOD.L", "user 1");
		assertEquals(0, quantity);
	}
	
	@Test
	public void testExecutedQuantity(){
		
		tradingExchange.addOrder(order_1_user_1_sell_1000_AT_100_2);
		tradingExchange.addOrder(order_2_user_2_buy_1000_AT_100_2);
		tradingExchange.addOrder(order_5_user_2_sell_500_102);
		tradingExchange.addOrder(order_6_user_1_buy_500_103);
		
		int user1quantity= tradingExchange.getExecutedQuantity("VOD.L", "user 1");
		assertEquals(-500, user1quantity);
		
		int user2quantity= tradingExchange.getExecutedQuantity("VOD.L", "user 2");
		assertEquals(500, user2quantity);
	}

}
