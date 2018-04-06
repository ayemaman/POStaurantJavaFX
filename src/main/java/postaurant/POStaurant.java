package postaurant;


import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import postaurant.context.FXMLoaderService;
import postaurant.database.UserDao;
import postaurant.model.Order;

import java.net.URL;
import java.util.List;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "postaurant")
public class POStaurant extends Application{
    @Autowired
    private FXMLoaderService fxmlLoaderService;

    @Value("/FXML/POStaurant.fxml")
    private Resource sample;

    @Override
    public void init(){
        ConfigurableApplicationContext applicationContext= SpringApplication.run(getClass());
        applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL url=sample.getURL();
        Parent root = fxmlLoaderService.getLoader(url).load();
        primaryStage.setTitle("POStaurant");
        Scene scene= new Scene(root, 800, 600);
        String css = POStaurant.class.getResource("/POStaurant.css").toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);

    }
}
