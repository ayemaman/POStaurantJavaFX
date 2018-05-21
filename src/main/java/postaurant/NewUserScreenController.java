package postaurant;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import javafx.scene.layout.*;
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

import java.io.IOException;
import java.util.ArrayList;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NewUserScreenController {


    private FXMLoaderService fxmLoaderService;
    private UserService userService;
    private ButtonCreationService buttonCreationService;

    @Value("FXML/ConfirmationUserSave.fxml")
    private Resource confirmationForm;
    @Value("FXML/ErrorWindow.fxml")
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
    @FXML
    private HBox spacebarHBox;
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
        ObservableList<String> positionList = FXCollections.observableArrayList("DUBDUB", "MANAGER","FOODRUNNER","KITCHEN","BAR","DRINKRUNNER");
        choicePosition.setValue("DUBDUB");
        choicePosition.setItems(positionList);

        ColumnConstraints constraints=new ColumnConstraints();
        constraints.setHalignment(HPos.CENTER);
        keyboardGrid.getColumnConstraints().add(constraints);

        setKeyboard(true);


        goBackButton.setOnAction(e -> {
            goBackButton.getScene().getWindow().hide();
        });

        nameField.setOnMouseClicked(e -> this.currentTextField = nameField);
        surnameField.setOnMouseClicked(e -> this.currentTextField = surnameField);

        saveUserButton.setOnAction(e -> {
            try {
                user = new User(name.getValue(), surname.getValue(), choicePosition.getValue());
            }
            catch (InputValidationException wrongUserData) {
                try {
                    FXMLLoader loader=fxmLoaderService.getLoader(wrongInputForm.getURL());
                    Parent root = loader.load();
                    ErrorWindowController errorWindowController=loader.getController();
                    errorWindowController.setErrorLabel("WRONG NAME/SURNAME");
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
            if (user != null) {
                try {
                    FXMLLoader loader = fxmLoaderService.getLoader(confirmationForm.getURL());
                    Parent parent = loader.load();
                    ConfirmationUserSaveController confirmationUserSaveController = loader.getController();
                    confirmationUserSaveController.setUser(user);
                    Scene scene = new Scene(parent);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.showAndWait();
                    if(confirmationUserSaveController.wasSaved()) {
                        setWasUserSaved(true);
                        ((Node)(e.getSource())).getScene().getWindow().hide();
                    }
                } catch (IOException e1) {
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


    private void setKeyboard(boolean lowercase){
        int x=0;
        int y=0;
        for(int i=0; i< (keyboardGrid.getChildren().size());){
            keyboardGrid.getChildren().remove(keyboardGrid.getChildren().get(i));
        }
        for(int i=0;i<spacebarHBox.getChildren().size();){
            spacebarHBox.getChildren().remove(spacebarHBox.getChildren().get(i));
        }

        ArrayList<Button> buttonList=buttonCreationService.createKeyboardButtons(lowercase,55,475,30);

        for(int i=0; i<buttonList.size()-2;i++) {
            buttonList.get(i).setOnAction(this::onKeyboardPress);
            this.lowercase = lowercase;
            keyboardGrid.add(buttonList.get(i), x, y);
            if (x > 8) {
                x = 0;
                y++;
            } else {
                x++;
            }
        }
        buttonList.get(40).setOnAction(this::onKeyboardPress);
        buttonList.get(41).setOnAction(this::onKeyboardPress);
        spacebarHBox.getChildren().add(buttonList.get(40));
        spacebarHBox.getChildren().add(buttonList.get(41));
        spacebarHBox.setMargin(buttonList.get(40),new Insets(0,60,0,3));
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
                case "DELETE":
                        stringProperty.set("");
                    break;

                default:
                    stringProperty.set(stringProperty.getValue() + button.getText());
                    break;

            }
        }

    }
}

