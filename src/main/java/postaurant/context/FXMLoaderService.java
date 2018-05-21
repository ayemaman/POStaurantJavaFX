package postaurant.context;


import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;

@Component
@Scope("singleton")
public class FXMLoaderService {

    @Autowired
    private ConfigurableApplicationContext context;

    public FXMLLoader getLoader(){
        FXMLLoader loader= new FXMLLoader();
        loader.setControllerFactory(param -> context.getBean(param));
        return loader;
    }

    public FXMLLoader getLoader(URL location){
        FXMLLoader loader=new FXMLLoader(location);
        loader.setControllerFactory(param -> context.getBean(param));
        return loader;
    }

}
