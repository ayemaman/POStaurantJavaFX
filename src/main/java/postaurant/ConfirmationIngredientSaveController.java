package postaurant;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import postaurant.model.Ingredient;
import postaurant.service.MenuService;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConfirmationIngredientSaveController {

    private final MenuService menuService;
    private static Label confirmationLabel=new Label();

    @FXML
    private Label ingredientLabel;
    @FXML
    private Button yesButton;
    @FXML
    private Button noButton;
    @FXML
    private HBox bottomConfirmationBox;
    @FXML
    private VBox topConfirmationBox;

    private Ingredient ingredient;

    private boolean saved;


    public ConfirmationIngredientSaveController(MenuService menuService) {
        this.menuService = menuService;
    }
    public void initialize(){
        saved=false;
        yesButton.setOnAction(event -> {
            ingredient=menuService.saveNewIngredient(ingredient);
            if(ingredient!=null) {
                for (int i = 0; i < bottomConfirmationBox.getChildren().size(); ) {
                    bottomConfirmationBox.getChildren().remove(bottomConfirmationBox.getChildren().get(i));
                }
                for(Node n: topConfirmationBox.getChildren()){
                    System.out.println(n);
                }
                int times=0;
                while(times<2){
                    topConfirmationBox.getChildren().remove(topConfirmationBox.getChildren().get(0));
                    times++;
                }
                System.out.println("HERE");
                for(Node n: topConfirmationBox.getChildren()){
                    System.out.println(n);
                }
                confirmationLabel.setText("Successfully saved");
                bottomConfirmationBox.getChildren().add(confirmationLabel);
                Button button = new Button("OK");
                button.setOnAction(e -> button.getScene().getWindow().hide());
                ingredientLabel.setText("INGREDIENT ID: " + ingredient.getId());
                bottomConfirmationBox.getChildren().add(button);
                saved = true;
            }else{
                for (int i = 0; i < bottomConfirmationBox.getChildren().size(); ) {
                    bottomConfirmationBox.getChildren().remove(bottomConfirmationBox.getChildren().get(i));
                }
                topConfirmationBox.getChildren().remove(topConfirmationBox.getChildren().get(0));
                topConfirmationBox.getChildren().remove(topConfirmationBox.getChildren().get(0));
                confirmationLabel.setText("Something went wrong. Please, try again.");
                topConfirmationBox.getChildren().add(confirmationLabel);
                Button button = new Button("OK");
                button.setOnAction(e -> button.getScene().getWindow().hide());
                bottomConfirmationBox.getChildren().add(button);
                saved =false;
            }
        });

        noButton.setOnAction(event -> {
            saved=false;
            ((Node) event.getSource()).getScene().getWindow().hide();
        });

    }

    public void setup(Ingredient ingredient){
        this.ingredient=ingredient;
        ingredientLabel.setText(ingredient.toFullString());
    }

    public boolean isSaved(){
        return saved;
    }
}
