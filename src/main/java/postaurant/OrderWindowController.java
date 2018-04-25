package postaurant;


import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.NumberStringConverter;
import jdk.internal.util.xml.impl.Input;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import postaurant.context.FXMLoaderService;
import postaurant.database.UserDatabase;
import postaurant.exception.InputValidationException;
import postaurant.model.Ingredient;
import postaurant.model.Item;
import postaurant.model.Order;
import postaurant.model.User;
import postaurant.service.ButtonCreationService;
import postaurant.service.MenuService;
import postaurant.service.OrderService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OrderWindowController {
    private ArrayList<Button> itemButtonList;
    private ArrayList<Button> sectionButtonList;
    private Order order;
    private User user;
    private ObservableList<Map.Entry<Item,Integer>> observableOrder = FXCollections.observableArrayList();
    private ObservableList<Map.Entry<Item, Integer>> originalOrder= FXCollections.observableArrayList();
    private SimpleDoubleProperty total=new SimpleDoubleProperty(0.00);


    private int pageSections;
    private int pageItems;

    private final ButtonCreationService buttonCreationService;
    private final MenuService menuService;
    private final UserDatabase userDatabase;
    private final FXMLoaderService fxmLoaderService;
    private final OrderService orderService;

    @Value("/FXML/AlreadySentWindow.fxml")
    private Resource alreadySentWindow;
    @Value("/FXML/ModifyitemWindow.fxml")
    private Resource modifyItemWindow;
    @Value("/FXML/DubScreen.fxml")
    private Resource dubScreen;
    @Value("POStaurant.css")
    private Resource css;

    @FXML
    private Label labelTableNo;
    @FXML
    private GridPane itemGrid;
    @FXML
    private GridPane sectionGrid;
    @FXML
    private Button foodTypeButton;
    @FXML
    private Button drinkTypeButton;
    @FXML
    private Button modifyButton;
    @FXML
    private Button plusButton;
    @FXML
    private Button minusButton;
    @FXML
    private Button voidButton;
    @FXML
    private Button sendButton;
    @FXML
    private TextField totalTextField;
    @FXML
    private TableView<Map.Entry<Item, Integer>> orderTableView;
    @FXML
    private TableColumn<Map.Entry<Item, Integer>,String> itemColumn;
    @FXML
    private TableColumn<Map.Entry<Item, Integer>,Number> priceColumn;
    @FXML
    private TableColumn<Map.Entry<Item, Integer>,Number> qtyColumn;

    public OrderWindowController(ButtonCreationService buttonCreationService, MenuService menuService, UserDatabase userDatabase, FXMLoaderService fxmLoaderService,OrderService orderService) {
        this.buttonCreationService =buttonCreationService ;
        this.menuService=menuService;
        this.userDatabase=userDatabase;
        this.fxmLoaderService=fxmLoaderService;
        this.orderService=orderService;
    }

    public void initialize(){
        sectionButtonList=buttonCreationService.createOrderSectionButtons(true);
        addOnActionToSectionButtons();
        setSectionButtons(sectionGrid,16,true, sectionButtonList);
        sendButton.setOnAction(event -> {
            if(originalOrder.equals(observableOrder)){
                //((Button)event.getSource()).getScene().getWindow().hide();
                orderService.setCheckedByDub(this.order,new Date());
            }else {
                //searching for new items
                orderService.setCheckedByDub(this.order,new Date());
                for (int i = 0; i < observableOrder.size(); ) {
                    if (originalOrder.contains(observableOrder.get(i))) {
                        observableOrder.remove(observableOrder.get(i));
                    } else {
                        i++;
                    }
                }
                //adding together similar items
                for(int i=0;i<observableOrder.size()-1;){
                    System.out.println(observableOrder.get(i).getKey().equals(observableOrder.get(i+1).getKey()));
                    if(observableOrder.get(i).getKey().equals(observableOrder.get(i+1).getKey())){
                        observableOrder.get(i).setValue(observableOrder.get(i).getValue()+observableOrder.get(i+1).getValue());
                        observableOrder.remove(observableOrder.get(i+1));
                    }
                    else{
                        i++;
                    }
                }

                orderService.sendOrder(this.order.getId(),observableOrder);
                try {
                    FXMLLoader loader=fxmLoaderService.getLoader(dubScreen.getURL());
                    Parent parent=loader.load();
                    DubScreenController dubScreenController=loader.getController();
                    dubScreenController.setUser(this.user);
                    Scene scene=new Scene(parent);
                    Stage stage= (Stage)((Button) event.getSource()).getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                }catch (IOException ioE){
                    ioE.printStackTrace();
                }
            }
        });


        modifyButton.setOnAction(event -> {
            Map.Entry<Item,Integer> entry=orderTableView.getSelectionModel().getSelectedItem();
            if(entry!=null) {
                if (originalOrder.contains(entry)) {
                    try {
                        FXMLLoader loader = fxmLoaderService.getLoader(alreadySentWindow.getURL());
                        Parent parent = loader.load();
                        Scene scene = new Scene(parent);
                        scene.getStylesheets().add(css.getURL().toString());
                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.setScene(scene);
                        stage.showAndWait();
                    } catch (IOException ioE) {
                        ioE.printStackTrace();
                    }
                } else {
                    try {
                        FXMLLoader loader = fxmLoaderService.getLoader(modifyItemWindow.getURL());
                        Parent parent = loader.load();
                        ModifyItemWindowController modifyItemWindowController = loader.getController();
                        modifyItemWindowController.setup(entry.getKey());
                        Scene scene = new Scene(parent);
                        scene.getStylesheets().add(css.getURL().toString());
                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.setScene(scene);
                        stage.showAndWait();
                        Item item=modifyItemWindowController.getItem();
                        observableOrder.remove(entry);
                        TreeMap<Item,Integer> treeMap=new TreeMap<>();
                        treeMap.put(item,entry.getValue());
                        Map.Entry<Item, Integer> entryNew=treeMap.entrySet().iterator().next();
                        observableOrder.add(entryNew);
                    } catch (IOException ioE) {
                        ioE.printStackTrace();
                    }
                }
            }else{
                //TODO
                System.out.println("SELECT ITEM TO MODIFY");
            }

        });





        foodTypeButton.setOnAction(event -> {
            pageSections=0;
            sectionButtonList=buttonCreationService.createOrderSectionButtons(true);
            if(!sectionButtonList.isEmpty()) {
                addOnActionToSectionButtons();
                setSectionButtons(sectionGrid, 16, true, sectionButtonList);
            }
        });

        drinkTypeButton.setOnAction(event -> {
            pageSections=0;
            sectionButtonList=buttonCreationService.createOrderSectionButtons(false);
            if(!sectionButtonList.isEmpty()) {
                addOnActionToSectionButtons();
                setSectionButtons(sectionGrid, 16, true, sectionButtonList);
            }
        });

        plusButton.setOnAction(event -> {
                Map.Entry<Item, Integer> entry = orderTableView.getSelectionModel().getSelectedItem();
            if (!originalOrder.contains(entry)) {
               entry.setValue(entry.getValue()+1);
                setTotal();
                orderTableView.refresh();

            }else{
                //todo
                System.out.println("You can't do that");
            }
        });

        minusButton.setOnAction(event -> {
            Map.Entry<Item, Integer> entry = orderTableView.getSelectionModel().getSelectedItem();
            if (!originalOrder.contains(entry)) {
                if (entry.getValue() > 1) {
                    entry.setValue(entry.getValue() - 1);
                    setTotal();
                    orderTableView.refresh();
                }

                else {
                    //todo
                    System.out.println("You can't do that");
                }
            } else {
                //todo
                System.out.println("You can't do that");
            }
        });

        voidButton.setOnAction(event -> {
            Map.Entry<Item,Integer> entry=orderTableView.getSelectionModel().getSelectedItem();
            if(!originalOrder.contains(entry)){
                observableOrder.remove(entry);
                orderTableView.refresh();
                setTotal();
            }else{
                //todo
                System.out.println("You can't do that");
            }

        });


    }
    public void setUser(User user){
        this.user=user;
    }
    public void setOrderId(Long id) {
        this.order = userDatabase.getOrderById(id);
        this.observableOrder.addAll(order.getOrderItems().entrySet());
        this.originalOrder.addAll(order.getOrderItems().entrySet());
        setTotal();

        totalTextField.textProperty().bindBidirectional(total, new NumberStringConverter());


        itemColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getKey().getName()));

        priceColumn.setCellValueFactory(data -> new ReadOnlyDoubleWrapper(data.getValue().getKey().getPrice()));
        qtyColumn.setCellValueFactory(data -> new ReadOnlyIntegerWrapper(data.getValue().getValue()));
        orderTableView.setItems(observableOrder);
        orderTableView.setRowFactory(new Callback<TableView<Map.Entry<Item, Integer>>, TableRow<Map.Entry<Item, Integer>>>() {
            @Override
            public TableRow<Map.Entry<Item, Integer>> call(TableView<Map.Entry<Item, Integer>> param) {
                final TableRow<Map.Entry<Item, Integer>> row = new TableRow<Map.Entry<Item, Integer>>() {
                    @Override
                    protected void updateItem(Map.Entry<Item, Integer> entry, boolean empty) {
                        super.updateItem(entry, empty);
                        if (entry != null) {
                            if (entry.getKey().getKitchenStatus().equals("SENT")) {
                                this.setStyle("-fx-background-color: blue");
                                SimpleDateFormat ft = new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");

                            } else if(entry.getKey().getKitchenStatus().equals("SEEN")) {
                                //if 20 minutes has passed since ordering
                                if (new Date().getTime() - entry.getKey().getDateOrdered().getTime() > 20L * 60 * 1000) {
                                    this.setStyle("-fx-background-color:red");
                                }
                                //if less then 20 minutes
                                else{
                                    this.setStyle("-fx-background-color:yellow");
                                }

                            } else if(entry.getKey().getKitchenStatus().equals("COOKED")) {
                                this.setStyle("-fx-background-color:#90EE90");
                            }else if(entry.getKey().getKitchenStatus().equals("BUMPED")){
                                this.setStyle("-fx-background-color:green");
                            }
                            else {
                                setStyle("");
                            }
                        }
                    }
                };
                return row;
            }
        });

        orderTableView.getSelectionModel().selectLast();

    }

    public void setTotal(){
        Double totalDouble=0.00;
        for(Map.Entry<Item,Integer> entry: observableOrder){
            Double price=(entry.getKey().getPrice()*entry.getValue());
            totalDouble+=price;
        }
        total.setValue(totalDouble);
    }


    public void setTableNo(int i){
        String text=""+i;
        labelTableNo.setText(text);
    }

    private boolean isNextPage(int page, List<Button> list, int size) {
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

    public void setSectionButtons(GridPane gridPane, Integer size, boolean forward, List<Button> list) {
        int start;
        int x = 0;
        int y = 0;
        if (forward) {
            if (pageSections == 0) {
                start = 0;
            } else {
                start = pageSections * size;
            }
            //if all buttons don't fit in gridPane
            if (isNextPage(pageSections, list, size)) {
                for (int i = 0; i < gridPane.getChildren().size(); ) {
                    gridPane.getChildren().remove(gridPane.getChildren().get(i));
                }
                if (start == 0) {
                    for (int i = start; i < size; i++) {
                        gridPane.add(list.get(i), x, y);
                        GridPane.setMargin(list.get(i), new Insets(2, 2, 10, 2));
                        if (x == 3) {
                            x = 0;
                            y++;
                        } else {
                            x++;
                        }

                    }
                    pageSections++;
                } else {
                    if (isNextPage(pageSections + 1, list, size)) {
                        for (int i = start; i < start + size; i++) {
                            gridPane.add(list.get(i), x, y);
                            GridPane.setMargin(list.get(i), new Insets(2, 2, 2, 2));
                            if (x == 3) {
                                x = 0;
                                y++;
                            } else {
                                x++;
                            }
                        }
                        pageSections++;
                    } else {
                        for (int i = start; i < list.size(); i++) {
                            gridPane.add(list.get(i), x, y);
                            GridPane.setMargin(list.get(i), new Insets(2, 2, 2, 2));
                            if (x == 3) {
                                x = 0;
                                y++;
                            } else {
                                x++;
                            }
                        }
                        pageSections++;
                    }
                }
            } else {
                if (start == 0) {
                    for (int i = 0; i < gridPane.getChildren().size(); ) {
                        gridPane.getChildren().remove(gridPane.getChildren().get(i));
                    }
                    for (int i = start; i < list.size(); i++) {


                        gridPane.add(list.get(i), x, y);

                        GridPane.setMargin(list.get(i), new Insets(2, 2, 2, 2));
                        if (x == 3) {
                            x = 0;
                            y++;
                        } else {
                            x++;
                        }
                    }

                    pageSections++;
                }

            }
        } else {
            if (pageSections > 1) {
                for (int i = 0; i < gridPane.getChildren().size(); ) {
                    gridPane.getChildren().remove(gridPane.getChildren().get(i));
                }
                if (pageSections == 2) {
                    start = 0;
                } else {
                    start = (pageSections - 2) * size;
                }
                for (int i = start; i < (start + size); i++) {
                    gridPane.add(list.get(i), x, y);
                    GridPane.setMargin(list.get(i), new Insets(2, 2, 2, 2));
                    if (x == 3) {
                        x = 0;
                        y++;
                    } else {
                        x++;
                    }
                }
                pageSections--;
            }

        }
    }


    public void addOnActionToSectionButtons(){
        for(Button b:sectionButtonList){
            for (int i=0;i<(itemGrid.getChildren()).size();){
                itemGrid.getChildren().remove(itemGrid.getChildren().get(i));
            }
            b.setOnAction(e-> {
                pageItems=0;
                itemButtonList = buttonCreationService.createItemButtonsForSection(b.getText(), false);
                addOnActionToItemButtons();
                setItemButtons(itemGrid, 21, true, itemButtonList, b.getText());
            });
        }
    }

    public void addOnActionToItemButtons(){
        for(Button b: itemButtonList){
            b.setOnAction(e->{
                if(!b.getId().equals("86") && !b.getId().equals("85")){
                    Item item = menuService.getItemById(Long.parseLong(b.getText().substring(0, b.getText().indexOf("\n"))));
                    TreeMap<Item, Integer> treeMap = new TreeMap<>();
                    treeMap.put(item, 1);
                    observableOrder.add(treeMap.entrySet().iterator().next());
                    orderTableView.getSelectionModel().selectLast();
                    setTotal();
                }
            });
        }
    }


    public void setItemButtons(GridPane gridPane, Integer size, boolean forward, List<Button> list, String section) {
        int start;
        int x = 0;
        int y = 0;
        if (forward) {
            if (pageItems == 0) {
                start = 0;
            } else {
                start = pageItems * size;
            }
            //if all buttons don't fit in gridPane
            if (isNextPage(pageItems, list, size)) {
                for (int i = 0; i < gridPane.getChildren().size(); ) {
                    gridPane.getChildren().remove(gridPane.getChildren().get(i));
                }
                if (start == 0) {
                    for (int i = start; i < size; i++) {
                        gridPane.add(list.get(i), x, y);
                        GridPane.setMargin(list.get(i), new Insets(2, 2, 10, 2));
                        if (x == 3) {
                            x = 0;
                            y++;
                        } else {
                            x++;
                        }

                    }
                    pageItems++;
                } else {
                    if (isNextPage(pageItems + 1, list, size)) {
                        for (int i = start; i < start + size; i++) {
                            gridPane.add(list.get(i), x, y);
                            GridPane.setMargin(list.get(i), new Insets(2, 2, 2, 2));
                            if (x == 3) {
                                x = 0;
                                y++;
                            } else {
                                x++;
                            }
                        }
                        pageItems++;
                    } else {
                        for (int i = start; i < list.size(); i++) {
                            gridPane.add(list.get(i), x, y);
                            GridPane.setMargin(list.get(i), new Insets(2, 2, 2, 2));
                            if (x == 3) {
                                x = 0;
                                y++;
                            } else {
                                x++;
                            }
                        }
                        pageItems++;
                    }
                }
            } else {
                if (start == 0) {
                    for (int i = 0; i < gridPane.getChildren().size(); ) {
                        gridPane.getChildren().remove(gridPane.getChildren().get(i));
                    }
                    for (int i = start; i < list.size(); i++) {


                        gridPane.add(list.get(i), x, y);

                        GridPane.setMargin(list.get(i), new Insets(2, 2, 2, 2));
                        if (x == 3) {
                            x = 0;
                            y++;
                        } else {
                            x++;
                        }
                    }

                    pageItems++;
                }

            }
        } else {
            if (pageItems > 1) {
                for (int i = 0; i < gridPane.getChildren().size(); ) {
                    gridPane.getChildren().remove(gridPane.getChildren().get(i));
                }
                if (pageItems == 2) {
                    start = 0;
                } else {
                    start = (pageItems - 2) * size;
                }
                for (int i = start; i < (start + size); i++) {
                    gridPane.add(list.get(i), x, y);
                    GridPane.setMargin(list.get(i), new Insets(2, 2, 2, 2));
                    if (x == 3) {
                        x = 0;
                        y++;
                    } else {
                        x++;
                    }
                }
                pageItems--;
            }

        }

    }



}
