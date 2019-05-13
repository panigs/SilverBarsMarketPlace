package com.service.inmemory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.model.SilverBarOrder;
import com.model.SilverBarOrderStatusEnum;
import com.model.SilverBarOrderTypeEnum;

/**
 * Data service for Silver Bar Orders.
 * This is an in-memory solution and uses map to store orders.
 * This has logic to hold 4 existing orders as below when initialized.
 * SELL 3.50kg for £306,  
 * SELL 1.50kg for £307,  
 * SELL 1.20kg for £310, 
 * SELL 2.00kg for £306
 * 
 * It also models 3 user ids (user1/2/3) for simplicity.
 * @author panigs
 *
 */

public class SBInMemoryDataService {
	
	/** 
	 * In memory solution for UserIds
	 */
	public static final String USER1 = "user1";
	public static final String USER2 = "user2";
	public static final String USER3 = "user3";

	/**
	 * In memory storage to store orders. 
	 */
	private Map<Integer, SilverBarOrder> orders;

	
	public SBInMemoryDataService() {
		super();
		orders = new ConcurrentHashMap<Integer, SilverBarOrder>();
		init();
	}

	/**
	 * Given a order, saves to the storage
	 * @param newOrder
	 */
	public void saveOrder(SilverBarOrder newOrder) {
		orders.put(newOrder.getOrderId(), newOrder);
	}
	
	/**
	 * Given the order, updates the existing order
	 * If its a new order then it will just save it.
	 * @param updatedOrder
	 */
	public void updateOrder(SilverBarOrder updatedOrder) {
		orders.put(updatedOrder.getOrderId(), updatedOrder);
	}
	
	/**
	 * Given a orderId find the order and returns it.
	 * @param orderId
	 * @return
	 */
	public SilverBarOrder getOrder(Integer orderId) {
		return orders.get(orderId);
	}
	
	/**
	 * Retrieves all stored order.
	 * @return
	 */
	public Collection<SilverBarOrder> getAllOrders() {
		return orders.values();
	}

	private void init() {
		//Populate some orders to start with. Same as the give example
		
		saveOrder(new SilverBarOrder(USER1, 3.5, 306, SilverBarOrderTypeEnum.SELL, 1, SilverBarOrderStatusEnum.LIVE));
		saveOrder(new SilverBarOrder(USER2, 1.2, 310, SilverBarOrderTypeEnum.SELL, 2, SilverBarOrderStatusEnum.LIVE));
		saveOrder(new SilverBarOrder(USER3, 1.5, 307, SilverBarOrderTypeEnum.SELL, 3, SilverBarOrderStatusEnum.LIVE));
		saveOrder(new SilverBarOrder(USER1, 2.0, 306, SilverBarOrderTypeEnum.SELL, 4, SilverBarOrderStatusEnum.LIVE));
	}
}
