/**
 * Service that works with database data related to Users and their Orders;
 * @see postaurant.model.User
 *
 */
package postaurant.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import postaurant.database.UserDatabase;
import postaurant.model.Order;
import postaurant.model.User;
import java.util.List;


@Component
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserDatabase userDatabase;


    public UserService(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
    }

    /**
     * Retrieves user data from database with given id and creates User
     * @param userId ID of user
     * @return returns User if found, null if not
     */
    public User getUser(String userId) {
        try {
            return userDatabase.getUser(userId);
        } catch (Exception ex) {
            logger.error("error retrieving user", ex);
            return null;
        }
    }

    /**
     * Retrieves all active users from database
     * @return List<User> that holds all active Users from database
     */
    public List<User> getAllActiveUsers(){
        return userDatabase.retrieveAllActiveUsers();

    }

    /**
     * Adds new User data to database
     * @param user to be saved
     * @return new User that was saved to database
     */
    public User saveNewUser(User user){
        return userDatabase.saveNewUser(user);
    }

    /**
     * Blocks user access to app by setting specified users accessible field to 0;
     * @param user to be blocked
     */
    public void blockUser(User user){userDatabase.blockUser(user);}
    }



