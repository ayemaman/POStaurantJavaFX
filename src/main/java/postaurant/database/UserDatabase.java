package postaurant.database;

import postaurant.model.Ingredient;
import postaurant.model.Item;
import postaurant.model.Order;
import postaurant.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface UserDatabase {
    User getUser(String userId);
    List<Order> getUserOrders(User user);
    Order getOrderById(Long orderId);
    boolean openTableExists(String value);
    List<String> retrieveItemsForSection(String section);
    List<User> retrieveAllActiveUsers();
    User saveNewUser(User user);
    void blockUser(User user);
    List<Item> getMenu();
    List<Item> getFoodMenu();
    List<Item> getDrinkMenu();
    List<Item> getAllItems();
    List<Item> getItemById(long itemID);
    List<Item> getItemByName(String name);
    List<Item> getCustomItemsByName(String name);
    void changeItemAvailability(Item item, Integer integer);
    List<Ingredient> getAllIngredients();
    Ingredient getIngredientById(long id);
    List<Item> saveNewItem(Item item);
    void saveNewCustomItem(Item item);
    void setNewItem(Item item);
    List<String> getSections();
    Ingredient getIngredientByNameAmountPrice(String name, Integer amount, Double price);
    void changeIngredientAvailability(Ingredient ingredient,Integer integer);
    void changeIngredientAllergy(Ingredient ingredient, String allergy);
    void saveNewIngredient(Ingredient ingredient);


}

