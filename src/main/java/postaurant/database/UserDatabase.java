package postaurant.database;

import postaurant.model.Item;
import postaurant.model.Order;
import postaurant.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface UserDatabase {
    User getUser(String userId);
    List<Order> retrieveUserOrders(User user);
    boolean openTableExists(String value);
    List<String> retrieveItemsForSection(String section);
    List<User> retrieveAllActiveUsers();
    User saveNewUser(User user);
    void blockUser(User user);
    List<Item> getMenu();
    List<Item> getItem(String itemID);
}

