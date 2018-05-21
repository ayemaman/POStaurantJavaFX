package postaurant.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import postaurant.context.OrderInfo;
import postaurant.database.rowMappers.*;
import postaurant.exception.InputValidationException;
import postaurant.model.*;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class UserDao implements UserDatabase {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);
    private final JdbcTemplate jdbcTemplate;

    public UserDao(@Qualifier("first")DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private final String retrieveUser = "SELECT * FROM dubdubs WHERE dub_id=?";
    @Override
    public User getUser(String userId) {
        return jdbcTemplate.queryForObject(retrieveUser, new UserRowMapper(), userId);
    }



    private final String getOrderSQL="select * from orders WHERE order_id=?";
    private final String getOrderItemSQL="select * from order_has_items WHERE order_id=? ORDER BY  time_ordered";

    @Override
    public Order getOrderById(Long orderId){
        Map<Integer,Item> items=getAllItemsMap();
        Order order=jdbcTemplate.queryForObject(getOrderSQL,new EmptyOrderMapper(),orderId);
        List<OrderItem> orderItems=jdbcTemplate.query(getOrderItemSQL, new OrderItemRowMapper(), orderId);

        for(OrderItem orderItem:orderItems) {
            Item item=items.get(orderItem.getItemId());
            try{
                Item newItem=new Item(item.getId(), item.getName(), item.getPrice(), item.getType(), item.getSection(), item.getStation(), item.getAvailability(), item.getDateCreated());
                newItem.setKitchenStatus(orderItem.getKitchenStatus());
                newItem.setDateOrdered(orderItem.getDateOrdered());
                order.addItem(newItem,orderItem.getAmount());
            }catch (InputValidationException iE){
                iE.printStackTrace();
            }

        }
        return order;
    }

    private final String createNewOrderSQL="INSERT INTO orders (table_no, dub_id, time_opened, last_time_checked) VALUES (?,?,?,?)";

    @Override
    public void createNewOrder(Double tableNo, String  dubId, LocalDateTime timeOpened, LocalDateTime lastTimeChecked) {
        jdbcTemplate.update(createNewOrderSQL,tableNo,dubId,timeOpened,lastTimeChecked);

    }

    private final String tableGotCheckedSQL="UPDATE orders SET last_time_checked=? WHERE order_id=?";
    @Override
    public void setCheckedByDub(Order order, LocalDateTime date) {
        jdbcTemplate.update(tableGotCheckedSQL,date,order.getId());
    }



    private final String getLatestCreatedOrderSQL="SELECT * FROM(SELECT * From orders WHERE dub_id=? AND status='OPEN' ORDER BY time_opened desc) WHERE ROWNUM=1";

    @Override
    public Order getLatestCreatedOrder(String dubId){
        return jdbcTemplate.queryForObject(getLatestCreatedOrderSQL,new EmptyOrderMapper(),dubId);
    }



    private final String getUserOrdersSQL="SELECT * FROM orders WHERE dub_id=? AND status<>'PAID' ORDER BY table_no";



    @Override
    public List<Order> getUserOrders(User user){
        return jdbcTemplate.query(getUserOrdersSQL,new EmptyOrderMapper(), user.getUserID());

    }

    private final String getTransferableOrdersSQL="SELECT * FROM orders WHERE dub_id<>? AND status<>'PAID' ORDER BY table_no";
    @Override
    public List<Order> getTransferableOrders(User user){
        return jdbcTemplate.query(getTransferableOrdersSQL,new EmptyOrderMapper(), user.getUserID());
    }

    private final String transferTableSQL="UPDATE orders SET dub_id=? WHERE order_id=?";
    @Override
    public void transferTable(Long orderId, User user) {
        jdbcTemplate.update(transferTableSQL,user.getUserID(),orderId);
    }

    private final String addItemToOrderSQL="INSERT INTO order_has_items (order_id, item_id, item_qty) VALUES(?,?,?)";
    @Override
    public void addItemToOrder(Long orderId, Long itemId, Integer qty) {
        jdbcTemplate.update(addItemToOrderSQL,orderId,itemId,qty);
    }

    private final String voidItemFromOrderSQL="DELETE FROM order_has_items WHERE order_id=? AND item_id=? AND item_qty=?";
    @Override
    public void voidItemFromOrder(Long orderId, Long itemId, Integer qty){
        jdbcTemplate.update(voidItemFromOrderSQL,orderId,itemId,qty);
    }


    private final String openTableExists = "SELECT count(*) FROM orders WHERE status='OPEN' AND table_no=?";
    @Override
    public boolean openTableExists(String value) {
        int openTableCount = jdbcTemplate.queryForObject(openTableExists, Integer.class, value);
        return openTableCount > 0;
    }


    private final String retriveAllActiveUsers = "SELECT * FROM dubdubs WHERE accessible=1 ORDER BY position, first_name";

    @Override
    public List<User> retrieveAllActiveUsers() {
        return jdbcTemplate.query(retriveAllActiveUsers, new UserRowMapper());
    }

    private final String saveNewUserSQL = "INSERT INTO dubdubs (first_name,last_name,position) VALUES(?,?,?)";
    private final String getLastSavedUserSQL = "SELECT * FROM (SELECT * FROM dubdubs ORDER BY date_added DESC) WHERE ROWNUM=1";

    @Override
    public User saveNewUser(User user) {
        jdbcTemplate.update(saveNewUserSQL, user.getFirstName(), user.getLastName(), user.getPosition());
        return jdbcTemplate.queryForObject(getLastSavedUserSQL, new UserRowMapper());
    }

    private final String blockUserSQL = "UPDATE dubdubs SET accessible=0 WHERE dub_id=?";
    @Override
    public void blockUser(User user) {
        jdbcTemplate.update(blockUserSQL, user.getUserID());
    }

    private final String getQCBarOrderInfoSQL="SELECT DISTINCT order_id, table_no, item_id, item_name, time_ordered,item_qty,item_station,item_kitchen_status FROM orders NATURAL JOIN order_has_items NATURAL JOIN items NATURAL JOIN item_has_ingredients NATURAL JOIN ingredients  WHERE time_closed IS NULL AND item_type='DRINKITEM' AND item_kitchen_status<>'BUMPED' ORDER BY time_ordered";
    @Override
    public List<OrderInfo> getQCBarOrderInfo(){
        Map<Long,Ingredient> ingredients = getAllIngredientsMap();
        List<OrderInfo> list=jdbcTemplate.query(getQCBarOrderInfoSQL, new BarOrderMapper());
        List<ItemIngredient> itemIngredients=jdbcTemplate.query(getItemIngredientIdsForKitchenSQL, new ItemIngredientRowMapper());

        for(OrderInfo orderInfo :list){
            Item item= orderInfo.getItem();
            for( ItemIngredient itemIngredient: itemIngredients){
                if(itemIngredient.getItemId().equals(item.getId())){
                    item.addIngredient(ingredients.get(itemIngredient.getIngredientId()),itemIngredient.getAmount());
                }
            }
        }
        return list;
    }


    private final String getBarOrderInfoSQL="SELECT DISTINCT order_id,table_no,item_id,item_name,time_ordered,item_type,item_qty,item_station,item_kitchen_status FROM orders NATURAL JOIN order_has_items NATURAL JOIN items NATURAL JOIN item_has_ingredients NATURAL JOIN ingredients  WHERE time_closed IS NULL AND item_type='DRINKITEM' AND item_kitchen_status NOT IN('BUMPED','READY') ORDER BY time_ordered";
    @Override
    public List<OrderInfo> getBarOrderInfo(){
        Map<Long,Ingredient> ingredients=getAllIngredientsMap();
        List<OrderInfo> list=jdbcTemplate.query(getBarOrderInfoSQL, new BarOrderMapper());
        List<ItemIngredient> itemIngredients=jdbcTemplate.query(getItemIngredientIdsForKitchenSQL, new ItemIngredientRowMapper());

        for(OrderInfo orderInfo :list){
            Item item= orderInfo.getItem();
            for( ItemIngredient itemIngredient: itemIngredients){
                if(itemIngredient.getItemId().equals(item.getId())){
                    item.addIngredient(ingredients.get(itemIngredient.getIngredientId()),itemIngredient.getAmount());
                }
            }
        }
        return list;
    }


    private final String getKitchenOrderInfoSQL ="SELECT DISTINCT order_id, table_no, item_id, item_name, time_ordered,item_qty,item_station,item_kitchen_status FROM orders NATURAL JOIN order_has_items NATURAL JOIN items NATURAL JOIN item_has_ingredients NATURAL JOIN ingredients  WHERE item_type='FOODITEM' AND time_closed IS NULL AND item_kitchen_status NOT IN('BUMPED','READY') ORDER BY time_ordered";
    private final String getItemIngredientIdsForKitchenSQL="SELECT * FROM item_has_ingredients";
    @Override
    public List<OrderInfo> getKitchenOrderInfo(){
        Map<Long,Ingredient> ingredients = getAllIngredientsMap();
         List<OrderInfo> list=jdbcTemplate.query(getKitchenOrderInfoSQL, new KitchenOrderMapper());
         List<ItemIngredient> itemIngredients=jdbcTemplate.query(getItemIngredientIdsForKitchenSQL, new ItemIngredientRowMapper());

         for(OrderInfo orderInfo :list){
             Item item= orderInfo.getItem();
             for( ItemIngredient itemIngredient: itemIngredients){
                 if(itemIngredient.getItemId().equals(item.getId())){
                     item.addIngredient(ingredients.get(itemIngredient.getIngredientId()),itemIngredient.getAmount());
                 }
             }
         }
         return list;

    }
    private final String getQCOrderInfoSQL ="SELECT DISTINCT order_id, table_no, item_id, item_name, time_ordered,item_qty,item_station,item_kitchen_status FROM orders NATURAL JOIN order_has_items NATURAL JOIN items NATURAL JOIN item_has_ingredients NATURAL JOIN ingredients  WHERE item_type='FOODITEM' AND time_closed IS NULL AND item_kitchen_status<>'BUMPED' ORDER BY time_ordered";

    @Override
    public List<OrderInfo> getQCOrderInfo() {
        Map<Long,Ingredient> ingredients = getAllIngredientsMap();
        List<OrderInfo> list=jdbcTemplate.query(getQCOrderInfoSQL, new KitchenOrderMapper());
        List<ItemIngredient> itemIngredients=jdbcTemplate.query(getItemIngredientIdsForKitchenSQL, new ItemIngredientRowMapper());

        for(OrderInfo orderInfo :list){
            Item item= orderInfo.getItem();
            for( ItemIngredient itemIngredient: itemIngredients){
                if(itemIngredient.getItemId().equals(item.getId())){
                    item.addIngredient(ingredients.get(itemIngredient.getIngredientId()),itemIngredient.getAmount());
                }
            }
        }
        return list;
    }



    private final String getMenuSQL = "SELECT * FROM items WHERE custom<>1 ORDER BY item_id DESC";
    private final String getItemIngredientIdsSQL ="SELECT ITEM_ID, INGREDIENT_ID, INGREDIENT_QTY FROM items NATURAL JOIN item_has_ingredients NATURAL JOIN ingredients WHERE custom<>1";
    @Override
    public List<Item> getMenu() {
        Map<Long, Ingredient> ingredients = getAllIngredientsMap();
        List<Item> items = jdbcTemplate.query(getMenuSQL, new EmptyItemMapper());
        Map<Long, Item> itemsMap = items.stream().collect(Collectors.toMap(Item::getId, it -> it));
        List<ItemIngredient> itemIngredients = jdbcTemplate.query(getItemIngredientIdsSQL, new ItemIngredientRowMapper());

        for (ItemIngredient itemIngredient : itemIngredients) {
            Item item = itemsMap.get(itemIngredient.getItemId());
            Ingredient ingredient = ingredients.get(itemIngredient.getIngredientId());
            item.addIngredient(ingredient, itemIngredient.getAmount());
        }
        return items;
    }


    private final String getDrinkMenuSQL="SELECT * FROM items WHERE custom<>1 AND item_type='DRINKITEM' ORDER BY item_id DESC";
    private final String getDrinkIngredientIdsSQL ="SELECT ITEM_ID, INGREDIENT_ID, INGREDIENT_QTY FROM items  NATURAL JOIN item_has_ingredients NATURAL JOIN ingredients WHERE custom<>1 AND item_type='DRINKITEM'";
    @Override
    public List<Item> getDrinkMenu() {
        Map<Long, Ingredient> ingredients = getAllIngredientsMap();
        List<Item> items = jdbcTemplate.query(getDrinkMenuSQL, new EmptyItemMapper());
        Map<Long, Item> itemsMap = items.stream().collect(Collectors.toMap(Item::getId, it -> it));
        List<ItemIngredient> itemIngredients = jdbcTemplate.query(getDrinkIngredientIdsSQL, new ItemIngredientRowMapper());
        if(!items.isEmpty() && !itemIngredients.isEmpty()) {
            for (ItemIngredient itemIngredient : itemIngredients) {
                Item item = itemsMap.get(itemIngredient.getItemId());
                Ingredient ingredient = ingredients.get(itemIngredient.getIngredientId());
                item.addIngredient(ingredient, itemIngredient.getAmount());
            }
        }
        return items;
    }

    private final String getFoodMenuSQL="SELECT * FROM items WHERE custom<>1 AND item_type='FOODITEM' ORDER BY item_id DESC";
    private final String getFoodIngredientIdsSQL ="SELECT ITEM_ID, INGREDIENT_ID, INGREDIENT_QTY FROM items NATURAL JOIN item_has_ingredients NATURAL JOIN ingredients WHERE custom<>1 AND item_type='FOODITEM' ";

    @Override
    public List<Item> getFoodMenu() {
        Map<Long, Ingredient> ingredients = getAllIngredientsMap();
        List<Item> items = jdbcTemplate.query(getFoodMenuSQL, new EmptyItemMapper());
        Map<Long, Item> itemsMap = items.stream().collect(Collectors.toMap(Item::getId, it -> it));
        List<ItemIngredient> itemIngredients = jdbcTemplate.query(getFoodIngredientIdsSQL, new ItemIngredientRowMapper());
        if(!items.isEmpty() && !itemIngredients.isEmpty()) {
            for (ItemIngredient itemIngredient : itemIngredients) {
                Item item = itemsMap.get(itemIngredient.getItemId());
                Ingredient ingredient = ingredients.get(itemIngredient.getIngredientId());
                item.addIngredient(ingredient, itemIngredient.getAmount());
            }
        }
        return items;
    }



    private final String getItemByIDSQL= "SELECT * FROM items WHERE item_id=?";
    private final String getItemIngredientIDsForItemSQL="SELECT ITEM_ID, INGREDIENT_ID, INGREDIENT_QTY FROM items NATURAL JOIN item_has_ingredients NATURAL JOIN ingredients WHERE item_id=?";
    @Override
    public Item getItemById(long itemID) {
        Map<Long,Ingredient> ingredients=getAllIngredientsMap();
        List<Item> items=jdbcTemplate.query(getItemByIDSQL, new EmptyItemMapper(),itemID);
        if(!items.isEmpty()){
            Item item=items.get(0);
            List<ItemIngredient> itemIngredients =jdbcTemplate.query(getItemIngredientIDsForItemSQL,new ItemIngredientRowMapper(),itemID);
            for (ItemIngredient itemIngredient : itemIngredients) {
                Ingredient ingredient = ingredients.get(itemIngredient.getIngredientId());
                item.addIngredient(ingredient, itemIngredient.getAmount());
            }
            return item;
        }else{
            return null;
        }
    }


    private final String getItemByNameSQL = "SELECT * FROM items NATURAL JOIN item_has_ingredients NATURAL JOIN ingredients WHERE item_name=? ORDER BY item_date_added DESC";
    @Override
    public Item getItemByName(String name){
        Map<Long,Ingredient> ingredients=getAllIngredientsMap();
        List<Item> items=jdbcTemplate.query(getItemByNameSQL, new EmptyItemMapper(),name);
        if(!items.isEmpty()){
            Item item=items.get(0);
            List<ItemIngredient> itemIngredients =jdbcTemplate.query(getItemIngredientIDsForItemSQL,new ItemIngredientRowMapper(),item.getId());
            for (ItemIngredient itemIngredient : itemIngredients) {
                Ingredient ingredient = ingredients.get(itemIngredient.getIngredientId());
                item.addIngredient(ingredient, itemIngredient.getAmount());
            }
            return item;
        }else{
            return null;
        }
    }

    private final String changeItemAvailabilitySQL="UPDATE items SET item_availability=? WHERE item_id=?";
    @Override
    public void changeItemAvailability(Item item, Integer integer) {
        jdbcTemplate.update(changeItemAvailabilitySQL,integer, item.getId());
    }


    private final String getAllItemsSQL="SELECT * FROM items ORDER BY item_name";
    @Override
    public List<Item> getAllItems(){
        return jdbcTemplate.query(getAllItemsSQL, new EmptyItemMapper());
    }

    public Map<Integer,Item> getAllItemsMap(){
        return getAllItems().stream().collect(Collectors.toMap(item->item.getId().intValue(),item->item));
    }

    private final String getAllIngredientsSQL = "SELECT * FROM ingredients ORDER BY ingredient_name";

    @Override
    public List<Ingredient> getAllIngredients() {
        return jdbcTemplate.query(getAllIngredientsSQL, new IngredientMapper());
    }


    public Map<Long, Ingredient> getAllIngredientsMap() {
        return getAllIngredients().stream().collect(Collectors.toMap(ing -> ing.getId(), ing -> ing));
    }

    private final String getIngredientByIdSQL = "SELECT * FROM ingredients WHERE ingredient_id=?";

    @Override
    public Ingredient getIngredientById(long id) {
        return jdbcTemplate.queryForObject(getIngredientByIdSQL, new IngredientMapper(), id);
    }



    private final String saveItemSQL = "INSERT INTO items(item_name, item_price, item_type, item_section, item_station, item_availability) VALUES(?,?,?,?,?,?)";
    private final String insertIngredients = "INSERT INTO item_has_ingredients VALUES (?,?,?)";
    private final String getSavedItemSQL="SELECT * FROM items WHERE item_name=? AND ROWNUM=1 ORDER BY item_date_added DESC";


    @Override
    public Item saveNewItem(Item item) {
        if (jdbcTemplate.update(saveItemSQL, item.getName(), item.getPrice(), item.getType(), item.getSection(), item.getStation(), item.getAvailability()) == 1) {
            Item savedItem=jdbcTemplate.queryForObject(getSavedItemSQL,new EmptyItemMapper(),item.getName());
            for (Map.Entry<Ingredient, Integer> entry : item.getRecipe().entrySet()) {
                jdbcTemplate.update(insertIngredients, savedItem.getId(), entry.getKey().getId(), entry.getValue());
            }
            return getItemByName(item.getName());
        } else {
            return null;
        }
    }
    private final String setNewItemSQL ="UPDATE items SET item_availability=86 WHERE item_name=? and item_id!=?";
    @Override
    public void setNewItem(Item item){
        jdbcTemplate.update(setNewItemSQL,item.getName(),item.getId());
    }

    private final String saveCustomItemSQL ="INSERT INTO items(item_name, item_price, item_type, item_section, item_station, item_availability, custom) VALUES(?,?,?,?,?,?,?)";
    @Override
    public void saveNewCustomItem(Item item){
        if(jdbcTemplate.update(saveCustomItemSQL,item.getName(),item.getPrice(),item.getType(),item.getSection(),item.getStation(),item.getAvailability(),1)==1){
            System.out.println("ne here");
            Item savedItem=jdbcTemplate.queryForObject(getSavedItemSQL,new EmptyItemMapper(),item.getName());
            for (Map.Entry<Ingredient, Integer> entry : item.getRecipe().entrySet()) {
                jdbcTemplate.update(insertIngredients, savedItem.getId(), entry.getKey().getId(), entry.getValue());
            }
        }
    }


    private final String getCustomItem="SELECT * FROM items WHERE item_name=?";
    private final String getCustomItemIngredientIDsSQL="SELECT ITEM_ID, INGREDIENT_ID, INGREDIENT_QTY FROM items NATURAL JOIN item_has_ingredients NATURAL JOIN ingredients WHERE custom=1 AND item_name=?";
    @Override
    public List<Item> getCustomItemsByName(String name){
        Map<Long,Ingredient> ingredients=getAllIngredientsMap();
        List<Item> items=jdbcTemplate.query(getCustomItem,new EmptyItemMapper(), name);
        if(!items.isEmpty()) {
        Map<Long,Item> itemsMap=items.stream().collect(Collectors.toMap(it-> it.getId(),it->it));
        List<ItemIngredient> itemIngredients = jdbcTemplate.query(getCustomItemIngredientIDsSQL, new ItemIngredientRowMapper(),name);
            for (ItemIngredient itemIngredient : itemIngredients) {
                Item item = itemsMap.get(itemIngredient.getItemId());
                Ingredient ingredient = ingredients.get(itemIngredient.getIngredientId());
                item.addIngredient(ingredient, itemIngredient.getAmount());
            }
        }
        return items;
    }


    private final String getSectionsSQL = "SELECT DISTINCT item_section from items";

    @Override
    public List<String> getSections() {
        return jdbcTemplate.queryForList(getSectionsSQL, String.class);
    }

    private final String getIngredientByNameAmountPriceSQL="SELECT * FROM ingredients WHERE ingredient_name=? and ingredient_amount=? AND ingredient_price=? ";
    @Override
    public Ingredient getIngredientByNameAmountPrice(String name, Integer amount, Double price) {
        try {
            return jdbcTemplate.queryForObject(getIngredientByNameAmountPriceSQL, new IngredientMapper(), name, amount, price);
        }catch (EmptyResultDataAccessException ERDAe){
            return null;
        }

    }


    private final String changeIngredientAvailabilitySQL="UPDATE ingredients SET ingredient_availability=? WHERE ingredient_id=?";

    @Override
    public void changeIngredientAvailability(Ingredient ingredient, Integer integer) {
        jdbcTemplate.update(changeIngredientAvailabilitySQL, integer,ingredient.getId());
    }

    private final String changeIngredientAllergySQL="UPDATE ingredients SET ingredient_allergy=? WHERE ingredient_id=?";
    @Override
    public void changeIngredientAllergy(Ingredient ingredient, String allergy) {
        jdbcTemplate.update(changeIngredientAllergySQL,allergy,ingredient.getId());
    }

    private final String saveNewIngredientSQL="INSERT INTO ingredients(ingredient_name, ingredient_amount, ingredient_price, ingredient_availability,ingredient_allergy) VALUES(?,?,?,?,?)";
    @Override
    public void saveNewIngredient(Ingredient ingredient) {
        jdbcTemplate.update(saveNewIngredientSQL,ingredient.getName(),ingredient.getAmount(),ingredient.getPrice(),ingredient.getAvailability(),ingredient.getAllergy());
    }

    private final String setKitchenStatusToSeenSQL="UPDATE order_has_items SET item_kitchen_status='SEEN' WHERE order_id=? AND item_id=? AND time_ordered=?";
    @Override
    public void setKitchenStatusToSeen(Long orderId, Long itemId, LocalDateTime timeOrdered) {
        jdbcTemplate.update(setKitchenStatusToSeenSQL,orderId,itemId,timeOrdered);
    }



    private final String setKitchenStatusToReadySQL="UPDATE order_has_items SET item_kitchen_status='READY' WHERE order_id=? AND item_id=? AND time_ordered=?";
    @Override
    public void setKitchenStatusToReady(Long orderId, Long itemId, LocalDateTime timeOrdered) {
    jdbcTemplate.update(setKitchenStatusToReadySQL,orderId,itemId,timeOrdered);
    }

    private final String setKitchenStatusToBumpedSQL="UPDATE order_has_items SET item_kitchen_status='BUMPED' WHERE order_id=? AND item_id=? AND time_ordered=?";
    @Override
    public void setKitchenStatusToBumped(Long orderId, Long itemId, LocalDateTime timeOrdered){
        jdbcTemplate.update(setKitchenStatusToBumpedSQL,orderId,itemId,timeOrdered);
    }
    private final String addNewCashPaymentSQL ="INSERT INTO payments (order_id, amount,dub_id) VALUES(?,?,?)";
    @Override
    public void addNewPayment(Payment payment,User user) {
        jdbcTemplate.update(addNewCashPaymentSQL, payment.getOrderId(), payment.getAmount(),user.getUserID());
    }

    private final String voidPaymentSQL="DELETE FROM payments WHERE pay_id=?";
    @Override
    public void voidPayment(Payment payment) {
        jdbcTemplate.update(voidPaymentSQL,payment.getPaymentId());
    }

    private final String voidPaymentsSQL="DELETE FROM payments WHERE order_id=?";
    @Override
    public void voidPayments(Order order) {jdbcTemplate.update(voidPaymentsSQL,order.getId());

    }

    private final String addNewCardPaymentSQL="INSERT INTO payments (order_id, amount,card_no,exp_date,cname,bank,ctype,dub_id) VALUES (?,?,?,?,?,?,?,?)";
    @Override
    public void addNewCardPayment(CardPayment cardPayment,User user){
        jdbcTemplate.update(addNewCardPaymentSQL,cardPayment.getOrderId(),cardPayment.getAmount(),cardPayment.getCardNo(),cardPayment.getExpDate(),cardPayment.getCardName(),cardPayment.getBankName(),cardPayment.getCardType(),user.getUserID());
    }

    private final String getCashPaymentsForTableSQL="SELECT * from payments WHERE order_id=? AND card_no IS NULL";
    @Override
    public List<Payment> getCashPaymentsForTable(Long orderId){
        return jdbcTemplate.query(getCashPaymentsForTableSQL,new CashPaymentMapper(),orderId);
    }

    private final String getCardPaymentsForTableSQL="SELECT * FROM payments WHERE card_no IS NOT NULL AND order_id=?";
    @Override
    public List<CardPayment> getCardPaymentsForTable(Long orderId){
        return jdbcTemplate.query(getCardPaymentsForTableSQL,new CardPaymentMapper(),orderId);
    }

    private final String setTableStatusToPaidSQL="UPDATE orders SET status='PAID' WHERE order_id=?";
    private final String setPaidDubId="UPDATE orders SET dub_id=? WHERE order_id=?";
    private final String setTableCloseTimeSQL="UPDATE orders SET time_closed=? WHERE order_id=?";
    @Override
    public void setTableStatusToClosed(Long orderId,User user) {
        jdbcTemplate.update(setTableStatusToPaidSQL,orderId);
        jdbcTemplate.update(setPaidDubId,user.getUserID(),orderId);
        jdbcTemplate.update(setTableCloseTimeSQL,LocalDateTime.now(),orderId);
    }

    private final String getDayReportCashSQL="SELECT * FROM payments WHERE card_no IS NULL AND date_time BETWEEN (TO_DATE(?, 'DD-MM-YYYY HH24:MI')) and (TO_DATE(?, 'DD-MM-YYYY HH24:MI'))";
    private final String getDayReportCardSQL="SELECT * FROM payments WHERE card_no IS NOT NULL AND date_time BETWEEN (TO_DATE(?, 'DD-MM-YYYY HH24:MI')) AND (TO_DATE(?, 'DD-MM-YYYY HH24:MI'))";
    @Override
    public List<Payment> getDayReport(String start, String end) {
        List<Payment> cash=jdbcTemplate.query(getDayReportCashSQL,new CashPaymentMapper(),start, end);
        List<CardPayment> card=jdbcTemplate.query(getDayReportCardSQL,new CardPaymentMapper(),start,end);
        List<Payment> payments=new ArrayList<>();
        payments.addAll(cash);
        payments.addAll(card);
        return payments;
    }
    private final String getDubReportCashSQL="SELECT * FROM payments WHERE dub_id=? AND card_no IS NULL AND date_time BETWEEN (TO_DATE(?, 'DD-MM-YYYY HH24:MI')) and (TO_DATE(?, 'DD-MM-YYYY HH24:MI'))";
    private final String getDubReportCardSQL="SELECT * FROM payments WHERE dub_id=? AND card_no IS NOT NULL AND date_time BETWEEN (TO_DATE(?, 'DD-MM-YYYY HH24:MI')) AND (TO_DATE(?, 'DD-MM-YYYY HH24:MI'))";

    @Override
    public List<Payment> getDubReport(User user, String start, String end) {
        List<Payment> cash=jdbcTemplate.query(getDubReportCashSQL,new CashPaymentMapper(),user.getUserID(),start,end);
        List<CardPayment> card=jdbcTemplate.query(getDubReportCardSQL,new CardPaymentMapper(),user.getUserID(),start,end);
        List<Payment> payments=new ArrayList<>();
        payments.addAll(cash);
        payments.addAll(card);
        return payments;
    }

    private final String getDubTotalSoldSQL="SELECT SUM(item_qty*item_price) from orders NATURAL JOIN order_has_items NATURAL join items WHERE status='PAID' AND dub_id=? AND time_closed BETWEEN (TO_DATE(?, 'DD-MM-YYYY HH24:MI')) AND (TO_DATE(?, 'DD-MM-YYYY HH24:MI'))";

    @Override
    public Double getDubTotalSold(User user, String start, String end){
        return jdbcTemplate.queryForObject(getDubTotalSoldSQL,Double.class,user.getUserID(),start,end);
    }

    private final String getTotalSoldSQL="SELECT SUM(item_qty*item_price) from orders NATURAL JOIN order_has_items NATURAL join items WHERE status='PAID' AND time_closed BETWEEN (TO_DATE(?, 'DD-MM-YYYY HH24:MI')) AND (TO_DATE(?, 'DD-MM-YYYY HH24:MI'))";
    @Override
    public Double getTotalSoldSQL(String start, String end) {
        return jdbcTemplate.queryForObject(getTotalSoldSQL,Double.class,start,end);
    }

    private final String areAllTablesPaidSQL="SELECT * FROM orders WHERE dub_id=? AND status='OPEN' AND time_opened BETWEEN (TO_DATE(?, 'DD-MM-YYYY HH24:MI')) AND (TO_DATE(?, 'DD-MM-YYYY HH24:MI'))";
    @Override
    public List<Order> areAllTablesPaid(User user, String start, String end){
        return jdbcTemplate.query(areAllTablesPaidSQL,new EmptyOrderMapper(),user.getUserID(),start,end);
    }

    private final String areAllTablesPaidSQL2="SELECT * FROM orders WHERE status='OPEN' AND time_opened BETWEEN (TO_DATE(?, 'DD-MM-YYYY HH24:MI')) AND (TO_DATE(?, 'DD-MM-YYYY HH24:MI'))";
    @Override
    public List<Order> areAllTablesPaid(String start, String end) {
        return jdbcTemplate.query(areAllTablesPaidSQL2,new EmptyOrderMapper(),start,end);
    }


    private final String getAllOpenOrdersSQL="SELECT * FROM orders WHERE status='OPEN'";
    @Override
    public List<Order> getAllOpenOrders(){
        return jdbcTemplate.query(getAllOpenOrdersSQL,new EmptyOrderMapper());
    }

}
