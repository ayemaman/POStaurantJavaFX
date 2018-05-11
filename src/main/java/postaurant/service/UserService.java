package postaurant.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import postaurant.database.UserDatabase;
import postaurant.model.Ingredient;
import postaurant.model.Item;
import postaurant.model.Order;
import postaurant.model.User;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserDatabase userDatabase;


    public UserService(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
    }

    public User getUser(String userId) {
        try {
            return userDatabase.getUser(userId);
        } catch (Exception ex) {
            logger.error("error retrieving user", ex);
            return null;
        }
    }
    public List<Order> getUserOrders(User user){
        return userDatabase.getUserOrders(user);
    }
    public List<Order> getTransferableOrders(User user){
        return userDatabase.getTransferableOrders(user);
    }

    public void transferTable(Long orderId, User user){
        userDatabase.transferTable(orderId,user);
    }
    /*
    public List<Order> getUserOrders(User user) {
        try {
            List<Order> sorted = userDatabase.retrieveUserOrders(user);
            if (!sorted.isEmpty()){
                Item lastItem = sorted.get(0).getOrderItems().keySet().iterator().next();
                for (int i = 0; i < sorted.size() - 1; ) {
                    //if order id are the same
                    if (sorted.get(i).getId() == sorted.get(i + 1).getId()) {
                        Map.Entry<Item, Integer> entry = sorted.get(i + 1).getOrderItems().entrySet().iterator().next();
                        Integer qty = entry.getValue();
                        Item item2 = entry.getKey();

                        //if items are the same (checking by id and date_ordered)
                        if (lastItem.compareTo(item2) == 0) {
                            lastItem.getRecipe().put(item2.getRecipe().keySet().iterator().next(), item2.getRecipe().values().iterator().next());
                            sorted.remove(i + 1);
                        } else {
                            sorted.get(i).getOrderItems().put(item2, qty);
                            sorted.remove(i + 1);
                            lastItem = item2;
                        }
                    } else {
                        i++;
                        lastItem = sorted.get(i).getOrderItems().keySet().iterator().next();
                    }
                }
                return sorted;
            }else{
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
*/
            /*
            Collections.sort(sorted, (o1, o2) -> {
                long result = o1.getId() - o2.getId();
                if(result==0) {
                    return (int) result;
                }

                for(Map.Entry<Item,Integer> entry:o1.getOrderItems().entrySet()){
                    entry.getKey().getDateOrdered()
                }

            });
            for(int i=0;i<sorted.size()-1;) {
                if (sorted.get(i).getId() == sorted.get(i + 1).getId()) {
                    for (Map.Entry<Item, Integer> entry : sorted.get(i + 1).getOrderItems().entrySet()) {
                        sorted.get(i).getOrderItems().put(entry.getKey(), entry.getValue());
                    }
                } else {
                    i++;
                }
            }
            return sorted;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
*/
    public List<User> getAllActiveUsers(){
        return userDatabase.retrieveAllActiveUsers();

    }

    public User saveNewUser(User user){
        return userDatabase.saveNewUser(user);
    }

    public void blockUser(User user){userDatabase.blockUser(user);}
    }



