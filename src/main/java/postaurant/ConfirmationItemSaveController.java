package postaurant;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import postaurant.model.Item;
import postaurant.service.MenuService;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConfirmationItemSaveController {

    private boolean saved;

    public boolean isSaved() {
        return saved;
    }

    private Item item;

    private final MenuService menuService;
    private static Label confirmationLabel=new Label();

    @FXML
    private Label itemLabel;
    @FXML
    private Button yesButton;
    @FXML
    private Button noButton;
    @FXML
    private HBox bottomConfirmationBox;
    @FXML
    private VBox topConfirmationBox;




    public ConfirmationItemSaveController(MenuService menuService){
        this.menuService=menuService;
    }
    public void initialize(){
        saved=false;
        yesButton.setOnAction(e->{
            item=menuService.saveNewItem(item);
            if(item!=null) {
                for (int i = 0; i < bottomConfirmationBox.getChildren().size(); ) {
                    bottomConfirmationBox.getChildren().remove(bottomConfirmationBox.getChildren().get(i));
                }
                for(Node n: topConfirmationBox.getChildren()){
                    System.out.println(n);
                }
                int times=0;
                while(times<3){
                    topConfirmationBox.getChildren().remove(topConfirmationBox.getChildren().get(0));
                    times++;
                }
                for(Node n: topConfirmationBox.getChildren()){
                    System.out.println(n);
                }
                confirmationLabel.setText("Successfully saved");
                bottomConfirmationBox.getChildren().add(confirmationLabel);
                Button button = new Button("OK");
                button.setOnAction(event -> button.getScene().getWindow().hide());
                itemLabel.setText("ITEM ID: " + item.getId());
                bottomConfirmationBox.getChildren().add(button);
                saved = true;
            }else{
                for (int i = 0; i < bottomConfirmationBox.getChildren().size(); ) {
                    bottomConfirmationBox.getChildren().remove(bottomConfirmationBox.getChildren().get(i));
                }
                topConfirmationBox.getChildren().remove(topConfirmationBox.getChildren().get(0));
                topConfirmationBox.getChildren().remove(topConfirmationBox.getChildren().get(0));
                confirmationLabel.setText("Something went wrong. Please, try again.");
                topConfirmationBox.getChildren().add(confirmationLabel);
                Button button = new Button("OK");
                button.setOnAction(event -> button.getScene().getWindow().hide());
                bottomConfirmationBox.getChildren().add(button);
                saved =false;
            }
        });

        noButton.setOnAction(e->{
            saved=false;
            ((Node) e.getSource()).getScene().getWindow().hide();
        });
    }

    public void setup(Item item){
        this.item=item;
        itemLabel.setText(item.toString());
    }

}
