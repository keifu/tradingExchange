package com.orderexchange;


/**
 * A wrapper class which represents 2 orders that are matched
 * 
 * @author Keith
 *
 */
public class MatchedOrder {
	
	private final String ric;
	private final double executedPrice;
	private final int quantity;
	
	private final Order newOrder;
	private final Order matchingOrder;
	
	public MatchedOrder(String ric, double executedPrice, int quanity, Order newOrder, Order matchingOrder){
		 this.ric = ric;
		 this.executedPrice = executedPrice;
		 this.quantity = quanity;
		 this.newOrder = newOrder;
		 this.matchingOrder = matchingOrder;
	}
	
	public String getRic() {
		return ric;
	}
	
	public double getExecutedPrice() {
		return executedPrice;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public Order getNewOrder() {
		return newOrder;
	}
	
	public Order getMatchingOrder() {
		return matchingOrder;
	}
	
	@Override
	public String toString() {
		return "[RIC = " + this.ric + ", Quantity = " + this.quantity + ", Executed Price = " + this.executedPrice
				+ ", new order = " + this.newOrder +  ", matchingorder = " + this.matchingOrder +  "]";
	}

}
