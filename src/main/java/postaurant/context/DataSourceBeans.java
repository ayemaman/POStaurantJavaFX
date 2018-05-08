package postaurant.context;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import postaurant.database.UserDao;
import postaurant.database.UserDatabase;

import javax.sql.DataSource;

@Configuration
public class DataSourceBeans {

    @Bean
    public UserDatabase userDatabase(DataSource dataSource) {
        return new UserDao(dataSource);
    }

    @Bean
    public DataSource oracleDataSource(){
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(oracle.jdbc.driver.OracleDriver.class.getName());
        ds.setUrl("jdbc:oracle:thin:@localhost:1521:GDB01");
        ds.setUsername("C##MANAGER");
        ds.setPassword("entangle");
        return ds;
        
    }

/*
    @Bean
    public DataSource mySQLDataSource(){
        DriverManagerDataSource ds= new DriverManagerDataSource();
        ds.setDriverClassName(com.mysql.jdbc.Driver.class.getName());
        ds.setUrl("jdbc:mysql://hostname:port/dbname");
        ds.setUsername();
        ds.setPassword();
        return ds;
    }
*/


}
