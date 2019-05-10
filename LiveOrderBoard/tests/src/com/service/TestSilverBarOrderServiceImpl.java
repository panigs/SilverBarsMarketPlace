/**
 * 
 */
package com.service;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.model.SilverBarOrder;
import com.model.SilverBarOrderStatusEnum;
import com.model.SilverBarOrderTypeEnum;

/**
 * @author panigs
 *
 */
public class TestSilverBarOrderServiceImpl {
	
	private SilverBarOrderService service = SilverBarOrderServiceImpl.INSTANCE;

	@Test
	//just using one test for simplicity	
	public void test() {
		SilverBarOrder order = service.registerOrder(1, 3.5, 306, SilverBarOrderTypeEnum.SELL);	
		verifyOrder(order, 1,3.5, 306, SilverBarOrderTypeEnum.SELL, SilverBarOrderStatusEnum.LIVE);
		
		service.registerOrder(2, 1.2, 310, SilverBarOrderTypeEnum.SELL);
		service.registerOrder(3, 1.5, 307, SilverBarOrderTypeEnum.SELL);
		service.registerOrder(4, 2.0, 306, SilverBarOrderTypeEnum.SELL);
		service.registerOrder(4, 2.0, 406, SilverBarOrderTypeEnum.BUY);
		service.registerOrder(4, 2.5, 407, SilverBarOrderTypeEnum.BUY);
		service.registerOrder(4, 1.5, 406, SilverBarOrderTypeEnum.BUY);
		
		List<SilverBarOrder> liveOrderSummary = service.getLiveOrderSummary();
		SilverBarOrder[] mergedOrders = 
			{
				 createOrder(2.5, 407, SilverBarOrderTypeEnum.BUY),
				 createOrder(3.5, 406, SilverBarOrderTypeEnum.BUY),
				 createOrder(5.5, 306, SilverBarOrderTypeEnum.SELL),
				 createOrder(1.5, 307, SilverBarOrderTypeEnum.SELL),
				 createOrder(1.2, 310, SilverBarOrderTypeEnum.SELL)
			};
		
		verifyOrderSummary(liveOrderSummary, mergedOrders);
		
		order = service.cancelOrder(order.getOrderId());//This is the first order that was added
		assertTrue("Order is not cancelled", order.getOrderStatus() == SilverBarOrderStatusEnum.CANCELLED);
		
		liveOrderSummary = service.getLiveOrderSummary();
		mergedOrders[2] = createOrder(2.0, 306, SilverBarOrderTypeEnum.SELL);
		verifyOrderSummary(liveOrderSummary, mergedOrders);
	}

	private void verifyOrder(SilverBarOrder order, int userId, double quantity, Integer price, 
			SilverBarOrderTypeEnum type, SilverBarOrderStatusEnum status) {
		assertTrue("UserId doesn't match on the order", order.getUserId() == userId);
		verifyOrder(order, quantity, price, type);
		assertTrue("Status doesn't match on the order", order.getOrderStatus() == status);
	}

	private void verifyOrder(SilverBarOrder order, double quantity, Integer price, 
			SilverBarOrderTypeEnum type) {
		assertTrue("Quantity doesn't match on the order", order.getQuantity() == quantity);
		assertTrue("Price doesn't match on the order", order.getPrice().intValue() == price);
		assertTrue("Type doesn't match on the order", order.getType() == type);
	}
	
	private void verifyOrderSummary( List<SilverBarOrder> summary, SilverBarOrder[] mergedOrders) {
		assertTrue("Live Order Summary doesn't have correct numner of records", summary.size() == mergedOrders.length);
		
		for(int i= 0; i< mergedOrders.length; i++) {
			verifyOrder(summary.get(i), 
					mergedOrders[i].getQuantity(),
					mergedOrders[i].getPrice(),
					mergedOrders[i].getType()
					);
			
		}
	}
	
	private SilverBarOrder createOrder(double quantity, Integer price, SilverBarOrderTypeEnum type) {
		SilverBarOrder order = new SilverBarOrder();
		order.setQuantity(quantity);
		order.setPrice(price);
		order.setType(type);
		return order;
	}
}
