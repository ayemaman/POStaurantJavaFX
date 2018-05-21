package postaurant.database.rowMappers;


import org.springframework.jdbc.core.RowMapper;

import postaurant.model.KitchenOrderInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;

public class KitchenOrderMapper implements RowMapper<KitchenOrderInfo> {
    @Override
    public KitchenOrderInfo mapRow(ResultSet rs, int i) throws SQLException {
        //public KitchenOrderInfo(Long orderId, Long itemId, String itemName, LocalDateTime timeOrdered,String station, String status, int qty)
        return new KitchenOrderInfo(rs.getLong("order_id"),rs.getDouble("table_no"),rs.getLong("item_id"),rs.getString("item_name"), convertToLocalDateTimeViaSqlTimestamp(rs.getDate("time_ordered")),rs.getString("item_station"),rs.getString("item_kitchen_status"),rs.getInt("item_qty"));
    }

    public LocalDateTime convertToLocalDateTimeViaSqlTimestamp(Date dateToConvert) {
        return new java.sql.Timestamp(
                dateToConvert.getTime()).toLocalDateTime();
    }
}
