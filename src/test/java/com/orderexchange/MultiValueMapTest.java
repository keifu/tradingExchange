package com.orderexchange;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.orderexchange.Order.Direction;

public class MultiValueMapTest {
	
	private MultiValueMap<String, Order> map;
	
	@Before
	public void setup(){
		map = new MultiValueMap<>();
	}
	
	@Test
	public void testGet_Empty(){
		List<Order> orders = map.get("VOD.L");
		assertTrue(orders.isEmpty());
	}
	
	@Test
	public void testPutAndGet(){
		
		Order order_1 = new Order("VOD.L", 1000, 100.2, "user 1" , Direction.SELL);
		Order order_2 = new Order("VOD.L", 1000, 100.2, "user 2" , Direction.BUY);
		Order order_3 = new Order("VOD.L", 1000, 99, "user 1" , Direction.BUY);
		
		map.put("VOD.L", order_1);
		map.put("VOD.L", order_2);
		map.put("VOD.L", order_3);
		
		List<Order> orders = map.get("VOD.L");
		assertEquals(3, orders.size());
	}
	
	@Test
	public void testRemove(){
		
		Order order_1 = new Order("VOD.L", 1000, 100.2, "user 1" , Direction.SELL);
		Order order_2 = new Order("VOD.L", 1000, 100.2, "user 2" , Direction.BUY);
		Order order_3 = new Order("VOD.L", 1000, 99, "user 1" , Direction.BUY);
		
		map.put("VOD.L", order_1);
		map.put("VOD.L", order_2);
		map.put("VOD.L", order_3);
		
		map.removeValue("VOD.L", order_1);
		List<Order> orders = map.get("VOD.L");
		assertEquals(2, orders.size());
		assertTrue(orders.contains(order_2));
		assertTrue(orders.contains(order_3));
		
		map.removeValue("VOD.L", order_2);
		map.removeValue("VOD.L", order_3);
		orders = map.get("VOD.L");
		assertTrue(orders.isEmpty());
	}
	

}
