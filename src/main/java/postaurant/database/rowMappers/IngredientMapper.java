package postaurant.database.rowMappers;

import org.springframework.jdbc.core.RowMapper;
import postaurant.exception.InputValidationException;
import postaurant.model.Ingredient;

import java.sql.ResultSet;
import java.sql.SQLException;


public class IngredientMapper implements RowMapper<Ingredient> {
    @Override
    public Ingredient mapRow(ResultSet rs, int i) throws SQLException {
        try {
            Ingredient ingredient = new Ingredient(rs.getLong("ingredient_id"), rs.getString("ingredient_name"), rs.getInt("ingredient_amount"), rs.getDouble("ingredient_price"), rs.getInt("ingredient_availability"), rs.getString("ingredient_allergy"), rs.getDate("ingredient_date_created"));
            return ingredient;
        } catch (InputValidationException iEx) {
            iEx.printStackTrace();
        }
        return null;
    }
}
