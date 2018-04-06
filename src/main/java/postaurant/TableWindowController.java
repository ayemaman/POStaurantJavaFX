package postaurant;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import postaurant.service.OrderService;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TableWindowController {
    private ArrayList<Button> itemButtonList;
    private int page;

    private final OrderService orderService;

    @FXML
    private Label labelTableNo;
    @FXML
    private GridPane itemGrid;

    public TableWindowController(OrderService orderService) {
        this.orderService = orderService;
    }

    public void setTableNo(int i){
        String text=""+i;
        labelTableNo.setText(text);
    }

    public void handleTypeButton(ActionEvent event){
        Button button=(Button) event.getSource();
        String section=button.getText();
        for (int i=0;i<(itemGrid.getChildren()).size();){
            itemGrid.getChildren().remove(itemGrid.getChildren().get(i));
        }
        setItemButtons(section);

    }

    public void setItemButtons(String type){
        createItemButtons(type);
        int x=0;
        int y=0;
        if(itemButtonList.size()<22){
            for( int i=0; i < itemButtonList.size();i++){
                itemGrid.add(itemButtonList.get(i),x,y);
                GridPane.setMargin(itemButtonList.get(i),new Insets(2,2,2,2));
                if(x==3) {
                    x = 0;
                    y++;
                }else{
                    x++;
                }

            }
        }
        else{
            for(int i=0;i<22;i++){
                itemGrid.add(itemButtonList.get(i),x,y);
                GridPane.setMargin(itemButtonList.get(i),new Insets(2,2,2,2));
                if(x==3) {
                    x = 0;
                    y++;
                }else{
                    x++;
                }

            }
        }
    }

    public void createItemButtons(String section){
        try {
            itemButtonList = new ArrayList<>();
            List<String> items = orderService.getItemsForSection(section);
            for (String s : items) {
                Button button = new Button(s);
                button.setPrefHeight(59.0);
                button.setPrefWidth(92.0);
                button.setMnemonicParsing(false);
                itemButtonList.add(button);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
