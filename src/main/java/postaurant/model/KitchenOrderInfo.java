package postaurant.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import postaurant.exception.InputValidationException;

import java.time.LocalDateTime;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class KitchenOrderInfo{

    private Long orderId;
    private Double tableNo;
    private Item item;
    private int qty;

    public KitchenOrderInfo(Long orderId, Double tableNo, Long itemId, String itemName, LocalDateTime timeOrdered, String station, String status, int qty) {
        try {
            Item item = new Item();
            item.setId(itemId);
            item.setName(itemName);
            item.setDateOrdered(timeOrdered);
            item.setStation(station);
            item.setKitchenStatus(status);
            this.item = item;
            setOrderId(orderId);
            setTableNo(tableNo);
            setQty(qty);
        } catch (InputValidationException e) {
            e.printStackTrace();
        }
    }


    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }


    public int getQty() {
        return qty;
    }

    public void setQty(int itqm_qty) {
        this.qty = itqm_qty;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Double getTableNo() {
        return tableNo;
    }

    public void setTableNo(Double tableNo) {
        this.tableNo = tableNo;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Order ID: ").append(getOrderId()).append(" / ").append(getItem().getName()).append(" Qty.: ").append(getQty());
        return buffer.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof KitchenOrderInfo){
            KitchenOrderInfo k=(KitchenOrderInfo) obj;
            if(k.getItem().compareTo(getItem())==0){
                if(k.getQty()==getQty()){
                    if(k.getOrderId().equals(getOrderId())){
                        if(k.getTableNo().equals(getTableNo())){
                            return true;
                        }else{
                            return false;
                        }
                    }else{
                        return false;
                    }
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
}

