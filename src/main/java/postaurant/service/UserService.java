package postaurant.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import postaurant.database.UserDatabase;
import postaurant.model.Order;
import postaurant.model.User;

import java.util.List;

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
            logger.error("error retrieving user" , ex);
            return null;
        }
    }

    public List<Order> getUserOrders(User user){
        try{
            int order=0;
            int item=0;
            List<Order> sorted= userDatabase.retrieveUserOrders(user);
            for (Order o:sorted){
                System.out.println(o);
            }
            System.out.println("--------------");

            while(order<sorted.size()-1) {
                if (sorted.get(order).getOrderID() == sorted.get(order + 1).getOrderID()) {
                    if (sorted.get(order).getOrderItems().get(item).getId().equals(sorted.get(order + 1).getOrderItems().get(0).getId())) {
                        sorted.get(order).getOrderItems().get(item).addIngredient(sorted.get(order + 1).getOrderItems().get(0).getRecipe().get(0));
                        sorted.remove(order + 1);
                        System.out.println("item= "+item);
                        System.out.println("order= "+order);
                        for (Order o:sorted){
                            System.out.println(o);
                        }
                        System.out.println("--------------");
                    } else {
                        sorted.get(order).addItem(sorted.get(order + 1).getOrderItems().get(0));
                        item++;
                        sorted.remove(order+1);
                        System.out.println("item= "+item);
                        System.out.println("order= "+order);
                        for (Order o:sorted){
                            System.out.println(o);
                        }
                        System.out.println("--------------");
                    }
                } else {
                    order++;
                    item = 0;
                    System.out.println("item= "+item);
                    System.out.println("order= "+order);
                    for (Order o:sorted){
                        System.out.println(o);
                    }
                    System.out.println("--------------");
                }
            }
            return sorted;
        }catch (Exception ex1){
            System.out.println("error retrieving user orders"+ ex1);
            return null;
        }
    }

    public List<User> getAllActiveUsers(){
        List<User> users=userDatabase.retrieveAllActiveUsers();
        for(User u:users){
            System.out.println(u);
        }
        return users;
    }

    public void saveNewUser(User user){
        userDatabase.saveNewUser(user);
    }



}
