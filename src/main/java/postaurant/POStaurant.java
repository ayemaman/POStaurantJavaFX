package postaurant;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
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

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "postaurant")
public class POStaurant extends Application{
    @Autowired
    private FXMLoaderService fxmlLoaderService;

    @Value("/FXML/POStaurant.fxml")
    private Resource postaurantScreen;

    @Override
    public void init(){
        ConfigurableApplicationContext applicationContext= SpringApplication.run(getClass());
        applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader=fxmlLoaderService.getLoader(postaurantScreen.getURL());
        Parent root = loader.load();
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
