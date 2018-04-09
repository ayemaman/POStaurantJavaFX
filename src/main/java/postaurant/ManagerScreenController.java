package postaurant;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import postaurant.context.FXMLoaderService;
import postaurant.service.ButtonCreationService;

import java.util.List;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ManagerScreenController {

    private final FXMLoaderService fxmLoaderService;
    private final ButtonCreationService buttonCreationService;

    @Value("/FXML/UsersScreen.fxml")
    private Resource usersForm;

    @Value("/FXML/POStaurant.fxml")
    private Resource postaurantForm;

    @Value("/FXML/MenuScreen.fxml")
    private Resource menuForm;

    @Value("POStaurant.css")
    private Resource css;

    @FXML
    private Button usersButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button menuButton;

    public ManagerScreenController(FXMLoaderService fxmLoaderService, ButtonCreationService buttonCreationService){
        this.fxmLoaderService=fxmLoaderService;
        this.buttonCreationService=buttonCreationService;
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
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }catch (Exception e1){
                System.out.println(e1);
        }
        });

        menuButton.setOnAction(e->{
            try{
                FXMLLoader loader=fxmLoaderService.getLoader(menuForm.getURL());
                Parent parent=loader.load();
                MenuScreenController menuScreenController=loader.getController();
                List<Tab> list=buttonCreationService.createSectionTabs();
                menuScreenController.setSectionTabList(list);
                menuScreenController.setItemButtonList(buttonCreationService.createItemButtonsForSection(list.get(0).getText()));
                menuScreenController.setSectionTabs();
                Scene scene= new Scene(parent);
                scene.getStylesheets().add(""+css.getURL());
                Stage stage= (Stage)((Node) e.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }catch (Exception e1){
               e1.printStackTrace();
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
