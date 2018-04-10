package postaurant.service;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import postaurant.context.FXMLoaderService;
import postaurant.context.KeyboardList;
import postaurant.model.Ingredient;
import postaurant.model.Item;
import postaurant.model.Order;
import postaurant.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ButtonCreationService {


    @Value("/img/upArrow.png")
    private Resource upArrow;
    private final UserService userService;
    private final KeyboardList keyboardList;
    private final MenuService menuService;
    private final FXMLoaderService fxmLoaderService;

    public ButtonCreationService(UserService userService, KeyboardList keyboardList, MenuService menuService, FXMLoaderService fxmLoaderService) {
        this.userService = userService;
        this.keyboardList=keyboardList;
        this.menuService=menuService;
        this.fxmLoaderService=fxmLoaderService;
    }

    public ArrayList<Button> createTableButtons(User user) {
        ArrayList<Button> tableButtonList = new ArrayList<>();
        try {
            List<Order> tables = userService.getUserOrders(user);
            for (Order o : tables) {
                String text = "" + o.getTableNo();
                Button button = new Button(text);
                button.setPrefHeight(70.0);
                button.setPrefWidth(95.0);
                button.setMnemonicParsing(false);
                tableButtonList.add(button);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tableButtonList;
    }

    public ArrayList<Button> createKeyboardButtons(boolean lowercase, double buttonWidth, double spacebarWidth, double height) {

        ArrayList<String> list= (keyboardList.getQwerty());
        ArrayList<Button> keyboardButtonList = new ArrayList<>();
        for(int i=0;i<10;i++) {
            Button button = new Button();
            button.setText(list.get(i));
            button.setMinWidth(buttonWidth);
            button.setMnemonicParsing(false);
            keyboardButtonList.add(button);
        }
        if (lowercase) {
            for (int i = 10; i < 40; i++) {
                Button button = new Button();
                button.setMinWidth(buttonWidth);
                button.setMnemonicParsing(false);
                if (list.get(i).equals("")) {
                    ImageView caps = new ImageView();
                    caps.setFitHeight(20);
                    caps.setFitWidth(30);
                    try {
                        Image image = new Image(upArrow.getURL().toString());
                        caps.setImage(image);
                        button.setGraphic(caps);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    button.setText(list.get(i));
                }
                keyboardButtonList.add(button);
            }
        }
        else {
            for (int i = 40; i < list.size(); i++) {
                Button button = new Button(list.get(i));
                button.setMinWidth(buttonWidth);
                button.setMnemonicParsing(false);
                if (list.get(i).equals("")) {
                    ImageView caps = new ImageView();
                    caps.setFitHeight(20);
                    caps.setFitWidth(30);
                    try {
                        Image image = new Image(upArrow.getURL().toString());
                        caps.setImage(image);
                        button.setGraphic(caps);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    button.setText(list.get(i));
                }
                keyboardButtonList.add(button);
            }
        }
        Button delete=new Button("DELETE");
        delete.setMinHeight(height);
        delete.setMinWidth(buttonWidth);
        keyboardButtonList.add(delete);

        Button spacebar=new Button();
        spacebar.setMinWidth(spacebarWidth);
        spacebar.setMinHeight(height);
        spacebar.setMnemonicParsing(false);
        keyboardButtonList.add(spacebar);

        return keyboardButtonList;
    }

    public ArrayList<Tab> createSectionTabs(){
        ArrayList<Tab> tabs=new ArrayList<>();
        Map<String, List<Item>> sectionsWithItems= menuService.getSectionsWithItems();
        for (Map.Entry<String, List<Item> > entry : sectionsWithItems.entrySet()){

            GridPane gridPane=new GridPane();
            gridPane.setAlignment(Pos.CENTER);
            for (int i = 0; i < 4; i++) {
                ColumnConstraints colConst = new ColumnConstraints();
                colConst.setPercentWidth(428.0 / 4);
                gridPane.getColumnConstraints().add(colConst);
            }
            for (int i = 0; i < 4; i++) {
                RowConstraints rowConst = new RowConstraints();
                rowConst.setPercentHeight(500/4);
                gridPane.getRowConstraints().add(rowConst);
            }
            AnchorPane anchorPane=new AnchorPane();
            anchorPane.setPrefWidth(428);
            anchorPane.setPrefHeight(500);
            anchorPane.getChildren().add(gridPane);
            anchorPane.setLeftAnchor(gridPane,0.0);
            anchorPane.setRightAnchor(gridPane,0.0);
            anchorPane.setTopAnchor(gridPane,0.0);
            anchorPane.setBottomAnchor(gridPane,0.0);

            Tab sectionTab=new Tab(entry.getKey());
            sectionTab.setContent(anchorPane);
            tabs.add(sectionTab);
        }
        return tabs;
    }

    public ArrayList<Button> createItemButtonsForSection(String section){
        ArrayList<Button> buttons=new ArrayList<>();
        Map<String, List<Item>> sectionsWithItems= menuService.getSectionsWithItems();
        List<Item> items=sectionsWithItems.get(section);
        for(Item i:items){
            Button button=new Button(i.getId()+"\n"+i.getName());
            button.setMinHeight(120);
            button.setMinWidth(100);
            button.setMnemonicParsing(false);
            button.setId("itemButton");
            buttons.add(button);
        }
        return buttons;
    }

    public ArrayList<Button> createIngridientButtons(){
        ArrayList<Button> buttons=new ArrayList<>();
        List<Ingredient> list=menuService.getAllIngredients();
        for(Ingredient i:list){
            Button button=new Button(i.getId() + "\n" + i.getName()+ "\n" + i.getAmount());
            button.setMinWidth(73);
            button.setMinHeight(82.5);
            button.setStyle("-fx-font-size:10px");
            button.setMnemonicParsing(false);
            button.setId("IngredientButton");
            buttons.add(button);
        }
        return buttons;
    }


}
