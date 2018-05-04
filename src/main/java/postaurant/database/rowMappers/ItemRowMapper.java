package postaurant.database.rowMappers;

import org.springframework.jdbc.core.RowMapper;
import postaurant.exception.InputValidationException;
import postaurant.model.Ingredient;
import postaurant.model.Item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;

public class ItemRowMapper implements RowMapper<Item> {
    @Override
    public Item mapRow(ResultSet rs, int i) throws SQLException {
        try {
            Ingredient ingredient = new Ingredient(rs.getLong("ingredient_id"), rs.getString("ingredient_name"), rs.getInt("ingredient_amount"), rs.getDouble("ingredient_price"), rs.getInt("ingredient_availability"), rs.getString("ingredient_allergy"), rs.getDate("ingredient_date_created"));
            int ingredientQuantity = rs.getInt("ingredient_qty");
            TreeMap<Ingredient, Integer> map = new TreeMap<>();
            map.put(ingredient, ingredientQuantity);
            try {
                return new Item(rs.getLong("item_id"), rs.getString("item_name"), rs.getDouble("item_price"), rs.getString("item_type"), rs.getString("item_section"), rs.getString("item_station"), rs.getInt("item_availability"), map, rs.getDate("item_date_added"));
            } catch (InputValidationException iEx2) {
                iEx2.printStackTrace();
            }

        } catch (InputValidationException iEx1) {
            iEx1.printStackTrace();
        }
        return null;
    }
}
