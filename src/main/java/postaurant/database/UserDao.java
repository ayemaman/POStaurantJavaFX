package postaurant.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import postaurant.database.rowMappers.*;
import postaurant.model.*;

import javax.sql.DataSource;
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


    private final String retrieveUserOrders = "SELECT * FROM orders NATURAL JOIN order_has_items NATURAL JOIN items NATURAL JOIN item_has_ingredients NATURAL JOIN ingredients WHERE dub_id=? AND status<>'CLOSED' ORDER BY table_no, time_ordered";
    @Override
    public List<Order> retrieveUserOrders(User user) {
        return jdbcTemplate.query(retrieveUserOrders, new OrderMapper(), user.getUserID());
    }


    private final String openTableExists = "SELECT count(*) FROM orders WHERE status='OPEN' AND table_no=?";
    @Override
    public boolean openTableExists(String value) {
        int openTableCount = jdbcTemplate.queryForObject(openTableExists, Integer.class, value);
        return openTableCount > 0;
    }

    private final String retrieveItemsSql = "SELECT item_name FROM items WHERE item_type=? AND item_availability=68";

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


    private final String getAllIngredientsSQL = "SELECT * FROM ingredients ORDER BY ingredient_name";

    @Override
    public List<Ingredient> getAllIngredients() {
        return jdbcTemplate.query(getAllIngredientsSQL, new IngredientMapper());
    }


    public Map<Integer, Ingredient> getAllIngredientsMap() {
        return getAllIngredients().stream().collect(Collectors.toMap(ing -> ing.getId().intValue(), ing -> ing));
    }

    private final String getIngredientByIdSQL = "SELECT * FROM ingredients WHERE ingredient_id=?";

    @Override
    public Ingredient getIngredientById(long id) {
        return jdbcTemplate.queryForObject(getIngredientByIdSQL, new IngredientMapper(), id);
    }


    private final String saveItemSQL = "INSERT INTO items(item_name, item_price, item_type, item_section, item_availability) VALUES(?,?,?,?,?)";
    private final String insertIngredients = "INSERT INTO item_has_ingredients VALUES (?,?,?)";
    private final String getSavedItemSQL="SELECT * FROM items WHERE item_name=? AND ROWNUM=1 ORDER BY item_date_added DESC";


    @Override
    public List<Item> saveNewItem(Item item) {
        if (jdbcTemplate.update(saveItemSQL, item.getName(), item.getPrice(), item.getType(), item.getSection(), item.getAvailability()) == 1) {
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


    private final String getSectionsSQL = "SELECT DISTINCT item_section from items";

    @Override
    public List<String> getSections() {
        return jdbcTemplate.queryForList(getSectionsSQL, String.class);
    }

}
