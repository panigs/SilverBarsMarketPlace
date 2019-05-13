/**
 * 
 */
package com.service;

import static org.junit.Assert.assertTrue;
import static com.service.inmemory.SBInMemoryDataService.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.model.SilverBarOrder;
import com.model.SilverBarOrderStatusEnum;
import com.model.SilverBarOrderTypeEnum;
import com.service.order.SilverBarOrderService;
import com.service.order.SilverBarOrderServiceImpl;

/**
 * Test Class to test SilverBarOrderService
 * 
 * @author panigs
 *
 */
public class TestSilverBarOrderServiceImpl {
	
	private SilverBarOrderService service;

	@Before
	public void setUp() throws Exception {
		service = new SilverBarOrderServiceImpl();
	}

	@Test
	public void testRegisterOrder() {
		SilverBarOrder order = service.registerOrder(USER1, 2.0, 406, SilverBarOrderTypeEnum.BUY);
		verifyOrder(order, USER1, 2.0, 406, SilverBarOrderTypeEnum.BUY, SilverBarOrderStatusEnum.LIVE);		
	}
	
	@Test
	public void testCancelOrder() {
		SilverBarOrder order = service.registerOrder(USER1, 2.0, 406, SilverBarOrderTypeEnum.BUY);
		order = service.cancelOrder(order.getOrderId());//This is the first order that was added
		assertTrue("Order is not cancelled", order.getStatus() == SilverBarOrderStatusEnum.CANCELLED);		
	}

	@Test
	public void testLiveOrderSummary() {

		// 1. Verify the existing orders summary
		List<SilverBarOrder> liveOrderSummary = service.getLiveOrderSummary();
		SilverBarOrder[] mergedOrders1 = 
			{
					//based on initial orders
				 createOrder(5.5, 306, SilverBarOrderTypeEnum.SELL),
				 createOrder(1.5, 307, SilverBarOrderTypeEnum.SELL),
				 createOrder(1.2, 310, SilverBarOrderTypeEnum.SELL)
			};
		
		verifyOrderSummary(liveOrderSummary, mergedOrders1);
				
		// 2. Register new Order(s) and verify 
		SilverBarOrder order = service.registerOrder(USER1, 2.0, 406, SilverBarOrderTypeEnum.BUY);
		service.registerOrder(USER1, 2.5, 407, SilverBarOrderTypeEnum.BUY);
		service.registerOrder(USER1, 1.5, 406, SilverBarOrderTypeEnum.BUY);

		liveOrderSummary = service.getLiveOrderSummary();
		SilverBarOrder[] mergedOrders2 = 
			{
				 createOrder(2.5, 407, SilverBarOrderTypeEnum.BUY),
				 createOrder(3.5, 406, SilverBarOrderTypeEnum.BUY),
				 createOrder(5.5, 306, SilverBarOrderTypeEnum.SELL),
				 createOrder(1.5, 307, SilverBarOrderTypeEnum.SELL),
				 createOrder(1.2, 310, SilverBarOrderTypeEnum.SELL)
			};
		
		verifyOrderSummary(liveOrderSummary, mergedOrders2);
		
		// 3. Cancel a order and verify
		order = service.cancelOrder(order.getOrderId());//This is the first order that was added

		liveOrderSummary = service.getLiveOrderSummary();
		mergedOrders2[1] = createOrder(1.5, 406, SilverBarOrderTypeEnum.BUY);
		verifyOrderSummary(liveOrderSummary, mergedOrders2);
	}
	
	private void verifyOrder(SilverBarOrder order, String userId, double quantity, Integer price, 
			SilverBarOrderTypeEnum type, SilverBarOrderStatusEnum status) {
		assertTrue("UserId doesn't match on the order", order.getUserId().equals(userId));
		verifyOrder(order, quantity, price, type);
		assertTrue("Status doesn't match on the order", order.getStatus() == status);
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
		
		//Helpful to developer during testing
		//System.out.println(summary);
	}
	
	private SilverBarOrder createOrder(double quantity, Integer price, SilverBarOrderTypeEnum type) {
		return new SilverBarOrder(
				null, 
				quantity, 
				price, 
				type, 
				null, 
				null);
	}
}
