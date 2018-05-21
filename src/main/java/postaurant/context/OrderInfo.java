package postaurant.context;


import postaurant.exception.InputValidationException;
import postaurant.model.Item;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.time.LocalDateTime;


public class OrderInfo implements Printable {

    private Long orderId;
    private Double tableNo;
    private Item item;
    private int qty;

    public OrderInfo(Long orderId, Double tableNo, Long itemId, String itemName, LocalDateTime timeOrdered, String status, int qty){
        try {
            Item item = new Item();
            item.setId(itemId);
            item.setName(itemName);
            item.setDateOrdered(timeOrdered);
            item.setKitchenStatus(status);
            this.item=item;
            setOrderId(orderId);
            setTableNo(tableNo);
            setQty(qty);
        }catch (InputValidationException e){
            e.printStackTrace();
        }
    }

    public OrderInfo(Long orderId, Double tableNo, Long itemId, String itemName, LocalDateTime timeOrdered, String station, String status, int qty) {
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
        if (obj instanceof OrderInfo) {
            OrderInfo k = (OrderInfo) obj;
            if (k.getItem().compareTo(getItem()) == 0) {
                if (k.getQty() == getQty()) {
                    if (k.getOrderId().equals(getOrderId())) {
                        if (k.getTableNo().equals(getTableNo())) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {   //Here
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        graphics.drawString("testing...", 100, 100);
        return PAGE_EXISTS;
    }
}



