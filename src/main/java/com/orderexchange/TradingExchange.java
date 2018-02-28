package com.orderexchange;

import java.util.List;

import com.orderexchange.Order.Direction;

/**
 * Main interface used for adding/retrieving orders and to gather relevant statistics
 * 
 * @author Keith
 *
 */
public interface TradingExchange {
	
	public void addOrder(Order order);
	
	public List<Order> getOpenInterest(String ric, Direction direction);
	
	public double getAverageExecutedPrice(String ric);
	
	public int getExecutedQuantity(String ric, String user);
	
}
