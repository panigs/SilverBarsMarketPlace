package com.model;

//Ideally i should have provided a builder. for simplicity ignored constructor too
public class SilverBarOrder {
	/* userId of the user who placed/registered the order. 
	 * Could be an UserId type as well. 
	 * But for simplicity used int with value -1 as no valid user.
	 */
	int userId; 
	
	/* quantity of the order. 
	 * We could ask the question to Business Desk how big the quantity can be.
	 * For this exercise i have used double for simplicity
	 */
	double quantity;
	
	/* Price at which is the order is place/registered
	 * Ideally should be currency and value.
	 * The value should be BigDecimal instead of float/double
	 * as we need to perform equal operation to merge orders with same price.
	 * However i have kept it simple with Integer (to match with the examples provided).
	 * Integer as i will be using it in a map as key
	 */	
	Integer price;
	
	//OrderType, if Buy or Sell.
	SilverBarOrderTypeEnum type;
	
	/* Order Id, should be unique for each order.
	 * Could be a Custom type, with a OrderId generation service.
	 * Keeping it simple as Integer. 
	 * Integer so that it can store in a map for in memory solution
	 * so that we could query on order id to get the order.
	 * 
	 * Null value means not a valid order and can be used as a container to merge order.
	 * again keeping it simple instead of creating a new type.
	 * 
	 * The Id will be generated by the server at creation of the new order.
	 */	
	Integer orderId;
	
	/*
	 * Status of the Order if Live or Cancelled
	 */
	SilverBarOrderStatusEnum orderStatus;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public SilverBarOrderTypeEnum getType() {
		return type;
	}

	public void setType(SilverBarOrderTypeEnum type) {
		this.type = type;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public SilverBarOrderStatusEnum getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(SilverBarOrderStatusEnum orderStatus) {
		this.orderStatus = orderStatus;
	}
	
	//we have assumed only one currency, �
	public String toString() {
		return String.format("%s %.2fkg for �%d", type.toString(), quantity, price ); 
	}
}