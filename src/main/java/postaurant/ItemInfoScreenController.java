package postaurant;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import postaurant.context.FXMLoaderService;
import postaurant.model.Ingredient;
import postaurant.model.Item;
import postaurant.service.ButtonCreationService;

import java.util.ArrayList;
import java.util.Map;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ItemInfoScreenController {

    private int page;
    private ArrayList<Button> ingredientButtonList;
    private Item item;
    private ObservableList<Ingredient> ingredientsList;

    private final ButtonCreationService buttonCreationService;
    private final FXMLoaderService fxmLoaderService;
    @FXML
    private GridPane keyboardGrid;
    @FXML
    private GridPane ingredientGrid;
    @FXML
    private TableView<Ingredient> ingredientTable;

    @FXML
    private TableColumn<Ingredient, String> recipeColumn;


    public ItemInfoScreenController(ButtonCreationService buttonCreationService, FXMLoaderService fxmLoaderService){
        this.buttonCreationService=buttonCreationService;
        this.fxmLoaderService=fxmLoaderService;
    }

    public void setIngredientButtonList(ArrayList<Button> ingredientButtonList) {
        this.ingredientButtonList = ingredientButtonList;
    }

    public void initialize(){

    }

    public void setup(Item item){
        this.page=0;
        setIngredientButtons(ingredientGrid, true);
        setItem(item);
        recipeColumn.setMinWidth(200);
        recipeColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ingredientTable.setItems(ingredientsList);
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


    public void onKeyboardPress(ActionEvent event) {

    }

    private boolean isNextPage() {
        try {
            System.out.println(ingredientButtonList.get((this.page * 12)));
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }



    public void setIngredientButtons(GridPane gridPane, boolean forward){
        int start;
        int x=0;
        int y=0;
        if(forward){
            if (this.page==0){
                start = 0;
            }else{
                start=this.page*12;
            }
            if(isNextPage()){
                for(int i=0; i<gridPane.getChildren().size();){
                    gridPane.getChildren().remove(gridPane.getChildren().get(i));
                }
            }
            if(ingredientButtonList.size()-start >11){
                for(int i=start; i< (start + 12);i++){
                    gridPane.add(ingredientButtonList.get(i),x, y);
                    gridPane.setMargin(ingredientButtonList.get(i), new Insets(2,2,2,2));
                    if(x==2) {
                        x = 0;
                        y++;
                    }else{
                        x++;
                    }
                }
            }else{
                for(int i = start; i<ingredientButtonList.size();i++){
                    gridPane.add(ingredientButtonList.get(i), x, y);
                    gridPane.setMargin(ingredientButtonList.get(i), new Insets(2,2,2,2));
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
            if(this.page>1){
                if(this.page==2){
                    start = 0;
                }
                else{
                    start = (this.page-2)*12;
                }
                for(int i=start; i<(start+12);i++){
                    gridPane.add(ingredientButtonList.get(i),x,y);
                    gridPane.setMargin(ingredientButtonList.get(i), new Insets(2,2,2,2));
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





}
