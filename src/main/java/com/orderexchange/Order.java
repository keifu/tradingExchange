package com.orderexchange;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import com.sun.org.apache.xerces.internal.util.Status;

public class Order {

	public static enum Direction{BUY,SELL};
	
	public static enum Status{OPEN,EXECUTED};

	private static AtomicInteger sequenceGenerator = new AtomicInteger();
	
	//simple sequence generator so that each order has a unique id
	private final Integer id = sequenceGenerator.incrementAndGet();
	
	private final String ric;
	
	private final int quantity;
	
	private final double price;
	
	private final String user;
	
	private final Direction direction;
	
	private double executedPrice;
	
	private Status status = Status.OPEN;

	public Order(String ric, int quantity, double price, String user, Direction direction){
		this.ric = Objects.requireNonNull(ric, "RIC cannot be null");
		this.quantity =  Objects.requireNonNull(quantity, "Quantity cannot be null");
		this.price =  Objects.requireNonNull(price, "Quantity cannot be null");
		this.user =  Objects.requireNonNull(user, "User cannot be null");
		this.direction = Objects.requireNonNull(direction, "Order Direction cannot be null");
	}
	
	public int getId() {
		return id;
	}

	public String getRic() {
		return ric;
	}

	public int getQuantity() {
		return quantity;
	}
	
	public double getPrice() {
		return price;
	}

	public String getUser() {
		return user;
	}

	public Direction getDirection() {
		return direction;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public Status getStatus() {
		return status;
	}
	
    public double getExecutedPrice() {
		return executedPrice;
	}
    
    public void setExecutedPrice(double executedPrice) {
		this.executedPrice = executedPrice;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		return id.equals(other.id);
	}

	@Override
	public String toString() {
		return "[ID = " + this.id + ", RIC = " + this.ric + ", Quantity = " + this.quantity + ", Price = " + this.price
				+ ", User = " + this.user + ", Direction = " + this.direction + ", Executed Price = " + this.executedPrice + ", Status = " + this.status +  "]";
	}
	
}
