package postaurant;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import postaurant.context.FXMLoaderService;
import postaurant.service.UserService;



@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ManagerScreenController {

    private final FXMLoaderService fxmLoaderService;
    private final UserService userService;

    @Value("/FXML/UsersScreen.fxml")
    private Resource usersForm;
    @Value("FXML/POStaurant.fxml")
    private Resource postaurantForm;
    @Value("POStaurant.css")
    private Resource css;

    @FXML
    private Button usersButton;
    @FXML Button exitButton;

    public ManagerScreenController(FXMLoaderService fxmLoaderService, UserService userService){
        this.fxmLoaderService=fxmLoaderService;
        this.userService=userService;
    }
    public void initialize(){
        usersButton.setOnAction(e-> {
            try {
                FXMLLoader loader = fxmLoaderService.getLoader(usersForm.getURL());
                Parent parent = loader.load();
                UsersScreenController usersScreenController= loader.getController();
                usersScreenController.setUserButtons();
                Scene scene = new Scene(parent);
                scene.getStylesheets().add(""+css.getURL());
                System.out.println(scene.getStylesheets().get(0));
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }catch (Exception e1){
                System.out.println(e1);
        }
        });

        exitButton.setOnAction(e->{
            try {
                FXMLLoader loader = fxmLoaderService.getLoader(postaurantForm.getURL());
                Parent parent = loader.load();
                Scene scene = new Scene(parent);
                scene.getStylesheets().add("" + css.getURL());
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }catch (Exception e1){
                e1.printStackTrace();
            }
        });
    }

}
