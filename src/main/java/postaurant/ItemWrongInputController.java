package postaurant;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ItemWrongInputController {

    @FXML
    private Label errorLabel;
    @FXML
    private Button reenter;

    public void initialize(){
            reenter.setOnAction(e-> reenter.getScene().getWindow().hide());


    }
    public void setErrorLabelText(String error){
        errorLabel.setText("Wrong "+error);
    }
}
