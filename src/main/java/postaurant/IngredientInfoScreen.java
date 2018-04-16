package postaurant;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import postaurant.context.AllergenList;



@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IngredientInfoScreen {

    private ObservableList<String> allergenList;

    @FXML
    private TextField nameField;
    @FXML
    private TextField amountField;
    @FXML
    private ChoiceBox<String> allergyChoice;
    @FXML
    private Button availabilityButton;
    @FXML
    private Button goBackButton;
    @FXML
    private Button deleteButton;


    public void initialize(){
        this.allergenList= AllergenList.getAllergens();
        allergyChoice.setItems(allergenList);

    }



}
