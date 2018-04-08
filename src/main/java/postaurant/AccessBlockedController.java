package postaurant;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.stereotype.Component;

@Component
public class AccessBlockedController {
    @FXML
    private Button okButton;


    public void initialize(){
        okButton.setOnAction(e->okButton.getScene().getWindow().hide());
    }

}
