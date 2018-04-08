package postaurant.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.lang.Nullable;
import postaurant.exception.InputValidationException;
import postaurant.model.Ingredient;
import postaurant.model.Item;
import postaurant.model.Order;
import postaurant.model.User;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    private final String retrieveUserOrders="SELECT o.order_id, o.table_no, o.time_opened, o.status, ohi.i_id, it.i_name, it.i_price, ohi.kitchen_status, ihi.i_id, ihi.ingr_id, i.ingr_name, i.ingr_amount \n" +
            "FROM orders o \n" +
            "RIGHT OUTER JOIN order_has_item ohi ON o.order_id=ohi.order_id \n" +
            "RIGHT OUTER JOIN item_has_ingredient ihi ON ohi.i_id=ihi.i_id \n" +
            "RIGHT OUTER JOIN items it ON ihi.i_id=it.i_id \n" +
            "RIGHT OUTER JOIN ingredients i ON ihi.ingr_id=i.ingr_id \n" +
            "WHERE dub_id=? AND status='OPEN' ORDER BY table_no";


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
        if(items.isEmpty()){
            System.out.println("pusto blja");
        }
        for(String s:items){
            System.out.println(s);
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


    private static final class UserMapper implements RowMapper<User>{
        @Override
        public User mapRow(ResultSet rs, int i) {
            try {
                User user = new User();
                System.out.println(rs.getString("first_name"));
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

            Item item=new Item();
            item.setId(rs.getString("i_id"));
            item.setName(rs.getString("i_name"));
            item.setPrice(rs.getDouble("i_price"));
            item.setKitchen_status(rs.getString("kitchen_status"));

            Ingredient ingredient=new Ingredient();
            ingredient.setId(rs.getString("ingr_id"));
            ingredient.setName(rs.getString("ingr_name"));
            ingredient.setAmount(rs.getInt("ingr_amount"));

            item.addIngredient(ingredient);
            order.addItem(item);
            return order;
        }
    }

}
