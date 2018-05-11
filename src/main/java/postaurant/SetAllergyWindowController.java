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


@Component
public class SetAllergyWindowController {


    @FXML
    private Button saveButton;
    @FXML
    private VBox vBox;
    public void initialize(){

        saveButton.setOnAction(e->{
            ArrayList<String> allergyList=new ArrayList<>();
            int i=1;
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
           for(String s:allergyList){
               System.out.println(s);
           }
        });
    }
    @FXML
    private void handleAllergyButtons(ActionEvent event){
        ToggleButton toggleButton=(ToggleButton)event.getSource();
            if(toggleButton.isSelected()) {
                toggleButton.setStyle("-fx-background-color: red");
            }
            else{
                toggleButton.setStyle("-fx-background-color:green");
            }
    }


}
