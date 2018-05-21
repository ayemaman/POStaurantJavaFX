package postaurant;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;


@Component
public class ConfirmationExitWindowController {

        private boolean exit;

        @FXML
        private Label confirmationLabel;
        @FXML
        private Button yesButton;
        @FXML
        private Button noButton;



        private ConfirmationExitWindowController(){
        }

        public void initialize(){
            yesButton.setOnAction(e -> {
                exit=true;
                ((Node)e.getSource()).getScene().getWindow().hide();
            });

            noButton.setOnAction(e -> {
                exit=false;
                ((Node)e.getSource()).getScene().getWindow().hide();
            });

        }
        public boolean confirmed(){
            return exit;
        }
        public void setConfirmationLabel(String text){
            confirmationLabel.setText(text);
        }
    }
