package postaurant;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import postaurant.context.AllergenList;
import postaurant.context.FXMLoaderService;
import postaurant.exception.InputValidationException;
import postaurant.model.Ingredient;
import postaurant.service.ButtonCreationService;
import postaurant.serviceWindowsControllers.ErrorWindowController;

import java.io.IOException;
import java.util.ArrayList;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IngredientInfoScreenController {

    private Ingredient ingredient;

    private ObservableList<String> allergenList;

    private boolean lowercase = true;
    private StringProperty name;
    private StringProperty amount;
    private StringProperty price;
    private StringProperty allergy;

    private final FXMLoaderService fxmLoaderService;
    private final ButtonCreationService buttonCreationService;

    @Value("FXML/ErrorWindow.fxml")
    private Resource wrongInputForm;
    @Value("FXML/ConfirmationIngredientSave.fxml")
    private Resource confirmationSaveForm;

    @Value("POStaurant.css")
    private Resource css;
    /*

     */
    private boolean saved;
    private Node currentTextField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField amountField;
    @FXML
    private TextField priceField;
    @FXML
    private ChoiceBox<String> allergyChoiceBox;
    @FXML
    private Button availabilityButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button goBackButton;
    @FXML
    private GridPane keyboardGrid;
    @FXML
    private HBox spacebarHBox;

    public IngredientInfoScreenController(FXMLoaderService fxmLoaderService, ButtonCreationService buttonCreationService) {
        this.buttonCreationService = buttonCreationService;
        this.fxmLoaderService = fxmLoaderService;
        this.saved=false;
    }


    public void initialize() {
        name = new SimpleStringProperty("");
        amount = new SimpleStringProperty("");
        price = new SimpleStringProperty("");
        allergy = new SimpleStringProperty("");

        allergenList = FXCollections.observableArrayList();
        allergenList.addAll(AllergenList.getAllergens());
        allergyChoiceBox.setItems(allergenList);

        saveButton.setOnAction(this::saveButtonAction);

        goBackButton.setOnAction(e -> {
            goBackButton.getScene().getWindow().hide();
        });

        nameField.textProperty().bind(name);
        amountField.textProperty().bind(amount);
        priceField.textProperty().bind(price);
        allergyChoiceBox.valueProperty().bind(allergy);
        currentTextField = nameField;

        nameField.setOnMouseClicked(e -> this.currentTextField = nameField);
        amountField.setOnMouseClicked(e -> this.currentTextField = amountField);
        priceField.setOnMouseClicked(e -> this.currentTextField = priceField);
        //listen for changes to the typeComboBox selection and update the displayed section StringProperty accordingly.
        allergyChoiceBox.getSelectionModel().selectedItemProperty().addListener((selected, oldString, newString) -> {
            if (newString != null) {
                allergy.setValue(allergyChoiceBox.getSelectionModel().getSelectedItem());
            }
        });

        availabilityButton.setOnAction(e -> {
            if (availabilityButton.getStyleClass().get(0).equals("Availability68")) {
                availabilityButton.getStyleClass().clear();
                availabilityButton.getStyleClass().add("Availability85");
            } else if (availabilityButton.getStyleClass().get(0).equals("Availability85")) {
                availabilityButton.getStyleClass().clear();
                availabilityButton.getStyleClass().add("Availability86");
            } else {
                availabilityButton.getStyleClass().clear();
                availabilityButton.getStyleClass().add("Availability68");
            }
        });
    }

    public void setup(Ingredient ingredient) {
        if (ingredient != null) {
            name.setValue(ingredient.getName());
            amount.setValue(ingredient.getAmount() + "");
            price.setValue("" + ingredient.getPrice());
            allergy.setValue(ingredient.getAllergy());
        }
        setKeyboard(lowercase);
        setIngredient(ingredient);
        try {
            availabilityButton.getStyleClass().clear();
            if (ingredient.getAvailability() == 86) {
                availabilityButton.getStyleClass().add("Availability86");
            } else if (ingredient.getAvailability() == 85) {
                availabilityButton.getStyleClass().add("Availability85");
            } else {
                availabilityButton.getStyleClass().add("Availability68");
            }
        } catch (NullPointerException nE) {
            availabilityButton.getStyleClass().clear();
            availabilityButton.getStyleClass().add("Availability68");
        }
    }

    /*


     */

    public void setIngredient(Ingredient ingredient) {
        if (ingredient == null) {
            this.ingredient = new Ingredient();
        } else {
            this.ingredient = ingredient;
        }

    }

    private void setKeyboard(boolean lowercase) {
        int x = 0;
        int y = 0;
        for (int i = 0; i < (keyboardGrid.getChildren().size()); ) {
            keyboardGrid.getChildren().remove(keyboardGrid.getChildren().get(i));
        }
        for (int i = 0; i < spacebarHBox.getChildren().size(); ) {
            spacebarHBox.getChildren().remove(spacebarHBox.getChildren().get(i));
        }

        ArrayList<Button> buttonList = buttonCreationService.createKeyboardButtons(lowercase, 55, 475, 30);

        for (int i = 0; i < buttonList.size() - 2; i++) {
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
        spacebarHBox.setMargin(buttonList.get(40), new Insets(0, 60, 0, 3));
    }


    public void onKeyboardPress(ActionEvent event) {
        StringProperty stringProperty = null;
        if (currentTextField.equals(nameField)) {
            stringProperty = name;
        } else if (currentTextField.equals(priceField)) {
            stringProperty = price;
        } else if (currentTextField.equals(amountField)) {
            stringProperty = amount;
        } else if (currentTextField.equals(allergyChoiceBox)) {
            stringProperty = allergy;
        }
        if (stringProperty != null) {
            Button button = (Button) event.getSource();
            switch (button.getId()) {
                case "capsKey":
                    setKeyboard(!lowercase);
                    break;
                case "backspaceKey":
                    if (!stringProperty.getValue().equals("")) {
                        stringProperty.set(stringProperty.getValue().substring(0, stringProperty.getValue().length() - 1));
                    }
                    break;
                case "deleteKey":
                    stringProperty.set("");
                    break;
                case "spacebarKey":
                    stringProperty.set(stringProperty.getValue() + " ");
                    break;
                default:
                    stringProperty.set(stringProperty.getValue() + button.getText());
                    break;

            }
        }

    }

    private void saveButtonAction(ActionEvent e) {
        try {
            Ingredient ingredient = new Ingredient();
            try {
                ingredient.setName(name.getValue());
            } catch (InputValidationException eName) {
                FXMLLoader loader = fxmLoaderService.getLoader(wrongInputForm.getURL());
                Parent parent = loader.load();
                ErrorWindowController errorWindowController = loader.getController();
                errorWindowController.setErrorLabel("Wrong name");
                Scene scene = new Scene(parent);
                scene.getStylesheets().add(css.getURL().toExternalForm());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.showAndWait();
            }
            try {
                ingredient.setAmount(Integer.parseInt(amount.getValue()));
            } catch (Exception ePrice) {
                FXMLLoader loader = fxmLoaderService.getLoader(wrongInputForm.getURL());
                Parent parent = loader.load();
                ErrorWindowController errorWindowController = loader.getController();
                errorWindowController.setErrorLabel("Wrong amount");
                Scene scene = new Scene(parent);
                scene.getStylesheets().add(css.getURL().toExternalForm());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.showAndWait();

            }
            try {
                ingredient.setPrice(Double.parseDouble(price.getValue()));
            }catch (NumberFormatException NFe){
                FXMLLoader loader = fxmLoaderService.getLoader(wrongInputForm.getURL());
                Parent parent = loader.load();
                ErrorWindowController errorWindowController = loader.getController();
                errorWindowController.setErrorLabel("Wrong price");
                Scene scene = new Scene(parent);
                scene.getStylesheets().add(css.getURL().toExternalForm());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.showAndWait();
            }
            ingredient.setAllergy(allergy.getValue());

            int avail;
            if (availabilityButton.getStyleClass().get(0).equals("Availability86")) {
                avail = 86;
            } else if (availabilityButton.getStyleClass().get(0).equals("Availability85")) {
                avail = 85;
            } else {
                avail = 68;
            }
            ingredient.setAvailability(avail);

            System.out.println(ingredient.getPrice());
            if ((ingredient.getName() != null) && (ingredient.getAmount() != null) && (ingredient.getPrice() != null) && (ingredient.getAllergy() != null)) {
                FXMLLoader loader = fxmLoaderService.getLoader(confirmationSaveForm.getURL());
                Parent parent = loader.load();
                ConfirmationIngredientSaveController confirmationIngredientSaveController = loader.getController();
                confirmationIngredientSaveController.setup(ingredient);
                Scene scene = new Scene(parent);
                scene.getStylesheets().add(css.getURL().toExternalForm());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.showAndWait();
                if (confirmationIngredientSaveController.isSaved()) {
                    this.saved=true;
                    Stage stage1 = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    stage1.hide();

                }
            }
        } catch (IOException ioE) {
            ioE.printStackTrace();
        }

    }

    public boolean wasSaved(){
        return this.saved;
    }
}



