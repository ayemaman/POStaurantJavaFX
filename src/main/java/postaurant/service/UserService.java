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
    public List<Order> getAllOpenOrders(){return userDatabase.getAllOpenOrders();}

    public void transferTable(Long orderId, User user){
        userDatabase.transferTable(orderId,user);
    }

    public List<User> getAllActiveUsers(){
        return userDatabase.retrieveAllActiveUsers();

    }

    public User saveNewUser(User user){
        return userDatabase.saveNewUser(user);
    }

    public void blockUser(User user){userDatabase.blockUser(user);}
    }



