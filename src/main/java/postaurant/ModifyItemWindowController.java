package postaurant;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import jdk.internal.util.xml.impl.Input;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import postaurant.exception.InputValidationException;
import postaurant.model.Ingredient;
import postaurant.model.Item;
import postaurant.service.ButtonCreationService;
import postaurant.service.MenuService;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ModifyItemWindowController {


    private int ingredientPage;
    private Item item;

    private List<Ingredient> addOns=new ArrayList<>();
    private List<Button> currentButtonListSelected;
    private Map<String,List<Button>> ingredientButtonsAZ;
    private List<Ingredient> originalRecipeList;
    private ObservableList<Ingredient> recipeList;

    private final ButtonCreationService buttonCreationService;
    private final MenuService menuService;

    public ModifyItemWindowController(ButtonCreationService buttonCreationService, MenuService menuService) {
        this.buttonCreationService = buttonCreationService;
        this.menuService=menuService;
    }

    @FXML
    private Label itemNameLabel;
    @FXML
    private TableView<Ingredient> recipeTableView;
    @FXML
    private TableColumn<Ingredient, String> recipeColumn;
    @FXML
    private GridPane ingredientsGridPane;
    @FXML
    private Button removeButton;
    @FXML
    private Button upButtonIngredients;
    @FXML
    private Button downButtonIngredients;
    @FXML
    private Button saveButton;
    @FXML
    private Button exitButton;


    public void initialize() {

        downButtonIngredients.setOnAction(event -> {
            setIngredientButtons(ingredientsGridPane,8,true, currentButtonListSelected);

        });
        upButtonIngredients.setOnAction(event -> {
            setIngredientButtons(ingredientsGridPane,8,false, currentButtonListSelected);

        });
        removeButton.setOnAction(event -> {
            ObservableList<Ingredient> selected = recipeTableView.getSelectionModel().getSelectedItems();
            addOns.remove(selected.get(0));
            selected.forEach(recipeList::remove);

        });

        saveButton.setOnAction(event -> {
            if(wasChanged()) {
                //INSERT INTO items(item_name, item_price, item_type, item_section, item_availability)
                Item item=new Item();
                try {
                    System.out.println(this.item.getName());

                    System.out.println(this.item.getName().matches("CUSTOM_(.*)"));
                    if(!this.item.getName().matches("CUSTOM_(.*)")){
                        item.setName("CUSTOM_" + this.item.getName());
                    }else{
                        item.setName(this.item.getName());
                    }
                    Double total=0.00;
                    for(int i=0;i<addOns.size();i++){
                        total=total+addOns.get(i).getPrice();
                    }
                    item.setPrice(this.item.getPrice()+total);
                    item.setType(this.item.getType());
                    item.setSection(this.item.getSection());
                    item.setAvailability(this.item.getAvailability());
                    for(Ingredient ingredient:recipeList){
                        item.addIngredient(ingredient,1);
                    }
                    this.item=menuService.saveNewCustomItem(item);
                    ((Button) event.getSource()).getScene().getWindow().hide();
                }catch (InputValidationException e){
                    e.printStackTrace();
                }



            }else{
                (((Button) event.getSource()).getScene().getWindow()).hide();
            }

        });
        exitButton.setOnAction(event -> {
            (((Button) event.getSource()).getScene().getWindow()).hide();

        });
    }


    public void setup(Item item) {
        ingredientPage = 0;
        this.item=item;
        recipeList = FXCollections.observableArrayList();
        for (Map.Entry<Ingredient, Integer> entry : item.getRecipe().entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                recipeList.add(entry.getKey());
            }
        }
        //List<String> list2 = list1.stream().collect(Collectors.toList());
        originalRecipeList= new ArrayList<>(recipeList);

        //FXCollections.copy(recipeList,originalRecipeList);
        itemNameLabel.textProperty().bind(item.getNameProperty());
        ingredientButtonsAZ = buttonCreationService.createIngredientButtonsForSections(3);
        for (Map.Entry<String, List<Button>> entry : ingredientButtonsAZ.entrySet()) {
            for (Button b : entry.getValue()) {
                b.setOnAction(event -> {
                    Ingredient ingredient = menuService.getIngredientById(Long.parseLong(b.getText().substring(0, b.getText().indexOf("\n"))));
                    recipeList.add(ingredient);
                    addOns.add(ingredient);
                    Comparator<Ingredient> ingredientNameComparator = Comparator.comparing(Ingredient::getName);
                    FXCollections.sort(recipeList, ingredientNameComparator);
                });
            }
            currentButtonListSelected=ingredientButtonsAZ.get("A-C");
            setIngredientButtons(ingredientsGridPane,8,true,currentButtonListSelected);

            //setIngredientButtons(ingredientsGridPane,8,true,ingredientButtons);
            recipeColumn.setCellFactory(param -> new AvailabilityFormatCell());
            recipeColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            recipeTableView.setItems(recipeList);
            recipeTableView.getSortOrder().add(recipeColumn);
            recipeTableView.setPlaceholder(new Label("Add ingredients"));

        }
    }

    private class AvailabilityFormatCell extends TableCell<Ingredient, String> {
        public AvailabilityFormatCell() {
        }

        @Override
        protected void updateItem(String string, boolean empty) {
            super.updateItem(string, empty);
            setText(string == null ? "" : string);
            if (string != null) {
                for (Ingredient i : recipeList) {
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

    public Item getItem(){
        return this.item;
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

    private void setIngredientButtons(GridPane gridPane, Integer size, boolean forward, List<Button> list) {
        int start;
        int x = 0;
        int y = 0;
        if (forward) {

            if (this.ingredientPage == 0) {
                start = 0;
            } else {
                start = this.ingredientPage * size;
            }
            //if all buttons don't fit in gridPane
            if (isNextPage(this.ingredientPage, list, size)) {
                for (int i = 0; i < gridPane.getChildren().size(); ) {
                    gridPane.getChildren().remove(gridPane.getChildren().get(i));
                }
                if (start == 0) {
                    for (int i = start; i < size; i++) {
                        gridPane.add(list.get(i), x, y);
                        GridPane.setMargin(list.get(i), new Insets(2, 2, 10, 2));
                        if (x == 1) {
                            x = 0;
                            y++;
                        } else {
                            x++;
                        }

                    }
                    this.ingredientPage++;
                } else {
                    if (isNextPage(this.ingredientPage + 1, list, size)) {
                        for (int i = start; i < start + size; i++) {
                            gridPane.add(list.get(i), x, y);
                            GridPane.setMargin(list.get(i), new Insets(2, 2, 2, 2));
                            if (x == 1) {
                                x = 0;
                                y++;
                            } else {
                                x++;
                            }
                        }
                        this.ingredientPage++;
                    } else {
                        for (int i = start; i < list.size(); i++) {
                            gridPane.add(list.get(i), x, y);
                            GridPane.setMargin(list.get(i), new Insets(2, 2, 2, 2));
                            if (x == 1) {
                                x = 0;
                                y++;
                            } else {
                                x++;
                            }
                        }
                        this.ingredientPage++;
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
                        if (x == 1) {
                            x = 0;
                            y++;
                        } else {
                            x++;
                        }
                    }

                    this.ingredientPage++;
                }

            }
        } else {
            if (this.ingredientPage > 1) {
                for (int i = 0; i < gridPane.getChildren().size(); ) {
                    gridPane.getChildren().remove(gridPane.getChildren().get(i));
                }
                if (this.ingredientPage == 2) {
                    start = 0;
                } else {
                    start = (this.ingredientPage - 2) * size;
                }
                for (int i = start; i < (start + size); i++) {
                    gridPane.add(list.get(i), x, y);
                    GridPane.setMargin(list.get(i), new Insets(2, 2, 2, 2));
                    if (x == 1) {
                        x = 0;
                        y++;
                    } else {
                        x++;
                    }
                }
                this.ingredientPage--;
            }

        }
    }
    public boolean wasChanged(){
        boolean changed=false;
        for(Ingredient i:recipeList){
            if(!originalRecipeList.contains(i)){
                changed=true;
            }
        }
        if(recipeList.size()!=originalRecipeList.size()){
            changed=true;
        }
        return changed;
    }

    @FXML
    private void handleAZButtons(ActionEvent event){
        Button button=(Button)event.getSource();
        String section=button.getText();
        currentButtonListSelected=ingredientButtonsAZ.get(section);
        ingredientPage=0;
        setIngredientButtons(ingredientsGridPane,8,true, currentButtonListSelected);
    }




}



