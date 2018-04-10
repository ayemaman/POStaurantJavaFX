package postaurant;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import postaurant.model.Item;
import postaurant.service.MenuService;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ConfirmationItemSaveController {

    private boolean saved=false;
    private Item item;

    private final MenuService menuService;
    private static Label confirmationLabel=new Label("Successfully saved");

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
        yesButton.setOnAction(e->{
            //item=menuService.saveItem(item);
            for(int i=0;i<bottomConfirmationBox.getChildren().size();){
                bottomConfirmationBox.getChildren().remove(bottomConfirmationBox.getChildren().get(i));
            }
            topConfirmationBox.getChildren().remove(topConfirmationBox.getChildren().get(0));
            bottomConfirmationBox.getChildren().add(confirmationLabel);
            Button button=new Button("OK");

            button.setOnAction(event->button.getScene().getWindow().hide());
            itemLabel.setText("NEW ITEM ID: "+item.getId());
            bottomConfirmationBox.getChildren().add(button);
            saved=true;

            saved=true;
        });

        noButton.setOnAction(e->{

            saved=false;
        });
    }

    public void setup(Item item){
        this.item=item;
        itemLabel.setText(item.toString());
    }

}
