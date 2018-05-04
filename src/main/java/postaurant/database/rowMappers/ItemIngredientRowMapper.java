package postaurant.database.rowMappers;

import org.springframework.jdbc.core.RowMapper;
import postaurant.model.ItemIngredient;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemIngredientRowMapper implements RowMapper<ItemIngredient> {
    @Override
    public ItemIngredient mapRow(ResultSet rs, int i) throws SQLException {
        return new ItemIngredient(rs.getLong("ITEM_ID"), rs.getLong("INGREDIENT_ID"), rs.getInt("INGREDIENT_QTY"));
    }
}
