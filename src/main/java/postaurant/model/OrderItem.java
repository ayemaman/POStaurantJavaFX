package postaurant.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class OrderItem {
    private  Integer orderId;
    private  Integer itemId;
    private  Integer amount;
    private String kitchenStatus;
    private LocalDateTime dateOrdered;

    public OrderItem(Integer orderId, Integer itemId, Integer amount, String kitchenStatus, LocalDateTime dateOrdered) {
        this.orderId = orderId;
        this.itemId = itemId;
        this.amount = amount;
        this.kitchenStatus=kitchenStatus;
        this.dateOrdered=dateOrdered;

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

    public String getKitchenStatus() {
        return kitchenStatus;
    }
    public LocalDateTime getDateOrdered(){return dateOrdered;}
}
