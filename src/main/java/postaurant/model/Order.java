package postaurant.model;


import postaurant.exception.InputValidationException;

import java.util.*;

public class Order{
    private Long id;
    private Double tableNo;
    private Date timeOpened;
    private String status;
    private Date lastTimeChecked;
    private Date timeBumped;
    private Date timeClosed;
    private Map<Item, Integer> orderItems;

    //private Map<Item, Map<Integer,Date> orderItems;

    //private List<Item> orderItems = new ArrayList<>();

    private double total;

    public Order() {
        orderItems=new HashMap<>();
    }

    public Order(long orderID, Double tableNo, Date timeOpened, String status, Date lastTimeChecked, Date timeBumped){
        this();
        setId(orderID);
        setTableNo(tableNo);
        setTimeOpened(timeOpened);
        setStatus(status);
        setLastTimeChecked(lastTimeChecked);
        setTimeBumped(timeBumped);
    }

    public Order(long orderID, Double tableNo, Date timeOpened, String status, Date lastTimeChecked, Date timeBumped, Map<Item,Integer> orderItems)throws InputValidationException{
        this(orderID,tableNo,timeOpened,status,lastTimeChecked,timeBumped);
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

    public Date getTimeOpened() {
        return timeOpened;
    }

    public void setTimeOpened(Date timeOpened) {
        this.timeOpened = timeOpened;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLastTimeChecked() {
        return lastTimeChecked;
    }

    public void setLastTimeChecked(Date lastTimeChecked) {
        this.lastTimeChecked = lastTimeChecked;
    }

    public Date getTimeBumped() {
        return timeBumped;
    }

    public void setTimeBumped(Date timeBumped) {
        this.timeBumped = timeBumped;
    }

    public Date getTimeClosed() {
        return timeClosed;
    }

    public void setTimeClosed(Date timeClosed) {
        this.timeClosed = timeClosed;
        setTotal();
    }

    private void setTotal(){
        this.total=0;
        if(!getOrderItems().isEmpty()) {
            for (Map.Entry<Item, Integer> entry : getOrderItems().entrySet()) {
                this.total = total + (entry.getKey().getPrice() * entry.getValue());
            }
        }
    }


    public Map<Item, Integer> getOrderItems(){ return this.orderItems;}



    public void setOrderItems(Map<Item, Integer> orderItems) throws InputValidationException {
        if (!orderItems.isEmpty()) {
            this.orderItems = orderItems;
        } else {
            throw new InputValidationException();
        }
        setTotal();
    }

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
