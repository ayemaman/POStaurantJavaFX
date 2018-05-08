package postaurant.context;



import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import postaurant.model.KitchenOrderInfo;

public class QCNode extends VBox {
    private HBox hBox;
    private ListView<KitchenOrderInfo> listView;
    private Long orderId;

    public QCNode() {
        setPrefSize(160, 200);
        this.hBox = new HBox();
        hBox.setPrefSize(160, 40);

        VBox vbox = new VBox();
        vbox.setPrefSize(160, 40);
        Label label1 = new Label();
        label1.setText("TABLE NO: ");
        vbox.getChildren().add(label1);
        hBox.getChildren().add(vbox);

        listView = new ListView<>();
        listView.setPrefSize(160, 160);
        listView.setCellFactory(lv -> {
            ListCell<KitchenOrderInfo> cell = new ListCell<>();
            cell.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem == null) {
                    cell.setText(null);
                    cell.setStyle("-fx-background-color:grey");
                } else {
                    cell.setText(newItem.getItem().getName() + " Qty: " + newItem.getQty());
                    if (newItem.getItem().getKitchenStatus().equals("SEEN")) {
                        cell.setStyle("-fx-background-color:blue");

                    } else if (newItem.getItem().getKitchenStatus().equals("READY")) {
                        cell.setStyle("-fx-background-color:green");
                    }
                }
            });
            return cell;
        });
    }


    public Label getFirstLabel(){
        VBox vbox1=(VBox) this.hBox.getChildren().get(0);
        return (Label)vbox1.getChildren().get(0);
    }

    public ListView<KitchenOrderInfo> getListView(){
        return listView;
    }

}
