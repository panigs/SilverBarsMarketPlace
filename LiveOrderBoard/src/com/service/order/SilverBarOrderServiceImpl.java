package com.service.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicInteger;

import com.model.SilverBarOrder;
import com.model.SilverBarOrderStatusEnum;
import com.model.SilverBarOrderTypeEnum;
import com.service.inmemory.SBInMemoryDataService;

/**
 * An implementation of Silver Bar Order Service.
 * @author panigs
 *
 */

public class SilverBarOrderServiceImpl implements SilverBarOrderService {

	// /ignored use of interface for simplicity
	private SBInMemoryDataService sbOrderDataService;

	//Live order board summary cache
	private SBLiveOrderBoardSummaryCache liveOrderBoardCache;
	
	/*
	 * current orderId in use. Should be a service, but this implementation is for simplicity.
	 */
	private AtomicInteger currentOrderId;
	
	public SilverBarOrderServiceImpl() {
		sbOrderDataService = new SBInMemoryDataService();
		liveOrderBoardCache = new SBLiveOrderBoardSummaryCache(sbOrderDataService);
		OptionalInt maxOrderId = sbOrderDataService.getAllOrders()
										.stream()
										.mapToInt(SilverBarOrder::getOrderId)
										.max();
		if(maxOrderId.isPresent()) {
			currentOrderId = new AtomicInteger(maxOrderId.getAsInt());
		} else {
			currentOrderId = new AtomicInteger(0);
		}
	}

	private Integer getNextOrderId() {		
		//Will leave with boxing to get the thread safety
		return currentOrderId.incrementAndGet();
	}
	
	
	@Override
	public SilverBarOrder registerOrder(String userId, double quantity, Integer price, SilverBarOrderTypeEnum type) {
		SilverBarOrder newOrder = new SilverBarOrder(
				userId, 
				quantity, 
				price, 
				type, 
				getNextOrderId(), 
				SilverBarOrderStatusEnum.LIVE);
		
		//save the order
		sbOrderDataService.saveOrder(newOrder);
		
		//Update the cache, should have been in a different thread for performance.
		//However used below for simplicity
		liveOrderBoardCache.updateLiveOrderSummaryCache(newOrder);
		
		return newOrder;
	}

	@Override
	public SilverBarOrder cancelOrder(Integer orderId) {
		if(orderId != null && sbOrderDataService.getOrder(orderId) != null) {
			SilverBarOrder cancelledOrder = sbOrderDataService.getOrder(orderId);
			
			if(SilverBarOrderStatusEnum.CANCELLED == cancelledOrder.getStatus()) {
				//order already cancelled. can't be cancelled again
				return cancelledOrder;
			}
			
			cancelledOrder.setStatus(SilverBarOrderStatusEnum.CANCELLED);
			
			sbOrderDataService.updateOrder(cancelledOrder);
			
			//Update the cache, should have been in a different thread for performance.
			//However used below for simplicity
			liveOrderBoardCache.updateLiveOrderSummaryCache(cancelledOrder);

			return cancelledOrder;
		}
		
		return null;
	}

	@Override
	public List<SilverBarOrder> getLiveOrderSummary() {
		List<SilverBarOrder> liveOrderSummary = new ArrayList<SilverBarOrder>();
		liveOrderSummary.addAll(liveOrderBoardCache.getCache().get(SilverBarOrderTypeEnum.BUY).descendingMap().values());
		liveOrderSummary.addAll(liveOrderBoardCache.getCache().get(SilverBarOrderTypeEnum.SELL).values());
		return Collections.unmodifiableList(liveOrderSummary);
	}				
}

