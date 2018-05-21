package postaurant;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import postaurant.context.FXMLoaderService;

import postaurant.model.Ingredient;
import postaurant.model.ItemIngredientTreeCellValues;
import postaurant.model.KitchenOrderInfo;
import postaurant.service.MenuService;
import postaurant.service.TimeService;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class KitchenScreenController {

    private Thread databaseInfoPulling;

    private List<KitchenOrderInfo> itemList;
    private List<KitchenOrderInfo> fryList;
    private List<KitchenOrderInfo> grillList;
    private List<KitchenOrderInfo> sauteList;
    private List<KitchenOrderInfo> dessertList;

    private Tab currentTab;
    private TreeView<ItemIngredientTreeCellValues> currentTreeView;
    private List<KitchenOrderInfo> currentList;
    private TreeItem<ItemIngredientTreeCellValues> fryRoot;
    private TreeItem<ItemIngredientTreeCellValues> grillRoot;
    private TreeItem<ItemIngredientTreeCellValues> sauteRoot;
    TreeItem<ItemIngredientTreeCellValues> dessertsRoot;

    private final FXMLoaderService fxmLoaderService;
    private final MenuService menuService;
    private final TimeService timeService;

    @Value("FXML/POStaurant.fxml")
    private Resource postaurantScreen;

    @Value("img/logo.png")
    private Resource logo;



    @FXML
    private Tab fryTab;
    @FXML
    private TreeView<ItemIngredientTreeCellValues> fryTreeView;

    @FXML
    private Tab grillTab;
    @FXML
    private TreeView<ItemIngredientTreeCellValues> grillTreeView;

    @FXML
    private Tab sauteTab;
    @FXML
    private TreeView<ItemIngredientTreeCellValues> sauteTreeView;

    @FXML
    private Tab dessertsTab;
    @FXML
    private TreeView<ItemIngredientTreeCellValues> dessertsTreeView;

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




    public KitchenScreenController(FXMLoaderService fxmLoaderService, MenuService menuService,TimeService timeService){
        this.fxmLoaderService=fxmLoaderService;
        this.menuService=menuService;
        this.timeService=timeService;
    }


    public void initialize() throws IOException {
        logoImage.setImage(new Image(logo.getURL().toExternalForm()));
        itemList = menuService.getAllOrderedItemsForKitchen();
        currentTreeView=fryTreeView;
        currentList=fryList;
        currentTab=fryTab;
        setupTVCellFactory(fryTreeView);
        setupTVCellFactory(sauteTreeView);
        setupTVCellFactory(grillTreeView);
        setupTVCellFactory(dessertsTreeView);
        setTreeViewRoot();
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
        bumpButton.setOnAction(e->{
            ObservableList<TreeItem<ItemIngredientTreeCellValues>> i= currentTreeView.getSelectionModel().getSelectedItems();
            TreeItem<ItemIngredientTreeCellValues> treeItem=i.get(0);
            if(treeItem!=null) {
                ItemIngredientTreeCellValues cellValue = treeItem.getValue();
                menuService.setKitchenStatusToReady(cellValue.getOrderId(), cellValue.getItemId(), cellValue.getDateOrdered());
                itemList = menuService.getAllOrderedItemsForKitchen();
                filterItemLists(itemList);
                switch (getCurrentTab().getText()) {
                    case "FRY":
                        currentList = fryList;
                        break;
                    case "GRILL":
                        currentList = grillList;
                        break;
                    case "DESSERTS":
                        currentList = dessertList;
                        break;
                    case "PLATE/SAUTE":
                        currentList = sauteList;
                        break;
                }
                currentTreeView.getRoot().getChildren().remove(treeItem);
                currentTreeView.refresh();

            }
        });

        seenButton.setOnAction(e->{
            ObservableList<TreeItem<ItemIngredientTreeCellValues>> i= currentTreeView.getSelectionModel().getSelectedItems();
            TreeItem<ItemIngredientTreeCellValues> treeItem=i.get(0);
            if(treeItem!=null) {
                ItemIngredientTreeCellValues cellValue = treeItem.getValue();
                if (!cellValue.getStatus().equals("SEEN")) {
                    menuService.setKitchenStatusToSeen(cellValue.getOrderId(), cellValue.getItemId(), cellValue.getDateOrdered());

                    itemList = menuService.getAllOrderedItemsForKitchen();
                    filterItemLists(itemList);

                    Tab tab = getCurrentTab();
                    switch (tab.getText()) {
                        case "FRY":
                            currentList = fryList;
                            break;
                        case "GRILL":
                            currentList = grillList;
                            break;
                        case "DESSERTS":
                            currentList = dessertList;
                            break;
                        case "PLATE/SAUTE":
                            currentList = sauteList;
                            break;
                    }
                    cellValue.setStatus("SEEN");
                    currentTreeView.refresh();
                }
            }


        });

            fryTab.setOnSelectionChanged(e -> {
                currentTab = (Tab) e.getSource();
                currentList = fryList;
                currentTreeView = fryTreeView;
              // setupCurrentTableView();

            });
            grillTab.setOnSelectionChanged(e -> {
                currentTab = (Tab) e.getSource();
                currentList = grillList;
                currentTreeView = grillTreeView;
               // setupCurrentTableView();

            });
            sauteTab.setOnSelectionChanged(e -> {
                currentTab = (Tab) e.getSource();
                currentList = sauteList;
                currentTreeView = sauteTreeView;
              //  setupCurrentTableView();

            });
            dessertsTab.setOnSelectionChanged(e -> {
                currentTab = (Tab) e.getSource();
                currentList = dessertList;
                currentTreeView = dessertsTreeView;
               // setupCurrentTableView();

            });

        Runnable pullingFromDB = () -> {
            boolean run=true;
            while (run) {
                try {
                    List<KitchenOrderInfo> buffer = menuService.getAllOrderedItemsForKitchen();
                        if (!itemList.equals(buffer)) {
                            System.out.println("Updating");
                            switch (getCurrentTab().getText()) {
                                case "FRY":
                                    currentList = fryList;
                                    break;
                                case "GRILL":
                                    currentList = grillList;
                                    break;
                                case "DESSERTS":
                                    currentList = dessertList;
                                    break;
                                case "PLATE/SAUTE":
                                    currentList = sauteList;
                                    break;
                            }


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



    //Method to set a custom tree cell factory to a TreeView
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
    //Method, that sets Items for each station screen

    public void setTreeViewRoot() {
        grillRoot = new TreeItem<>();
        grillTreeView.setRoot(grillRoot);
        grillTreeView.setShowRoot(false);

        fryRoot = new TreeItem<>();
        fryTreeView.setRoot(fryRoot);
        fryTreeView.setShowRoot(false);

        dessertsRoot = new TreeItem<>();
        dessertsTreeView.setRoot(dessertsRoot);
        dessertsTreeView.setShowRoot(false);

        sauteRoot = new TreeItem<>();
        sauteTreeView.setRoot(sauteRoot);
        sauteTreeView.setShowRoot(false);

        filterItemLists(itemList);

        for (KitchenOrderInfo k : fryList) {
            ItemIngredientTreeCellValues cell = new ItemIngredientTreeCellValues(k.getOrderId(), k.getItem().getId(), k.getTableNo(), k.getItem().getName(), true, k.getQty(), k.getItem().getDateOrdered(), k.getItem().getKitchenStatus());
            TreeItem<ItemIngredientTreeCellValues> itemRoot = makeBranch(cell, fryRoot);
            for (Map.Entry<Ingredient, Integer> entry : k.getItem().getRecipe().entrySet()) {
                ItemIngredientTreeCellValues ingredientCell = new ItemIngredientTreeCellValues(k.getOrderId(), k.getItem().getId(), k.getTableNo(), entry.getKey().getName(), false, entry.getValue(), k.getItem().getDateOrdered(), k.getItem().getKitchenStatus());
                makeBranch(ingredientCell, itemRoot);
            }
        }

        for (KitchenOrderInfo k : grillList) {
            ItemIngredientTreeCellValues cell = new ItemIngredientTreeCellValues(k.getOrderId(), k.getItem().getId(), k.getTableNo(), k.getItem().getName(), true, k.getQty(), k.getItem().getDateOrdered(), k.getItem().getKitchenStatus());
            TreeItem<ItemIngredientTreeCellValues> itemRoot = makeBranch(cell, grillRoot);
            for (Map.Entry<Ingredient, Integer> entry : k.getItem().getRecipe().entrySet()) {
                ItemIngredientTreeCellValues ingredientCell = new ItemIngredientTreeCellValues(k.getOrderId(), k.getItem().getId(), k.getTableNo(), entry.getKey().getName(), false, entry.getValue(), k.getItem().getDateOrdered(), k.getItem().getKitchenStatus());
                makeBranch(ingredientCell, itemRoot);
            }
        }

        for(KitchenOrderInfo k:sauteList){
            ItemIngredientTreeCellValues cell = new ItemIngredientTreeCellValues(k.getOrderId(), k.getItem().getId(), k.getTableNo(), k.getItem().getName(), true, k.getQty(), k.getItem().getDateOrdered(), k.getItem().getKitchenStatus());
            TreeItem<ItemIngredientTreeCellValues> itemRoot = makeBranch(cell, grillRoot);
            for (Map.Entry<Ingredient, Integer> entry : k.getItem().getRecipe().entrySet()) {
                ItemIngredientTreeCellValues ingredientCell = new ItemIngredientTreeCellValues(k.getOrderId(), k.getItem().getId(), k.getTableNo(), entry.getKey().getName(), false, entry.getValue(), k.getItem().getDateOrdered(), k.getItem().getKitchenStatus());
                makeBranch(ingredientCell, itemRoot);
            }
        }

        for(KitchenOrderInfo k:dessertList){
            ItemIngredientTreeCellValues cell = new ItemIngredientTreeCellValues(k.getOrderId(), k.getItem().getId(), k.getTableNo(), k.getItem().getName(), true, k.getQty(), k.getItem().getDateOrdered(), k.getItem().getKitchenStatus());
            TreeItem<ItemIngredientTreeCellValues> itemRoot = makeBranch(cell, grillRoot);
            for (Map.Entry<Ingredient, Integer> entry : k.getItem().getRecipe().entrySet()) {
                ItemIngredientTreeCellValues ingredientCell = new ItemIngredientTreeCellValues(k.getOrderId(), k.getItem().getId(), k.getTableNo(), entry.getKey().getName(), false, entry.getValue(), k.getItem().getDateOrdered(), k.getItem().getKitchenStatus());
                makeBranch(ingredientCell, itemRoot);
            }
        }


    }

    public void addNewItemsToTreeViewRoots(){
        filterItemLists(itemList);
        for( int i=fryRoot.getChildren().size();i<fryList.size();i++){
            KitchenOrderInfo k = fryList.get(i);
            ItemIngredientTreeCellValues cell = new ItemIngredientTreeCellValues(k.getOrderId(), k.getItem().getId(), k.getTableNo(), k.getItem().getName(), true, k.getQty(), k.getItem().getDateOrdered(), k.getItem().getKitchenStatus());
            TreeItem<ItemIngredientTreeCellValues> itemRoot = makeBranch(cell, fryRoot);
            for (Map.Entry<Ingredient, Integer> entry : k.getItem().getRecipe().entrySet()) {
                ItemIngredientTreeCellValues ingredientCell = new ItemIngredientTreeCellValues(k.getOrderId(), k.getItem().getId(), k.getTableNo(), entry.getKey().getName(), false, entry.getValue(), k.getItem().getDateOrdered(), k.getItem().getKitchenStatus());
                makeBranch(ingredientCell, itemRoot);
            }
        }
        for( int i=grillRoot.getChildren().size();i<grillList.size();i++){
            KitchenOrderInfo k = grillList.get(i);
            ItemIngredientTreeCellValues cell = new ItemIngredientTreeCellValues(k.getOrderId(), k.getItem().getId(), k.getTableNo(), k.getItem().getName(), true, k.getQty(), k.getItem().getDateOrdered(), k.getItem().getKitchenStatus());
            TreeItem<ItemIngredientTreeCellValues> itemRoot = makeBranch(cell, grillRoot);
            for (Map.Entry<Ingredient, Integer> entry : k.getItem().getRecipe().entrySet()) {
                ItemIngredientTreeCellValues ingredientCell = new ItemIngredientTreeCellValues(k.getOrderId(), k.getItem().getId(), k.getTableNo(), entry.getKey().getName(), false, entry.getValue(), k.getItem().getDateOrdered(), k.getItem().getKitchenStatus());
                makeBranch(ingredientCell, itemRoot);
            }
        }
        for (int i=sauteRoot.getChildren().size();i<sauteList.size();i++){
            KitchenOrderInfo k = sauteList.get(i);
            ItemIngredientTreeCellValues cell = new ItemIngredientTreeCellValues(k.getOrderId(), k.getItem().getId(), k.getTableNo(), k.getItem().getName(), true, k.getQty(), k.getItem().getDateOrdered(), k.getItem().getKitchenStatus());
            TreeItem<ItemIngredientTreeCellValues> itemRoot = makeBranch(cell, sauteRoot);
            for (Map.Entry<Ingredient, Integer> entry : k.getItem().getRecipe().entrySet()) {
                ItemIngredientTreeCellValues ingredientCell = new ItemIngredientTreeCellValues(k.getOrderId(), k.getItem().getId(), k.getTableNo(), entry.getKey().getName(), false, entry.getValue(), k.getItem().getDateOrdered(), k.getItem().getKitchenStatus());
                makeBranch(ingredientCell, itemRoot);
            }
        }
        for(int i=dessertsRoot.getChildren().size();i<dessertList.size();i++){
            KitchenOrderInfo k = dessertList.get(i);
            ItemIngredientTreeCellValues cell = new ItemIngredientTreeCellValues(k.getOrderId(), k.getItem().getId(), k.getTableNo(), k.getItem().getName(), true, k.getQty(), k.getItem().getDateOrdered(), k.getItem().getKitchenStatus());
            TreeItem<ItemIngredientTreeCellValues> itemRoot = makeBranch(cell, dessertsRoot);
            for (Map.Entry<Ingredient, Integer> entry : k.getItem().getRecipe().entrySet()) {
                ItemIngredientTreeCellValues ingredientCell = new ItemIngredientTreeCellValues(k.getOrderId(), k.getItem().getId(), k.getTableNo(), entry.getKey().getName(), false, entry.getValue(), k.getItem().getDateOrdered(), k.getItem().getKitchenStatus());
                makeBranch(ingredientCell, itemRoot);
            }
        }

    }


    private void filterItemLists(List<KitchenOrderInfo> list){
        fryList = list.stream().filter(k-> k.getItem().getStation().equals("FRY")).collect(Collectors.toList());
        grillList=list.stream().filter(k-> k.getItem().getStation().equals("GRILL")).collect(Collectors.toList());
        dessertList=list.stream().filter(k-> k.getItem().getStation().equals("DESSERTS")).collect(Collectors.toList());
        sauteList=list.stream().filter(k-> k.getItem().getStation().equals("SAUTE")).collect(Collectors.toList());

    }

    private TreeItem<ItemIngredientTreeCellValues> makeBranch(ItemIngredientTreeCellValues cellValue, TreeItem<ItemIngredientTreeCellValues> parent){
        TreeItem<ItemIngredientTreeCellValues> treeItem= new TreeItem<>(cellValue);
        parent.getChildren().add(treeItem);
        return treeItem;
    }

    public Tab getCurrentTab() {
        return currentTab;
    }

}
