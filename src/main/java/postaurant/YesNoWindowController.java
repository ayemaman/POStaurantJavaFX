package postaurant;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

@Component
public class YesNoWindowController {
    boolean answer=false;
    @FXML
    private Label itemLabel;
    @FXML
    private Label questionLabel;
    @FXML
    private Button yesButton;
    @FXML
    private Button noButton;

    public void initialize(){
        yesButton.setOnAction(e->{
            answer=true;
            ((Node) e.getSource()).getScene().getWindow().hide();

        });

        noButton.setOnAction(e->{
            answer=false;
            ((Node) e.getSource()).getScene().getWindow().hide();
        });
    }

    public void setLabelTexts(String question, String item){
        questionLabel.setText(question);
        itemLabel.setText(item);
    }
    public boolean getAnswer(){
        return answer;
    }


}
