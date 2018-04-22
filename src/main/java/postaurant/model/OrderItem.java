package postaurant.model;

public class OrderItem {
    private  Integer orderId;
    private  Integer itemId;
    private  Integer amount;

    public OrderItem(Integer orderId, Integer itemId, Integer amount) {
        this.orderId = orderId;
        this.itemId = itemId;
        this.amount = amount;
    }

    public Integer getOrderId() {
        return orderId;
    }


    public Integer getItemId() {
        return itemId;
    }


    public Integer getAmount() {
        return amount;
    }

}
