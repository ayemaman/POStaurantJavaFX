package postaurant;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.stereotype.Component;

@Component
public class SentItemErrorController {
    @FXML
    private Button okButton;

    public void initialize(){
        okButton.setOnAction(event -> {
            ((Button)event.getSource()).getScene().getWindow().hide();
        });
    }

}


