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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
import postaurant.context.FXMLoaderService;
import postaurant.context.StationList;
import postaurant.context.TypeList;
import postaurant.exception.InputValidationException;
import postaurant.model.Ingredient;
import postaurant.model.Item;
import postaurant.service.ButtonCreationService;
import postaurant.service.MenuService;


import java.io.IOException;
import java.util.*;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ItemInfoScreenController {

    private int page;
    private ArrayList<Button> ingredientButtonList;
    private Item item;
    private ObservableList<Ingredient> ingredientsList;
    private ObservableList<String> sectionsList;
    private ObservableList<String> typeList;
    private ObservableList<String> stationList;

    private boolean lowercase = true;
    private StringProperty name;
    private StringProperty price;
    private StringProperty type;
    private StringProperty section;
    private StringProperty station;


    private final ButtonCreationService buttonCreationService;
    private final MenuService menuService;
    private final FXMLoaderService fxmLoaderService;


    @Value("/FXML/ItemWrongInputWindow.fxml")
    private Resource wrongInputForm;
    @Value("/FXML/MenuScreen.fxml")
    private Resource menuScreenForm;
    @Value("/FXML/ConfirmationItemSave.fxml")
    private Resource confirmationSaveForm;
    @Value("/POStaurant.css")
    private Resource css;


    private Node currentTextField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField priceField;
    @FXML
    private ComboBox<String> sectionComboBox;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private ComboBox<String> stationComboBox;

    @FXML
    private Button saveButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button availabilityButton;
    @FXML
    private GridPane keyboardGrid;
    @FXML
    private GridPane ingredientGrid;
    @FXML
    private TableView<Ingredient> ingredientTable;
    @FXML
    private TableColumn<Ingredient, String> recipeColumn;
    @FXML
    private HBox spacebarHBox;
    @FXML
    private Button upButton;
    @FXML
    private Button downButton;


    public ItemInfoScreenController(ButtonCreationService buttonCreationService, FXMLoaderService fxmLoaderService, MenuService menuService) {
        this.buttonCreationService = buttonCreationService;
        this.fxmLoaderService = fxmLoaderService;
        this.menuService = menuService;
    }

    public void setIngredientButtonList(ArrayList<Button> ingredientButtonList) {
        this.ingredientButtonList = ingredientButtonList;
    }

    public void initialize() {

        name = new SimpleStringProperty("");
        price = new SimpleStringProperty("0.00");
        type = new SimpleStringProperty("");
        section = new SimpleStringProperty("");
        station = new SimpleStringProperty("");

        sectionsList = FXCollections.observableArrayList();
        sectionsList.addAll(menuService.getSections());
        sectionComboBox.setItems(sectionsList);

        typeList = FXCollections.observableArrayList();
        typeList.addAll(TypeList.getItemTypes());
        typeComboBox.setItems(typeList);

        stationList= FXCollections.observableArrayList();
        stationList.addAll(StationList.getStationTypes());
        stationComboBox.setItems(stationList);

        saveButton.setOnAction(this::saveButtonAction);

        removeButton.setOnAction(e -> {
            ObservableList<Ingredient> selected = ingredientTable.getSelectionModel().getSelectedItems();
            selected.forEach(ingredientsList::remove);
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


        exitButton.setOnAction(e -> {
            try {
                FXMLLoader loader = fxmLoaderService.getLoader(menuScreenForm.getURL());
                Parent parent = loader.load();
                Scene scene = new Scene(parent);
                scene.getStylesheets().add(css.getURL().toExternalForm());
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException ioE) {
                ioE.printStackTrace();

            }
        });


        nameField.textProperty().bind(name);
        priceField.textProperty().bind(price);
        typeComboBox.valueProperty().bind(type);
        stationComboBox.valueProperty().bind(station);
        sectionComboBox.valueProperty().bind(section);
        currentTextField = nameField;
        nameField.setOnMouseClicked(e -> this.currentTextField = nameField);
        priceField.setOnMouseClicked(e -> this.currentTextField = priceField);

        //listen for changes to the typeComboBox selection and update the displayed section StringProperty accordingly.
        typeComboBox.getSelectionModel().selectedItemProperty().addListener((selected, oldString, newString) -> {
            if (newString != null) {
                type.setValue(typeComboBox.getSelectionModel().getSelectedItem());
            }
        });
        //listen for changes to the stationComboBox selection and update the displayed section StringProperty accordingly.
        stationComboBox.getSelectionModel().selectedItemProperty().addListener((selected, oldString, newString) ->{
            if( newString!=null){
                station.setValue(stationComboBox.getSelectionModel().getSelectedItem());
            }
        });
        sectionComboBox.setOnMouseClicked(e -> this.currentTextField = sectionComboBox);
        //listen for changes to the sectionComboBox selection and update the displayed section StringProperty accordingly.
        sectionComboBox.getSelectionModel().selectedItemProperty().addListener((selected, oldString, newString) -> {
            if (newString != null) {
                section.setValue(sectionComboBox.getSelectionModel().getSelectedItem());
            }
        });


        upButton.setOnAction(e -> {
            System.out.println(this.page);

            setIngredientButtons(this.ingredientGrid, 12, false, ingredientButtonList);

        });

        downButton.setOnAction(e -> {
            setIngredientButtons(this.ingredientGrid, 12, true, ingredientButtonList);
        });

    }

    public void setup(Item item) {
        if (item != null) {
            name.setValue(item.getName());
            price.setValue("" + item.getPrice());
            type.setValue(item.getType());
            section.setValue(item.getSection());
            station.setValue(item.getStation());
        }
        setKeyboard(lowercase);
        this.page = 0;
        addOnActionToIngredientButtons();
        setIngredientButtons(this.ingredientGrid, 12, true, ingredientButtonList);
        setItem(item);
        try {
            availabilityButton.getStyleClass().clear();
            if (item.getAvailability() == 86) {
                availabilityButton.getStyleClass().add("Availability86");
            } else if (item.getAvailability() == 85) {
                availabilityButton.getStyleClass().add("Availability85");
            } else {
                availabilityButton.getStyleClass().add("Availability68");
            }
        } catch (NullPointerException nE) {
            availabilityButton.getStyleClass().clear();
            availabilityButton.getStyleClass().add("Availability68");
        }
        recipeColumn.setMinWidth(200);
        recipeColumn.setCellFactory(param -> new AvailabilityFormatCell());
        recipeColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        ingredientTable.setItems(ingredientsList);

        ingredientTable.getSortOrder().add(recipeColumn);
        ingredientTable.setPlaceholder(new Label("Add ingredients"));

    }

    private class AvailabilityFormatCell extends TableCell<Ingredient, String> {
        public AvailabilityFormatCell() {
        }
        @Override
        protected void updateItem(String string, boolean empty) {
            super.updateItem(string, empty);
            setText(string == null ? "" : string);
            if (string != null) {
                for (Ingredient i : ingredientsList) {
                    if (string.equals(i.getName())) {
                        if (i.getAvailability() == 86) {
                            setStyle("-fx-background-color:red");
                        } else if (i.getAvailability() == 85) {
                            setStyle("-fx-background-color:orange");
                        }
                    }
                }
            } else {
                setStyle("");
            }
        }
    }

    public void setItem(Item item) {
        this.item = item;
        ingredientsList = FXCollections.observableArrayList();
        if (item != null) {
            for (Map.Entry<Ingredient, Integer> entry : item.getRecipe().entrySet()) {
                for (int i = 0; i < entry.getValue(); i++) {
                    ingredientsList.add(entry.getKey());
                }
            }
        } else {
            this.item = new Item();
        }
    }

    private boolean isNextPage(int page, List<Button> list, int size) {
        try {
            if (page > 0) {
                list.get((page * size));
            } else {
                list.get((size));
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }


    public void addOnActionToIngredientButtons() {
        for (Button b : ingredientButtonList) {
            b.setOnAction(e -> {
                Ingredient ingredient = menuService.getIngredientById(Long.parseLong(b.getText().substring(0, b.getText().indexOf("\n"))));
                ingredientsList.add(ingredient);
                Comparator<Ingredient> ingredientNameComparator = Comparator.comparing(Ingredient::getName);
                FXCollections.sort(ingredientsList, ingredientNameComparator);
            });
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

        ArrayList<Button> buttonList = buttonCreationService.createKeyboardButtons(lowercase, 78, 700, 30);

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
        spacebarHBox.setMargin(buttonList.get(40), new Insets(0, 11, 0, 0));
    }

    public void onKeyboardPress(ActionEvent event) {
        StringProperty stringProperty = null;
        if (currentTextField.equals(nameField)) {
            stringProperty = name;
        } else if (currentTextField.equals(priceField)) {
            stringProperty = price;

        } else if (currentTextField.equals(sectionComboBox)) {
            stringProperty = section;
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



    public void setIngredientButtons(GridPane gridPane, Integer size, boolean forward, List<Button> list) {
        int start;
        int x = 0;
        int y = 0;
        if (forward) {

            if (this.page == 0) {
                start = 0;
            } else {
                start = this.page * size;
            }
            //if all buttons don't fit in gridPane
            if (isNextPage(this.page, list, size)) {
                for (int i = 0; i < gridPane.getChildren().size(); ) {
                    gridPane.getChildren().remove(gridPane.getChildren().get(i));
                }
                if (start == 0) {
                    for (int i = start; i < size; i++) {
                        gridPane.add(list.get(i), x, y);
                        GridPane.setMargin(list.get(i), new Insets(2, 2, 10, 2));
                        if (x == 2) {
                            x = 0;
                            y++;
                        } else {
                            x++;
                        }

                    }
                    this.page++;
                } else {
                    if (isNextPage(this.page + 1, list, size)) {
                        for (int i = start; i < start + size; i++) {
                            gridPane.add(list.get(i), x, y);
                            GridPane.setMargin(list.get(i), new Insets(2, 2, 2, 2));
                            if (x == 2) {
                                x = 0;
                                y++;
                            } else {
                                x++;
                            }
                        }
                        this.page++;
                    } else {
                        for (int i = start; i < list.size(); i++) {
                            gridPane.add(list.get(i), x, y);
                            GridPane.setMargin(list.get(i), new Insets(2, 2, 2, 2));
                            if (x == 2) {
                                x = 0;
                                y++;
                            } else {
                                x++;
                            }
                        }
                        this.page++;
                    }
                }
            } else {
                if (start == 0) {
                    for (int i = 0; i < gridPane.getChildren().size(); ) {
                        gridPane.getChildren().remove(gridPane.getChildren().get(i));
                    }
                    for (int i = start; i < list.size(); i++) {


                        gridPane.add(list.get(i), x, y);

                        GridPane.setMargin(list.get(i), new Insets(2, 2, 2, 2));
                        if (x == 2) {
                            x = 0;
                            y++;
                        } else {
                            x++;
                        }
                    }

                    this.page++;
                }

            }
        } else {
            if (this.page > 1) {
                for (int i = 0; i < gridPane.getChildren().size(); ) {
                    gridPane.getChildren().remove(gridPane.getChildren().get(i));
                }
                if (this.page == 2) {
                    start = 0;
                } else {
                    start = (this.page - 2) * size;
                }
                for (int i = start; i < (start + size); i++) {
                    gridPane.add(list.get(i), x, y);
                    GridPane.setMargin(list.get(i), new Insets(2, 2, 2, 2));
                    if (x == 2) {
                        x = 0;
                        y++;
                    } else {
                        x++;
                    }
                }
                this.page--;
            }

        }
    }



    private void saveButtonAction(ActionEvent e) {
        try {
            Item item = new Item();
            try {
                item.setName(name.getValue());
            } catch (InputValidationException eName) {
                FXMLLoader loader = fxmLoaderService.getLoader(wrongInputForm.getURL());
                Parent parent = loader.load();
                ItemWrongInputController itemWrongInputController = loader.getController();
                itemWrongInputController.setErrorLabelText("name");
                Scene scene = new Scene(parent);
                scene.getStylesheets().add(css.getURL().toExternalForm());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.showAndWait();
            }
            try {
                item.setPrice(Double.parseDouble(price.getValue()));
            } catch (Exception ePrice) {
                FXMLLoader loader = fxmLoaderService.getLoader(wrongInputForm.getURL());
                Parent parent = loader.load();
                ItemWrongInputController itemWrongInputController = loader.getController();
                itemWrongInputController.setErrorLabelText("price");
                Scene scene = new Scene(parent);
                scene.getStylesheets().add(css.getURL().toExternalForm());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.showAndWait();
            }
            try {
                item.setType(type.get());
            } catch (InputValidationException eType) {
                FXMLLoader loader = fxmLoaderService.getLoader(wrongInputForm.getURL());
                Parent parent = loader.load();
                ItemWrongInputController itemWrongInputController = loader.getController();
                itemWrongInputController.setErrorLabelText("type");
                Scene scene = new Scene(parent);
                scene.getStylesheets().add(css.getURL().toExternalForm());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.showAndWait();

            }
            try{
                item.setStation(station.get());
            }catch(InputValidationException eStation){
                FXMLLoader loader = fxmLoaderService.getLoader(wrongInputForm.getURL());
                Parent parent = loader.load();
                ItemWrongInputController itemWrongInputController = loader.getController();
                itemWrongInputController.setErrorLabelText("station");
                Scene scene = new Scene(parent);
                scene.getStylesheets().add(css.getURL().toExternalForm());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.showAndWait();
            }
            try {
                item.setSection(section.getValue());
            } catch (InputValidationException eSection) {
                FXMLLoader loader = fxmLoaderService.getLoader(wrongInputForm.getURL());
                Parent parent = loader.load();
                ItemWrongInputController itemWrongInputController = loader.getController();
                itemWrongInputController.setErrorLabelText("section");
                Scene scene = new Scene(parent);
                scene.getStylesheets().add(css.getURL().toExternalForm());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.showAndWait();

            }
            try {
                int avail;
                if (availabilityButton.getStyleClass().get(0).equals("Availability86")) {
                    avail = 86;
                } else if (availabilityButton.getStyleClass().get(0).equals("Availability85")) {
                    avail = 85;
                } else {
                    avail = 68;
                }
                item.setAvailability(avail);
            } catch (InputValidationException eAvailability) {
                FXMLLoader loader = fxmLoaderService.getLoader(wrongInputForm.getURL());
                Parent parent = loader.load();
                ItemWrongInputController itemWrongInputController = loader.getController();
                itemWrongInputController.setErrorLabelText("availability");
                Scene scene = new Scene(parent);
                scene.getStylesheets().add(css.getURL().toExternalForm());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.showAndWait();

            }

            for (Ingredient ingr : ingredientsList) {
                item.addIngredient(ingr, 1);
            }
            if ((item.getRecipe().isEmpty())) {
                FXMLLoader loader = fxmLoaderService.getLoader(wrongInputForm.getURL());
                Parent parent = loader.load();
                ItemWrongInputController itemWrongInputController = loader.getController();
                itemWrongInputController.setErrorLabelText("recipe");
                Scene scene = new Scene(parent);
                scene.getStylesheets().add(css.getURL().toExternalForm());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.showAndWait();
            }
            if ((item.getName() != null) && (item.getPrice() != null) && (item.getType() != null) && (item.getSection() != null) && (!item.getRecipe().isEmpty())&& (item.getStation()!=null)) {
                FXMLLoader loader = fxmLoaderService.getLoader(confirmationSaveForm.getURL());
                Parent parent = loader.load();
                ConfirmationItemSaveController confirmationItemSaveController = loader.getController();
                confirmationItemSaveController.setup(item);
                Scene scene = new Scene(parent);
                scene.getStylesheets().add(css.getURL().toExternalForm());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.showAndWait();
                if (confirmationItemSaveController.isSaved()) {
                    loader = fxmLoaderService.getLoader(menuScreenForm.getURL());
                    parent = loader.load();
                    scene = new Scene(parent);
                    scene.getStylesheets().add(css.getURL().toExternalForm());
                    Stage stage1 = (Stage) ((Node) e.getSource()).getScene().getWindow();
                    stage1.setScene(scene);
                    stage1.show();
                }
            }
        } catch (IOException ioE) {
            ioE.printStackTrace();
        }
    }



}

    /*
    public class FormattedTableCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

        public FormattedTableCellFactory() {
        }

        @Override
        public TableCell<S, T> call(TableColumn<S, T> p) {
            TableCell<S, T> cell = new TableCell<S, T>() {
                @Override
                protected void updateItem(Object item, boolean empty) {
                    // CSS Styles
                    int available=68;
                    int almostGone=85;
                    int unavailable=86;

                    String cssStyle = "";
                    Ingredient ingredient = null;
                    if( getTableRow() != null ) {
                        ingredient = (Ingredient) getTableRow().getItem();

                        if (ingredient.getAvailability() == unavailable) {
                            setStyle("-fx-background-color:red");
                        } else if (ingredient.getAvailability() == almostGone) {
                            setStyle("-fx-background-color:orange");
                        }
                    }

                    //Set the CSS style on the cell and set the cell's text.
                    getStyleClass().add(cssStyle);
                    if( item != null ) {
                        setText( item.toString()  );
                    } else {
                        setText( "" );
                    }
                }
            };
            return cell;
        }
    }


}


*/