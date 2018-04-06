package postaurant.model;


import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    private double tableNo;
    private Date timeOpened;
    private String status;
    private Date lastTimeChecked;
    private Date timeBumped;
    private Date timeClosed;
    private List<Item> orderItems=new ArrayList<>();
    private int orderID;

    public Order(){
    }


    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
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
    }

    public List<Item> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<Item> orderItems) {
        this.orderItems = orderItems;
    }

    public void addItem(Item item){
        getOrderItems().add(item);
    }

    public String toString(){
        String buffer="";
        for(Item i: getOrderItems()){
            buffer+=i+" ";
            for(Ingredient ingr: i.getRecipe()){
                buffer+=ingr.getName()+"/ ";
            }

        }
        return "Order ID: "+getOrderID()+" Order Items: "+ buffer;

    }



}
