package postaurant.database.rowMappers;

import org.springframework.jdbc.core.RowMapper;
import postaurant.model.CardPayment;

import java.sql.ResultSet;
import java.sql.SQLException;


public class CardPaymentMapper implements RowMapper<CardPayment> {
    @Override
    public CardPayment mapRow(ResultSet rs, int i) throws SQLException {
        return new CardPayment(rs.getLong("pay_id"),rs.getLong("order_id"),rs.getDouble("amount"),rs.getLong("card_no"),rs.getString("cname"),rs.getString("bank"),rs.getString("ctype"),rs.getString("exp_date"));
    }
}
