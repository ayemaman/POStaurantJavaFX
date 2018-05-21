package postaurant;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class SetAllergyWindowController {
    private List<String> allergyList;


    @FXML
    private Button saveButton;
    @FXML
    private VBox vBox;
    public void initialize(){


        saveButton.setOnAction(e->{
            allergyList=new ArrayList<>();
           for(Node n: vBox.getChildren()){
               if(n instanceof HBox){
                   for(Node n2: ((HBox) n).getChildren()){
                       if(n2 instanceof ToggleButton){
                           if(((ToggleButton) n2).isSelected()){
                               allergyList.add(((ToggleButton) n2).getText());
                           }
                       }
                   }
               }
           }
            ((Node)e.getSource()).getScene().getWindow().hide();
        });
    }

    public List<String> getAllergyList() {
        return allergyList;
    }

    public void setAllergyList(List<String> allergyList) {
        if (allergyList != null) {
            this.allergyList = allergyList;
            for (Node n : vBox.getChildren()) {
                if (n instanceof HBox) {
                    for (Node n2 : ((HBox) n).getChildren()) {
                        if (n2 instanceof ToggleButton) {
                            if (allergyList.contains(((ToggleButton) n2).getText())) {
                                ((ToggleButton) n2).setSelected(true);
                            }
                        }
                    }
                }
            }
        }
    }
}
