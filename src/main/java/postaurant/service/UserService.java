package postaurant.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import postaurant.database.UserDatabase;
import postaurant.model.Ingredient;
import postaurant.model.Order;
import postaurant.model.User;

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
            logger.error("error retrieving user" , ex);
            return null;
        }
    }

    public List<Order> getUserOrders(User user){
        try{
            int orderInt=0;
            int itemInt=0;
            List<Order> sorted= userDatabase.retrieveUserOrders(user);
            for (Order o:sorted){
                System.out.println(o);
            }
            System.out.println("--------------");

            while(orderInt<sorted.size()-1) {
                if (sorted.get(orderInt).getOrderID() == sorted.get(orderInt + 1).getOrderID()) {
                    if (sorted.get(orderInt).getOrderItems().get(itemInt).getId().equals(sorted.get(orderInt + 1).getOrderItems().get(0).getId())) {
                        Map.Entry<Ingredient,Integer> entry = sorted.get(orderInt + 1).getOrderItems().get(0).getRecipe().entrySet().iterator().next();
                        sorted.get(orderInt).getOrderItems().get(itemInt).addIngredient(entry.getKey(),entry.getValue());
                        sorted.remove(orderInt + 1);
                        System.out.println("item= "+itemInt);
                        System.out.println("order= "+orderInt);
                        for (Order o:sorted){
                            System.out.println(o);
                        }
                        System.out.println("--------------");
                    } else {
                        sorted.get(orderInt).addItem(sorted.get(orderInt + 1).getOrderItems().get(0));
                        itemInt++;
                        sorted.remove(orderInt+1);
                        System.out.println("item= "+itemInt);
                        System.out.println("order= "+orderInt);
                        for (Order o:sorted){
                            System.out.println(o);
                        }
                        System.out.println("--------------");
                    }
                } else {
                    orderInt++;
                    itemInt = 0;
                    System.out.println("item= "+itemInt);
                    System.out.println("order= "+orderInt);
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

    public User saveNewUser(User user){
        return userDatabase.saveNewUser(user);
    }

    public void blockUser(User user){userDatabase.blockUser(user);}
    }



