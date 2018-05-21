package postaurant;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import postaurant.context.FXMLoaderService;
import postaurant.model.CardPayment;
import postaurant.model.Payment;
import postaurant.model.User;
import postaurant.service.OrderService;
import postaurant.service.PaymentService;


import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class PaymentsController {

    private User user;

    private boolean fullyPaid=false;
    private Long orderId;
    private SimpleDoubleProperty total;
    private SimpleDoubleProperty paid;
    private SimpleDoubleProperty left;

    private final FXMLoaderService fxmLoaderService;
    private final PaymentService paymentService;
    private final OrderService orderService;

    @Value("/FXML/NewPayment.fxml")
    private Resource newPayment;
    @Value("/FXML/ErrorWindow.fxml")
    private Resource errorWindow;
    @Value("FXML/YesNoWindow.fxml")
    private Resource yesNoWindow;

    @Value("POStaurant.css")
    private Resource css;

    @Value("img/logo.png")
    private Resource logo;

    @FXML private Button saveButton;
    @FXML private Button exitButton;
    @FXML private Button addButton;
    @FXML private Button removeButton;
    @FXML private Label totalLabel;
    @FXML private Label paidLabel;
    @FXML private Label leftLabel;
    @FXML private ImageView logoImg;
    @FXML private ListView<Payment> paymentsListView;


    private ObservableList<Payment> paymentList;
    private ObservableList<Payment> originalPaymentList;

//todo add payments service
    public PaymentsController(PaymentService paymentService, FXMLoaderService fxmLoaderService, OrderService orderService) {
        this.paymentService=paymentService;
        this.fxmLoaderService=fxmLoaderService;
        this.orderService = orderService;
    }


    public void initialize() throws IOException {
        total=new SimpleDoubleProperty(0.00);
        paid=new SimpleDoubleProperty(0.00);
        left=new SimpleDoubleProperty(0.00);
        totalLabel.textProperty().bindBidirectional(total,new DoubleConverter() );
        paidLabel.textProperty().bindBidirectional(paid, new DoubleConverter());
        leftLabel.textProperty().bindBidirectional(left, new DoubleConverter());

        logoImg.setImage(new Image(logo.getURL().toExternalForm()));
        paymentList= FXCollections.observableArrayList();
        originalPaymentList=FXCollections.observableArrayList();
        addButton.setOnAction(e->{
            try {
                FXMLLoader loader=fxmLoaderService.getLoader(newPayment.getURL());
                Parent parent=loader.load();
                NewPaymentController newPaymentController=loader.getController();
                newPaymentController.setOrderId(orderId);
                Scene scene=new Scene(parent);
                scene.getStylesheets().add(css.getURL().toExternalForm());
                Stage stage=new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(scene);
                stage.showAndWait();
                if(newPaymentController.wasSaved()) {
                    if (newPaymentController.isCash()) {
                        paymentList.add(newPaymentController.getPayment());
                    }else {
                        paymentList.add(newPaymentController.getCardPayment());
                    }
                    countPaidAndLeft();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        removeButton.setOnAction(e->{
            Payment payment=paymentsListView.getSelectionModel().getSelectedItem();
            if(originalPaymentList.contains(payment)){
                if(!user.getPosition().equals("MANAGER")) {
                    try {
                        FXMLLoader loader = fxmLoaderService.getLoader(errorWindow.getURL());
                        Parent parent = loader.load();
                        ErrorWindowController errorWindowController = loader.getController();
                        errorWindowController.setErrorLabel("This item was already saved!");
                        Scene scene = new Scene(parent);
                        scene.getStylesheets().add(css.getURL().toExternalForm());
                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.setScene(scene);
                        stage.showAndWait();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }else {
                    try {
                        FXMLLoader loader = fxmLoaderService.getLoader(yesNoWindow.getURL());
                        Parent parent = loader.load();
                        YesNoWindowController yesNoWindowController = loader.getController();
                        yesNoWindowController.setLabelTexts("Are you sure you want to void this payment?",""+payment.getPaymentId());
                        Scene scene = new Scene(parent);
                        scene.getStylesheets().add(css.getURL().toExternalForm());
                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.setScene(scene);
                        stage.showAndWait();
                        if (yesNoWindowController.getAnswer()) {
                            paymentService.voidPayment(payment);
                            paymentList.remove(payment);
                            originalPaymentList.remove(payment);
                            countPaidAndLeft();
                        }
                    } catch (IOException ioE) {
                        ioE.printStackTrace();
                    }
                }
            }else {
                paymentList.remove(payment);
                countPaidAndLeft();
            }
        });
        saveButton.setOnAction(e->{

            for (int i = 0; i < paymentList.size(); ) {
                if (originalPaymentList.contains(paymentList.get(i))) {
                    paymentList.remove(paymentList.get(i));
                } else {
                    i++;
                }
            }
            if(!paymentList.isEmpty()) {
                paymentService.savePayments(paymentList,user);
            }
            if(left.getValue()<=0.00) {
                orderService.setClosed(orderId,user);
                fullyPaid=true;
            }
            ((Node) e.getSource()).getScene().getWindow().hide();
        });

        exitButton.setOnAction(e->{
            ((Node)e.getSource()).getScene().getWindow().hide();
        });
    }


    public void setOrderId(Long orderId, Double total, User user){
        this.orderId=orderId;
        this.total.setValue(total);
        this.user=user;
        setListView();
    }

    public void setListView(){
        List<Payment> cash=paymentService.getCashPayments(orderId);
        List<CardPayment> card=paymentService.getCardPaymentsForTable(orderId);
        paymentList.addAll(cash);
        paymentList.addAll(card);
        originalPaymentList.addAll(cash);
        originalPaymentList.addAll(card);
        paymentsListView.setItems(paymentList);
        countPaidAndLeft();
    }

    public void countPaidAndLeft(){
        Double buffer=0.00;
        for(Payment p:paymentList){
            buffer+=p.getAmount();
        }
        paid.setValue(buffer);
        left.setValue(total.getValue()-paid.getValue());
    }

    public boolean isFullyPaid(){
        return fullyPaid;
    }

    public List<Payment> getPaymentList(){
        List<Payment> list=new ArrayList<>();
        for(Payment p:this.originalPaymentList){
            list.add(p);
        }
        for(Payment p:this.paymentList){
            list.add(p);
        }
        return list;
    }



    private class DoubleConverter extends StringConverter<Number> {
        NumberFormat format=new DecimalFormat("#0.00");

        @Override
        public String toString(Number object) {
            return object == null ? "" : format.format( object );
        }

        @Override
        public Number fromString(String string) {
            return (string != null && !string.isEmpty()) ? Double.valueOf( string ) : null;
        }
    }

}

