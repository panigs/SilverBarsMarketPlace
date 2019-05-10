package com.service;

import java.util.List;

import com.model.SilverBarOrder;
import com.model.SilverBarOrderTypeEnum;

public interface SilverBarOrderService {

	/*
	 * Registers a new Order with the given data. 
	 * Creates a new orderId and saves the order.
	 * returns the newly created order
	 * 
	 * returns null if not successful
	 */
	public SilverBarOrder registerOrder(int userId, double quantity, Integer price, SilverBarOrderTypeEnum orderType);
	
	/*
	 * Takes the order Id and cancels it. 
	 * Returns the cancelled order
	 */
	public SilverBarOrder cancelOrder(Integer orderId);

	/*
	 * Gets live order summary as per the requirement.
	 * I decided not to describe the requirement here.
	 */
	public List<SilverBarOrder> getLiveOrderSummary();
}
