package postaurant;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import postaurant.model.User;
import postaurant.service.UserService;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DeleteConfirmationController {
    private User user;

    private UserService userService;

    @FXML
    private Label userLabel;

    @FXML
    private Button yesButton;
    @FXML
    private Button noButton;

    private DeleteConfirmationController(UserService userService){
        this.userService=userService;
    }

    public void setUser(User user){
        this.user=user;
        userLabel.setText(user.toString());

    }
    public void initialize(){
        yesButton.setOnAction(event -> {
            //TODO
        });

        noButton.setOnAction(event -> noButton.getScene().getWindow().hide());

    }

}
