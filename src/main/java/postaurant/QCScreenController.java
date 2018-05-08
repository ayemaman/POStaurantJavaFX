package postaurant;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import postaurant.context.QCNode;
import postaurant.service.ButtonCreationService;
import postaurant.service.MenuService;

import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class QCScreenController {

    @FXML
    private GridPane gridPane;
    @FXML
    private HBox hBOX;



    private final MenuService menuService;
    private final ButtonCreationService buttonCreationService;


    public QCScreenController(MenuService menuService, ButtonCreationService buttonCreationService){
        this.menuService=menuService;
        this.buttonCreationService=buttonCreationService;
    }

    public void initialize(){
        List<VBox> list= buttonCreationService.createQCNodes();
        int column=0;
        int row=0;
        for(VBox v:list){
            gridPane.add(v,column, row);
            if(column>3){
                column=0;
                row++;
            }else {
                column++;
            }
        }


    }
}
