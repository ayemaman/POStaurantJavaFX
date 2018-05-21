package postaurant.database;

import postaurant.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface UserDatabase {
    User getUser(String userId);
    List<Order> getUserOrders(User user);
    List<Order> getTransferableOrders(User user);
    List<Order> getAllOpenOrders();
    void transferTable(Long orderId, User user);
    void addItemToOrder(Long orderId, Long itemId, Integer qty);
    void voidItemFromOrder(Long orderId, Long itemId, Integer qty);
    Order getOrderById(Long orderId);
    void createNewOrder(Double tableNo, String dubId, LocalDateTime timeOpened,LocalDateTime lastTimeChecked);
    Order getLatestCreatedOrder(String dubId);
    void setCheckedByDub(Order order, LocalDateTime date);
    List<KitchenOrderInfo> getKitchenOrderInfo();
    List<KitchenOrderInfo> getQCOrderInfo();
    List<KitchenOrderInfo> getBarOrderInfo();
    List<KitchenOrderInfo> getQCBarOrderInfo();
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
    void setKitchenStatusToSeen(Long orderId, Long itemId, LocalDateTime timeOrdered);
    void setKitchenStatusToReady(Long orderId, Long itemId, LocalDateTime timeOrdered);
    void setKitchenStatusToBumped(Long orderId, Long itemId, LocalDateTime timeOrdered);
    void addNewPayment(Payment payment,User user);
    void voidPayment(Payment payment);
    void voidPayments(Order order);
    void addNewCardPayment(CardPayment cardPayment, User user);
    List<Payment> getCashPaymentsForTable(Long orderId);
    List<CardPayment> getCardPaymentsForTable(Long orderId);
    void setTableStatusToClosed(Long orderId,User user);
    Double getDubTotalSold(User user, String start, String end);
    Double getTotalSoldSQL(String start, String end);
    List<Payment> getDayReport(String start, String end);
    List<Payment> getDubReport(User user, String start, String end);
    List<Order> areAllTablesPaid(User user, String start, String end);
    List<Order> areAllTablesPaid(String start, String end);

}

