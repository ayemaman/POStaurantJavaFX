package postaurant.context;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import postaurant.database.UserDao;
import postaurant.database.UserDatabase;

@Configuration
public class DataSourceBeans {

    @Bean
    public UserDatabase userDatabase() {
        return new UserDao();
    }



}
