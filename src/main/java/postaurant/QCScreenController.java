package postaurant;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import postaurant.context.FXMLoaderService;
import postaurant.context.QCBox;
import postaurant.model.KitchenOrderInfo;
import postaurant.service.*;

import javax.print.PrintException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class QCScreenController {

    private List<QCBox> qcNodes;
    private List<KitchenOrderInfo> kitchenOrderInfos;



    private ListView<KitchenOrderInfo> currentOrder;
    private int page;

    @Value("/FXML/POStaurant.fxml")
    private Resource postraunt;
    @Value("/FXML/ErrorWindow.fxml")
    private Resource errorWindow;
    @Value("Postaurant.css")
    private Resource css;

    @FXML
    private GridPane gridPane;
    @FXML
    private HBox hBOX;
    @FXML
    private Button bumpButton;
    @FXML
    private Button leftButton;
    @FXML
    private Button rightButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button timeButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button reprintButton;
    @FXML
    private TextField timeTextField;
    @FXML
    private Label pageLabel;



    private final MenuService menuService;
    private final ButtonCreationService buttonCreationService;
    private final TimeService timeService;
    private final FXMLoaderService fxmLoaderService;
    private final OrderService orderService;
    private final PrintTextFileService printTextFileService;


    public QCScreenController(MenuService menuService, ButtonCreationService buttonCreationService, TimeService timeService, FXMLoaderService fxmLoaderService, OrderService orderService, PrintTextFileService printTextFileService){
        this.menuService=menuService;
        this.buttonCreationService=buttonCreationService;
        this.timeService = timeService;
        this.fxmLoaderService = fxmLoaderService;
        this.orderService = orderService;
        this.printTextFileService = printTextFileService;
    }

    public void initialize() {
        page = 0;
        qcNodes = buttonCreationService.createQCNodes(false);
        kitchenOrderInfos = menuService.getAllOrderedItemsForQC();
        for (VBox v : qcNodes) {
            System.out.println(v);
        }
        styleQCVBoxes();
        setQCNodes(gridPane, 10, true, qcNodes);
        timeButton.setOnAction(e -> {
            timeService.doTime(timeTextField);
        });

        reprintButton.setOnAction(e->{
            File file=new File("./checks/qcCheck.txt");
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
                printTextFileService.printFileTest(file);
            } catch (IOException | PrintException e1) {
                e1.printStackTrace();
            }
        });
        bumpButton.setOnAction(e -> {
            boolean allReady = true;
            if (currentOrder == null) {
                try {
                    FXMLLoader loader = fxmLoaderService.getLoader(errorWindow.getURL());
                    Parent parent = loader.load();
                    ErrorWindowController errorWindowController = loader.getController();
                    errorWindowController.setErrorLabel("No order selected!");
                    Scene scene = new Scene(parent);
                    scene.getStylesheets().add("POStaurant.css");
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setScene(scene);
                    stage.showAndWait();
                } catch (IOException ioE) {
                    ioE.printStackTrace();
                }
            } else {
                for (KitchenOrderInfo k : currentOrder.getItems()) {
                    if (!k.getItem().getKitchenStatus().equals("READY")) {
                        allReady = false;
                    }
                }
                if (!allReady) {
                    try {
                        FXMLLoader loader = fxmLoaderService.getLoader(errorWindow.getURL());
                        Parent parent = loader.load();
                        ErrorWindowController errorWindowController = loader.getController();
                        errorWindowController.setErrorLabel("Not all items are ready!");
                        Scene scene = new Scene(parent);
                        scene.getStylesheets().add("POStaurant.css");
                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.setScene(scene);
                        stage.showAndWait();
                    } catch (IOException ioE) {
                        ioE.printStackTrace();
                    }
                } else {
                    orderService.createQCBump(currentOrder.getItems());
                    for (KitchenOrderInfo k : currentOrder.getItems()) {
                        menuService.setKitchenStatusToBumped(k.getOrderId(), k.getItem().getId(), k.getItem().getDateOrdered());
                        kitchenOrderInfos.remove(k);
                    }
                    qcNodes = buttonCreationService.createQCNodes(false);
                    styleQCVBoxes();
                    page = 0;
                    setQCNodes(gridPane, 10, true, qcNodes);
                }
            }

        });

        leftButton.setOnAction(e -> {
            setQCNodes(gridPane, 10, false, qcNodes);
        });

        rightButton.setOnAction(e -> {
            setQCNodes(gridPane, 10, true, qcNodes);
        });

        exitButton.setOnAction(e -> {
            try {
                Parent parent = fxmLoaderService.getLoader(postraunt.getURL()).load();
                Scene scene = new Scene(parent);
                scene.getStylesheets().add(css.getURL().toString());
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setScene(scene);
            } catch (IOException iOE) {
                iOE.printStackTrace();
            }
        });

        refreshButton.setOnAction(e->{
            List<KitchenOrderInfo> buffer=menuService.getAllOrderedItemsForQC();
            boolean equal=true;
            if(kitchenOrderInfos.size()!=buffer.size()){
                equal=false;
            }else {
                for (int i = 0; i < kitchenOrderInfos.size(); i++) {
                    if (!kitchenOrderInfos.get(i).getItem().getKitchenStatus().equals(buffer.get(i).getItem().getKitchenStatus())) {
                        equal = false;
                    }
                }
            }
            if(!equal){
                System.out.println("SETTING ITEMS");
                kitchenOrderInfos=buffer;
                qcNodes=buttonCreationService.createQCNodes(false);
                page--;
                setQCNodes(gridPane,10,true,qcNodes);
                styleQCVBoxes();
            }
            else{
                try {
                    FXMLLoader loader=fxmLoaderService.getLoader(errorWindow.getURL());
                    Parent parent=loader.load();
                    ErrorWindowController errorWindowController=loader.getController();
                    errorWindowController.setErrorLabel("No updates");
                    Scene scene=new Scene(parent);
                    scene.getStylesheets().add(""+css.getURL());
                    Stage stage=new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setScene(scene);
                    stage.showAndWait();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });


    }




    //styling custom QC Vboxes

    private void styleQCVBoxes(){
        for(QCBox q:qcNodes){
            ListView<KitchenOrderInfo> bufferList =(ListView<KitchenOrderInfo>) q.getChildren().get(1);
            q.setOnMouseClicked(e->{
                currentOrder=bufferList;
                for(QCBox q2:qcNodes){
                    Label buffer2=(Label)q2.getChildren().get(0);
                    buffer2.setId("UnselectedLabel");
                }
                Label buffer=(Label)q.getChildren().get(0);
                buffer.setId("SelectedLabel");
            });


            bufferList.setOnMouseClicked(e->{
                currentOrder=bufferList;
                for(QCBox q2:qcNodes){
                    Label buffer2=(Label)q2.getChildren().get(0);
                    buffer2.setId("UnselectedLabel");
                }
                Label buffer=(Label)q.getChildren().get(0);
                buffer.setId("SelectedLabel");
            });
        }
    }

    private void setQCNodes(GridPane gridPane, Integer size, boolean forward, List<QCBox> list) {
        int start;
        int x = 0;
        int y = 0;
        if (forward) {
            if (page == 0) {
                start = 0;
            } else {
                start = page * size;
            }
            //if all buttons don't fit in gridPane
            if (buttonCreationService.isNextPage(page, list, size)) {
                for (int i = 0; i < gridPane.getChildren().size();) {
                    gridPane.getChildren().remove(gridPane.getChildren().get(i));
                }
                if (start == 0) {
                    for (int i = start; i < size; i++) {
                        gridPane.add(list.get(i), x, y);
                        if (x == 4) {
                            x = 0;
                            y++;
                        } else {
                            x++;
                        }

                    }
                    float ceil =(float) list.size() / (float)10;
                    int roundInt = (int) Math.ceil(ceil);
                    page++;
                    pageLabel.setText("PAGE "+page+"/"+roundInt);
                } else {
                    if (buttonCreationService.isNextPage(page + 1, list, size)) {
                        for (int i = start; i < start + size; i++) {
                            gridPane.add(list.get(i), x, y);

                            if (x == 4) {
                                x = 0;
                                y++;
                            } else {
                                x++;
                            }
                        }
                        page++;
                        Double lastPage=Math.ceil(list.size()/10.0);
                        pageLabel.setText("PAGE "+page+"/"+lastPage);
                    } else {
                        for (int i = start; i < list.size(); i++) {
                            gridPane.add(list.get(i), x, y);

                            if (x == 4) {
                                x = 0;
                                y++;
                            } else {
                                x++;
                            }
                        }
                        float ceil =(float) list.size() / (float)10;
                        int roundInt = (int) Math.ceil(ceil);
                        page++;
                        pageLabel.setText("PAGE "+page+"/"+roundInt);
                    }
                }
            } else {
                if (start == 0) {
                    for (int i = 0; i < gridPane.getChildren().size(); ) {
                        gridPane.getChildren().remove(gridPane.getChildren().get(i));
                    }
                    for (int i = start; i < list.size(); i++) {
                        gridPane.add(list.get(i), x, y);

                        if (x == 4) {
                            x = 0;
                            y++;
                        } else {
                            x++;
                        }
                    }

                    float ceil =(float) list.size() / (float)10;
                    int roundInt = (int) Math.ceil(ceil);
                    page++;
                    pageLabel.setText("PAGE "+page+"/"+roundInt);
                }

            }
        } else {
            if (page > 1) {
                for (int i = 0; i < gridPane.getChildren().size(); ) {
                    gridPane.getChildren().remove(gridPane.getChildren().get(i));
                }
                if (page == 2) {
                    start = 0;
                } else {
                    start = (page - 2) * size;
                }
                for (int i = start; i < (start + size); i++) {
                    gridPane.add(list.get(i), x, y);
                    if (x == 4) {
                        x = 0;
                        y++;
                    } else {
                        x++;
                    }
                }
                float ceil =(float) list.size() / (float)10;
                int roundInt = (int) Math.ceil(ceil);
                page--;
                pageLabel.setText("PAGE "+page+"/"+roundInt);
            }

        }
    }



}
