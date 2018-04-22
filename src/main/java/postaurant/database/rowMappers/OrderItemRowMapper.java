package postaurant.database.rowMappers;


import org.springframework.jdbc.core.RowMapper;
import postaurant.model.ItemIngredient;
import postaurant.model.OrderItem;

import java.sql.ResultSet;
import java.sql.SQLException;


public class OrderItemRowMapper implements RowMapper<OrderItem> {
    @Override
    public OrderItem mapRow(ResultSet rs, int i) throws SQLException {
        return new OrderItem(rs.getInt("order_id"), rs.getInt("item_id"), rs.getInt("item_qty"));
    }
}
