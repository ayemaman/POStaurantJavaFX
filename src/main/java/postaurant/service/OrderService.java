package postaurant.service;

import org.springframework.stereotype.Component;
import postaurant.database.UserDatabase;

import java.util.List;

@Component
public class OrderService {
    private final UserDatabase userDatabase;
    public OrderService(UserDatabase userDatabase){
        this.userDatabase=userDatabase;
    }
    public boolean tableExists(String value) {
        return userDatabase.openTableExists(value);
    }

    public List<String> getItemsForSection(String section){
        return userDatabase.retrieveItemsForSection(section);
    }

}
