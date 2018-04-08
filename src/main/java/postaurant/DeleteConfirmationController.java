package postaurant;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import postaurant.model.User;
import postaurant.service.UserService;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DeleteConfirmationController {
    private boolean deleted=false;
    private User user;

    private UserService userService;
    private static Label confirmationLabel=new Label("Successfully deleted");

    @FXML
    private Label userLabel;

    @FXML
    private Button yesButton;
    @FXML
    private Button noButton;
    @FXML
    private HBox bottomConfirmationBox;
    @FXML
    private VBox topConfirmationBox;

    private DeleteConfirmationController(UserService userService){
        this.userService=userService;
        confirmationLabel.setTextFill(Paint.valueOf("#ffbf00"));
    }

    public void setUser(User user){
        this.user=user;
        userLabel.setText(user.toString());

    }
    public boolean wasDeleted(){
        return deleted;
    }

    public void initialize(){
        yesButton.setOnAction(event -> {
            userService.blockUser(user);
            for( int i=0;i<bottomConfirmationBox.getChildren().size();){
                bottomConfirmationBox.getChildren().remove(bottomConfirmationBox.getChildren().get(i));
            }
            topConfirmationBox.getChildren().remove(topConfirmationBox.getChildren().get(0));
            System.out.println("adding label");
            bottomConfirmationBox.getChildren().add(confirmationLabel);
            Button button=new Button("OK");
            button.setOnAction(e->button.getScene().getWindow().hide());
            bottomConfirmationBox.getChildren().add(button);
           deleted=true;
        });

        noButton.setOnAction(event -> {
            deleted=false;
            noButton.getScene().getWindow().hide();
        });

    }

}
