package postaurant;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AlreadySentController {

    @FXML
    private Button okButton;

    public void initialize(){
        okButton.setOnAction(event -> {
            ((Button)event.getSource()).getScene().getWindow().hide();
        });
    }
}
