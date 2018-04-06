package postaurant.database;

import postaurant.model.Order;
import postaurant.model.User;

import java.util.ArrayList;
import java.util.List;

public interface UserDatabase {
    User getUser(String userId);
    List<Order> retrieveUserOrders(User user);
    boolean openTableExists(String value);
    List<String> retrieveItemsForSection(String section);
    List<User> retrieveAllActiveUsers();
    void saveNewUser(User user);
}

