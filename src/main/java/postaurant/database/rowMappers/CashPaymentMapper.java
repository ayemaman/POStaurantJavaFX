package postaurant.database.rowMappers;

import org.springframework.jdbc.core.RowMapper;
import postaurant.model.Payment;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CashPaymentMapper implements RowMapper<Payment> {
    @Override
    public Payment mapRow(ResultSet rs, int i) throws SQLException {
        return new Payment(rs.getLong("pay_id"),rs.getLong("order_id"),rs.getDouble("amount"));
    }
}
