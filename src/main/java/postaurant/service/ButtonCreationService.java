/**
 * Class, that is used to dynamically create Nodes for this application
 * @see javafx.scene.control.Button
 * @see postaurant.context.QCBox
 */
package postaurant.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import postaurant.DubScreenController;
import postaurant.OrderWindowController;
import postaurant.context.FXMLoaderService;
import postaurant.context.KeyboardList;
import postaurant.context.OrderInfo;
import postaurant.context.QCBox;
import postaurant.model.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class ButtonCreationService {

    @Value("/FXML/OrderWindow.fxml")
    private Resource orderWindow;
    @Value("/FXML/DubScreen.fxml")
    private Resource dubScreen;

    @Value("/img/upArrow.png")
    private Resource upArrow;
    private final UserService userService;
    private final OrderService orderService;
    private final KeyboardList keyboardList;
    private final MenuService menuService;
    private final FXMLoaderService fxmLoaderService;
    private final TimeService timeService;

    public ButtonCreationService(UserService userService, OrderService orderService, KeyboardList keyboardList, MenuService menuService, FXMLoaderService fxmLoaderService, TimeService timeService) {
        this.userService = userService;
        this.orderService = orderService;
        this.keyboardList = keyboardList;
        this.menuService = menuService;
        this.fxmLoaderService = fxmLoaderService;
        this.timeService=timeService;
    }

    /**
     * Method that creates a Button for every active User in Database
     * @return ArrayList<Button> that holds all Buttons representing users
     */
    public ArrayList<Button> createUserButtons() {
        try {
            ArrayList<Button> userButtons=new ArrayList<>();
            List<User> users = userService.getAllActiveUsers();
            for (User u: users) {
                String text =""+u.getUserID()+System.lineSeparator()+u.getFirstName().substring(0,1)+u.getLastName()+System.lineSeparator()+u.getPosition();
                Button button = new Button(text);
                button.setPrefHeight(70.0);
                button.setPrefWidth(95.0);
                button.setMnemonicParsing(false);
                if(u.getPosition().equals("DUBDUB")) {
                    button.getStyleClass().add("DubButton");
                }
                else if (u.getPosition().equals("MANAGER")) {
                    button.getStyleClass().add("ManagerButton");
                }
                else if(u.getPosition().equals("KITCHEN")){
                    button.getStyleClass().add("KitchenButton");
                }
                else if(u.getPosition().equals("FOODRUNNER")){
                    button.getStyleClass().add("FoodRunnerButton");
                }
                else if(u.getPosition().equals("DRINKRUNNER")){
                    button.getStyleClass().add("DrinkRunnerButton");
                }
                else if(u.getPosition().equals("BAR")){
                    button.getStyleClass().add("BarButton");
                }
                userButtons.add(button);
            }
            return userButtons;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method, that is used to figure out, if list holds enough items to fill next page.
     * @param page current page number
     * @param list that is being checked
     * @param size of items that can fit on one page;
     * @return boolean (True if next page is possible, False if not)
     */
    public boolean isNextPage(int page, List list, int size) {
        try {
            if (page > 0) {
                list.get((page * size));
            } else {
                list.get((size));
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    /**
     * Method, that creates Buttons from all tables that are transferable for given user
     * @param user
     * @return ArrayList<Button> that holds Button for each table that can be transferred
     */
    public ArrayList<Button> createTransferTables(User user){
        ArrayList<Button> tableButtonList=new ArrayList<>();
        try {
            List<Order> tables = orderService.getTransferableOrders(user);
            if (tables != null) {
                for (Order o : tables) {
                    String text = "" + o.getTableNo();
                    Button button = new Button(text);
                    button.setPrefHeight(70.0);
                    button.setPrefWidth(95.0);
                    button.setMnemonicParsing(false);
                    if(ChronoUnit.HOURS.between(o.getLastTimeChecked(),LocalDateTime.now())>2){
                        button.setStyle("-fx-background-color:red");
                    }
                    if(ChronoUnit.HOURS.between(o.getLastTimeChecked(),LocalDateTime.now())==1) {
                        if ((60 - o.getLastTimeChecked().getMinute()) + LocalDateTime.now().getMinute() > 10) {
                            button.setStyle("-fx-background-color:red");
                        }else if(60-o.getLastTimeChecked().getMinute()+LocalDateTime.now().getMinute()>5){
                            button.setStyle("-fx-background-color:yellow");
                        }
                    }else if(ChronoUnit.HOURS.between(o.getLastTimeChecked(),LocalDateTime.now())==0){
                            if (ChronoUnit.MINUTES.between(o.getLastTimeChecked(), LocalDateTime.now()) > 10) {
                                button.setStyle("-fx-background-color:red");
                            }else if(ChronoUnit.MINUTES.between(o.getLastTimeChecked(), LocalDateTime.now())>5){
                                button.setStyle("-fx-background-color:yellow");
                            }
                    }

                    button.setOnAction(e -> {
                        try {
                            orderService.transferTable(o.getId(),user);
                            FXMLLoader loader = fxmLoaderService.getLoader(dubScreen.getURL());
                            Parent parent = loader.load();
                            DubScreenController dubScreenController = loader.getController();
                            dubScreenController.setUser(user);
                            Scene scene = new Scene(parent);
                            scene.getStylesheets().add("POStaurant.css");
                            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                            stage.setScene(scene);
                            stage.show();
                        } catch (Exception eX) {
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

    /**
     * Method, that creates Buttons from all tables that are served by given user;
     * @param user
     * @return ArrayList<Button> that holds Button for each table that given user is in charge of;
     */
    public ArrayList<Button> createTableButtons(User user) {
        ArrayList<Button> tableButtonList = new ArrayList<>();
        try {
            List<Order> tables;
            if(user.getPosition().equals("MANAGER")){
                tables=orderService.getAllOpenOrders();
            }else {
                tables =orderService.getUserOrders(user);
            }
            if (tables != null) {
                for (Order o : tables) {
                    String text = "" + o.getTableNo();
                    Button button = new Button(text);
                    button.setPrefHeight(70.0);
                    button.setPrefWidth(95.0);
                    button.setMnemonicParsing(false);
                    if(ChronoUnit.HOURS.between(o.getLastTimeChecked(),LocalDateTime.now())>1){
                        button.setStyle("-fx-background-color:red");
                    }
                    if(ChronoUnit.HOURS.between(o.getLastTimeChecked(),LocalDateTime.now())==1) {
                        if ((60 - o.getLastTimeChecked().getMinute()) + LocalDateTime.now().getMinute() > 10) {
                            button.setStyle("-fx-background-color:red");
                        }else if(60-o.getLastTimeChecked().getMinute()+LocalDateTime.now().getMinute()>5){
                            button.setStyle("-fx-background-color:yellow");
                        }
                    }else if(ChronoUnit.HOURS.between(o.getLastTimeChecked(),LocalDateTime.now())==0){
                        if (ChronoUnit.MINUTES.between(o.getLastTimeChecked(), LocalDateTime.now()) > 10) {
                            button.setStyle("-fx-background-color:red");
                        }else if(ChronoUnit.MINUTES.between(o.getLastTimeChecked(), LocalDateTime.now())>5){
                            button.setStyle("-fx-background-color:yellow");
                        }
                    }
                    button.setOnAction(e -> {
                        try {
                            FXMLLoader loader = fxmLoaderService.getLoader(orderWindow.getURL());
                            Parent parent = loader.load();
                            OrderWindowController orderWindowController = loader.getController();
                            orderWindowController.setOrderId(o.getId());
                            orderWindowController.setUser(user);
                            if (user.getPosition().equals("MANAGER")) {
                                orderWindowController.setLabels(o.getTableNo(), o.getId(),"MANAGER", o.getTimeOpened());
                            } else{
                                orderWindowController.setLabels(o.getTableNo(), o.getId(), user.getFirstName(), o.getTimeOpened());
                            }
                            Scene scene = new Scene(parent);
                            scene.getStylesheets().add("POStaurant.css");
                            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                            stage.setScene(scene);
                            stage.show();
                        } catch (Exception eX) {
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

    /**
     * Method, that creates KeyBoard Buttons
     * @param lowercase that specifies if it's for lowercase, or uppercase
     * @param buttonWidth width of buttons
     * @param spacebarWidth width of spaceBar
     * @param height height of buttons
     * @return ArrayList<Button>
     */
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

    public ArrayList<ToggleButton> createOrderSectionButtons(boolean food) {
        ArrayList<ToggleButton> sectionButtons = new ArrayList<>();
        Map<String, List<Item>> sectionsWithItems;
        if (food) {
            sectionsWithItems = menuService.getFoodSectionsWithItems();
        } else {
            sectionsWithItems = menuService.getDrinkSectionsWithItems();
        }
        for (Map.Entry<String, List<Item>> entry : sectionsWithItems.entrySet()) {
            ToggleButton button = new ToggleButton(entry.getKey());
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

    /**
     * Method that creates item Buttons for sections that are used in Order Window and don't hold any of specified allergy.
     * @param section name of section
     * @param large boolean to specify button size
     * @param allergyList restricted allergy
     * @return ArrayList<Button>
     */
    public ArrayList<Button> createItemButtonsForSection(String section, boolean large, ArrayList<String> allergyList) {
        ArrayList<Button> buttons = new ArrayList<>();
        Map<String, List<Item>> sectionsWithItems = menuService.getSectionsWithItems();
        List<Item> items = sectionsWithItems.get(section);
        if(allergyList!=null){
            Iterator<Item> iterator=items.iterator();
            while(iterator.hasNext()){
                Item item=iterator.next();
                for(Map.Entry<Ingredient,Integer> entry:item.getRecipe().entrySet()){
                    String allergy=entry.getKey().getAllergy();
                    if(allergyList.contains(allergy)){
                        iterator.remove();
                    }
                }
            }

        }
        for (Item i : items) {
            Button button;
            if (large) {
                button = new Button(i.getId() + "\n" + i.getName());
                button.setMinHeight(120);
                button.setMinWidth(100);
                button.setMnemonicParsing(false);

            } else {
                button = new Button(i.getId() + "\n" + i.getName());
                button.setMinHeight(59.0);
                button.setMinWidth(92.0);
                button.setMnemonicParsing(false);

            }
            //checking ingredients availability
            int avail = 68;
            for (Map.Entry<Ingredient, Integer> entry : i.getRecipe().entrySet()) {
                if (entry.getKey().getAvailability() == 86) {
                    avail = 86;
                    break;
                } else if (entry.getKey().getAvailability() == 85) {
                    avail = 85;
                }
            }
            if (avail == 86) {
                button.setStyle("-fx-background-color:red");
                if (!large) {
                    button.setStyle("-fx-background-color:red; " +
                            "-fx-font-size:10px;");
                    button.setId("86");
                }
            } else if (avail == 85) {
                button.setStyle("-fx-background-color:orange");
                if (!large) {
                    button.setStyle("-fx-background-color:orange; " +
                            "-fx-font-size:10px;");
                    button.setId("85");
                }
            } else {
                if (!large) {
                    button.setStyle("-fx-font-size:10px;");
                    button.setId("68");
                }
            }

            //checking items availability
            if ((i.getAvailability() == 86)) {
                button.setStyle("-fx-background-color:red");
                if (!large) {
                    button.setStyle("-fx-background-color:red; " +
                            "-fx-font-size:10px;");
                    button.setId("86");
                }
            } else if (i.getAvailability() == 85) {
                button.setStyle("-fx-background-color:orange");
                if (!large) {
                    button.setStyle("-fx-background-color:orange; " +
                            "-fx-font-size:10px;");
                    button.setId("85");
                }
            } else {
                if (!large) {
                    button.setStyle("-fx-font-size:10px;");
                    button.setId("68");
                }
            }

            buttons.add(button);
        }
        return buttons;
    }

    /**
     * Method that creates Ingredient buttons that are grouped by their first letter of their name;
     * @return Map<String,List<Button>> that holds Ingredient Buttons grouped by first letters of their name;
     */
    public Map<String, List<Button>> createIngredientButtonsForSections() {
        Map<String, List<Ingredient>> map = menuService.getAZSectionsWithIngredients();
        Map<String, List<Button>> map2 = new HashMap<>();
        for (Map.Entry<String, List<Ingredient>> entry : map.entrySet()) {
            map2.put(entry.getKey(), new ArrayList<>());
        }

        for (Map.Entry<String, List<Ingredient>> entry : map.entrySet()) {
            for (Ingredient i : entry.getValue()) {
                Button button = new Button();
                button.setText(i.getId() + "\n" + i.getName() + "\nAmount: " + i.getAmount() + "g\nPrice: " + i.getPrice() + "£");
                button.setMinWidth(140);
                button.setMinHeight(110);
                if (i.getAvailability() == 86) {
                    button.setStyle("-fx-background-color:red");
                } else if (i.getAvailability() == 85) {
                    button.setStyle("-fx-background-color:orange");
                }
                button.setMnemonicParsing(false);
                button.setId("IngredientButton");
                map2.get(entry.getKey()).add(button);
            }

        }
        return map2;
    }

    /**
     * Method, that creates Ingredient Buttons.
     * @param version int, you can specify button size: 1=small, 2=large;
     * @return ArrayList<Button>
     */
    public ArrayList<Button> createIngredientButtons(int version) {
        ArrayList<Button> buttons = new ArrayList<>();
        List<Ingredient> list = menuService.getAllIngredients();

        for (Ingredient i : list) {
            Button button = new Button();
            if (version == 1) {
                button.setText(+i.getId() + "\n" + i.getName() + "\namount(g.):\n " + i.getAmount());
                button.setMinWidth(73);
                button.setMinHeight(82.5);
                button.setStyle("-fx-font-size:9px");
            } else if (version == 2) {
                button.setText(i.getId() + "\n" + i.getName() + "\namount: " + i.getAmount() + "g\nprice: " + i.getPrice() + "£");
                button.setMinWidth(100);
                button.setMinHeight(120);
                button.setStyle("-fx-font-size:10px");
            }

            if (i.getAvailability() == 86) {
                button.setStyle("-fx-background-color:red");
            } else if (i.getAvailability() == 85) {
                button.setStyle("-fx-background-color:orange");
            }
            button.setMnemonicParsing(false);
            button.setId("IngredientButton");
            buttons.add(button);
        }
        return buttons;
    }

    /**
     * Method, that creates QCBoxes
     * @param bar specifies QC origin: true=bar, false=kitchen;
     * @return ArrayList<QCBox>
     * @see QCBox
     */
    public ArrayList<QCBox> createQCNodes(Boolean bar) {
        ArrayList<QCBox> list = new ArrayList<>();
        List<OrderInfo> orders;
        if(!bar) {
            orders = menuService.getAllOrderedItemsForQC();
        }else{
            orders=menuService.getAllOrderItemsForBarQC();
        }
        Double currentTable = -1.0;
        LocalDateTime currentTime = LocalDateTime.now().minusYears(1);
        ListView<OrderInfo> currentListView=null;

        for (OrderInfo k : orders) {
            if(currentTable.equals(k.getTableNo())) {
                if (currentListView != null) {
                    LocalDateTime timeOrdered=k.getItem().getDateOrdered();
                    if((ChronoUnit.DAYS.between(timeOrdered,currentTime)==0)&&(ChronoUnit.HOURS.between(timeOrdered,currentTime)==0)&&ChronoUnit.MINUTES.between(timeOrdered,currentTime)==0){
                        ObservableList<OrderInfo> observableList = currentListView.getItems();
                        observableList.add(k);
                    }else{
                        QCBox qcBox = new QCBox();
                        qcBox.setPrefSize(160, 200);
                        Label label = new Label();
                        label.setPrefSize(160, 40);
                        label.setText(" Table: " + k.getTableNo());
                        label.setId("UnselectedLabel");
                        ListView<OrderInfo> listView = new ListView<>();
                        listView.setPrefSize(160, 160);
                        listView.setCellFactory(lv -> {
                            ListCell<OrderInfo> cell = new ListCell<>();
                            cell.itemProperty().addListener((obs, oldItem, newItem) -> {
                                if (newItem == null) {
                                    cell.setText(null);
                                } else {
                                    cell.setText(newItem.getItem().getName() + " Qty: " + newItem.getQty());
                                    if (newItem.getItem().getKitchenStatus().equals("SEEN")) {
                                        cell.setId("QCCellSeen");
                                    } else if (newItem.getItem().getKitchenStatus().equals("READY")) {
                                        cell.setId("QCCellReady");
                                    }else{
                                        cell.setId("QCCell");
                                    }
                                }
                            });
                            return cell;
                        });

                        ObservableList<OrderInfo> observableList = FXCollections.observableArrayList();
                        observableList.add(k);
                        listView.setItems(observableList);
                        listView.setId("QCListView");
                        qcBox.getChildren().add(label);
                        qcBox.getChildren().add(listView);
                        currentTable = k.getTableNo();
                        currentTime = k.getItem().getDateOrdered();
                        currentListView = listView;

                        qcBox.setPadding(new Insets(1,1,1,1));
                        list.add(qcBox);

                    }
                }
            }
            else {
                    QCBox qcBox = new QCBox();
                    qcBox.setPrefSize(160, 200);
                    Label label = new Label();
                    label.setPrefSize(160, 40);
                    label.setText(" Table: " + k.getTableNo());
                    label.setId("UnselectedLabel");
                    ListView<OrderInfo> listView = new ListView<>();
                    listView.setPrefSize(160, 160);
                    listView.setCellFactory(lv -> {
                        ListCell<OrderInfo> cell = new ListCell<>();
                        cell.itemProperty().addListener((obs, oldItem, newItem) -> {
                            if (newItem == null) {
                                cell.setText(null);
                            } else {
                                cell.setText(newItem.getItem().getName() + " Qty: " + newItem.getQty());
                                if (newItem.getItem().getKitchenStatus().equals("SEEN")) {
                                    cell.setId("QCCellSeen");
                                } else if (newItem.getItem().getKitchenStatus().equals("READY")) {
                                    cell.setId("QCCellReady");
                                }else{
                                    cell.setId("QCCell");
                                }
                            }
                        });
                        return cell;
                    });

                    ObservableList<OrderInfo> observableList = FXCollections.observableArrayList();
                    observableList.add(k);
                    listView.setItems(observableList);
                    listView.setId("QCListView");
                    qcBox.getChildren().add(label);
                    qcBox.getChildren().add(listView);
                    currentTable = k.getTableNo();
                    currentTime = k.getItem().getDateOrdered();
                    currentListView = listView;

                    qcBox.setPadding(new Insets(1,1,1,1));
                    list.add(qcBox);
                }
            }

        return list;
    }
}

