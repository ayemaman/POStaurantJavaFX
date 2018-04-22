package postaurant.database.rowMappers;

import org.springframework.jdbc.core.RowMapper;
import postaurant.exception.InputValidationException;

import postaurant.model.Order;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmptyOrderMapper implements RowMapper<Order> {

    @Override
    public Order mapRow(ResultSet rs, int i) throws SQLException {
        // try {
        return new Order(rs.getLong("order_id"), rs.getDouble("table_no"), rs.getDate("time_opened"), rs.getString("status"), rs.getDate("last_time_checked"), rs.getDate("time_bumped"));
        // } catch (InputValidationException e) {
        //    e.printStackTrace();
    }
    //  return null;
}
