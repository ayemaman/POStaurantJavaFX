package postaurant.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
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

    public UserDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }



    private final String retrieveUser = "SELECT * FROM dubdubs WHERE dub_id=?";
    @Override
    public User getUser(String userId) {
        return jdbcTemplate.queryForObject(retrieveUser, new UserRowMapper(), userId);
    }


    private final String getOrderSQL="select * from orders WHERE order_id=?";
    private final String getOrderItemSQL="select * from order_has_items WHERE order_id=?";

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

    private final String tableGotCheckedSQL="UPDATE orders SET last_time_checked=? WHERE order_id=?";
    @Override
    public void setCheckedByDub(Order order, Date date) {
        jdbcTemplate.update(tableGotCheckedSQL,date,order.getId());
    }

    /*

    private final String getMenuSQL = "SELECT * FROM items WHERE custom<>1 ORDER BY item_id DESC";
    private final String getItemIngredientIdsSQL ="SELECT ITEM_ID, INGREDIENT_ID, INGREDIENT_QTY FROM items NATURAL JOIN item_has_ingredients NATURAL JOIN ingredients";

    @Override
    public List<Item> getMenu() {
        Map<Integer, Ingredient> ingredients = getAllIngredientsMap();
        List<Item> items = jdbcTemplate.query(getMenuSQL, new EmptyItemMapper());
        Map<Integer, Item> itemsMap = items.stream().collect(Collectors.toMap(it -> it.getId().intValue(), it -> it));

        List<ItemIngredient> itemIngredients = jdbcTemplate.query(getItemIngredientIdsSQL, new ItemIngredientRowMapper());

        for (ItemIngredient itemIngredient : itemIngredients) {
            Item item = itemsMap.get(itemIngredient.getItemId());
            Ingredient ingredient = ingredients.get(itemIngredient.getIngredientId());
            item.addIngredient(ingredient, itemIngredient.getAmount());
        }
        return items;
    }
     */
    private final String getUserOrdersSQL="SELECT * FROM orders WHERE dub_id=? AND status<>'CLOSED' ORDER BY table_no";
    private final String getOrderItemIdsSQL="SELECT * FROM orders NATURAL JOIN order_has_items NATURAL JOIN items WHERE dub_id=? AND status<>'CLOSED'";


    @Override
    public List<Order> getUserOrders(User user){
        Map<Integer,Item> items=getAllItemsMap();
        List<Order> orders=jdbcTemplate.query(getUserOrdersSQL,new EmptyOrderMapper(), user.getUserID());
        Map<Integer, Order> orderMap = orders.stream().collect(Collectors.toMap(or->or.getId().intValue(), or->or ));

        List<OrderItem> orderItems=jdbcTemplate.query(getOrderItemIdsSQL, new OrderItemRowMapper(), user.getUserID());

        for(OrderItem orderItem: orderItems){
            Order order= orderMap.get(orderItem.getOrderId());
            Item item= items.get(orderItem.getItemId());
            try{
                Item newItem=new Item(item.getId(), item.getName(), item.getPrice(), item.getType(), item.getSection(),item.getStation(), item.getAvailability(), item.getDateCreated());
                newItem.setKitchenStatus(orderItem.getKitchenStatus());
                newItem.setDateOrdered(orderItem.getDateOrdered());
                order.addItem(newItem, orderItem.getAmount());
            }catch (InputValidationException iE){
                iE.printStackTrace();
            }


        }
        return orders;
    }

    private final String addItemToOrderSQL="INSERT INTO order_has_items (order_id, item_id, item_qty) VALUES(?,?,?)";
    @Override
    public void addItemToOrder(Long orderId, Long itemId, Integer qty) {
        jdbcTemplate.update(addItemToOrderSQL,orderId,itemId,qty);
    }


    private final String openTableExists = "SELECT count(*) FROM orders WHERE status='OPEN' AND table_no=?";
    @Override
    public boolean openTableExists(String value) {
        int openTableCount = jdbcTemplate.queryForObject(openTableExists, Integer.class, value);
        return openTableCount > 0;
    }

    private final String retrieveItemsSql = "SELECT item_name FROM items WHERE item_section=? AND item_availability=68";

    @Override
    public List<String> retrieveItemsForSection(String section) {
        List<String> items = new ArrayList<>();
        try {
            items = jdbcTemplate.queryForList(retrieveItemsSql, String.class, section);
        } catch (Exception e) {
            logger.error("error retrieving items for selection", e);
        }

        return items;
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



    private final String getKitchenOrderInfoSQL ="SELECT DISTINCT order_id, table_no, item_id, item_name, time_ordered,item_qty,item_station,item_kitchen_status FROM orders NATURAL JOIN order_has_items NATURAL JOIN items NATURAL JOIN item_has_ingredients NATURAL JOIN ingredients  WHERE time_closed IS NULL AND item_kitchen_status NOT IN('BUMPED','READY') ORDER BY time_ordered";
    private final String getItemIngredientIdsForKitchenSQL="SELECT * FROM item_has_ingredients";
    @Override
    public List<KitchenOrderInfo> getKitchenOrderInfo(){
        Map<Long,Ingredient> ingredients = getAllIngredientsMap();
         List<KitchenOrderInfo> list=jdbcTemplate.query(getKitchenOrderInfoSQL, new KitchenOrderMapper());
         List<ItemIngredient> itemIngredients=jdbcTemplate.query(getItemIngredientIdsForKitchenSQL, new ItemIngredientRowMapper());

         for(KitchenOrderInfo kitchenOrderInfo:list){
             Item item=kitchenOrderInfo.getItem();
             for( ItemIngredient itemIngredient: itemIngredients){
                 if(itemIngredient.getItemId().equals(item.getId())){
                     item.addIngredient(ingredients.get(itemIngredient.getIngredientId()),itemIngredient.getAmount());
                 }
             }
         }
         return list;

    }
    //todo


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



    private final String getItemByIdSQL = "SELECT * FROM items NATURAL JOIN item_has_ingredients NATURAL JOIN ingredients WHERE item_id=? ORDER BY item_id";

    @Override
    public List<Item> getItemById(long itemID) {
        return jdbcTemplate.query(getItemByIdSQL, new ItemRowMapper(), itemID);
    }

    private final String getItemByNameSQL = "SELECT * FROM items NATURAL JOIN item_has_ingredients NATURAL JOIN ingredients WHERE item_name=? ORDER BY item_date_added DESC";
    @Override
    public List<Item> getItemByName(String name){
        return jdbcTemplate.query(getItemByNameSQL,new ItemRowMapper(),name);
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
    public List<Item> saveNewItem(Item item) {
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

    private final String saveCustomItemSQL ="INSERT INTO items(item_name, item_price, item_type, item_section, item_station, item_availability, custom) VALUES(?,?,?,?,?,1)";
    @Override
    public void saveNewCustomItem(Item item){
        if(jdbcTemplate.update(saveCustomItemSQL,item.getName(),item.getPrice(),item.getType(),item.getSection(),item.getStation(),item.getAvailability())==1){
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

}
