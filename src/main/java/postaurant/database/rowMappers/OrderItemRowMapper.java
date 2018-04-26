package postaurant.database.rowMappers;


import org.springframework.jdbc.core.RowMapper;
import postaurant.model.ItemIngredient;
import postaurant.model.OrderItem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;


public class OrderItemRowMapper implements RowMapper<OrderItem> {
    @Override
    public OrderItem mapRow(ResultSet rs, int i) throws SQLException {
        //OrderItem orderItem =new OrderItem(rs.getInt("order_id"), rs.getInt("item_id"), rs.getInt("item_qty"),rs.getString("item_kitchen_status"),convertToLocalDateTimeViaSqlTimestamp(rs.getDate("time_ordered")));
        //System.out.println(orderItem.getOrderId()+" "+orderItem.getItemId()+" "+orderItem.getDateOrdered()+" "+orderItem.getKitchenStatus()+" "+orderItem.getAmount());

        return new OrderItem(rs.getInt("order_id"), rs.getInt("item_id"), rs.getInt("item_qty"),rs.getString("item_kitchen_status"),convertToLocalDateTimeViaSqlTimestamp(rs.getDate("time_ordered")));
    }
    public LocalDateTime convertToLocalDateTimeViaSqlTimestamp(Date dateToConvert) {
        return new java.sql.Timestamp(
                dateToConvert.getTime()).toLocalDateTime();
    }
}
