package postaurant.context;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import postaurant.database.UserDao;
import postaurant.database.UserDatabase;

import javax.sql.DataSource;

@Configuration
public class DataSourceBeans {

    //todo
    @Bean
    public UserDatabase userDatabase(@Qualifier("first") dataSource, @Qualifier"second" dataSource) {
        return new UserDao(dataSource);
    }


    @Bean(name="first")
    public DataSource oracleDataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(oracle.jdbc.driver.OracleDriver.class.getName());
        //ds.setUrl("jdbc:oracle:thin:@144.21.64.179:1521/PDB1.601660219.oraclecloud.internal");
        //ds.setUsername("C##POSTAURANT");
        ds.setUrl("jdbc:oracle:thin:@localhost:1521:GDB01");
        ds.setUsername("C##MANAGER");
        ds.setPassword("entangle");
        return ds;
    }


    @Bean(name="second")
    public UserDatabase testDatabase(DataSource dataSource){
        return new UserDao(dataSource);
    }

    @Bean
    @Qualifier("second")
        public DataSource testDataSource(){
            DriverManagerDataSource ds=new DriverManagerDataSource();
            ds.setDriverClassName(oracle.jdbc.driver.OracleDriver.class.getName());
            //ds.setUrl("jdbc:oracle:thin:@144.21.64.179:1521/PDB1.601660219.oraclecloud.internal");
            //ds.setUsername("C##POSTAURANT");
            ds.setUrl("jdbc:oracle:thin:@localhost:1521:GDB01");
            ds.setUsername("C##TESTSERVER");
            ds.setPassword("test");
            return ds;
    }

}

