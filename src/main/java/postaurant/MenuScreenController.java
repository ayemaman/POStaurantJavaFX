package postaurant;


import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import postaurant.context.FXMLoaderService;
import postaurant.model.Ingredient;
import postaurant.model.Item;
import postaurant.service.ButtonCreationService;
import postaurant.service.MenuService;
import javafx.fxml.FXML;


import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


import java.util.List;


@Component
@Scope(value=ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MenuScreenController {

    private List<Button> itemButtonList;
    private List<Button> ingredientButtonList;
    private List<Tab> sectionTabList;
    private  int pageIngredients = 0;
    private  int pageItems = 0;


    private final MenuService menuService;
    private final FXMLoaderService fxmLoaderService;
    private final ButtonCreationService buttonCreationService;
    @Value("FXML/MenuScreen.fxml")
    private Resource menuScreen;
    @Value("/FXML/ItemInfoScreen.fxml")
    private Resource itemInfoForm;
    @Value("/FXML/IngredientInfoScreen.fxml")
    private Resource ingredientInfoForm;
    @Value("/FXML/ManagerScreen.fxml")
    private Resource managerScreen;
    @Value("/POStaurant.css")
    private Resource css;


    @FXML
    private TabPane sectionTabPane;
    @FXML
    private TabPane largeTabPane;
    @FXML
    private Tab ingredientTab;
    @FXML
    private GridPane ingredientGrid;
    @FXML
    private Button newItemButton;
    @FXML
    private Button newIngredientButton;
    @FXML
    private Button downButton;
    @FXML
    private Button upButton;
    @FXML
    private Button managerScreenButton;


    public MenuScreenController(MenuService menuService, FXMLoaderService fxmLoaderService, ButtonCreationService buttonCreationService) {
        this.menuService = menuService;
        this.fxmLoaderService = fxmLoaderService;
        this.buttonCreationService = buttonCreationService;
    }

    public void initialize() {
        menuService.getAllOrderedItemsForKitchen();
        setup();
        ingredientTab.setOnSelectionChanged(e->{
            pageItems=0;
            pageIngredients=0;
            setIngredientButtons(ingredientGrid,16,true, ingredientButtonList);
        });
        upButton.setOnAction(e -> {
            if (largeTabPane.getSelectionModel().getSelectedItem().getText().equals("ITEMS")) {
                Tab currentTab = sectionTabPane.getSelectionModel().getSelectedItem();
                AnchorPane anchorPane = (AnchorPane) currentTab.getContent();
                GridPane gridPane = (GridPane) anchorPane.getChildren().get(0);
                setItemButtonList(buttonCreationService.createItemButtonsForSection(currentTab.getText(),true));
                setItemButtonsForSection(gridPane, 16, false, itemButtonList, currentTab);
            } else {
                setIngredientButtons(ingredientGrid, 16, false, ingredientButtonList);
            }
        });

        downButton.setOnAction(e -> {

            if (largeTabPane.getSelectionModel().getSelectedItem().getText().equals("ITEMS")) {
                Tab currentTab = sectionTabPane.getSelectionModel().getSelectedItem();
                AnchorPane anchorPane = (AnchorPane) currentTab.getContent();
                GridPane gridPane = (GridPane) anchorPane.getChildren().get(0);
                setItemButtonList(buttonCreationService.createItemButtonsForSection(currentTab.getText(),true));
                setItemButtonsForSection(gridPane, 16, true, itemButtonList, currentTab);
            } else {
                setIngredientButtons(ingredientGrid, 16, true, ingredientButtonList);
            }
        });

        newItemButton.setOnAction(event -> {
            try {
                FXMLLoader loader = fxmLoaderService.getLoader(itemInfoForm.getURL());
                Parent parent = loader.load();
                ItemInfoScreenController itemInfoScreenController = loader.getController();
                itemInfoScreenController.setIngredientButtonList(buttonCreationService.createIngredientButtons(1));
                itemInfoScreenController.setup(null);
                Scene scene = new Scene(parent);
                scene.getStylesheets().add(css.getURL().toExternalForm());
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        newIngredientButton.setOnAction(event -> {
            try{
                FXMLLoader loader=fxmLoaderService.getLoader(ingredientInfoForm.getURL());
                Parent parent=loader.load();
                IngredientInfoScreenController ingredientInfoScreenController =loader.getController();
                ingredientInfoScreenController.setup(null);
                Scene scene = new Scene(parent);
                scene.getStylesheets().add(css.getURL().toExternalForm());
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(scene);
                stage.showAndWait();
                if(ingredientInfoScreenController.wasSaved()){
                    if(ingredientInfoScreenController.wasSaved()){
                        FXMLLoader loader1=fxmLoaderService.getLoader(menuScreen.getURL());
                        Parent parent1=loader1.load();
                        MenuScreenController menuScreenController=loader1.getController();
                        menuScreenController.largeTabPane.getSelectionModel().select(1);
                        Scene scene1= new Scene(parent1);
                        scene1.getStylesheets().add(""+css.getURL());
                        Stage stage1= (Stage)((Node) event.getSource()).getScene().getWindow();
                        stage1.setScene(scene1);
                        stage1.show();
                        stage.hide();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        managerScreenButton.setOnAction(event -> {
            try{
                FXMLLoader loader=fxmLoaderService.getLoader(managerScreen.getURL());
                Parent parent=loader.load();
                Scene scene=new Scene(parent);
                scene.getStylesheets().addAll(css.getURL().toExternalForm());
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }catch (Exception e){
                e.printStackTrace();
            }
        });


    }

    public void setup() {
        //creating section tabs
            setSectionTabs();
            setIngredientButtonList(buttonCreationService.createIngredientButtons(2));

    }


    public List<Button> getItemButtonList() {
        return itemButtonList;
    }

    public void setItemButtonList(List<Button> itemButtonList) {
        for (Button b : itemButtonList) {
            b.setOnAction(event1 -> {
                try {
                    FXMLLoader loader = fxmLoaderService.getLoader(itemInfoForm.getURL());
                    Parent parent = loader.load();
                    ItemInfoScreenController itemInfoScreenController = loader.getController();
                    itemInfoScreenController.setIngredientButtonList(buttonCreationService.createIngredientButtons(1));
                    Item item=menuService.getItemById(Long.parseLong(b.getText().substring(0, b.getText().indexOf("\n"))));
                    itemInfoScreenController.setup(item);
                    Scene scene = new Scene(parent);
                    scene.getStylesheets().add(css.getURL().toExternalForm());
                    Stage stage = (Stage) ((Node) event1.getSource()).getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        }
        this.itemButtonList = itemButtonList;
    }

    public void setIngredientButtonList(List<Button> ingredientButtonList) {
        for (Button b : ingredientButtonList) {
            b.setOnAction(event1 -> {
                try {
                    FXMLLoader loader = fxmLoaderService.getLoader(ingredientInfoForm.getURL());
                    Parent parent = loader.load();
                    IngredientInfoScreenController ingredientInfoScreenController =loader.getController();
                    Ingredient ingredient=menuService.getIngredientById(Long.parseLong(b.getText().substring(0, b.getText().indexOf("\n"))));
                    ingredientInfoScreenController.setup(ingredient);
                    Scene scene = new Scene(parent);
                    scene.getStylesheets().add(css.getURL().toExternalForm());
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setScene(scene);
                    stage.showAndWait();
                    if(ingredientInfoScreenController.wasSaved()){
                        FXMLLoader loader1=fxmLoaderService.getLoader(menuScreen.getURL());
                        Parent parent1=loader1.load();
                        MenuScreenController menuScreenController=loader1.getController();
                        menuScreenController.largeTabPane.getSelectionModel().select(1);
                        Scene scene1= new Scene(parent1);
                        scene1.getStylesheets().add(""+css.getURL());
                        Stage stage1= (Stage)((Node) event1.getSource()).getScene().getWindow();
                        stage1.setScene(scene1);
                        stage1.show();
                        stage.hide();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        this.ingredientButtonList = ingredientButtonList;
    }

    public List<Tab> getSectionTabList() {
        return sectionTabList;
    }

    public void setSectionTabList(List<Tab> sectionTabList) {
        this.sectionTabList = sectionTabList;
    }


    public void setSectionTabs() {
        List<Tab> list = buttonCreationService.createSectionTabs();
        if (!list.isEmpty()) {
            setSectionTabList(list);
            for (Tab t : sectionTabList) {
                t.setOnSelectionChanged(event -> {
                    pageItems = 0;
                    pageIngredients=0;
                    AnchorPane anchorPane = (AnchorPane) t.getContent();
                    GridPane gridPane = (GridPane) anchorPane.getChildren().get(0);
                    setItemButtonList(buttonCreationService.createItemButtonsForSection(t.getText(),true));
                    setItemButtonsForSection(gridPane, 16, true, itemButtonList, t);
                });
                sectionTabPane.getTabs().add(t);
            }
        }
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


    public void setIngredientButtons(GridPane gridPane, Integer size, boolean forward, List<Button> list) {
        int start;
        int x = 0;
        int y = 0;
        if (forward) {

            if (pageIngredients == 0) {
                start = 0;
            } else {
                start = pageIngredients * size;
            }
            //if all buttons don't fit in gridPane
            if (isNextPage(pageIngredients, list, size)) {
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
                    pageIngredients++;
                } else {
                    if (isNextPage(pageIngredients + 1, list, size)) {
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
                        pageIngredients++;
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
                        pageIngredients++;
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

                    pageIngredients++;
                }

            }
        } else {
            if (pageIngredients > 1) {
                for (int i = 0; i < gridPane.getChildren().size(); ) {
                    gridPane.getChildren().remove(gridPane.getChildren().get(i));
                }
                if (pageIngredients == 2) {
                    start = 0;
                } else {
                    start = (pageIngredients - 2) * size;
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
                pageIngredients--;
            }

        }
    }


    public void setItemButtonsForSection(GridPane gridPane, Integer size, boolean forward, List<Button> list, Tab tab) {
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
                else {
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

                //if all buttons fit in GridPane
            } else {

                if(start==0) {
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
                for (int i = start; i < (start + 16); i++) {
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
