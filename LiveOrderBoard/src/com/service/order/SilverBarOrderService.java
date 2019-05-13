package com.service.order;

import java.util.List;

import com.model.SilverBarOrder;
import com.model.SilverBarOrderTypeEnum;

/**
 * Interface for Silver Bar Order Services. It handles registering and cancelling of orders.
 * It can also prepare Live Order Board Summary details.
 * 
 * Given this service API will only be used by internal GUI team, no service creation factory
 * methods has been provided. Also helps keeping it simple.
 * @author panigs
 *
 */

public interface SilverBarOrderService {
	
	/*
	 * Registers a new Order with the given data. 
	 * Creates a new orderId and saves the order.
	 * returns the newly created order
	 * 
	 * returns null if not successful
	 */
	public SilverBarOrder registerOrder(String userId, double quantity, Integer price, SilverBarOrderTypeEnum type);
	
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
