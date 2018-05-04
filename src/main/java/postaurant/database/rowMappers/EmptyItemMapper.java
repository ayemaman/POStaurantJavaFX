package postaurant.database.rowMappers;

import org.springframework.jdbc.core.RowMapper;
import postaurant.exception.InputValidationException;
import postaurant.model.Item;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmptyItemMapper implements RowMapper<Item> {
    @Override
    public Item mapRow(ResultSet rs, int i) throws SQLException {
        try {
            return new Item(rs.getLong("item_id"), rs.getString("item_name"), rs.getDouble("item_price"), rs.getString("item_type"), rs.getString("item_section"), rs.getString("item_station"), rs.getInt("item_availability"), rs.getDate("item_date_added"));
        } catch (InputValidationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
