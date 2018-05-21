/**
 * Class that represents Orders in this system
 */
package postaurant.model;


import postaurant.exception.InputValidationException;

import java.time.LocalDateTime;
import java.util.*;

public class Order{
    private Long id;
    private Double tableNo;
    private LocalDateTime timeOpened;
    private String status;
    private LocalDateTime lastTimeChecked;
    private LocalDateTime timeClosed;
    private Map<Item, Integer> orderItems;


    private double total;

    public Order() {
        orderItems=new HashMap<>();
    }


    public Order(long orderID, Double tableNo, LocalDateTime timeOpened, String status, LocalDateTime lastTimeChecked){
        this();
        setId(orderID);
        setTableNo(tableNo);
        setTimeOpened(timeOpened);
        setStatus(status);
        setLastTimeChecked(lastTimeChecked);
    }

    public Order(long orderID, Double tableNo, LocalDateTime timeOpened, String status, LocalDateTime lastTimeChecked, Map<Item,Integer> orderItems)throws InputValidationException{
        this(orderID,tableNo,timeOpened,status,lastTimeChecked);
        setOrderItems(orderItems);
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public double getTableNo() {
        return tableNo;
    }

    public void setTableNo(double tableNo) {
        this.tableNo = tableNo;
    }

    public LocalDateTime getTimeOpened() {
        return timeOpened;
    }

    public void setTimeOpened(LocalDateTime timeOpened) {
        this.timeOpened = timeOpened;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLastTimeChecked() {
        return lastTimeChecked;
    }

    public void setLastTimeChecked(LocalDateTime lastTimeChecked) {
        this.lastTimeChecked = lastTimeChecked;
    }


    /**
     * Calculates total price for whole order from counting order_items_price*qty;
     */
    private void setTotal(){
        this.total=0;
        if(!getOrderItems().isEmpty()) {
            for (Map.Entry<Item, Integer> entry : getOrderItems().entrySet()) {
                this.total = total + (entry.getKey().getPrice() * entry.getValue());
            }
        }
    }


    public Map<Item, Integer> getOrderItems(){ return this.orderItems;}


    /**
     * Order's OrderItems Setter
     * Checks if given Map is not empty
     * @throws InputValidationException if Map is empty
     * @param orderItems
     */
    public void setOrderItems(Map<Item, Integer> orderItems) throws InputValidationException {
        if (!orderItems.isEmpty()) {
            this.orderItems = orderItems;
        } else {
            throw new InputValidationException();
        }
        setTotal();
    }

    /**
     * Checks if order already has this item,
     * if yes-> adds amount to EntryValue
     * if no-> adds new EntryKey with specified EntryValue
     * @param item Item being added to order
     * @param amount of Item being added to order
     */
    public void addItem(Item item, Integer amount){
        if(!getOrderItems().isEmpty()) {
            if(getOrderItems().containsKey(item)){
                getOrderItems().put(item,getOrderItems().get(item)+amount);
            }else{
                getOrderItems().put(item,amount);
            }
        }else{
            getOrderItems().put(item, amount);
        }

        setTotal();
    }


    public String toString(){
        StringBuilder buffer= new StringBuilder("ORDER ID: " + getId());
        for (Map.Entry<Item,Integer > entry : getOrderItems().entrySet()) {
            buffer.append("\n ITEM: ").append(entry.getKey()).append("|||ITEM_QTY> ").append(entry.getValue()).append("|||");
            buffer.append("\n");
        }

        return buffer.toString();
    }

}
