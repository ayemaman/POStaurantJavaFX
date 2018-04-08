package postaurant;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import postaurant.context.FXMLoaderService;
import postaurant.model.User;
import postaurant.service.UserService;




@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserInfoScreenController {
    private User user;
    private boolean wasDeleted=false;

    private FXMLoaderService fxmLoaderService;
    private UserService userService;

    @Value("FXML/ConfirmationDelete.fxml")
    private Resource confirmationForm;

    @FXML
    private TextField idTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField surnameTextField;
    @FXML
    private TextField positionTextField;
    @FXML
    private Button goBackButton;
    @FXML
    private Button deleteUserButton;

    private UserInfoScreenController(FXMLoaderService fxmLoaderService, UserService userService){
        this.fxmLoaderService=fxmLoaderService;
        this.userService=userService;
    }

    public void initialize(){
        deleteUserButton.setOnAction(e-> {
            try {
                FXMLLoader loader = fxmLoaderService.getLoader(confirmationForm.getURL());
                Parent root = loader.load();
                ConfirmationDeleteController confirmationDeleteController =loader.getController();
                confirmationDeleteController.setUser(user);
                Scene scene = new Scene(root);
                scene.getStylesheets().add("POStaurant.css");
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(scene);
                stage.showAndWait();
                if (confirmationDeleteController.wasDeleted()) {
                    wasDeleted= confirmationDeleteController.wasDeleted();
                    deleteUserButton.getScene().getWindow().hide();
                }
            }catch (Exception e1){
                e1.printStackTrace();
            }

        });

        goBackButton.setOnAction(e->goBackButton.getScene().getWindow().hide());
    }

    public void setUser(User user){
        this.user=user;
        idTextField.textProperty().bind(user.getIDProperty());
        nameTextField.textProperty().bind(user.getNameProperty());
        surnameTextField.textProperty().bind(user.getSurnameProperty());
        positionTextField.textProperty().bind(user.getPositionProperty());
    }
    public boolean wasDeleted(){
        return wasDeleted;
    }

}
