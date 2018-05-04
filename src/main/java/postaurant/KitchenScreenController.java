package postaurant;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.Node;
import javafx.scene.control.*;

import org.springframework.stereotype.Component;
import postaurant.context.FXMLoaderService;

import postaurant.model.Ingredient;
import postaurant.model.Item;
import postaurant.model.ItemIngredientTreeCellValues;
import postaurant.model.KitchenOrderInfo;
import postaurant.service.MenuService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class KitchenScreenController {

    private List<KitchenOrderInfo> itemList;
    private List<KitchenOrderInfo> fryList;
    private List<KitchenOrderInfo> grillList;
    private List<KitchenOrderInfo> sauteList;
    private List<KitchenOrderInfo> dessertList;

    private Tab currenTab;
    private TreeView<ItemIngredientTreeCellValues> currentTreeView;
    private List<KitchenOrderInfo> currentList;

    private final FXMLoaderService fxmLoaderService;
    private final MenuService menuService;



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
    private Button refreshButton;
    @FXML
    private Button seenButton;
    @FXML
    private Button bumpButton;




    public KitchenScreenController(FXMLoaderService fxmLoaderService, MenuService menuService){
        this.fxmLoaderService=fxmLoaderService;
        this.menuService=menuService;
    }


    public void initialize(){

        bumpButton.setOnAction(e->{
            ObservableList<TreeItem<ItemIngredientTreeCellValues>> i= currentTreeView.getSelectionModel().getSelectedItems();
            TreeItem<ItemIngredientTreeCellValues> treeItem=i.get(0);
            ItemIngredientTreeCellValues cellValue=treeItem.getValue();
            menuService.setKitchenStatusToReady(cellValue.getOrderId(),cellValue.getItemId(),cellValue.getDateOrdered());
            itemList= menuService.getAllOrderedItemsForKitchen();
            filterItemLists();
            setupCurrentTableView();
            setTreeView(currentList, currentTreeView);
            currentTreeView.refresh();
        });

        seenButton.setOnAction(e->{
            ObservableList<TreeItem<ItemIngredientTreeCellValues>> i= currentTreeView.getSelectionModel().getSelectedItems();
            TreeItem<ItemIngredientTreeCellValues> treeItem=i.get(0);
            ItemIngredientTreeCellValues cellValue=treeItem.getValue();
            menuService.setKitchenStatusToSeen(cellValue.getOrderId(),cellValue.getItemId(),cellValue.getDateOrdered());
            itemList= menuService.getAllOrderedItemsForKitchen();
            filterItemLists();
            setupCurrentTableView();
            setTreeView(currentList, currentTreeView);
            currentTreeView.refresh();


        });

        refreshButton.setOnAction(e->{
                itemList= menuService.getAllOrderedItemsForKitchen();
                filterItemLists();
                setupCurrentTableView();
                setTreeView(currentList, currentTreeView);
                currentTreeView.refresh();

        });


            fryTab.setOnSelectionChanged(e -> {
                currenTab = (Tab) e.getSource();
                currentList = fryList;
                currentTreeView = fryTreeView;
               setupCurrentTableView();
                if (!currentList.isEmpty()) {
                    setTreeView(currentList, currentTreeView);
                }
            });
            grillTab.setOnSelectionChanged(e -> {
                currenTab = (Tab) e.getSource();
                currentList = grillList;
                currentTreeView = grillTreeView;
                setupCurrentTableView();
                if (!currentList.isEmpty()) {
                    setTreeView(currentList, currentTreeView);
                }
            });
            sauteTab.setOnSelectionChanged(e -> {
                currenTab = (Tab) e.getSource();
                currentList = sauteList;
                currentTreeView = sauteTreeView;
                setupCurrentTableView();
                if (!currentList.isEmpty()) {
                    setTreeView(currentList, currentTreeView);
                }
            });
            dessertsTab.setOnSelectionChanged(e -> {
                currenTab = (Tab) e.getSource();
                currentList = dessertList;
                currentTreeView = dessertsTreeView;
                setupCurrentTableView();
                if (!currentList.isEmpty()) {
                    setTreeView(currentList, currentTreeView);
                }
            });
    }

    public void setup(){
        itemList = menuService.getAllOrderedItemsForKitchen();
        filterItemLists();
        currenTab = fryTab;
        currentList = fryList;
        currentTreeView = fryTreeView;
        setupCurrentTableView();
        setTreeView(currentList, currentTreeView);

    }

    public void setupCurrentTableView(){
        currentTreeView.setCellFactory(tv -> {
            TreeCell<ItemIngredientTreeCellValues> cell = new TreeCell<>();
            cell.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem == null) {
                    cell.setText(null);
                    cell.setStyle("-fx-background-color:grey");
                } else {
                    cell.setText(newItem.toString());
                    if (newItem.isItem()) {
                        if(newItem.getStatus().equals("SEEN")) {
                            cell.setStyle("-fx-background-color:green");

                        }else{
                            if (newItem.getName().matches("^CUSTOM.*")) {
                                cell.setStyle("-fx-background-color:red");
                            } else {
                                cell.setStyle("-fx-background-color:grey");
                            }
                        }
                    } else {
                        cell.setStyle("-fx-background-color:grey");
                    }
                }
            });
            return cell;
        });

    }

    public void setTreeView(List<KitchenOrderInfo> list,TreeView<ItemIngredientTreeCellValues> treeView)
    {
        treeView.setOnEditCommit(tv-> tv.getSource().refresh());
        TreeItem<ItemIngredientTreeCellValues> root=new TreeItem<>();

        for(KitchenOrderInfo k:list){
            ItemIngredientTreeCellValues cell=new ItemIngredientTreeCellValues(k.getOrderId(),k.getItem().getId(),k.getTableNo(),k.getItem().getName(),true,k.getQty(),k.getItem().getDateOrdered(),k.getItem().getKitchenStatus());

            TreeItem<ItemIngredientTreeCellValues> itemRoot= makeBranch(cell,root);
            for(Map.Entry<Ingredient,Integer> entry:k.getItem().getRecipe().entrySet()){
                ItemIngredientTreeCellValues ingredientCell=new ItemIngredientTreeCellValues(k.getOrderId(),k.getItem().getId(),k.getTableNo(),entry.getKey().getName(),false,entry.getValue(),k.getItem().getDateOrdered(),k.getItem().getKitchenStatus());

                makeBranch(ingredientCell,itemRoot);
            }
        }
        treeView.setRoot(root);
        treeView.setShowRoot(false);
        treeView.refresh();
    }

    private void filterItemLists(){
        fryList = itemList.stream().filter(k-> k.getItem().getStation().equals("FRY")).collect(Collectors.toList());
        grillList=itemList.stream().filter(k-> k.getItem().getStation().equals("GRILL")).collect(Collectors.toList());
        dessertList=itemList.stream().filter(k-> k.getItem().getStation().equals("DESSERTS")).collect(Collectors.toList());
        sauteList=itemList.stream().filter(k-> k.getItem().getStation().equals("SAUTE")).collect(Collectors.toList());

    }

    private TreeItem<ItemIngredientTreeCellValues> makeBranch(ItemIngredientTreeCellValues cellValue, TreeItem<ItemIngredientTreeCellValues> parent){
        TreeItem<ItemIngredientTreeCellValues> treeItem= new TreeItem<>(cellValue);
        treeItem.setExpanded(false);
        parent.getChildren().add(treeItem);
        return treeItem;
    }



}
