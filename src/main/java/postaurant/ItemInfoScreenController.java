package postaurant;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import postaurant.context.FXMLoaderService;
import postaurant.service.ButtonCreationService;

import java.util.ArrayList;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ItemInfoScreenController {

    private int page;



    private ArrayList<Button> ingredientButtonList;

    private final ButtonCreationService buttonCreationService;
    private final FXMLoaderService fxmLoaderService;
    @FXML
    private GridPane keyboardGrid;
    @FXML
    private GridPane ingredientGrid;

    public ItemInfoScreenController(ButtonCreationService buttonCreationService, FXMLoaderService fxmLoaderService){
        this.buttonCreationService=buttonCreationService;
        this.fxmLoaderService=fxmLoaderService;
    }

    public void setIngredientButtonList(ArrayList<Button> ingredientButtonList) {
        this.ingredientButtonList = ingredientButtonList;
    }

    public void initialize(){

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

    public void setup(){
        this.page=0;
        setIngredinetButtons(ingredientGrid, true);
    }

    public void setIngredinetButtons(GridPane gridPane, boolean forward){
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
