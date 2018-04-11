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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDao implements UserDatabase{

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);
    private final JdbcTemplate jdbcTemplate;

    public UserDao(){
        DataSource dataSource= getDataSource();
        jdbcTemplate= new JdbcTemplate(dataSource);
    }

    private DataSource getDataSource(){
        DriverManagerDataSource ds=new DriverManagerDataSource();
        ds.setDriverClassName(oracle.jdbc.driver.OracleDriver.class.getName());
        ds.setUrl("jdbc:oracle:thin:@localhost:1521:GDB01");
        ds.setUsername("C##MANAGER");
        ds.setPassword("entangle");
        return ds;
    }

    private final String retrieveUser ="SELECT * FROM dubdubs WHERE dub_id=?";

    @Override
    public User getUser(String userId) {
        return jdbcTemplate.queryForObject(retrieveUser, new UserMapper(), userId);
    }

    private final String retrieveUserOrders="SELECT *\n" +
            "FROM orders\n" +
            "NATURAL JOIN order_has_items\n" +
            "NATURAL JOIN items\n" +
            "NATURAL JOIN item_has_ingredients\n" +
            "NATURAL JOIN ingredients\n" +
            "WHERE dub_id=? AND status='OPEN'\n" +
            "ORDER BY table_no";



    @Override
    public List<Order> retrieveUserOrders(User user) {
            return jdbcTemplate.query(retrieveUserOrders, new OrderMapper(),user.getUserID());
    }

    private final String openTableExists="SELECT count(*) FROM orders WHERE status='OPEN' AND table_no=?";

    @Override
    public boolean openTableExists(String value) {
        int openTableCount = jdbcTemplate.queryForObject(openTableExists, Integer.class, value);
        return openTableCount > 0;
    }

    private final String retrieveItemsSql = "SELECT i_name FROM items WHERE i_type=?";

    @Override
    public List<String> retrieveItemsForSection(String section) {
        List<String> items = new ArrayList<>();
        try {
            items=jdbcTemplate.queryForList(retrieveItemsSql, String.class, section);
        } catch (Exception e) {
            logger.error("error retrieving items for selection", e);
        }

        return items;
    }

    private final String retriveAllActiveUsers = "SELECT * FROM dubdubs WHERE accessible=1 ORDER BY position, first_name";

    @Override
    public List<User> retrieveAllActiveUsers() {
        return jdbcTemplate.query(retriveAllActiveUsers,new UserMapper());
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
        jdbcTemplate.update(blockUserSQL,user.getUserID());
    }

    private final String getMenuSQL="SELECT *\n" +
            "FROM items\n" +
            "NATURAL JOIN item_has_ingredients\n" +
            "NATURAL JOIN ingredients\n"+
            "ORDER BY i_id";
    @Override
    public List<Item> getMenu() {
        return jdbcTemplate.query(getMenuSQL,new ItemMapper());
    }

    private final String getItemSQL="SELECT * FROM items\n" +
            "NATURAL JOIN item_has_ingredients\n" +
            "NATURAL JOIN ingredients\n"+
            "WHERE i_id=?";

    @Override
    public List<Item> getItem(String itemID) {
        return jdbcTemplate.query(getItemSQL,new ItemMapper(), itemID);
    }

    private final String getAllIngredientsSQL="SELECT * FROM ingredients ORDER BY ingr_name";

    @Override
    public List<Ingredient> getAllIngredients() {
        return jdbcTemplate.query(getAllIngredientsSQL, new IngredientMapper());
    }

    private final String getIngredientSQL="SELECT * FROM ingredients WHERE ingr_id=?";

    @Override
    public Ingredient getIngredient(String id){
        return jdbcTemplate.queryForObject(getIngredientSQL,new IngredientMapper(),id);
    }


    private final String saveItemSQL="INSERT INTO items VALUES(?,?,?,?,?,?)";
    private final String insertIngredients="INSERT INTO item_has_ingredients VALUES (?,?,?)";
    @Override
    public List<Item> saveNewItem(Item item) {
        if(jdbcTemplate.update(saveItemSQL, item.getId(), item.getName(), item.getPrice(), item.getType(), item.getSection(),item.getAvailability())==1) {
            for (Map.Entry<Ingredient, Integer> entry : item.getRecipe().entrySet()) {
                jdbcTemplate.update(insertIngredients, item.getId(), entry.getKey().getId(), entry.getValue());
            }
        }
        return getItem(item.getId());
    }



    private static final class UserMapper implements RowMapper<User>{
        @Override
        public User mapRow(ResultSet rs, int i) {
            try {
                User user = new User();
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setUserID(rs.getString("dub_id"));
                user.setPosition(rs.getString("position"));
                int access=rs.getInt("accessible");
                if(access==1){
                    user.setAccessible(true);
                }
                else {
                    user.setAccessible(false);
                }
                return user;
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }



    private static final class OrderMapper implements RowMapper<Order>{
        @Override
        public Order mapRow(ResultSet rs, int i) throws SQLException{
            Order order=new Order();
            order.setOrderID(rs.getInt("order_id"));
            order.setTableNo(rs.getDouble("table_no"));
            order.setStatus(rs.getString("status"));

            Ingredient ingredient=new Ingredient();
            ingredient.setId(rs.getString("ingr_id"));
            ingredient.setName(rs.getString("ingr_name"));
            ingredient.setAmount(rs.getInt("ingr_amount"));
            Integer quantity=rs.getInt("ingr_quantity");
            HashMap<Ingredient, Integer> map=new HashMap<>();
            map.put(ingredient,quantity);

            Item item=new Item();
            try {
                item=new Item(rs.getString("i_name"),rs.getDouble("i_price"), rs.getString("i_type"),rs.getString("i_section"),rs.getInt("i_availability"),map);
                item.setKitchen_status(rs.getString("kitchen_status"));
            }catch (InputValidationException e){
                e.printStackTrace();
            }


            order.addItem(item);
            return order;
        }
    }

    private static final class ItemMapper implements RowMapper<Item>{
        @Override
        public Item mapRow(ResultSet rs, int i) throws SQLException{
            Item item=new Item();
            try{
                Ingredient ingredient=new Ingredient();
                ingredient.setId(rs.getString("ingr_id"));
                ingredient.setName(rs.getString("ingr_name"));
                ingredient.setAmount(rs.getInt("ingr_amount"));
                Integer quantity=rs.getInt("ingr_quantity");
                HashMap<Ingredient, Integer> map=new HashMap<>();
                map.put(ingredient, quantity);

                item=new Item(rs.getString("i_name"),rs.getDouble("i_price"), rs.getString("i_type"),rs.getString("i_section"),rs.getInt("i_availability"),map);


            }catch (InputValidationException e){
                e.printStackTrace();
            }
            return item;
        }
    }

    private static final class IngredientMapper implements RowMapper<Ingredient>{
        @Override
        public Ingredient mapRow(ResultSet rs, int i) throws SQLException{
            Ingredient ingredient=new Ingredient();
            ingredient.setId(rs.getString("ingr_id"));
            ingredient.setName(rs.getString("ingr_name"));
            ingredient.setAmount(rs.getInt("ingr_amount"));
            return ingredient;
        }
    }

}
