package postaurant.service;

import org.mockito.internal.matchers.Or;
import org.springframework.stereotype.Component;
import postaurant.context.QCBox;
import postaurant.database.UserDatabase;
import postaurant.model.*;

import javax.print.PrintException;
import java.io.*;
import java.time.LocalDateTime;
import java.util.Date;
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

    public boolean tableExists(String value) {
        return userDatabase.openTableExists(value);
    }

    public List<String> getItemsForSection(String section) {
        return userDatabase.retrieveItemsForSection(section);
    }

    public void sendOrder(Long orderId, List<Map.Entry<Item, Integer>> newOrderItems) {
        for (Map.Entry<Item, Integer> entry : newOrderItems) {
            entry.getKey().setDateOrdered(LocalDateTime.now());
            userDatabase.addItemToOrder(orderId, entry.getKey().getId(), entry.getValue());
        }
    }
    public void voidItemFromOrder(Long orderId, Long itemId, Integer qty){
        userDatabase.voidItemFromOrder(orderId,itemId,qty);
    }

    public void setCheckedByDub(Order order) {
        userDatabase.setCheckedByDub(order, LocalDateTime.now());
    }

    public void createNewOrder(Double tableNo, String dubId, LocalDateTime timeOpened, LocalDateTime lastTimeChecked) {
        userDatabase.createNewOrder(tableNo, dubId, timeOpened, lastTimeChecked);
    }

    public Order getLatestSavedOrder(String dubId) {
        return userDatabase.getLatestCreatedOrder(dubId);

    }

    public void setClosed(Long orderID,User user) {
        userDatabase.setTableStatusToClosed(orderID,user);
    }

    public void createQCBump(List<KitchenOrderInfo> list){
        try{
            PrintWriter out = new PrintWriter("./checks/qcCheck.txt");
            out.println("DATE: "+timeService.createTime());
            out.println("TABLE NO: "+list.get(0).getTableNo());
            out.println("Item name\t\t  Item Qty ");
            out.println("-------------------------------------");
            for(KitchenOrderInfo k:list){
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
                File file=new File("./checks/qcCheck.txt");
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

    public void createPreCheck(List<Map.Entry<Item,Integer>> list, Order order, User user){
        try {
            PrintWriter out = new PrintWriter("./checks/precheck.txt");
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
            File file=new File("./checks/precheck.txt");
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



    public void createReceipt(List<Payment> paymentList, Order order, User user) {
        try {
            PrintWriter out = new PrintWriter( "./checks/receipt.txt");
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
            File file=new File("./checks/receipt.txt");
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

    public boolean areAllTablesPaid(User user, String date){
        List<Order> list=userDatabase.areAllTablesPaid(user,date,timeService.createDateOnly(timeService.createNextDayLocalDateTimeFromString(date)));
        if(list.isEmpty()){
            return true;
        }else{
            return false;
        }
    }
    public boolean areAllTablesPaid(String date){
        List<Order> list=userDatabase.areAllTablesPaid(date,timeService.createDateOnly(timeService.createNextDayLocalDateTimeFromString(date)));
        if(list.isEmpty()){
            return true;
        }else{
            return false;
        }
    }
}
