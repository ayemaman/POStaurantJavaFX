package postaurant;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.stereotype.Component;

@Component
public class TableAlreadyExistsController {



    @FXML
    private Button reenter;

    public void initialize(){
        reenter.setOnAction(e-> reenter.getScene().getWindow().hide());
    }

}
