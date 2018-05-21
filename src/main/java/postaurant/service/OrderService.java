/**
 * Service, that works with database data related to Orders
 * @see postaurant.model.Order
 */
package postaurant.service;

import org.springframework.stereotype.Component;
import postaurant.context.OrderInfo;
import postaurant.database.UserDatabase;
import postaurant.model.*;

import javax.print.PrintException;
import java.io.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class OrderService {
    private final UserDatabase userDatabase;
    private final TimeService timeService;
    private final PrintTextFileService printTextFileService;

    public OrderService(UserDatabase userDatabase, TimeService timeService, PrintTextFileService printTextFileService) {
        this.userDatabase = userDatabase;
        this.timeService = timeService;
        this.printTextFileService = printTextFileService;
    }

    /**
     * Verifies if specified table number is occupied at the moment
     * @param value table number
     * @return true if table is occupied, false if not.
     */
    public boolean tableExists(String value) {
        return userDatabase.openTableExists(value);
    }


    /**
     * Saves new order/order updates to database
     * @param orderId orders id that is being modified
     * @param newOrderItems new food/drink items for that order
     */
    public void sendOrder(Long orderId, List<Map.Entry<Item, Integer>> newOrderItems) {
        for (Map.Entry<Item, Integer> entry : newOrderItems) {
            entry.getKey().setDateOrdered(LocalDateTime.now());
            userDatabase.addItemToOrder(orderId, entry.getKey().getId(), entry.getValue());
        }
    }

    /**
     * Removes item from order in database
     * @param orderId orders id that holds removable item
     * @param itemId item id to be removed
     * @param qty item qty specific to that item
     */
    public void voidItemFromOrder(Long orderId, Long itemId, Integer qty){
        userDatabase.voidItemFromOrder(orderId,itemId,qty);
    }

    /**
     * Updates orders last_time_checked to present time
     * @param order order to be updated
     */
    public void setCheckedByDub(Order order) {
        userDatabase.setCheckedByDub(order, LocalDateTime.now());
    }

    /**
     * Adds new Order to database
     * @param tableNo orders table number
     * @param dubId servers id
     * @param timeOpened time table was open
     * @param lastTimeChecked time table was last checked
     */
    public void createNewOrder(Double tableNo, String dubId, LocalDateTime timeOpened, LocalDateTime lastTimeChecked) {
        userDatabase.createNewOrder(tableNo, dubId, timeOpened, lastTimeChecked);
    }

    /**
     * Retrieves newly saved order (to get access to automatically generated order ID)
     * @param dubId servers id
     * @return last saved Order
     */
    public Order getLatestSavedOrder(String dubId) {
        return userDatabase.getLatestCreatedOrder(dubId);

    }

    /**
     * "CLOSES" table in database. Sets order status to "PAID" and TIME_CLOSED to present time
     * @param orderID order that is being modified
     * @param user server that modifies order
     */
    public void setClosed(Long orderID,User user) {
        userDatabase.setTableStatusToClosed(orderID,user);
    }

    /**
     * Generates and prints .txt file, with information on QC order info
     * @param list that holds OrderInfos to be printed
     * @see PrintTextFileService
     */
    public void createQCBump(List<OrderInfo> list){
        try{
            File file=File.createTempFile("qcCheck","txt");
            PrintWriter out = new PrintWriter(file);
            out.println("DATE: "+timeService.createTime());
            out.println("TABLE NO: "+list.get(0).getTableNo());
            out.println("Item name\t\t  Item Qty ");
            out.println("-------------------------------------");
            for(OrderInfo k:list){
                StringBuilder name;
                if(k.getItem().getName().length()>15){
                    name = new StringBuilder(k.getItem().getName().substring(0, 14));
                }
                else{
                    int spaces=14-k.getItem().getName().length();
                    name = new StringBuilder(k.getItem().getName());
                    for(int i=0;i<spaces;i++){
                        name.append(" ");
                    }
                }
                out.println(name.toString()+"\t\t\t"+k.getQty());
                out.close();
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                printTextFileService.printFileTest(file);
            }
        } catch (PrintException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates and prints .txt file, with information on order with prices("Pre-Check")
     * @param list of items that were ordered
     * @param order Order that is being printed
     * @param user server printing the Pre-Check
     */
    public void createPreCheck(List<Map.Entry<Item,Integer>> list, Order order, User user){
        try {
            File file=File.createTempFile("preCheck","txt");
            PrintWriter out = new PrintWriter(file);
            out.println("DATE: "+timeService.createTime());
            out.println("TABLE NO: "+order.getTableNo());
            out.println("Server name: "+ user.getFirstName());
            out.println("Item name\t  Item Qty \t      PricePerOne");
            out.println("----------------------------------------------------------");
            Double total=0.00;
            for(Map.Entry<Item,Integer> entry:list){
                StringBuilder name;
                if(entry.getKey().getName().length()>15){
                    name = new StringBuilder(entry.getKey().getName().substring(0, 14));
                }
                else{
                    int spaces=14-entry.getKey().getName().length();
                    name = new StringBuilder(entry.getKey().getName());
                    for(int i=0;i<spaces;i++){
                        name.append(" ");
                    }
                }

                StringBuilder price=new StringBuilder();
                if((entry.getKey().getPrice()+"£").length()<8){
                    int spaces=8-(""+entry.getKey().getPrice()).length();
                    for(int i=0;i<spaces;i++){
                        price.append(" ");
                    }
                    price.append(entry.getKey().getPrice()).append("£");
                }else{
                    price.append(entry.getKey().getPrice()).append("£");
                }
                out.println(name.toString()+"\t\t"+entry.getValue()+"\t\t"+price);
                total+=(entry.getValue()*entry.getKey().getPrice());
            }
            out.println("----------------------------------------------------------");
            out.println("TOTAL:\t\t\t\t\t\t   "+String.format("%.2f",total)+"£");
            out.close();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            printTextFileService.printFileTest(file);
        } catch (PrintException | IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Generates and prints payment receipt for order that was just paid
     * @param paymentList list, that holds all Payments for this order
     * @param order that holds info
     * @param user server of this order
     */
    public void createReceipt(List<Payment> paymentList, Order order, User user) {
        try {
            File file=File.createTempFile("receipe","txt");
            PrintWriter out = new PrintWriter( file);
            out.println("Date:+" + timeService.createTime());
            out.println("TABLE NO: " + order.getTableNo());
            out.println("Server name: " + user.getFirstName());
            out.println("Item name\t  Item Qty \t      PricePerOne");
            out.println("----------------------------------------------------------");
            Double total = 0.00;
            Double paid = 0.00;
            for (Map.Entry<Item, Integer> entry : order.getOrderItems().entrySet()) {
                StringBuilder name;
                if (entry.getKey().getName().length() > 15) {
                    name = new StringBuilder(entry.getKey().getName().substring(0, 14));
                } else {
                    int spaces = 14 - entry.getKey().getName().length();
                    name = new StringBuilder(entry.getKey().getName());
                    for (int i = 0; i < spaces; i++) {
                        name.append(" ");
                    }
                }

                StringBuilder price = new StringBuilder();
                if ((entry.getKey().getPrice() + "£").length() < 8) {
                    int spaces = 8 - ("" + entry.getKey().getPrice()).length();
                    for (int i = 0; i < spaces; i++) {
                        price.append(" ");
                    }
                    price.append(entry.getKey().getPrice()).append("£");
                } else {
                    price.append(entry.getKey().getPrice()).append("£");
                }
                out.println(name.toString() + "\t\t" + entry.getValue() + "\t\t" + price);
                total += (entry.getValue() * entry.getKey().getPrice());
            }
            out.println("----------------------------------------------------------");
            out.println("TOTAL:\t\t\t\t\t\t   £" + String.format("%.2f", total));
            int paymentNo = 1;
            for (Payment p : paymentList) {
                out.println("\nPayment#" + paymentNo);
                paid += p.getAmount();
                if (p instanceof CardPayment) {
                    out.println("Card Payment " + ((CardPayment) p).getCardType() + "\nCardNo: " + ((CardPayment) p).formattedCardNo() + " Name: " + ((CardPayment) p).getCardName() + " Amount: £" + String.format("%.2f", p.getAmount()));
                    out.println("----------------------------------------------------------");
                    paymentNo++;
                } else {
                    out.println("Cash payment");
                    out.println("Amount: £" + String.format("%.2f", p.getAmount()));
                    paymentNo++;
                }
            }
            Double left = paid-total;
            out.println("----------------------------------------------------------");
            out.println("TOTAL:\t\t\t\t\t\t   £" + String.format("%.2f", total));
            out.println("PAID:\t\t\t\t\t\t   £" + String.format("%.2f", paid));
            out.println("CHANGE:\t\t\t\t\t\t   £" + String.format("%.2f", left));
            out.close();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            }
            printTextFileService.printFileTest(file);
        } catch (PrintException | IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Checks if all orders for this user and date are set to status "PAID"
     * @param user server who holds orders
     * @param date date to be checked for
     * @return true= if all paid, false= if not all paid
     */
    public boolean areAllTablesPaid(User user, String date){
        List<Order> list=userDatabase.areAllTablesPaid(user,date,timeService.createDateOnly(timeService.createNextDayLocalDateTimeFromString(date)));
        if(list.isEmpty()){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Checks if all orders for this date for all users are set to status "PAID"
     * @param date date to be checked for
     * @return if all paid, false= if not all paid
     */
    public boolean areAllTablesPaid(String date){
        List<Order> list=userDatabase.areAllTablesPaid(date,timeService.createDateOnly(timeService.createNextDayLocalDateTimeFromString(date)));
        if(list.isEmpty()){
            return true;
        }else{
            return false;
        }
    }
    /**
     * Retrieves all Orders that are not set to status "PAID" and are served by this user
     * @param user who is in charge of orders
     * @return List<Order> that holds all orders that are not "PAID" and are served by this user
     */
    public List<Order> getUserOrders(User user){
        return userDatabase.getUserOrders(user);
    }

    /**
     * Retrieves all Orders that are not set to status "PAID" and are not served by this user
     * @param user who is not in charge of orders
     * @return List<Order> that holds all orders that are not "PAID" and are not served by this user
     */
    public List<Order> getTransferableOrders(User user){
        return userDatabase.getTransferableOrders(user);
    }

    /**
     * Retrieves all Orders that are not set to status "PAID"
     * @return List<Order> that holds all orders that are not set to status "PAID"
     */
    public List<Order> getAllOpenOrders(){return userDatabase.getAllOpenOrders();}

    /**
     * Sets specified order dub_id to specified user id
     * @param orderId of order to be transferred
     * @param user -> new owner of order
     */
    public void transferTable(Long orderId, User user){ userDatabase.transferTable(orderId,user);}

}
