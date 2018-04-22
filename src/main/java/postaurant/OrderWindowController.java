package postaurant;


import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import postaurant.context.FXMLoaderService;
import postaurant.database.UserDatabase;
import postaurant.model.Item;
import postaurant.model.Order;
import postaurant.service.ButtonCreationService;
import postaurant.service.MenuService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OrderWindowController {
    private ArrayList<Button> itemButtonList;
    private ArrayList<Button> sectionButtonList;
    private Order order;
    private ObservableList<Map.Entry<Item,Integer>> observableOrder = FXCollections.observableArrayList();
    private ObservableList<Map.Entry<Item, Integer>> originalOrder= FXCollections.observableArrayList();

    private int pageSections;
    private int pageItems;

    private final ButtonCreationService buttonCreationService;
    private final MenuService menuService;
    private final UserDatabase userDatabase;
    private final FXMLoaderService fxmLoaderService;

    @Value("/FXML/AlreadySentWindow.fxml")
    private Resource alreadySentWindow;

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
    private TableView<Map.Entry<Item, Integer>> orderTableView;
    @FXML
    private TableColumn<Map.Entry<Item, Integer>,String> itemColumn;
    @FXML
    private TableColumn<Map.Entry<Item, Integer>,Number> priceColumn;
    @FXML
    private TableColumn<Map.Entry<Item, Integer>,Number> qtyColumn;

    public OrderWindowController(ButtonCreationService buttonCreationService, MenuService menuService, UserDatabase userDatabase, FXMLoaderService fxmLoaderService) {
        this.buttonCreationService =buttonCreationService ;
        this.menuService=menuService;
        this.userDatabase=userDatabase;
        this.fxmLoaderService=fxmLoaderService;
    }

    public void initialize(){
        sectionButtonList=buttonCreationService.createOrderSectionButtons(true);
        addOnActionToSectionButtons();
        setSectionButtons(sectionGrid,16,true, sectionButtonList);

        int i=4;
        Long l= (long) i;
        setOrder(userDatabase.getOrderById(l));




        this.observableOrder.addAll(order.getOrderItems().entrySet());
        this.originalOrder.addAll(order.getOrderItems().entrySet());

        itemColumn.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getKey().getName()));
        priceColumn.setCellValueFactory(data-> new ReadOnlyDoubleWrapper(data.getValue().getKey().getPrice()));
        qtyColumn.setCellValueFactory(data-> new ReadOnlyIntegerWrapper(data.getValue().getValue()));

        orderTableView.setItems(observableOrder);

        modifyButton.setOnAction(event -> {
            Map.Entry<Item,Integer> entry=orderTableView.getSelectionModel().getSelectedItem();
            if(originalOrder.contains(entry)){
                try {
                    FXMLLoader loader = fxmLoaderService.getLoader(alreadySentWindow.getURL());
                    Parent parent=loader.load();
                    Scene scene=new Scene(parent);
                    scene.getStylesheets().add(css.getURL().toString());
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setScene(scene);
                    stage.showAndWait();
                }catch (IOException ioE){
                    ioE.printStackTrace();
                }
            }
            else {
                System.out.println(entry.getKey() + " " + entry.getValue());
            }
                    /*
                    ObservableList<Ingredient> selected = ingredientTable.getSelectionModel().getSelectedItems();
            selected.forEach(ingredientsList::remove);
                     */
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
    }

     /*
        recipeColumn.setCellFactory(param -> new AvailabilityFormatCell());

         private class AvailabilityFormatCell extends TableCell<Ingredient, String> {
        public AvailabilityFormatCell() {
        }
        @Override
        protected void updateItem(String string, boolean empty) {
            super.updateItem(string, empty);
            setText(string == null ? "" : string);
            if (string != null) {
                for (Ingredient i : ingredientsList) {
                    if (string.equals(i.getName())) {
                        if (i.getAvailability() == 86) {
                            setStyle("-fx-background-color:red");
                        } else if (i.getAvailability() == 85) {
                            setStyle("-fx-background-color:orange");
                        }
                    }
                }
            } else {
                setStyle("");
            }
        }
    }
         */







    public void setOrder(Order order){
        this.order=order;
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
                Item item=menuService.getItemById(Long.parseLong(b.getText().substring(0, b.getText().indexOf("\n"))));

                TreeMap<Item,Integer> treeMap=new TreeMap<>();
                treeMap.put(item,1);
                observableOrder.add(treeMap.entrySet().iterator().next());
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
