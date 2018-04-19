package postaurant;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import postaurant.model.Ingredient;
import postaurant.service.MenuService;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConfirmationIngredientSaveController {

    private final MenuService menuService;

    @FXML
    private Label itemLabel;
    @FXML
    private Button yesButton;
    @FXML
    private Button noButton;

    private Ingredient ingredient;

    private boolean saved;


    public ConfirmationIngredientSaveController(MenuService menuService) {
        this.menuService = menuService;
    }
    public void initialize(){
        saved=false;
        yesButton.setOnAction(event -> {
            //todo
            ingredient=menuService.saveNewIngredient(ingredient);
        });

        noButton.setOnAction(event -> {
            saved=false;
            ((Node) event.getSource()).getScene().getWindow().hide();
        });

    }

    public void setup(Ingredient ingredient){
        this.ingredient=ingredient;
        itemLabel.setText(ingredient.toFullString());
    }

    public boolean isSaved(){
        return saved;
    }
}
