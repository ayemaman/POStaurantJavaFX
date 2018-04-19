package postaurant.database.rowMappers;

import org.springframework.jdbc.core.RowMapper;
import postaurant.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;


public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int i) throws SQLException {
        try {
            User user = new User();
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setUserID(rs.getString("dub_id"));
            user.setPosition(rs.getString("position"));
            int access = rs.getInt("accessible");
            if (access == 1) {
                user.setAccessible(true);
            } else {
                user.setAccessible(false);
            }
            return user;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
