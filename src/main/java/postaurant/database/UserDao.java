package postaurant.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import postaurant.exception.InputValidationException;
import postaurant.model.Ingredient;
import postaurant.model.Item;
import postaurant.model.Order;
import postaurant.model.User;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class UserDao implements UserDatabase {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);
    private final JdbcTemplate jdbcTemplate;

    public UserDao() {
        DataSource dataSource = getDataSource();
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private DataSource getDataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(oracle.jdbc.driver.OracleDriver.class.getName());
        ds.setUrl("jdbc:oracle:thin:@localhost:1521:GDB01");
        ds.setUsername("C##MANAGER");
        ds.setPassword("entangle");
        return ds;
    }

    private final String retrieveUser = "SELECT * FROM dubdubs WHERE dub_id=?";
    @Override
    public User getUser(String userId) {
        return jdbcTemplate.queryForObject(retrieveUser, new UserMapper(), userId);
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

    private final String retrieveItemsSql = "SELECT item_name FROM items WHERE item_type=?";

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
        return jdbcTemplate.query(retriveAllActiveUsers, new UserMapper());
    }

    private final String saveNewUserSQL = "INSERT INTO dubdubs (first_name,last_name,position) VALUES(?,?,?)";
    private final String getLastSavedUserSQL = "SELECT * FROM (SELECT * FROM dubdubs ORDER BY date_added DESC) WHERE ROWNUM=1";

    @Override
    public User saveNewUser(User user) {
        jdbcTemplate.update(saveNewUserSQL, user.getFirstName(), user.getLastName(), user.getPosition());
        return jdbcTemplate.queryForObject(getLastSavedUserSQL, new UserMapper());
    }

    private final String blockUserSQL = "UPDATE dubdubs SET accessible=0 WHERE dub_id=?";

    @Override
    public void blockUser(User user) {
        jdbcTemplate.update(blockUserSQL, user.getUserID());
    }

    private final String getMenuSQL = "SELECT * FROM items NATURAL JOIN item_has_ingredients NATURAL JOIN ingredients ORDER BY item_id";

    @Override
    public List<Item> getMenu() {
        return jdbcTemplate.query(getMenuSQL, new ItemMapper());
    }

    private final String getItemByIdSQL = "SELECT * FROM items NATURAL JOIN item_has_ingredients NATURAL JOIN ingredients WHERE item_id=? ORDER BY item_id";

    @Override
    public List<Item> getItemById(long itemID) {
        return jdbcTemplate.query(getItemByIdSQL, new ItemMapper(), itemID);
    }


    private final String getItemByNameSQL = "SELECT * FROM items NATURAL JOIN item_has_ingredients NATURAL JOIN ingredients WHERE item_name=? ORDER BY item_date_added DESC";
    @Override
    public List<Item> getItemByName(String name){
        return jdbcTemplate.query(getItemByNameSQL,new ItemMapper(),name);
    }


    private final String getAllIngredientsSQL = "SELECT * FROM ingredients ORDER BY ingredient_name";

    @Override
    public List<Ingredient> getAllIngredients() {
        return jdbcTemplate.query(getAllIngredientsSQL, new IngredientMapper());
    }

    private final String getIngredientSQL = "SELECT * FROM ingredients WHERE ingredient_id=?";

    @Override
    public Ingredient getIngredient(long id) {
        return jdbcTemplate.queryForObject(getIngredientSQL, new IngredientMapper(), id);
    }


    private final String saveItemSQL = "INSERT INTO items VALUES(?,?,?,?,?,?)";
    private final String insertIngredients = "INSERT INTO item_has_ingredients VALUES (?,?,?)";

    @Override
    public List<Item> saveNewItem(Item item) {
        if (jdbcTemplate.update(saveItemSQL, item.getId(), item.getName(), item.getPrice(), item.getType(), item.getSection(), item.getAvailability()) == 1) {
            for (Map.Entry<Ingredient, Integer> entry : item.getRecipe().entrySet()) {
                jdbcTemplate.update(insertIngredients, item.getId(), entry.getKey().getId(), entry.getValue());
            }
            return getItemById(item.getId());
        } else {
            return null;
        }
    }

    private final String getSectionsSQL = "SELECT DISTINCT item_section from items";

    @Override
    public List<String> getSections() {
        return jdbcTemplate.queryForList(getSectionsSQL, String.class);
    }


    private static final class UserMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int i) {
            try {
                User user = new User();
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setUserID(rs.getString("dub_id"));
                user.setPosition(rs.getString("position"));
                int access = rs.getInt("accessible");
                if (access == 1) {
                    user.setAccessible(true);
                } else {
                    user.setAccessible(false);
                }
                return user;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
//todo

    private static final class OrderMapper implements RowMapper<Order> {
        @Override
        public Order mapRow(ResultSet rs, int i) throws SQLException {
            try {
                Ingredient ingredient = new Ingredient(rs.getLong("ingredient_id"), rs.getString("ingredient_name"), rs.getInt("ingredient_amount"), rs.getDouble("ingredient_price"), rs.getInt("ingredient_availability"), rs.getString("ingredient_allergy"),rs.getDate("ingredient_date_created"));
                int ingredientQuantity = rs.getInt("ingredient_qty");
                TreeMap<Ingredient, Integer> map = new TreeMap<>();
                map.put(ingredient, ingredientQuantity);
                Integer itemQuantity = rs.getInt("item_qty");
                try {
                    Item item = new Item(rs.getLong("item_id"), rs.getString("item_name"), rs.getDouble("item_price"), rs.getString("item_type"), rs.getString("item_section"), rs.getInt("item_availability"),map, rs.getString("item_kitchen_status"),rs.getDate("item_date_added"),rs.getDate("time_ordered"));
                    TreeMap<Item, Integer> map2 = new TreeMap<Item,Integer>((o1, o2) -> {
                        int idCmp = Long.compare(o1.getId(), o2.getId());
                        if (idCmp != 0) {
                            return idCmp;
                        }
                        return o1.getDateOrdered().compareTo(o2.getDateOrdered());
                    });
                    map2.put(item, itemQuantity);

                    try {
                        return new Order(rs.getLong("order_id"), rs.getDouble("table_no"), rs.getDate("time_opened"), rs.getString("status"), rs.getDate("last_time_checked"), rs.getDate("time_bumped"), map2);

                    } catch (InputValidationException iEx) {
                        iEx.printStackTrace();
                    }
                } catch (InputValidationException iEx2) {
                    iEx2.printStackTrace();
                }

            } catch (InputValidationException iEx1) {
                iEx1.printStackTrace();
            }
            return null;
        }
    }

    private static final class ItemMapper implements RowMapper<Item> {
        @Override
        public Item mapRow(ResultSet rs, int i) throws SQLException {
            try {
                Ingredient ingredient = new Ingredient(rs.getLong("ingredient_id"), rs.getString("ingredient_name"), rs.getInt("ingredient_amount"), rs.getDouble("ingredient_price"), rs.getInt("ingredient_availability"), rs.getString("ingredient_allergy"), rs.getDate("ingredient_date_created"));
                int ingredientQuantity = rs.getInt("ingredient_qty");
                TreeMap<Ingredient, Integer> map = new TreeMap<>();
                map.put(ingredient, ingredientQuantity);
                try {
                    return new Item(rs.getLong("item_id"), rs.getString("item_name"), rs.getDouble("item_price"), rs.getString("item_type"), rs.getString("item_section"), rs.getInt("item_availability"), map, rs.getDate("item_date_added"));
                } catch (InputValidationException iEx2) {
                    iEx2.printStackTrace();
                }

            } catch (InputValidationException iEx1) {
                iEx1.printStackTrace();
            }
            return null;
        }
    }


    private static final class IngredientMapper implements RowMapper<Ingredient> {
        @Override
        public Ingredient mapRow(ResultSet rs, int i) throws SQLException {
            try {
                Ingredient ingredient = new Ingredient(rs.getLong("ingredient_id"), rs.getString("ingredient_name"), rs.getInt("ingredient_amount"), rs.getDouble("ingredient_price"), rs.getInt("ingredient_availability"), rs.getString("ingredient_allergy"), rs.getDate("ingredient_date_created"));
                return ingredient;
            } catch (InputValidationException iEx) {
                iEx.printStackTrace();
            }
            return null;
        }

    }
}
