package postaurant;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class WrongInputWindowController {

    @FXML private Button reenter;
    public void initialize(){
        reenter.setOnAction(e-> reenter.getScene().getWindow().hide());

    }
}
