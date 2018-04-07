package postaurant;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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
import postaurant.service.ButtonCreationService;
import postaurant.service.UserService;
import java.net.URL;



@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NewUserScreenController {


    private FXMLoaderService fxmLoaderService;
    private UserService userService;
    private ButtonCreationService buttonCreationService;

    @Value("FXML/WrongInputWindow.fxml")
    private Resource wrongInputForm;

    @FXML
    private Button goBackButton;
    @FXML
    private Button saveUserButton;
    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    @FXML
    private ChoiceBox<String> choicePosition;
    @FXML
    private AnchorPane keyboard;
    @FXML
    private GridPane keyboardGrid;
    private boolean lowercase=true;

    private StringProperty name = new SimpleStringProperty("");
    private StringProperty surname = new SimpleStringProperty("");
    private TextField currentTextField;


    private boolean wasUserSaved = false;
    private User user = null;


    public NewUserScreenController(FXMLoaderService fxmLoaderService, UserService userService, ButtonCreationService buttonCreationService) {
        this.fxmLoaderService = fxmLoaderService;
        this.userService = userService;
        this.buttonCreationService=buttonCreationService;
    }


    public void initialize() {
        currentTextField=nameField;
        nameField.textProperty().bind(name);
        surnameField.textProperty().bind(surname);
        ObservableList<String> positionList = FXCollections.observableArrayList("DUBDUB", "MANAGER");
        choicePosition.setValue("DUBDUB");
        choicePosition.setItems(positionList);

        ColumnConstraints constraints=new ColumnConstraints();
        constraints.setHalignment(HPos.CENTER);
        keyboardGrid.getColumnConstraints().add(constraints);

        setKeyboard(true);


        goBackButton.setOnAction(e -> {
            exitWindow(goBackButton);
            goBackButton.getScene().getWindow().hide();
        });

        nameField.setOnMouseClicked(e -> this.currentTextField = nameField);
        surnameField.setOnMouseClicked(e -> this.currentTextField = surnameField);

        saveUserButton.setOnAction(e -> {
            try {
                user = new User(name.getValue(), surname.getValue(), choicePosition.getValue());
                userService.saveNewUser(this.user);
                exitWindow(saveUserButton);
                goBackButton.getScene().getWindow().hide();
            } catch (InputValidationException wrongUserData) {
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
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

        });
    }

    public void setWasUserSaved(boolean wasUserSaved) {
        this.wasUserSaved = wasUserSaved;
    }

    public boolean getWasUserSaved() {
        return this.wasUserSaved;
    }

    private void exitWindow(Button button) {
        if (button.getId().equals("goBackButton")) {
            setWasUserSaved(false);
        } else if (button.getId().equals("saveUserButton")) {
            setWasUserSaved(true);
        }
    }

    private void setKeyboard(boolean lowercase){
        int x=0;
        int y=0;
        for(int i=0; i< (keyboardGrid.getChildren().size());){
            keyboardGrid.getChildren().remove(keyboardGrid.getChildren().get(i));
        }

        for(Button button: buttonCreationService.createKeyboardButtons(lowercase)){
            button.setOnAction(event-> onKeyboardPress(event));
            this.lowercase=lowercase;
            keyboardGrid.add(button, x,y);
            if(x>8){
                x=0;
                y++;
            }else {
                x++;
            }
        }



    }

    public void onKeyboardPress(ActionEvent event) {
        StringProperty stringProperty = null;
        if (currentTextField.equals(nameField)) {
            stringProperty = name;
        } else if (currentTextField.equals(surnameField)) {
            stringProperty = surname;
        }
        if (stringProperty != null) {
            Button button = (Button) event.getSource();
            switch (button.getText()) {
                case "":
                        setKeyboard(!lowercase);
                        break;
                case "<--":
                    if(!stringProperty.getValue().equals("")) {
                        stringProperty.set(stringProperty.getValue().substring(0, stringProperty.getValue().length() - 1));
                    }
                    break;
                case "UP":
                    break;
                default:
                    stringProperty.set(stringProperty.getValue() + button.getText());
                    break;

            }
        }

    }
}

