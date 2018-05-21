package postaurant.database.rowMappers;

import org.springframework.jdbc.core.RowMapper;
import postaurant.exception.InputValidationException;

import postaurant.model.Order;
import postaurant.service.ButtonCreationService;
import postaurant.service.TimeService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;

public class EmptyOrderMapper implements RowMapper<Order> {

    @Override
    public Order mapRow(ResultSet rs, int i) throws SQLException {
        return new Order(rs.getLong("order_id"), rs.getDouble("table_no"), convertToLocalDateTimeViaSqlTimestamp(rs.getDate("time_opened")), rs.getString("status"), convertToLocalDateTimeViaSqlTimestamp(rs.getDate("last_time_checked")));

    }
    public LocalDateTime convertToLocalDateTimeViaSqlTimestamp(Date dateToConvert) {
        if(dateToConvert!=null) {
            return new java.sql.Timestamp(
                    dateToConvert.getTime()).toLocalDateTime();
        }
        else{
            return null;
        }
    }

}
