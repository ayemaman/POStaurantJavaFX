package postaurant;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import postaurant.context.FXMLoaderService;
import postaurant.model.Ingredient;
import postaurant.context.ItemIngredientTreeCellValues;
import postaurant.context.OrderInfo;
import postaurant.service.MenuService;
import postaurant.service.TimeService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
@Component
public class BarScreenController {
    private Thread databaseInfoPulling;

    private final FXMLoaderService fxmLoaderService;
    private final MenuService menuService;
    private final TimeService timeService;

    @Value("FXML/POStaurant.fxml")
    private Resource postaurantScreen;

    @Value("img/logo.png")
    private Resource logo;

    @FXML
    private TreeView<ItemIngredientTreeCellValues> itemsTreeView;
    @FXML
    private TextField timeTextField;
    @FXML
    private Button seenButton;
    @FXML
    private Button bumpButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button timeButton;
    @FXML
    private ImageView logoImage;

    private TreeItem<ItemIngredientTreeCellValues> root;
    private List<OrderInfo> itemList;

    public BarScreenController(FXMLoaderService fxmLoaderService, MenuService menuService, TimeService timeService) {
        this.fxmLoaderService = fxmLoaderService;
        this.menuService = menuService;
        this.timeService = timeService;
    }
    public void initialize(){
        setTreeView();

        timeButton.setOnAction(e->{
            timeService.doTime(timeTextField);
        });

        exitButton.setOnAction(e->{
            try {
                FXMLLoader loader=fxmLoaderService.getLoader(postaurantScreen.getURL());
                Parent parent=loader.load();
                Scene scene=new Scene(parent);
                scene.getStylesheets().add("POStaurant.css");
                Stage stage=(Stage)((Node)e.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
                databaseInfoPulling.interrupt();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        seenButton.setOnAction(e->{
            ObservableList<TreeItem<ItemIngredientTreeCellValues>> i= itemsTreeView.getSelectionModel().getSelectedItems();
            TreeItem<ItemIngredientTreeCellValues> treeItem=i.get(0);
            if(treeItem!=null) {
                ItemIngredientTreeCellValues cellValue = treeItem.getValue();
                if (!cellValue.getStatus().equals("SEEN")) {
                    menuService.setKitchenStatusToSeen(cellValue.getOrderId(), cellValue.getItemId(), cellValue.getDateOrdered());
                    itemList = menuService.getAllOrderedItemsForBar();
                    cellValue.setStatus("SEEN");
                    itemsTreeView.refresh();
                }
            }
        });
        bumpButton.setOnAction(e->{
            ObservableList<TreeItem<ItemIngredientTreeCellValues>> i= itemsTreeView.getSelectionModel().getSelectedItems();
            TreeItem<ItemIngredientTreeCellValues> treeItem=i.get(0);
            if(treeItem!=null) {
                ItemIngredientTreeCellValues cellValue = treeItem.getValue();
                menuService.setKitchenStatusToReady(cellValue.getOrderId(), cellValue.getItemId(), cellValue.getDateOrdered());
                itemList = menuService.getAllOrderedItemsForBar();
                itemsTreeView.getRoot().getChildren().remove(treeItem);
                itemsTreeView.refresh();
            }
        });

        Runnable pullingFromDB = () -> {
            boolean run=true;
            while (run) {
                try {
                    List<OrderInfo> buffer = menuService.getAllOrderedItemsForBar();
                    if (!itemList.equals(buffer)) {
                        itemList = buffer;
                        addNewItemsToTreeViewRoots();

                    }
                    Thread.sleep(10000);


                } catch (InterruptedException e) {
                    run=false;
                }
            }

        };
        databaseInfoPulling = new Thread(pullingFromDB);
        databaseInfoPulling.setDaemon(true);
        databaseInfoPulling.start();

    }


    private void setTreeView() {
        root=new TreeItem<>();
        setupTVCellFactory(itemsTreeView);
        itemsTreeView.setRoot(root);
        itemsTreeView.setShowRoot(false);
        itemList=menuService.getAllOrderedItemsForBar();
        for (OrderInfo k : itemList) {
            ItemIngredientTreeCellValues cell = new ItemIngredientTreeCellValues(k.getOrderId(), k.getItem().getId(), k.getTableNo(), k.getItem().getName(), true, k.getQty(), k.getItem().getDateOrdered(), k.getItem().getKitchenStatus());
            TreeItem<ItemIngredientTreeCellValues> itemRoot = makeBranch(cell, root);
            for (Map.Entry<Ingredient, Integer> entry : k.getItem().getRecipe().entrySet()) {
                ItemIngredientTreeCellValues ingredientCell = new ItemIngredientTreeCellValues(k.getOrderId(), k.getItem().getId(), k.getTableNo(), entry.getKey().getName(), false, entry.getValue(), k.getItem().getDateOrdered(), k.getItem().getKitchenStatus());
                makeBranch(ingredientCell, itemRoot);
            }
        }
    }

    public void addNewItemsToTreeViewRoots() {
        for (int i = root.getChildren().size(); i < itemList.size(); i++) {
            OrderInfo k = itemList.get(i);
            ItemIngredientTreeCellValues cell = new ItemIngredientTreeCellValues(k.getOrderId(), k.getItem().getId(), k.getTableNo(), k.getItem().getName(), true, k.getQty(), k.getItem().getDateOrdered(), k.getItem().getKitchenStatus());
            TreeItem<ItemIngredientTreeCellValues> itemRoot = makeBranch(cell, root);
            for (Map.Entry<Ingredient, Integer> entry : k.getItem().getRecipe().entrySet()) {
                ItemIngredientTreeCellValues ingredientCell = new ItemIngredientTreeCellValues(k.getOrderId(), k.getItem().getId(), k.getTableNo(), entry.getKey().getName(), false, entry.getValue(), k.getItem().getDateOrdered(), k.getItem().getKitchenStatus());
                makeBranch(ingredientCell, itemRoot);
            }
        }

    }
    private TreeItem<ItemIngredientTreeCellValues> makeBranch(ItemIngredientTreeCellValues cellValue, TreeItem<ItemIngredientTreeCellValues> parent){
        TreeItem<ItemIngredientTreeCellValues> treeItem= new TreeItem<>(cellValue);
        parent.getChildren().add(treeItem);
        return treeItem;
    }

    public void setupTVCellFactory(TreeView<ItemIngredientTreeCellValues> treeV){
        treeV.setCellFactory(tv -> {
            TreeCell<ItemIngredientTreeCellValues> cell = new TreeCell<>();
            cell.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem == null) {
                    cell.setText(null);
                    cell.setId("");
                } else {
                    cell.setText(newItem.toString());
                    cell.setId("KTreeCell");
                    if (newItem.isItem()) {
                        if(newItem.getStatus().equals("SEEN")) {
                            cell.setId("KTreeCellSeen");
                        }else{
                            if (newItem.getName().matches("^CUSTOM.*")) {
                                cell.setId("KTreeCellCustom");
                            }
                        }
                    }
                }

            });
            return cell;

        });

    }
}
