package postaurant.service;

import org.springframework.stereotype.Component;
import postaurant.database.UserDatabase;
import postaurant.model.Item;
import postaurant.model.Order;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;

@Component
public class OrderService {
    private final UserDatabase userDatabase;
    public OrderService(UserDatabase userDatabase){
        this.userDatabase=userDatabase;
    }
    public boolean tableExists(String value) {
        return userDatabase.openTableExists(value);
    }

    public List<String> getItemsForSection(String section){
        return userDatabase.retrieveItemsForSection(section);
    }

    public void sendOrder(Long orderId, List<Map.Entry<Item,Integer>> newOrderItems){
        for(Map.Entry<Item,Integer> entry:newOrderItems){
            userDatabase.addItemToOrder(orderId,entry.getKey().getId(),entry.getValue());
        }

    }
    public void setCheckedByDub(Order order, Date date){
        userDatabase.setCheckedByDub(order,new Date());
    }

}
