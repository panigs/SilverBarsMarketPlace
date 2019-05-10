package com.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.model.SilverBarOrder;
import com.model.SilverBarOrderStatusEnum;
import com.model.SilverBarOrderTypeEnum;

public class SilverBarOrderServiceImpl implements SilverBarOrderService {

	// Making it singleton
	public static final SilverBarOrderServiceImpl INSTANCE = new SilverBarOrderServiceImpl();
	
	
	/*
	 * Live Order Board changes quite often with every new/cancel order.
	 * Even if there is no change, GUI may call server side at regular intervals
	 * to get the latest summary. Hence having a cache will be beneficial
	 * 
	 * Buy/Sell->Price->MergedOrder
	 * 
	 * Decided to lazy load cache. We need to ask how many orders needs to be 
	 * loaded while starting the service and accordingly should implement.
	 */
	private Map<SilverBarOrderTypeEnum, TreeMap<Integer, SilverBarOrder>> liveOrderBoardSummaryCache;

	//In memory storage to store orders. Haven't handled the size properly for simplicity
	private Map<Integer, SilverBarOrder> orders = new ConcurrentHashMap<Integer, SilverBarOrder>();
	
	/*
	 * current orderId in use. Should be a service, but this implementation is for simplicity.
	 * Always initializing to 0 for simplicity.
	 */
	private AtomicInteger currentOrderId = new AtomicInteger(0);
	
	private SilverBarOrderServiceImpl() {
		//With the in memory solution i will always start with zero orders. So empty summary info

		liveOrderBoardSummaryCache = new HashMap<SilverBarOrderTypeEnum, TreeMap<Integer,SilverBarOrder>>();
		liveOrderBoardSummaryCache.put(SilverBarOrderTypeEnum.BUY, new TreeMap<Integer, SilverBarOrder>());
		liveOrderBoardSummaryCache.put(SilverBarOrderTypeEnum.SELL, new TreeMap<Integer, SilverBarOrder>());
	}

	private Integer getNextOrderId() {		
		//Will leave with boxing to get the thread safety
		return currentOrderId.incrementAndGet();
	}
	
	//Used synchronized for simplicity
	private synchronized void updateLiveOrderSummaryCache(final SilverBarOrder order) {
		Map<Integer,SilverBarOrder> priceOrderMap = liveOrderBoardSummaryCache.get(order.getType());
		SilverBarOrder mergedOrder = priceOrderMap.get(order.getPrice());
		
		if(SilverBarOrderStatusEnum.LIVE == order.getOrderStatus()) {			
			if(mergedOrder == null) {
				//create a new order for merging purpose
				SilverBarOrder newOrder = new SilverBarOrder();
				newOrder.setOrderId(null);
				newOrder.setUserId(-1);//as per model
				newOrder.setQuantity(order.getQuantity());
				newOrder.setPrice(order.getPrice());
				newOrder.setType(order.getType());
				newOrder.setOrderStatus(SilverBarOrderStatusEnum.LIVE);
				
				priceOrderMap.put(order.getPrice(), newOrder);
			} else {
				mergedOrder.setQuantity(mergedOrder.getQuantity() + order.getQuantity());
			}
			
		} else if(SilverBarOrderStatusEnum.CANCELLED == order.getOrderStatus()) {
			//If we are canceling the order then it wont be null. Hence no null check is required.
			mergedOrder.setQuantity(mergedOrder.getQuantity() - order.getQuantity());
		} 
		
		return;
	}
	
	@Override
	public SilverBarOrder registerOrder(int userId, double quantity, Integer price, SilverBarOrderTypeEnum orderType) {
		// TODO Auto-generated method stub
		SilverBarOrder newOrder = new SilverBarOrder();
		newOrder.setOrderId(getNextOrderId());
		newOrder.setUserId(userId);
		newOrder.setQuantity(quantity);
		newOrder.setPrice(price);
		newOrder.setType(orderType);
		newOrder.setOrderStatus(SilverBarOrderStatusEnum.LIVE);
		
		//save the order
		orders.put(newOrder.getOrderId(), newOrder);
		
		//Update the cache, should have been in a different thread for performance.
		//However used below for simplicity
		updateLiveOrderSummaryCache(newOrder);
		
		return newOrder;
	}

	@Override
	public SilverBarOrder cancelOrder(Integer orderId) {
		if(orderId != null && orders.get(orderId) != null) {
			SilverBarOrder cancelledOrder = orders.get(orderId);
			
			if(SilverBarOrderStatusEnum.CANCELLED == cancelledOrder.getOrderStatus()) {
				//order already cancelled. can't be cancelled again
				return cancelledOrder;
			}
			
			cancelledOrder.setOrderStatus(SilverBarOrderStatusEnum.CANCELLED);
			//Another put is not really required given the implementation

			//Update the cache, should have been in a different thread for performance.
			//However used below for simplicity
			updateLiveOrderSummaryCache(cancelledOrder);

			return cancelledOrder;
		}
		
		return null;
	}

	@Override
	public List<SilverBarOrder> getLiveOrderSummary() {
		List<SilverBarOrder> liveOrderSummary = new ArrayList<SilverBarOrder>();
		liveOrderSummary.addAll(liveOrderBoardSummaryCache.get(SilverBarOrderTypeEnum.BUY).descendingMap().values());
		liveOrderSummary.addAll(liveOrderBoardSummaryCache.get(SilverBarOrderTypeEnum.SELL).values());
		return Collections.unmodifiableList(liveOrderSummary);
	}				
}

