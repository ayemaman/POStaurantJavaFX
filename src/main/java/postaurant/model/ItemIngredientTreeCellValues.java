package postaurant.model;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class ItemIngredientTreeCellValues {
    private Long orderId;
    private Long itemId;
    private Double tableNo;
    private String name;
    private boolean item;
    private int qty;
    private LocalDateTime dateOrdered;
    private String status;

    public ItemIngredientTreeCellValues(Long orderId,Long itemId,Double tableNo,String name, boolean item, int qty, LocalDateTime dateOrdered, String status ){
        setOrderId(orderId);
        setItemId(itemId);
        setTableNo(tableNo);
        setName(name);
        setItem(item);
        setQty(qty);
        if(dateOrdered==null){
           setDateOrdered(LocalDateTime.now());
        }else{
            setDateOrdered(dateOrdered);
        }
        setStatus(status);
    }

    public Long getOrderId(){
        return orderId;
    }

    public void setOrderId(Long orderId){
        this.orderId=orderId;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isItem() {
        return item;
    }

    public void setItem(boolean item) {
        this.item = item;
    }


    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public LocalDateTime getDateOrdered() {
        return dateOrdered;
    }

    public void setDateOrdered(LocalDateTime dateOrdered) {
        this.dateOrdered = dateOrdered;
    }

    public Double getTableNo() {
        return tableNo;
    }

    public void setTableNo(Double tableNo) {
        this.tableNo = tableNo;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String toString(){
        StringBuilder buffer=new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        String time=getDateOrdered().format(formatter);
        if(isItem()) {
            buffer.append("-----------------------------------------------------\n").append(getTableNo()).append("  ").append(getName()).append(" Qty.: ").append(getQty()).append("\n\t\t\t\tTIME: ").append(time).append("\n");
        }else{
            buffer.append("\t\t\t\t\t\t\t\t").append(getName()).append(" Qty.: ").append(getQty());
        }
        return buffer.toString();
    }
}
