package postaurant;
import postaurant.context.FXMLoaderService;
import postaurant.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import postaurant.service.UserService;

import java.net.URL;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LogInController {
    private StringProperty buffer = new SimpleStringProperty("");

    private User user;

    private final UserService userService;
    private final FXMLoaderService fxmlLoaderService;

    @Value("/FXML/noID.fxml")
    private Resource noID;

    @FXML
    private TextField userID;
    @FXML
    private GridPane gridPane;
    @FXML
    private Button buttonLOGIN;


    public LogInController(UserService userService, FXMLoaderService fxmlLoader) {
        this.userService = userService;
        this.fxmlLoaderService=fxmlLoader;
    }

    public void initialize() {
        userID.textProperty().bind(buffer);
        for (Node n : gridPane.getChildren()) {
            Button b = (Button) n;
            b.setOnAction(event -> {
                try {
                    buttonAction(b);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        buttonLOGIN.setOnAction(this::onLoginButtonClicked);
    }

    private void buttonAction(Button button) {
        if (button.getText().equals("DELETE")) {
            buffer.setValue("");
        } else if (button.getText().equals("EXIT")) {
            button.getScene().getWindow().hide();
        } else {
            if (buffer.getValue().length() < 5) {
                String string = button.getText();
                buffer.set(buffer.getValue() + string);
            }
        }

    }
    public User getUser(){
        return user;
    }

    private void onLoginButtonClicked(ActionEvent e) {
        user = userService.getUser(buffer.getValue());
        if (user == null) {
            try {
                URL url = noID.getURL();
                Parent root = fxmlLoaderService.getLoader(url).load();
                Scene scene = new Scene(root);
                scene.getStylesheets().add("mainScreen.css");
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(scene);
                stage.showAndWait();
                buffer.setValue("");
            } catch (Exception e2) {
                e2.printStackTrace();

            }

        } else {
            buttonLOGIN.getScene().getWindow().hide();
        }
    }
}
