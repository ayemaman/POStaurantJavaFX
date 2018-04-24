package postaurant.service;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import postaurant.OrderWindowController;
import postaurant.context.FXMLoaderService;
import postaurant.context.KeyboardList;
import postaurant.model.Ingredient;
import postaurant.model.Item;
import postaurant.model.Order;
import postaurant.model.User;

import java.io.IOException;
import java.util.*;

@Component
public class ButtonCreationService {

    @Value("/FXML/OrderWindow.fxml")
    private Resource orderWindow;

    @Value("/img/upArrow.png")
    private Resource upArrow;
    private final UserService userService;
    private final KeyboardList keyboardList;
    private final MenuService menuService;
    private final FXMLoaderService fxmLoaderService;

    public ButtonCreationService(UserService userService, KeyboardList keyboardList, MenuService menuService, FXMLoaderService fxmLoaderService) {
        this.userService = userService;
        this.keyboardList = keyboardList;
        this.menuService = menuService;
        this.fxmLoaderService = fxmLoaderService;
    }

    public ArrayList<Button> createTableButtons(User user) {
        ArrayList<Button> tableButtonList = new ArrayList<>();
        try {
            List<Order> tables = userService.getUserOrders(user);
            if (tables != null) {
                for (Order o : tables) {
                    String text = "" + o.getTableNo();
                    Button button = new Button(text);
                    button.setPrefHeight(70.0);
                    button.setPrefWidth(95.0);
                    button.setMnemonicParsing(false);
                    button.setOnAction(e->{
                        try {
                            FXMLLoader loader = fxmLoaderService.getLoader(orderWindow.getURL());
                            Parent parent = loader.load();
                            OrderWindowController orderWindowController = loader.getController();
                            orderWindowController.setOrderId(o.getId());
                            Scene scene = new Scene(parent);
                            scene.getStylesheets().add("POStaurant.css");
                            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                            stage.setScene(scene);
                            stage.show();
                        }catch (Exception eX){
                            eX.printStackTrace();
                        }
                    });
                    tableButtonList.add(button);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tableButtonList;
    }

    public ArrayList<Button> createKeyboardButtons(boolean lowercase, double buttonWidth, double spacebarWidth, double height) {

        ArrayList<String> list = (keyboardList.getQwerty());
        ArrayList<Button> keyboardButtonList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Button button = new Button();
            button.setText(list.get(i));
            button.setMinWidth(buttonWidth);
            button.setMnemonicParsing(false);
            button.setId("numberKey");
            keyboardButtonList.add(button);
        }
        if (lowercase) {
            for (int i = 10; i < 40; i++) {
                Button button = new Button();
                button.setMinWidth(buttonWidth);
                button.setMnemonicParsing(false);
                if (list.get(i).equals("")) {
                    button.setId("capsKey");
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
                } else if (list.get(i).equals("<--")) {
                    button.setId("backspaceKey");
                    button.setText(list.get(i));
                } else {
                    button.setId("lowerCharKey");
                    button.setText(list.get(i));
                }
                keyboardButtonList.add(button);
            }
        } else {
            for (int i = 40; i < list.size(); i++) {
                Button button = new Button(list.get(i));
                button.setMinWidth(buttonWidth);
                button.setMnemonicParsing(false);
                if (list.get(i).equals("")) {
                    button.setId("capsKey");
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
                } else if (list.get(i).equals("<--")) {
                    button.setId("backspaceKey");
                    button.setText(list.get(i));
                } else {
                    button.setId("upperCharKey");
                    button.setText(list.get(i));
                }
                keyboardButtonList.add(button);
            }
        }
        Button delete = new Button("DELETE");
        delete.setMinHeight(height);
        delete.setMinWidth(buttonWidth);
        delete.setId("deleteKey");
        keyboardButtonList.add(delete);

        Button spacebar = new Button();
        spacebar.setMinWidth(spacebarWidth);
        spacebar.setMinHeight(height);
        spacebar.setMnemonicParsing(false);
        spacebar.setId("spacebarKey");
        keyboardButtonList.add(spacebar);

        return keyboardButtonList;
    }

    public ArrayList<Button> createOrderSectionButtons(boolean food){
        ArrayList<Button> sectionButtons =new ArrayList<>();
        Map<String, List<Item>> sectionsWithItems;
        if(food) {
            sectionsWithItems= menuService.getFoodSectionsWithItems();
        }else{
            sectionsWithItems = menuService.getDrinkSectionsWithItems();
        }
        for(Map.Entry<String, List<Item>> entry:sectionsWithItems.entrySet()) {
            Button button = new Button(entry.getKey());
            button.setMinHeight(50);
            button.setMinWidth(100);
            button.setMnemonicParsing(false);
            button.setId("sectionButton");
            sectionButtons.add(button);
        }
        return sectionButtons;
    }



    public ArrayList<Tab> createSectionTabs() {
        ArrayList<Tab> tabs = new ArrayList<>();
        Map<String, List<Item>> sectionsWithItems = menuService.getSectionsWithItems();
        for (Map.Entry<String, List<Item>> entry : sectionsWithItems.entrySet()) {
            GridPane gridPane = new GridPane();
            gridPane.setAlignment(Pos.CENTER);
            for (int i = 0; i < 4; i++) {
                ColumnConstraints colConst = new ColumnConstraints();
                colConst.setPercentWidth(428.0 / 4);
                gridPane.getColumnConstraints().add(colConst);
            }
            for (int i = 0; i < 4; i++) {
                RowConstraints rowConst = new RowConstraints();
                rowConst.setPercentHeight(500 / 4);
                gridPane.getRowConstraints().add(rowConst);
            }
            AnchorPane anchorPane = new AnchorPane();
            anchorPane.setPrefWidth(428);
            anchorPane.setPrefHeight(500);
            anchorPane.getChildren().add(gridPane);
            anchorPane.setLeftAnchor(gridPane, 0.0);
            anchorPane.setRightAnchor(gridPane, 0.0);
            anchorPane.setTopAnchor(gridPane, 0.0);
            anchorPane.setBottomAnchor(gridPane, 0.0);

            Tab sectionTab = new Tab(entry.getKey());
            sectionTab.setContent(anchorPane);
            tabs.add(sectionTab);
        }
        return tabs;
    }

    public ArrayList<Button> createItemButtonsForSection(String section, boolean large) {
        ArrayList<Button> buttons = new ArrayList<>();
        Map<String, List<Item>> sectionsWithItems = menuService.getSectionsWithItems();
        List<Item> items = sectionsWithItems.get(section);
        for (Item i : items) {
            Button button;
            if(large) {
                button = new Button(i.getId() + "\n" + i.getName());
                button.setMinHeight(120);
                button.setMinWidth(100);
                button.setMnemonicParsing(false);

            }
            else{
                button = new Button(i.getId()+"\n" +i.getName());
                button.setMinHeight(59.0);
                button.setMinWidth(92.0);
                button.setMnemonicParsing(false);

            }
            //checking ingredients availability
            int avail=68;
            for(Map.Entry<Ingredient,Integer> entry:i.getRecipe().entrySet()){
                if(entry.getKey().getAvailability()==86){
                    avail=86;
                    break;
                }else if(entry.getKey().getAvailability()==85){
                    avail=85;
                }
            }
            if(avail==86){
                button.setStyle("-fx-background-color:red");
                if(!large){
                    button.setStyle("-fx-background-color:red; " +
                                    "-fx-font-size:10px;");
                    button.setId("86");
                }
            }else if(avail==85){
                button.setStyle("-fx-background-color:orange");
                if(!large){
                    button.setStyle("-fx-background-color:orange; " +
                                    "-fx-font-size:10px;");
                    button.setId("85");
                }
            }else {
                if (!large) {
                    button.setStyle("-fx-font-size:10px;");
                    button.setId("68");
                }
            }

            //checking items availability
            if((i.getAvailability()==86)) {
                button.setStyle("-fx-background-color:red");
                if(!large){
                    button.setStyle("-fx-background-color:red; " +
                                    "-fx-font-size:10px;");
                    button.setId("86");
                }
            }else if(i.getAvailability()==85) {
                button.setStyle("-fx-background-color:orange");
                if(!large){
                    button.setStyle("-fx-background-color:orange; " +
                                    "-fx-font-size:10px;");
                    button.setId("85");
                }
            }else{
                if (!large) {
                    button.setStyle("-fx-font-size:10px;");
                    button.setId("68");
                }
            }

            buttons.add(button);
        }
        return buttons;
    }

    //if(i.getName().substring(0,1).equals("A") || i.getName().substring(0,1).equals("B") || i.getName().substring(0,1).equals("C") )

    public Map<String, List<Button>> createIngredientButtonsForSections(int version){
        Map<String, List<Ingredient>> map=menuService.getAZSectionsWithIngredients();
        Map<String, List<Button>> map2=new HashMap<>();
        for(Map.Entry<String, List<Ingredient>> entry: map.entrySet()){
            map2.put(entry.getKey(),new ArrayList<>());
        }

        for(Map.Entry<String, List<Ingredient>> entry: map.entrySet()){
            for(Ingredient i:entry.getValue()){
                Button button = new Button();
                if (version==1) {
                    button.setText(+i.getId() + "\n" + i.getName() + "\namount(g.):\n " + i.getAmount());
                    button.setMinWidth(73);
                    button.setMinHeight(82.5);
                    button.setStyle("-fx-font-size:9px");
                } else if(version==2){
                    button.setText(i.getId() + "\n" + i.getName() + "\namount: " + i.getAmount()+"g\nprice: "+i.getPrice()+"£");
                    button.setMinWidth(100);
                    button.setMinHeight(120);
                    button.setStyle("-fx-font-size:10px");
                }else if(version==3){
                    button.setText(i.getId() + "\n" + i.getName() +"\nAmount: "+ i.getAmount()+"g\nPrice: "+i.getPrice()+"£");
                    button.setMinWidth(140);
                    button.setMinHeight(110);
                }

                if(i.getAvailability()==86){
                    button.setStyle("-fx-background-color:red");
                }else if(i.getAvailability()==85){
                    button.setStyle("-fx-background-color:orange");
                }
                button.setMnemonicParsing(false);
                button.setId("IngredientButton");
                map2.get(entry.getKey()).add(button);
            }

        }
        return map2;
    }

    public ArrayList<Button> createIngredientButtons(int version) {
        ArrayList<Button> buttons = new ArrayList<>();
        List<Ingredient> list = menuService.getAllIngredients();

        for (Ingredient i : list) {
            Button button = new Button();
            if (version==1) {
                button.setText(+i.getId() + "\n" + i.getName() + "\namount(g.):\n " + i.getAmount());
                button.setMinWidth(73);
                button.setMinHeight(82.5);
                button.setStyle("-fx-font-size:9px");
            } else if(version==2){
                button.setText(i.getId() + "\n" + i.getName() + "\namount: " + i.getAmount()+"g\nprice: "+i.getPrice()+"£");
                button.setMinWidth(100);
                button.setMinHeight(120);
                button.setStyle("-fx-font-size:10px");
            }else if(version==3){
                button.setText(i.getId() + "\n" + i.getName() +"\nAmount: "+ i.getAmount()+"g\nPrice: "+i.getPrice()+"£");
                button.setMinWidth(140);
                button.setMinHeight(110);
            }

            if(i.getAvailability()==86){
                button.setStyle("-fx-background-color:red");
            }else if(i.getAvailability()==85){
                button.setStyle("-fx-background-color:orange");
            }
            button.setMnemonicParsing(false);
            button.setId("IngredientButton");
            buttons.add(button);
        }
        return buttons;
    }


}

