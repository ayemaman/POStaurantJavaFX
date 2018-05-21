package postaurant;


import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.converter.NumberStringConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import postaurant.context.FXMLoaderService;
import postaurant.database.UserDatabase;
import postaurant.model.*;
import postaurant.service.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OrderWindowController {
    private ArrayList<Button> itemButtonList;
    private ArrayList<ToggleButton> sectionButtonList;
    private ArrayList<String> allergyList=null;
    private Order order;
    private User user;
    private ObservableList<Map.Entry<Item,Integer>> observableOrder = FXCollections.observableArrayList();
    private ObservableList<Map.Entry<Item, Integer>> originalOrder= FXCollections.observableArrayList();
    private SimpleDoubleProperty total=new SimpleDoubleProperty(0.00);
    private Thread timeThread;


    private int pageSections;
    private int pageItems;

    private final ButtonCreationService buttonCreationService;
    private final MenuService menuService;
    private final UserDatabase userDatabase;
    private final FXMLoaderService fxmLoaderService;
    private final OrderService orderService;
    private final TimeService timeService;
    private final PaymentService paymentService;


    @Value("/FXML/ErrorWindow.fxml")
    private Resource errorWindow;
    @Value("/FXML/AlreadySentWindow.fxml")
    private Resource alreadySentWindow;
    @Value("/FXML/ModifyitemWindow.fxml")
    private Resource modifyItemWindow;
    @Value("/FXML/DubScreen.fxml")
    private Resource dubScreen;
    @Value("POStaurant.css")
    private Resource css;
    @Value("/FXML/SetAllergyWindow.fxml")
    private Resource setAllergyForm;
    @Value("/FXML/ConfirmationExitWindow.fxml")
    private Resource confirmationWindow;
    @Value("img/logo.png")
    private Resource logo;
    @Value("/FXML/Payments.fxml")
    private Resource payments;
    @Value("/FXML/YesNoWindow.fxml")
    private Resource yesNoWindow;

    @FXML
    private Label labelTableNo;
    @FXML
    private Label labelDubId;
    @FXML
    private Label labelOrderId;
    @FXML
    private Label labelTimeOpened;
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
    private Button exitButton;

    @FXML
    private Button setAllergyButton;
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
    @FXML
    private ImageView logoImg;
    @FXML
    private Button upButton;
    @FXML
    private Button downButton;
    @FXML
    private TextField timeField;
    @FXML
    private Button payButton;
    @FXML
    private Button printButton;

    public OrderWindowController(ButtonCreationService buttonCreationService, MenuService menuService, UserDatabase userDatabase, FXMLoaderService fxmLoaderService, OrderService orderService, TimeService timeService, PaymentService paymentService) {
        this.buttonCreationService =buttonCreationService ;
        this.menuService=menuService;
        this.userDatabase=userDatabase;
        this.fxmLoaderService=fxmLoaderService;
        this.orderService=orderService;
        this.timeService=timeService;
        this.paymentService = paymentService;
    }

    public void initialize() throws IOException {
        logoImg.setImage(new Image(logo.getURL().toExternalForm()));
        if(allergyList!=null){
            if(!allergyList.isEmpty()){
                setAllergyButton.setStyle("selectedAllergy");
            }
        }
        sectionButtonList=buttonCreationService.createOrderSectionButtons(true);
        addOnActionToSectionButtons();
        setSectionButtons(sectionGrid,16,true, sectionButtonList);

        itemColumn.setSortable(false);
        priceColumn.setSortable(false);
        qtyColumn.setSortable(false);

        payButton.setOnAction(e-> {
            if (user.getPosition().equals("MANAGER")) {
                if (order != null) {
                    if (observableOrder.isEmpty() && originalOrder.isEmpty()) {
                        orderService.setClosed(order.getId(), user);
                        paymentService.voidPayments(order);
                        try {
                            FXMLLoader loader2 = fxmLoaderService.getLoader(dubScreen.getURL());
                            Parent parent2 = loader2.load();
                            DubScreenController dubScreenController = loader2.getController();
                            dubScreenController.setUser(user);
                            Scene scene2 = new Scene(parent2);
                            scene2.getStylesheets().add(css.getURL().toExternalForm());
                            Stage stage2 = (Stage) ((Node) e.getSource()).getScene().getWindow();
                            stage2.setScene(scene2);
                            stage2.show();
                            timeThread.interrupt();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        if (!observableOrder.equals(originalOrder)) {
                            try {
                                FXMLLoader loader = fxmLoaderService.getLoader(errorWindow.getURL());
                                System.out.println("1herererere");
                                Parent parent = loader.load();
                                ErrorWindowController errorWindowController = loader.getController();
                                errorWindowController.setErrorLabel("Not all items were sent!");
                                Scene scene = new Scene(parent);
                                scene.getStylesheets().add("POStaurant.css");
                                Stage stage = new Stage();
                                stage.initModality(Modality.APPLICATION_MODAL);
                                stage.initStyle(StageStyle.UNDECORATED);
                                stage.setScene(scene);
                                stage.showAndWait();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            try {
                                FXMLLoader loader = fxmLoaderService.getLoader(payments.getURL());
                                Parent parent = loader.load();
                                PaymentsController paymentsController = loader.getController();
                                paymentsController.setOrderId(order.getId(), total.getValue(), user);
                                Scene scene = new Scene(parent);
                                scene.getStylesheets().add(css.getURL().toExternalForm());
                                Stage stage = new Stage();
                                stage.initModality(Modality.APPLICATION_MODAL);
                                stage.initStyle(StageStyle.UNDECORATED);
                                stage.setScene(scene);
                                stage.showAndWait();
                                if (paymentsController.isFullyPaid()) {
                                    List<Payment> paymentList = paymentsController.getPaymentList();
                                    orderService.createReceipt(paymentList, order, user);
                                    try {
                                        FXMLLoader loader2 = fxmLoaderService.getLoader(dubScreen.getURL());
                                        Parent parent2 = loader2.load();
                                        DubScreenController dubScreenController = loader2.getController();
                                        dubScreenController.setUser(user);
                                        Scene scene2 = new Scene(parent2);
                                        scene2.getStylesheets().add(css.getURL().toExternalForm());
                                        Stage stage2 = (Stage) ((Node) e.getSource()).getScene().getWindow();
                                        stage2.setScene(scene2);
                                        stage2.show();
                                        timeThread.interrupt();

                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }


                    }
                } else {
                    try {
                        FXMLLoader loader = fxmLoaderService.getLoader(errorWindow.getURL());
                        Parent parent = loader.load();
                        ErrorWindowController errorWindowController = loader.getController();
                        errorWindowController.setErrorLabel("Table is not saved.");
                        Scene scene = new Scene(parent);
                        scene.getStylesheets().add("POStaurant.css");
                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.setScene(scene);
                        stage.showAndWait();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            } else {
                if (!observableOrder.equals(originalOrder) || originalOrder.isEmpty()) {
                    try {
                        FXMLLoader loader = fxmLoaderService.getLoader(errorWindow.getURL());
                        Parent parent = loader.load();
                        ErrorWindowController errorWindowController = loader.getController();
                        errorWindowController.setErrorLabel("Not all items were sent!");
                        Scene scene = new Scene(parent);
                        scene.getStylesheets().add("POStaurant.css");
                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.setScene(scene);
                        stage.showAndWait();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    try {
                        FXMLLoader loader = fxmLoaderService.getLoader(payments.getURL());
                        Parent parent = loader.load();
                        PaymentsController paymentsController = loader.getController();
                        paymentsController.setOrderId(order.getId(), total.getValue(), user);
                        Scene scene = new Scene(parent);
                        scene.getStylesheets().add(css.getURL().toExternalForm());
                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.setScene(scene);
                        stage.showAndWait();
                        if (paymentsController.isFullyPaid()) {
                            List<Payment> paymentList = paymentsController.getPaymentList();
                            orderService.createReceipt(paymentList, order, user);
                            try {
                                FXMLLoader loader2 = fxmLoaderService.getLoader(dubScreen.getURL());
                                Parent parent2 = loader2.load();
                                DubScreenController dubScreenController = loader2.getController();
                                dubScreenController.setUser(user);
                                Scene scene2 = new Scene(parent2);
                                scene2.getStylesheets().add(css.getURL().toExternalForm());
                                Stage stage2 = (Stage) ((Node) e.getSource()).getScene().getWindow();
                                stage2.setScene(scene2);
                                stage2.show();
                                timeThread.interrupt();

                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        printButton.setOnAction(e-> {
            if ((!originalOrder.equals(observableOrder))) {
                try {
                    FXMLLoader loader = fxmLoaderService.getLoader(errorWindow.getURL());
                    Parent parent = loader.load();
                    ErrorWindowController errorWindowController = loader.getController();
                    errorWindowController.setErrorLabel("Not all items were sent!");
                    Scene scene = new Scene(parent);
                    scene.getStylesheets().add("POStaurant.css");
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setScene(scene);
                    stage.showAndWait();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else if (originalOrder.isEmpty()) {
                try {
                    FXMLLoader loader = fxmLoaderService.getLoader(errorWindow.getURL());
                    Parent parent = loader.load();
                    ErrorWindowController errorWindowController = loader.getController();
                    errorWindowController.setErrorLabel("No items were put through!");
                    Scene scene = new Scene(parent);
                    scene.getStylesheets().add("POStaurant.css");
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setScene(scene);
                    stage.showAndWait();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else {
                orderService.createPreCheck(originalOrder, order, user);
            }

        });

        upButton.setOnAction(e->setItemButtons(itemGrid, 21, false, itemButtonList));

        downButton.setOnAction(e-> setItemButtons(itemGrid, 21, true, itemButtonList));

        setAllergyButton.setOnAction(e->{
            try {
                FXMLLoader loader=fxmLoaderService.getLoader(setAllergyForm.getURL());
                Parent parent=loader.load();
                SetAllergyWindowController setAllergyWindowController=loader.getController();
                setAllergyWindowController.setAllergyList(allergyList);
                Scene scene=new Scene(parent);
                scene.getStylesheets().add("POStaurant.css");
                Stage stage=new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(scene);
                stage.showAndWait();
                allergyList= (ArrayList<String>)setAllergyWindowController.getAllergyList();
                addOnActionToSectionButtons();
                for(ToggleButton tb:sectionButtonList){
                    tb.setSelected(false);
                }
                setSectionButtons(sectionGrid, 16, true, sectionButtonList);

            } catch (IOException e1) {
                e1.printStackTrace();
            }
            if(allergyList!=null){
                if(!allergyList.isEmpty()){
                    setAllergyButton.setId("selectedAllergy");
                }else{
                    setAllergyButton.setId("nonSelectedAllergy");
                }
            }
        });

        exitButton.setOnAction(e->{
            try{
                FXMLLoader loader=fxmLoaderService.getLoader(confirmationWindow.getURL());
                Parent parent=loader.load();
                ConfirmationExitWindowController confirmationExitWindowController =loader.getController();
                confirmationExitWindowController.setConfirmationLabel("Are you sure you want to exit?");
                Scene scene=new Scene(parent);
                scene.getStylesheets().add("POStaurant.css");
                Stage stage=new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(scene);
                stage.showAndWait();
                if(confirmationExitWindowController.confirmed()){
                    if(!originalOrder.isEmpty() && !observableOrder.isEmpty()) {
                        orderService.setCheckedByDub(this.order);
                    }
                    try {
                        timeThread.interrupt();
                        FXMLLoader loader1=fxmLoaderService.getLoader(dubScreen.getURL());
                        Parent parent1=loader1.load();
                        DubScreenController dubScreenController=loader1.getController();
                        dubScreenController.setUser(user);
                        Scene scene1=new Scene(parent1);
                        scene1.getStylesheets().add("POStaurant.css");
                        Stage stage1=(Stage)((Node)e.getSource()).getScene().getWindow();
                        stage1.setScene(scene1);
                        stage1.show();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }catch (IOException iOE){
                iOE.printStackTrace();
            }
        });

        sendButton.setOnAction(event -> {
            if(!originalOrder.isEmpty() && !observableOrder.isEmpty()){
                orderService.setCheckedByDub(this.order);
            }
            if(originalOrder.equals(observableOrder)){
                try {
                    FXMLLoader loader=fxmLoaderService.getLoader(dubScreen.getURL());
                    Parent parent=loader.load();
                    DubScreenController dubScreenController=loader.getController();
                    dubScreenController.setUser(this.user);
                    Scene scene=new Scene(parent);
                    scene.getStylesheets().add("POStaurant.css");
                    Stage stage= (Stage)((Button) event.getSource()).getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                    timeThread.interrupt();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else {
                //searching for new items

                for (int i = 0; i < observableOrder.size(); ) {
                    if (originalOrder.contains(observableOrder.get(i))) {
                        observableOrder.remove(observableOrder.get(i));
                    } else {
                        i++;
                    }
                }


                //adding together similar items
                for(int i=0;i<observableOrder.size()-1;){
                    for(int j=i+1;j<observableOrder.size();) {
                        if (observableOrder.get(i).getKey().compareTo(observableOrder.get(j).getKey()) == 0) {
                            observableOrder.get(i).setValue(observableOrder.get(i).getValue() + observableOrder.get(j).getValue());
                            observableOrder.remove(observableOrder.get(j));
                        } else {
                            j++;
                        }
                    }
                    i++;
                }

                if(this.order==null) {
                    orderService.createNewOrder(Double.parseDouble(labelTableNo.getText()), user.getUserID(), LocalDateTime.now(),  LocalDateTime.now());
                    this.order=orderService.getLatestSavedOrder(user.getUserID());
                }

                orderService.sendOrder(this.order.getId(), observableOrder);

                try {
                    FXMLLoader loader=fxmLoaderService.getLoader(dubScreen.getURL());
                    Parent parent=loader.load();
                    DubScreenController dubScreenController=loader.getController();
                    dubScreenController.setUser(this.user);
                    Scene scene=new Scene(parent);
                    scene.getStylesheets().add("POStaurant.css");
                    Stage stage= (Stage)((Button) event.getSource()).getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                    timeThread.interrupt();
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
                        scene.getStylesheets().add(css.getURL().toExternalForm());
                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.setScene(scene);
                        stage.showAndWait();
                    } catch (IOException ioE) {
                        ioE.printStackTrace();
                    }
                } else {
                    if (entry.getKey().getName().substring(0, 6).matches("CUSTOM")) {
                        try {
                            FXMLLoader loader = fxmLoaderService.getLoader(errorWindow.getURL());
                            Parent parent = loader.load();
                            ErrorWindowController errorWindowController = loader.getController();
                            errorWindowController.setErrorLabel("You can't modify CUSTOM items.");
                            Scene scene = new Scene(parent);
                            scene.getStylesheets().add(css.getURL().toExternalForm());
                            Stage stage = new Stage();
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.initStyle(StageStyle.UNDECORATED);
                            stage.setScene(scene);
                            stage.showAndWait();
                        }catch (IOException ioE){
                            ioE.printStackTrace();
                        }
                    } else {
                        try {
                            FXMLLoader loader = fxmLoaderService.getLoader(modifyItemWindow.getURL());
                            Parent parent = loader.load();
                            ModifyItemWindowController modifyItemWindowController = loader.getController();
                            modifyItemWindowController.setup(entry.getKey());
                            Scene scene = new Scene(parent);
                            scene.getStylesheets().add(css.getURL().toExternalForm());
                            Stage stage = new Stage();
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.initStyle(StageStyle.UNDECORATED);
                            stage.setScene(scene);
                            stage.showAndWait();
                            Item item = modifyItemWindowController.getItem();
                            observableOrder.remove(entry);
                            TreeMap<Item, Integer> treeMap = new TreeMap<>();
                            treeMap.put(item, entry.getValue());
                            Map.Entry<Item, Integer> entryNew = treeMap.entrySet().iterator().next();
                            observableOrder.add(entryNew);
                        } catch (IOException ioE) {
                            ioE.printStackTrace();
                        }
                    }
                }
            }else{
                try {
                    FXMLLoader loader = fxmLoaderService.getLoader(errorWindow.getURL());
                    Parent parent = loader.load();
                    ErrorWindowController errorWindowController = loader.getController();
                    errorWindowController.setErrorLabel("No item selected");
                    Scene scene = new Scene(parent);
                    scene.getStylesheets().add(css.getURL().toExternalForm());
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setScene(scene);
                    stage.showAndWait();
                }catch (IOException ioE){
                    ioE.printStackTrace();
                }
            }

        });


        foodTypeButton.setId("typeButton");
        foodTypeButton.setOnAction(event -> {
            pageSections=0;
            sectionButtonList=buttonCreationService.createOrderSectionButtons(true);
            if(!sectionButtonList.isEmpty()) {
                addOnActionToSectionButtons();
                setSectionButtons(sectionGrid, 16, true, sectionButtonList);
            }
            else{
                setSectionButtons(sectionGrid, 16, true, new ArrayList<>());

            }
        });

        drinkTypeButton.setId("typeButton");
        drinkTypeButton.setOnAction(event -> {
            pageSections=0;
            sectionButtonList=buttonCreationService.createOrderSectionButtons(false);
            if(!sectionButtonList.isEmpty()) {
                addOnActionToSectionButtons();
                setSectionButtons(sectionGrid, 16, true, sectionButtonList);
            }else{
                setSectionButtons(sectionGrid, 16, true, new ArrayList<>());

            }
        });

        plusButton.setOnAction(event -> {
                Map.Entry<Item, Integer> entry = orderTableView.getSelectionModel().getSelectedItem();
                if(entry!=null) {
                    if (!originalOrder.contains(entry)) {
                        entry.setValue(entry.getValue() + 1);
                        setTotal();
                        orderTableView.refresh();
                    } else {
                        try {
                            FXMLLoader loader = fxmLoaderService.getLoader(alreadySentWindow.getURL());
                            Parent parent = loader.load();
                            Scene scene = new Scene(parent);
                            scene.getStylesheets().add(css.getURL().toExternalForm());
                            Stage stage = new Stage();
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.initStyle(StageStyle.UNDECORATED);
                            stage.setScene(scene);
                            stage.showAndWait();
                        } catch (IOException ioE) {
                            ioE.printStackTrace();
                        }
                    }
                }else{
                    try {
                        FXMLLoader loader = fxmLoaderService.getLoader(errorWindow.getURL());
                        Parent parent = loader.load();
                        ErrorWindowController errorWindowController = loader.getController();
                        errorWindowController.setErrorLabel("No item selected");
                        Scene scene = new Scene(parent);
                        scene.getStylesheets().add(css.getURL().toExternalForm());
                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.setScene(scene);
                        stage.showAndWait();
                    }catch (IOException ioE){
                        ioE.printStackTrace();
                    }
                }
        });

        minusButton.setOnAction(event -> {
            Map.Entry<Item, Integer> entry = orderTableView.getSelectionModel().getSelectedItem();
            if(entry!=null) {
                if (!originalOrder.contains(entry)) {
                    if (entry.getValue() > 1) {
                        entry.setValue(entry.getValue() - 1);
                        setTotal();
                        orderTableView.refresh();
                    } else {
                        try {
                            FXMLLoader loader=fxmLoaderService.getLoader(errorWindow.getURL());
                            Parent parent=loader.load();
                            ErrorWindowController errorWindowController=loader.getController();
                            errorWindowController.setErrorLabel("Qty. is 1");
                            Scene scene=new Scene(parent);
                            scene.getStylesheets().add("POStaurant.css");
                            Stage stage=new Stage();
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.initStyle(StageStyle.UNDECORATED);
                            stage.setScene(scene);
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    try {
                        FXMLLoader loader = fxmLoaderService.getLoader(alreadySentWindow.getURL());
                        Parent parent = loader.load();
                        Scene scene = new Scene(parent);
                        scene.getStylesheets().add(css.getURL().toExternalForm());
                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.setScene(scene);
                        stage.showAndWait();
                    } catch (IOException ioE) {
                        ioE.printStackTrace();
                    }
                }
            }else{
                try {
                    FXMLLoader loader = fxmLoaderService.getLoader(errorWindow.getURL());
                    Parent parent = loader.load();
                    ErrorWindowController errorWindowController = loader.getController();
                    errorWindowController.setErrorLabel("No item selected");
                    Scene scene = new Scene(parent);
                    scene.getStylesheets().add(css.getURL().toExternalForm());
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setScene(scene);
                    stage.showAndWait();
                }catch (IOException ioE){
                    ioE.printStackTrace();
                }
            }
        });

        voidButton.setOnAction(event -> {
            Map.Entry<Item,Integer> entry=orderTableView.getSelectionModel().getSelectedItem();
            if(entry!=null) {
                if (!originalOrder.contains(entry)) {
                    observableOrder.remove(entry);
                    orderTableView.refresh();
                    setTotal();
                } else {
                        if (!user.getPosition().equals("MANAGER")) {
                            try {
                                FXMLLoader loader = fxmLoaderService.getLoader(alreadySentWindow.getURL());
                                Parent parent = loader.load();
                                Scene scene = new Scene(parent);
                                scene.getStylesheets().add(css.getURL().toExternalForm());
                                Stage stage = new Stage();
                                stage.initModality(Modality.APPLICATION_MODAL);
                                stage.initStyle(StageStyle.UNDECORATED);
                                stage.setScene(scene);
                                stage.showAndWait();
                            } catch (IOException ioE) {
                                ioE.printStackTrace();
                            }
                        } else{
                            try{
                                FXMLLoader loader=fxmLoaderService.getLoader(yesNoWindow.getURL());
                                Parent parent=loader.load();
                                YesNoWindowController yesNoWindowController=loader.getController();
                                yesNoWindowController.setLabelTexts("Are you sure you want to void this item?",entry.getKey().getName());
                                Scene scene = new Scene(parent);
                                scene.getStylesheets().add(css.getURL().toExternalForm());
                                Stage stage = new Stage();
                                stage.initModality(Modality.APPLICATION_MODAL);
                                stage.initStyle(StageStyle.UNDECORATED);
                                stage.setScene(scene);
                                stage.showAndWait();
                                if(yesNoWindowController.getAnswer()){
                                    originalOrder.remove(entry);
                                    observableOrder.remove(entry);
                                    orderTableView.refresh();
                                    setTotal();
                                    orderService.voidItemFromOrder(order.getId(),entry.getKey().getId(),entry.getValue());
                                }
                            }catch (IOException ioE){
                                ioE.printStackTrace();
                            }

                    }
                }
            }else{
                try {
                    FXMLLoader loader = fxmLoaderService.getLoader(errorWindow.getURL());
                    Parent parent = loader.load();
                    ErrorWindowController errorWindowController = loader.getController();
                    errorWindowController.setErrorLabel("No item selected");
                    Scene scene = new Scene(parent);
                    scene.getStylesheets().add(css.getURL().toExternalForm());
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setScene(scene);
                    stage.showAndWait();
                }catch (IOException ioE){
                    ioE.printStackTrace();
                }
            }

        });

        Runnable time = () -> {
            boolean run=true;
            while (run) {
                try {
                   timeField.setText(timeService.createTimeOnly());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    run=false;
                }
            }

        };
        timeThread= new Thread(time);
        timeThread.setDaemon(true);
        timeThread.start();


    }
    public void setUser(User user){
        this.user=user;
    }
    public void setOrderId(Long id) {
        if (id != null) {
            this.order = userDatabase.getOrderById(id);
            this.observableOrder.addAll(order.getOrderItems().entrySet());
            Comparator<Map.Entry<Item,Integer>> byTime = Comparator.comparing(s1 -> s1.getKey().getDateOrdered());
            Comparator<Map.Entry<Item,Integer>> byName = Comparator.comparing(s1 -> s1.getKey().getName());
            this.originalOrder.addAll(order.getOrderItems().entrySet());
            FXCollections.sort(observableOrder,byTime.thenComparing(byName));
            FXCollections.sort(originalOrder, byTime.thenComparing(byName));
            setTotal();
        }

        totalTextField.textProperty().bindBidirectional(total, new NumberStringConverter());

        itemColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue().getKey().getName()));
        priceColumn.setCellValueFactory(data -> new ReadOnlyDoubleWrapper(data.getValue().getKey().getPrice()));
        qtyColumn.setCellValueFactory(data -> new ReadOnlyIntegerWrapper(data.getValue().getValue()));
        orderTableView.setItems(observableOrder);
        orderTableView.setRowFactory(new Callback<TableView<Map.Entry<Item, Integer>>, TableRow<Map.Entry<Item, Integer>>>() {
            @Override
            public TableRow<Map.Entry<Item, Integer>> call(TableView<Map.Entry<Item, Integer>> param) {
                return new TableRow<Map.Entry<Item, Integer>>() {
                    @Override
                    protected void updateItem(Map.Entry<Item, Integer> entry, boolean empty) {
                        super.updateItem(entry, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            if (entry != null) {
                                if (entry.getKey().getKitchenStatus().equals("SENT")) {
                                    this.setStyle("-fx-background-color: grey");
                                } else if (entry.getKey().getKitchenStatus().equals("SEEN")) {
                                    this.setId(("SeenCells"));
                                } else if (entry.getKey().getKitchenStatus().equals("READY")) {
                                    this.setStyle("-fx-background-color:#90EE90");
                                } else {
                                    setStyle("");
                                }
                                // if 20 minutes has passed since ordering and it wasn't bumped
                                if (!entry.getKey().getKitchenStatus().equals("")) {
                                    if (!entry.getKey().getKitchenStatus().equals("BUMPED")) {
                                        if (LocalDateTime.now().isAfter(entry.getKey().getDateOrdered().plusMinutes(20))) {
                                            this.setStyle("-fx-background-color:red");
                                        }
                                    } else {
                                        this.setStyle("-fx-background-color:green");
                                    }

                                }
                            }


                            }
                        }

                };
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

    public void setLabels(Double tableNo, Long orderId, String firstName,LocalDateTime timeOpened){
        labelTableNo.setText(""+tableNo);
        if(orderId!=null) {
            labelOrderId.setText("Order ID: " + orderId);
        }else{
            labelOrderId.setText("Order ID: N/A");
        }
        labelDubId.setText(firstName);
        if(timeOpened!=null) {
            labelTimeOpened.setText(""+timeOpened);
        }else{
            labelTimeOpened.setText("TIME OPENED: N/A");
        }
    }

    private boolean isNextPage(int page, List list, int size) {
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

    public void setSectionButtons(GridPane gridPane, Integer size, boolean forward, List<ToggleButton> list) {
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
        for(ToggleButton b:sectionButtonList){
            for (int i=0;i<(itemGrid.getChildren()).size();){
                itemGrid.getChildren().remove(itemGrid.getChildren().get(i));
            }
            b.setOnAction(e-> {
                if(!sectionButtonList.isEmpty()) {
                    for(ToggleButton tb:sectionButtonList){
                        tb.setSelected(false);
                    }
                }
                pageItems=0;
                itemButtonList = buttonCreationService.createItemButtonsForSection(b.getText(), false,allergyList);
                addOnActionToItemButtons();
                setItemButtons(itemGrid, 21, true, itemButtonList);
                b.setSelected(true);
            });
        }
    }

    public void addOnActionToItemButtons(){
        for(Button b: itemButtonList){
            b.setOnAction(e->{
                if(!b.getId().equals("86")){
                    Item item = menuService.getItemById(Long.parseLong(b.getText().substring(0, b.getText().indexOf("\n"))));
                    TreeMap<Item, Integer> treeMap = new TreeMap<>();
                    treeMap.put(item, 1);
                    observableOrder.add(treeMap.entrySet().iterator().next());
                    orderTableView.refresh();
                    orderTableView.getSelectionModel().selectLast();
                    setTotal();
                }
            });
        }
    }


    public void setItemButtons(GridPane gridPane, Integer size, boolean forward, List<Button> list) {
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
                        if (x == 2) {
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
                            if (x == 2) {
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
                            if (x == 2) {
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
                        if (x == 2) {
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
                    if (x == 2) {
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
