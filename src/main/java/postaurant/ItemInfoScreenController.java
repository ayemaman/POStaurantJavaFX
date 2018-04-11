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
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import postaurant.context.FXMLoaderService;
import postaurant.exception.InputValidationException;
import postaurant.model.Ingredient;
import postaurant.model.Item;
import postaurant.service.ButtonCreationService;
import postaurant.service.MenuService;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ItemInfoScreenController {

    private int page;
    private ArrayList<Button> ingredientButtonList;
    private Item item;
    private ObservableList<Ingredient> ingredientsList;

    private boolean lowercase=true;
    private StringProperty name = new SimpleStringProperty("");
    private StringProperty price = new SimpleStringProperty("");
    private StringProperty type = new SimpleStringProperty("");
    private StringProperty section = new SimpleStringProperty("");



    private final ButtonCreationService buttonCreationService;
    private final MenuService menuService;
    private final FXMLoaderService fxmLoaderService;



    @Value("/FXML/ItemWrongInputWindow.fxml")
    private Resource wrongInputForm;
    @Value("/FXML/MenuScreen.fxml")
    private Resource menuScreenForm;
    @Value("/FXML/ConfirmationItemSave.fxml")
    private Resource confirmationSaveForm;


    private TextField currentTextField;
    @FXML
    private TextField  nameField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField typeField;
    @FXML
    private TextField sectionField;

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


    public ItemInfoScreenController(ButtonCreationService buttonCreationService, FXMLoaderService fxmLoaderService, MenuService menuService){
        this.buttonCreationService=buttonCreationService;
        this.fxmLoaderService=fxmLoaderService;
        this.menuService=menuService;
    }

    public void setIngredientButtonList(ArrayList<Button> ingredientButtonList) {
        this.ingredientButtonList = ingredientButtonList;
    }

    public void initialize() {
        nameField.textProperty().bind(name);
        priceField.textProperty().bind(price);
        typeField.textProperty().bind(type);
        sectionField.textProperty().bind(section);

        removeButton.setOnAction(e -> {
            ObservableList<Ingredient> selected = ingredientTable.getSelectionModel().getSelectedItems();
            selected.forEach(ingredientsList::remove);
        });

        availabilityButton.setOnAction(e->{
           if(availabilityButton.getStyleClass().get(0).equals("AvailabilityONButton")){
               availabilityButton.getStyleClass().clear();
               availabilityButton.getStyleClass().add("AvailabilityOFFButton");
           }else{
               availabilityButton.getStyleClass().clear();
               availabilityButton.getStyleClass().add("AvailabilityONButton");
           }
        });


        exitButton.setOnAction(e->{
            try {
                FXMLLoader loader = fxmLoaderService.getLoader(menuScreenForm.getURL());
                Parent parent=loader.load();
                Scene scene=new Scene(parent);
                Stage stage=(Stage)((Node) e.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }catch (IOException ioE){
                ioE.printStackTrace();

            }
        });

        saveButton.setOnAction(this::saveButtonAction);

        currentTextField=nameField;
        nameField.setOnMouseClicked(e->this.currentTextField=nameField);
        priceField.setOnMouseClicked(e->this.currentTextField=priceField);
        typeField.setOnMouseClicked(e->this.currentTextField=typeField);
        sectionField.setOnMouseClicked(e->this.currentTextField=sectionField);

    }

    public void setup(Item item){
        name.setValue(item.getName());
        price.setValue(""+item.getPrice());
        type.setValue(item.getType());
        section.setValue(item.getSection());
        setKeyboard(lowercase);
        this.page=0;
        addOnActionToIngredientButtons();
        setIngredientButtons(this.page,this.ingredientGrid, 12,true,ingredientButtonList);
        setItem(item);
        if(item.getAvailability()==86){
            availabilityButton.getStyleClass().clear();
            availabilityButton.getStyleClass().add("AvailabilityOFFButton");
        }else{
            availabilityButton.getStyleClass().clear();
            availabilityButton.getStyleClass().add("AvailabilityONButton");

        }
        recipeColumn.setMinWidth(200);
        recipeColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ingredientTable.setItems(ingredientsList);
        ingredientTable.getSortOrder().add(recipeColumn);
        ingredientTable.setPlaceholder(new Label("Add ingredients"));

    }



    public void setItem(Item item) {
        this.item = item;
        ingredientsList = FXCollections.observableArrayList();
        for (Map.Entry<Ingredient, Integer> entry : item.getRecipe().entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                ingredientsList.add(entry.getKey());
            }
        }
    }



    private boolean isNextPage() {
        try {
            ingredientButtonList.get((this.page * 12));
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    public void addOnActionToIngredientButtons(){
        for(Button b:ingredientButtonList){
            b.setOnAction(e->{
                Ingredient ingredient=menuService.getIngredient(b.getText().substring(0,b.getText().indexOf("\n")));
                ingredientsList.add(ingredient);
                Comparator<Ingredient> ingredientNameComparator = Comparator.comparing(Ingredient::getName);
                FXCollections.sort(ingredientsList,ingredientNameComparator);
                for(int i=0;i<ingredientsList.size();i++){
                    System.out.println(ingredientsList.get(i));
                }
            });
        }
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

        ArrayList<Button> buttonList=buttonCreationService.createKeyboardButtons(lowercase,78,700,30);

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
        spacebarHBox.setMargin(buttonList.get(40),new Insets(0,11,0,0));
    }

    public void onKeyboardPress(ActionEvent event) {
        StringProperty stringProperty = null;
        if (currentTextField.equals(nameField)) {
            stringProperty = name;
        } else if (currentTextField.equals(priceField)) {
            stringProperty = price;
        } else if(currentTextField.equals(typeField)){
            stringProperty=type;
        } else if (currentTextField.equals(sectionField)) {
            stringProperty=section;
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


    public void setIngredientButtons(Integer page,GridPane gridPane, Integer size, boolean forward, List<Button> list){
        int start;
        int x=0;
        int y=0;
        if(forward){
            if (page==0){
                start = 0;
            }else{
                start=page*size;
            }
            if(isNextPage()){
                for(int i=0; i<gridPane.getChildren().size();){
                    gridPane.getChildren().remove(gridPane.getChildren().get(i));
                }
            }
            if(list.size()-start >size-1){
                for(int i=start; i< (start + size);i++){
                    gridPane.add(list.get(i),x, y);
                    gridPane.setMargin(list.get(i), new Insets(2,2,2,2));
                    if(x==2) {
                        x = 0;
                        y++;
                    }else{
                        x++;
                    }
                }
            }else{
                for(int i = start; i<list.size();i++){
                    gridPane.add(list.get(i), x, y);
                    gridPane.setMargin(list.get(i), new Insets(2,2,2,2));
                    if(x==2){
                        x=0;
                        y++;
                    }else{
                        x++;
                    }
                }
            }
            page++;
        }else{
            if(page>1){
                if(page==2){
                    start = 0;
                }
                else{
                    start = (page-2)*size;
                }
                for(int i=start; i<(start+size);i++){
                    gridPane.add(list.get(i),x,y);
                    gridPane.setMargin(list.get(i), new Insets(2,2,2,2));
                    if(x==2){
                        x=0;
                        y++;
                    }else{
                        x++;
                    }
                }
            }
            page--;
        }

    }


    private void saveButtonAction(ActionEvent e) {
        try {
            int avail;
            if (availabilityButton.getId().equals("AvailabilityONButton")) {
                avail = 86;
            } else {
                avail = 68;
            }

            Item item = new Item();
            try {
                item.setName(name.getValue());
            } catch (InputValidationException eName) {
                FXMLLoader loader = fxmLoaderService.getLoader(wrongInputForm.getURL());
                Parent parent = loader.load();
                ItemWrongInputController itemWrongInputController = loader.getController();
                itemWrongInputController.setErrorLabelText("name");
                Scene scene = new Scene(parent);
                Stage stage = new Stage();
                stage.setScene(scene);
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
                Stage stage = new Stage();
                stage.setScene(scene);
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
                Stage stage = new Stage();
                stage.setScene(scene);
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
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.showAndWait();


            }
            try {
                item.setAvailability(avail);
            } catch (InputValidationException eAvailability) {
                FXMLLoader loader = fxmLoaderService.getLoader(wrongInputForm.getURL());
                Parent parent = loader.load();
                ItemWrongInputController itemWrongInputController = loader.getController();
                itemWrongInputController.setErrorLabelText("availability");
                Scene scene = new Scene(parent);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.showAndWait();

            }
            for (Ingredient ingr : ingredientsList) {
                item.addIngredient(ingr, 1);
            }

            if ((item.getName() != null) && (item.getPrice() != null) && (item.getType() != null) && (item.getSection() != null)) {
                item.setId();
                FXMLLoader loader = fxmLoaderService.getLoader(confirmationSaveForm.getURL());
                Parent parent = loader.load();
                ConfirmationItemSaveController confirmationItemSaveController = loader.getController();
                confirmationItemSaveController.setup(item);
                Scene scene = new Scene(parent);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.showAndWait();
                if (confirmationItemSaveController.isSaved()) {
                    loader = fxmLoaderService.getLoader(menuScreenForm.getURL());
                    parent = loader.load();
                    scene = new Scene(parent);
                    Stage stage1 = (Stage)((Node) e.getSource()).getScene().getWindow();
                    stage1.setScene(scene);
                    stage1.show();
                }
            }
        } catch (IOException ioE) {
            ioE.printStackTrace();
        }
    }
}
