package postaurant.database.rowMappers;

import org.springframework.jdbc.core.RowMapper;
import postaurant.exception.InputValidationException;
import postaurant.model.Ingredient;
import postaurant.model.Item;
import postaurant.model.Order;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.TreeMap;

public class OrderMapper implements RowMapper<Order> {

    //todo
    public LocalDateTime convertToLocalDateTimeViaSqlTimestamp(Date dateToConvert) {
        return new java.sql.Timestamp(
                dateToConvert.getTime()).toLocalDateTime();
    }
    //readjust to make usage od ItemIngredient Class
    @Override
    public Order mapRow(ResultSet rs, int i) throws SQLException {
        try {
            Ingredient ingredient = new Ingredient(rs.getLong("ingredient_id"), rs.getString("ingredient_name"), rs.getInt("ingredient_amount"), rs.getDouble("ingredient_price"), rs.getInt("ingredient_availability"), rs.getString("ingredient_allergy"), rs.getDate("ingredient_date_created"));
            int ingredientQuantity = rs.getInt("ingredient_qty");
            TreeMap<Ingredient, Integer> map = new TreeMap<>();
            map.put(ingredient, ingredientQuantity);
            Integer itemQuantity = rs.getInt("item_qty");
            try {
                /*
                public LocalDateTime convertToLocalDateTimeViaSqlTimestamp(Date dateToConvert) {
    return new java.sql.Timestamp(
      dateToConvert.getTime()).toLocalDateTime();
}
                 */
                Item item = new Item(rs.getLong("item_id"), rs.getString("item_name"), rs.getDouble("item_price"), rs.getString("item_type"), rs.getString("item_section"), rs.getInt("item_availability"), map, rs.getString("item_kitchen_status"), rs.getDate("item_date_added"), (convertToLocalDateTimeViaSqlTimestamp(rs.getDate("time_ordered"))));
                TreeMap<Item, Integer> map2 = new TreeMap<>((o1, o2) -> {
                    int idCmp = Long.compare(o1.getId(), o2.getId());
                    System.out.println("COMPARING: "+o1.getId()+" "+o2.getId());
                    System.out.println("RESULT: "+Long.compare(o1.getId(), o2.getId()));
                    if (idCmp != 0) {
                        return idCmp;
                    }
                    System.out.println("COMPARING: "+o1.getId()+" "+o2.getId());
                    System.out.println("RESULT: "+o1.getDateOrdered().compareTo(o2.getDateOrdered()));
                    return o1.getDateOrdered().compareTo(o2.getDateOrdered());
                });
                map2.put(item, itemQuantity);

                try {
                    return new Order(rs.getLong("order_id"), rs.getDouble("table_no"), rs.getDate("time_opened"), rs.getString("status"), rs.getDate("last_time_checked"), rs.getDate("time_bumped"), map2);

                } catch (InputValidationException iEx) {
                    iEx.printStackTrace();
                }
            } catch (InputValidationException iEx2) {
                iEx2.printStackTrace();
            }

        } catch (InputValidationException iEx1) {
            iEx1.printStackTrace();
        }
        return null;
    }
}


