package postaurant;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import postaurant.context.FXMLoaderService;
import postaurant.database.UserDatabase;
import postaurant.model.User;
import postaurant.service.UserService;

import java.awt.event.ActionEvent;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NewUserScreenController {

    private boolean wasUserSaved=false;
    private User user=null;



    private FXMLoaderService fxmLoaderService;
    private UserService userService;

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
            user=new User();
            user.setFirst_name(nameField.getText());
            user.setLast_name(surnameField.getText());
            user.setPosition(choicePosition.getValue());
            if(this.user!=null) {
            userService.saveNewUser(this.user);
            }
            exitWindow(saveUserButton);
            goBackButton.getScene().getWindow().hide();
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
