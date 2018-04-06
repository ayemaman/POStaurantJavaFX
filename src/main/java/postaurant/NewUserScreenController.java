package postaurant;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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
import postaurant.exception.InputValidationException;
import postaurant.model.User;
import postaurant.service.UserService;
import java.net.URL;
;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NewUserScreenController {

    private boolean wasUserSaved=false;
    private User user=null;



    private FXMLoaderService fxmLoaderService;
    private UserService userService;

    @Value("FXML/WrongInputWindow.fxml")
    private Resource wrongInputForm;

    @FXML
    private Button goBackButton;
    @FXML
    private Button saveUserButton;
    @FXML private TextField nameField;
    @FXML private TextField surnameField;
    @FXML private ChoiceBox<String> choicePosition;


    public NewUserScreenController(FXMLoaderService fxmLoaderService, UserService userService){
        this.fxmLoaderService=fxmLoaderService;
        this.userService=userService;
    }

    public void initialize(){
        ObservableList<String> positionList= FXCollections.observableArrayList("DUBDUB", "MANAGER");
        choicePosition.setValue("DUBDUB");
        choicePosition.setItems(positionList);

        goBackButton.setOnAction(e-> {
            exitWindow(goBackButton);
            goBackButton.getScene().getWindow().hide();
        });

        saveUserButton.setOnAction(e->{
            try {
                user = new User(nameField.getText(), surnameField.getText(), choicePosition.getValue());
                userService.saveNewUser(this.user);
                exitWindow(saveUserButton);
                goBackButton.getScene().getWindow().hide();
            }catch (InputValidationException wrongUserData){
                try {
                    URL url = wrongInputForm.getURL();
                    Parent root = fxmLoaderService.getLoader(url).load();
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add("POStaurant.css");
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setScene(scene);
                    stage.showAndWait();
                }catch (Exception e1){
                    e1.printStackTrace();
                }
            }

        });
    }

    public void setWasUserSaved(boolean wasUserSaved){
        this.wasUserSaved=wasUserSaved;
    }

    public boolean getWasUserSaved(){
        return this.wasUserSaved;
    }

    private void exitWindow(Button button){
        if(button.getId().equals("goBackButton")){
            setWasUserSaved(false);
        }else if(button.getId().equals("saveUserButton")){
            setWasUserSaved(true);
        }

    }
/*
    public void onKeyBoardButtonClick(ActionEvent event, TextField textField){
        Button button=(Button) event.getSource();
        String buffer=button.getText();
        if(buffer.equals("<--")){
            textField.

        }
    }
*/

}
