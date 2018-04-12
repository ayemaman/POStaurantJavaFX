package postaurant;


import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import postaurant.context.FXMLoaderService;
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
    private int page;


    private final MenuService menuService;
    private final FXMLoaderService fxmLoaderService;
    private final ButtonCreationService buttonCreationService;

    @Value("/FXML/ItemInfoScreen.fxml")
    private Resource itemInfoForm;
    @Value("/POStaurant.css")
    private Resource css;


    @FXML
    private TabPane sectionTabPane;
    @FXML
    private TabPane largeTabPane;

    @FXML
    private GridPane ingredientGrid;
    @FXML
    private Button newItemButton;
    @FXML
    private Button downButton;
    @FXML
    private Button upButton;


    public MenuScreenController(MenuService menuService, FXMLoaderService fxmLoaderService, ButtonCreationService buttonCreationService){
        this.menuService=menuService;
        this.fxmLoaderService=fxmLoaderService;
        this.buttonCreationService=buttonCreationService;
    }
    public void initialize(){
        List<Tab> list=buttonCreationService.createSectionTabs();
        if(!list.isEmpty()) {
            setSectionTabList(list);
            setItemButtonList(buttonCreationService.createItemButtonsForSection(list.get(0).getText()));
            setSectionTabs();
            setIngredientButtonList(buttonCreationService.createIngredientButtonsSmall(false));
            System.out.println(this.page);
            setIngredientButtons(ingredientGrid, 16, true, ingredientButtonList);
        }


        upButton.setOnAction(e->{
            if(largeTabPane.getSelectionModel().getSelectedItem().getText().equals("ITEMS")) {
                Tab currentTab = sectionTabPane.getSelectionModel().getSelectedItem();
                AnchorPane anchorPane = (AnchorPane) currentTab.getContent();
                GridPane gridPane = (GridPane) anchorPane.getChildren().get(0);
                setItemButtonsForSection(gridPane, false);
            }else {
                setIngredientButtons(ingredientGrid,16,false,ingredientButtonList);
            }


        });

        downButton.setOnAction(e->{
            if(largeTabPane.getSelectionModel().getSelectedItem().getText().equals("ITEMS")) {
            Tab currentTab=sectionTabPane.getSelectionModel().getSelectedItem();
            AnchorPane anchorPane=(AnchorPane)currentTab.getContent();
            GridPane gridPane=(GridPane) anchorPane.getChildren().get(0);
            setItemButtonsForSection(gridPane, true);
            }else {
                setIngredientButtons(ingredientGrid,16,false,ingredientButtonList);
            }
        });

        newItemButton.setOnAction(event->{
            try {
                FXMLLoader loader = fxmLoaderService.getLoader(itemInfoForm.getURL());
                Parent parent=loader.load();
                ItemInfoScreenController itemInfoScreenController=loader.getController();
                itemInfoScreenController.setIngredientButtonList(buttonCreationService.createIngredientButtonsSmall(true));
                itemInfoScreenController.setup(null);
                Scene scene = new Scene(parent);
                scene.getStylesheets().add(css.getURL().toExternalForm());
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }catch (Exception e){
                e.printStackTrace();
            }
        });



    }


    public List<Button> getItemButtonList() {
        return itemButtonList;
    }

    public void setItemButtonList(List<Button> itemButtonList) {
        this.itemButtonList = itemButtonList;
    }

    public void setIngredientButtonList(List<Button> ingredientButtonList){
        this.ingredientButtonList=ingredientButtonList;
    }

    public List<Tab> getSectionTabList() {
        return sectionTabList;
    }

    public void setSectionTabList(List<Tab> sectionTabList) {
        this.sectionTabList = sectionTabList;
    }



    public void setSectionTabs(){
        for(Tab t: sectionTabList) {
            t.setOnSelectionChanged(event-> {
                AnchorPane anchorPane = (AnchorPane) t.getContent();
                GridPane gridPane = (GridPane) anchorPane.getChildren().get(0);
                setItemButtonList(buttonCreationService.createItemButtonsForSection(t.getText()));
                this.page = 0;
                setItemButtonsForSection(gridPane, true);
                for (Button b : itemButtonList) {
                    b.setOnAction(event1 -> {
                        try {
                            FXMLLoader loader = fxmLoaderService.getLoader(itemInfoForm.getURL());
                            Parent parent = loader.load();
                            ItemInfoScreenController itemInfoScreenController = loader.getController();
                            itemInfoScreenController.setIngredientButtonList(buttonCreationService.createIngredientButtonsSmall(true));
                            itemInfoScreenController.setup(menuService.getItem(b.getText().substring(0, b.getText().indexOf("\n"))));
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
            });
            sectionTabPane.getTabs().add(t);
        }
        AnchorPane anchorPane=(AnchorPane)sectionTabPane.getTabs().get(0).getContent();
        GridPane gridPane =(GridPane)anchorPane.getChildren().get(0);

        setItemButtonsForSection(gridPane, true);

    }

    private boolean isNextPageIngredients(int size){
        try{
            ingredientButtonList.get((this.page * size));
        }catch (IndexOutOfBoundsException e){
            return false;
        }
        return true;
    }
    private boolean isNextPageItems() {
        try {
            itemButtonList.get((this.page * 16));
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;

    }
    //Integer page,GridPane gridPane, Integer size, boolean forward, List<Button> list
/*
    public void setIngredientButtons(Integer page,GridPane gridPane, Integer size, boolean forward, List<Button> list){
        int start;
        int x=0;
        int y=0;
        if(forward){
            if (page==0){
                start = 0;
            }else{
                start=page*size;
            }
            if(isNextPage()) {
                for (int i = 0; i < gridPane.getChildren().size(); ) {
                    gridPane.getChildren().remove(gridPane.getChildren().get(i));
                }

                if (list.size() - start > size - 1) {
                    for (int i = start; i < (start + size); i++) {
                        gridPane.add(list.get(i), x, y);
                        gridPane.setMargin(list.get(i), new Insets(2, 2, 2, 2));
                        if (x == 2) {
                            x = 0;
                            y++;
                        } else {
                            x++;
                        }
                    }
                } else {
                    for (int i = start; i < list.size(); i++) {
                        gridPane.add(list.get(i), x, y);
                        gridPane.setMargin(list.get(i), new Insets(2, 2, 2, 2));
                        if (x == 2) {
                            x = 0;
                            y++;
                        } else {
                            x++;
                        }
                    }
                }
                page++;
            }
        }else{
            if(page>1){
                if(page==2){
                    start = 0;
                }
                else{
                    start = (page-2)*size;
                }
                for(int i=start; i<(start+size);i++){
                    gridPane.add(list.get(i),x,y);
                    gridPane.setMargin(list.get(i), new Insets(2,2,2,2));
                    if(x==2){
                        x=0;
                        y++;
                    }else{
                        x++;
                    }
                }
                page--;
            }

        }

    }
     */
    public void setIngredientButtons(GridPane gridPane, Integer size, boolean forward, List<Button> list) {
        int start;
        int x = 0;
        int y = 0;
        gridPane.setAlignment(Pos.CENTER);
        if (forward) {
            System.out.println("Is forward");
            if (this.page == 1) {
                start=0;
            }else {
                start = this.page * size;
            }
            if (isNextPageIngredients(size)) {
                for (int i = 0; i < gridPane.getChildren().size(); ) {
                    gridPane.getChildren().remove(gridPane.getChildren().get(i));
                }
                if (list.size() - start > size - 1) {
                    for (int i = start; i < (start + size); i++) {
                        gridPane.add(list.get(i), x, y);
                        GridPane.setMargin(list.get(i), new Insets(2, 2, 10, 2));
                        if (x == 3) {
                            x = 0;
                            y++;
                        } else {
                            x++;
                        }
                    }
                }
                this.page++;
            } else {
                for (int i = 0; i < gridPane.getChildren().size();) {
                    System.out.println(gridPane.getChildren());
                    gridPane.getChildren().remove(gridPane.getChildren().get(i));
                    System.out.println(gridPane.getChildren());
                }
                System.out.println(list.size());
                System.out.println(start);
                for (int i = start; i < list.size(); i++) {
                        System.out.println(" or here");
                        gridPane.add(list.get(i), x, y);
                    GridPane.setMargin(list.get(i), new Insets(2, 2, 10, 2));
                        if (x == 3) {
                            x = 0;
                            y++;
                        } else {
                            x++;
                        }
                    }
                }
                this.page++;
        }
        else {
            if (this.page > 1) {
                if (this.page == 2) {
                    start = 0;
                } else {
                    start = (this.page - 2) * size;
                }
                for (int i = start; i < (start + size); i++) {
                    gridPane.add(list.get(i), x, y);
                    GridPane.setMargin(list.get(i), new Insets(2, 2, 10, 2));
                    if (x == 3) {
                        x = 0;
                        y++;
                    } else {
                        x++;
                    }
                }
                this.page--;
            }

        }

    }



    public void setItemButtonsForSection(GridPane gridPane, boolean forward){
        int start;
        int x=0;
        int y=0;

        if(forward) {
            if (this.page == 0) {
                start = 0;
            } else {
                start = this.page * 16;
            }
            if (isNextPageItems()) {
                for (int i = 0; i < gridPane.getChildren().size(); ) {
                    gridPane.getChildren().remove(gridPane.getChildren().get(i));
                }
                if (itemButtonList.size() - start > 15) {
                    for (int i = start; i < (start + 16); i++) {
                        gridPane.add(itemButtonList.get(i), x, y);
                        GridPane.setMargin(itemButtonList.get(i), new Insets(2, 2, 2, 2));
                        if (x == 3) {
                            x = 0;
                            y++;
                        } else {
                            x++;
                        }
                    }
                } else {
                    for (int i = start; i < itemButtonList.size(); i++) {
                        gridPane.add(itemButtonList.get(i), x, y);
                        GridPane.setMargin(itemButtonList.get(i), new Insets(2, 2, 2, 2));
                        if (x == 3) {
                            x = 0;
                            y++;
                        } else {
                            x++;
                        }
                    }
                }
                page++;
            }
        }else{
            if(this.page>1) {
                if (this.page == 2) {
                    start = 0;
                } else {
                    start = (this.page - 2) * 16;
                }
                for (int i = start; i < (start + 16); i++) {
                    gridPane.add(itemButtonList.get(i), x, y);
                    GridPane.setMargin(itemButtonList.get(i), new Insets(2, 2, 2, 2));
                    if (x == 3) {
                        x = 0;
                        y++;
                    } else {
                        x++;
                    }
                }
                page--;
            }

        }

    }
}
