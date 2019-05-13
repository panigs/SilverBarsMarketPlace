package com.service.order;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.model.SilverBarOrder;
import com.model.SilverBarOrderStatusEnum;
import com.model.SilverBarOrderTypeEnum;
import com.service.inmemory.SBInMemoryDataService;

/**
 * Live Order Board changes quite often with every new/cancel order.
 * Even if there is no change, GUI may call server side at regular intervals
 * to get the latest summary. Hence having a cache will be beneficial
 * 
 * Buy/Sell->Price->MergedOrder
 * @author panigs
 *
 */

public class SBLiveOrderBoardSummaryCache {

	private SBInMemoryDataService sbOrderDataService;

	private Map<SilverBarOrderTypeEnum, TreeMap<Integer, SilverBarOrder>> liveOrderBoardSummaryCache;

	public SBLiveOrderBoardSummaryCache(SBInMemoryDataService sbOrderDataService) {
		this.sbOrderDataService = sbOrderDataService;
		
		liveOrderBoardSummaryCache = new HashMap<SilverBarOrderTypeEnum, TreeMap<Integer,SilverBarOrder>>();
		liveOrderBoardSummaryCache.put(SilverBarOrderTypeEnum.BUY, new TreeMap<Integer, SilverBarOrder>());
		liveOrderBoardSummaryCache.put(SilverBarOrderTypeEnum.SELL, new TreeMap<Integer, SilverBarOrder>());
		
		init();
	}
	
	/**
	 * Given a order updated the cache. 
	 * It can accept both LIVE and CANCELLED order
	 * 
	 * It is thread safe. (Used synchronized for simplicity)
	 * @param order
	 */
	public synchronized void updateLiveOrderSummaryCache(final SilverBarOrder order) {
		Map<Integer,SilverBarOrder> priceOrderMap = liveOrderBoardSummaryCache.get(order.getType());
		SilverBarOrder mergedOrder = priceOrderMap.get(order.getPrice());
		
		if(SilverBarOrderStatusEnum.LIVE == order.getStatus()) {			
			if(mergedOrder == null) {
				//create a new order for merging purpose
				SilverBarOrder newOrder = new SilverBarOrder(
						null, 
						order.getQuantity(), 
						order.getPrice(), 
						order.getType(), 
						null, 
						SilverBarOrderStatusEnum.LIVE);
				
				priceOrderMap.put(order.getPrice(), newOrder);
			} else {
				mergedOrder.setQuantity(mergedOrder.getQuantity() + order.getQuantity());
			}
			
		} else if(SilverBarOrderStatusEnum.CANCELLED == order.getStatus()) {
			//If we are canceling the order then it wont be null. Hence no null check is required.
			mergedOrder.setQuantity(mergedOrder.getQuantity() - order.getQuantity());
		} 
		
		return;
	}
	
	/**
	 * Returns the internal cached object.
	 * @return
	 */
	public Map<SilverBarOrderTypeEnum, TreeMap<Integer, SilverBarOrder>> getCache(){
		return liveOrderBoardSummaryCache;
	}
	
	private void init() {
		//initialize the cache for all existing orders
		sbOrderDataService.getAllOrders().forEach(order -> updateLiveOrderSummaryCache(order));
	}
}
