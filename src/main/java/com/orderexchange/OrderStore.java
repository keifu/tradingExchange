package com.orderexchange;

import java.util.List;

import com.orderexchange.Order.Direction;

public interface OrderStore {
	
	public void addOpenOrder(Order order);
	
	public void removeOpenOrder(Order order);
	
	public void addExecutedOrder(Order order);
	
	public void addMatchedOrder(MatchedOrder matchedOrder); 
	
	public List<MatchedOrder> getMatchedOrders(String ric); 
	
	public List<Order> getOpenOrders(String ric, Direction direction);
	
	public List<Order> getExecutedOrders(String ric, String user);
	

}
