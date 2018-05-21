package postaurant;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import postaurant.context.FXMLoaderService;
import postaurant.exception.InputValidationException;
import postaurant.model.CardPayment;
import postaurant.model.Payment;
import postaurant.model.User;
import postaurant.service.ButtonCreationService;

import java.io.IOException;
import java.util.ArrayList;


@Component
public class NewPaymentController {

    private final FXMLoaderService fxmLoaderService;
    private final ButtonCreationService buttonCreationService;
    private Payment payment;
    private CardPayment cardPayment;
    private Long orderId;


    private boolean saved=false;
    private boolean cash;

    @Value("/FXML/ErrorWindow.fxml")
    private Resource errorWindow;

    @Value("POStaurant.css")
    private Resource css;



    private boolean lowercase=true;
    private StringProperty amount;
    private StringProperty name;
    private StringProperty card;
    private StringProperty exp;
    private StringProperty bank;
    private TextField currentTextField;

    @FXML
    private Button addButton;
    @FXML
    private Button exitButton;
    @FXML
    private TextField amountField;
    @FXML
    private ChoiceBox<String> typeBox;
    @FXML
    private TextField nameField;
    @FXML
    private TextField cardField;
    @FXML
    private ChoiceBox<String> cardTypeField;
    @FXML
    private TextField expField;
    @FXML
    private TextField bankField;
    @FXML
    private GridPane keyboardGrid;
    @FXML
    private HBox spacebarHBox;

    public NewPaymentController(FXMLoaderService fxmLoaderService, ButtonCreationService buttonCreationService) {
        this.fxmLoaderService = fxmLoaderService;
        this.buttonCreationService = buttonCreationService;
    }

    public void initialize(){
        amount=new SimpleStringProperty("");
        name=new SimpleStringProperty("");
        card=new SimpleStringProperty("");
        exp=new SimpleStringProperty("");
        bank=new SimpleStringProperty("");;
        currentTextField=amountField;
        amountField.textProperty().bind(amount);
        ObservableList<String> typeList = FXCollections.observableArrayList("CASH", "CARD");
        typeBox.setValue("CASH");
        typeBox.setItems(typeList);
        nameField.textProperty().bind(name);
        cardField.textProperty().bind(card);
        expField.textProperty().bind(exp);
        bankField.textProperty().bind(bank);

        ObservableList<String> cardTypeList = FXCollections.observableArrayList("VISA", "VISA ELECTRON","DINNERS CLUB","AMERICAN EXPRESS","MASTERCARD","MAESTRO");
        cardTypeField.setValue("VISA");
        cardTypeField.setItems(cardTypeList);

        setKeyboard(true);
        ColumnConstraints constraints=new ColumnConstraints();
        constraints.setHalignment(HPos.CENTER);
        keyboardGrid.getColumnConstraints().add(constraints);

        typeBox.setOnAction(e->{
            if(typeBox.getSelectionModel().getSelectedItem().equals("CASH")){
                currentTextField=amountField;
                nameField.setDisable(true);
                cardField.setDisable(true);
                expField.setDisable(true);
                cardTypeField.setDisable(true);
                bankField.setDisable(true);
            }else if(typeBox.getSelectionModel().getSelectedItem().equals("CARD")){
                nameField.setDisable(false);
                cardField.setDisable(false);
                expField.setDisable(false);
                cardTypeField.setDisable(false);
                bankField.setDisable(false);
            }
        });
        amountField.setOnMouseClicked(e->{
            this.currentTextField=amountField;
            System.out.println(currentTextField);
        });

        nameField.setOnMouseClicked(e->{
            this.currentTextField=(TextField) e.getSource();
        });

        cardField.setOnMouseClicked(e->{
            this.currentTextField=(TextField) e.getSource();
        });
        expField.setOnMouseClicked(e->{
            this.currentTextField=(TextField) e.getSource();
        });
        bankField.setOnMouseClicked(e->{
            this.currentTextField=(TextField) e.getSource();
        });



        exitButton.setOnAction(e-> {
                    saved=false;
                    ((Node) e.getSource()).getScene().getWindow().hide();
                });

        addButton.setOnAction(e->{
            if(typeBox.getSelectionModel().getSelectedItem().equals("CASH")) {

                payment = new Payment();
                payment.setOrderId(orderId);
                try {
                    payment.setAmount(Double.parseDouble(amount.getValue()));
                } catch (NumberFormatException ex) {
                    try {
                        loadErrorWindow(errorWindow, css, "Wrong Amount");
                    } catch (IOException e1) {

                        e1.printStackTrace();
                    }
                } catch (InputValidationException e1) {
                    try {
                        loadErrorWindow(errorWindow, css, "Amount can't be 0");
                    } catch (IOException e2) {

                        e1.printStackTrace();
                    }
                }
                if(payment.getAmount()!=null) {
                    ((Node) e.getSource()).getScene().getWindow().hide();
                    saved=true;
                    cash=true;
                }

            }else {

                cardPayment = new CardPayment();
                cardPayment.setOrderId(orderId);
                try {
                    cardPayment.setAmount(Double.parseDouble(amount.getValue()));
                } catch (NumberFormatException ex) {
                    try {
                        loadErrorWindow(errorWindow, css, "Wrong Amount");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } catch (InputValidationException e1) {
                    try {
                        loadErrorWindow(errorWindow, css, "Can't be 0.00");
                    } catch (IOException e2) {
                        e1.printStackTrace();
                    }
                }
                try{
                    cardPayment.setCardName(name.getValue());
                }catch (InputValidationException e2){

                    try {
                        loadErrorWindow(errorWindow,css,"Wrong Client Name");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                try {
                    String buffer=card.getValue().replace(" ","");
                    cardPayment.setCardNo(Long.parseLong(buffer));
                } catch (Exception e1) {

                    try {
                        loadErrorWindow(errorWindow,css,"Wrong Card Number");
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                try{
                    cardPayment.setBankName(bank.getValue());

                } catch (InputValidationException e1) {

                    try {
                        loadErrorWindow(errorWindow,css,"Wrong Bank Name");
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                try{
                    cardPayment.setExpDate(exp.getValue());
                } catch (InputValidationException e1) {

                    try {
                        loadErrorWindow(errorWindow,css,"Wrong Exp Date");
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                try {
                    cardPayment.setCardType(cardTypeField.getSelectionModel().getSelectedItem());
                } catch (InputValidationException e1) {

                    e1.printStackTrace();
                }
                if(cardPayment.getCardNo()!=null&&cardPayment.getCardName()!=null&&cardPayment.getCardType()!=null&&cardPayment.getBankName()!=null&&cardPayment.getExpDate()!=null&&cardPayment.getAmount()!=null){
                    ((Node)e.getSource()).getScene().getWindow().hide();
                    saved=true;
                    cash=false;
                }

            }
        });


    }

    private void loadErrorWindow(Resource fxmlresource, Resource css, String error) throws IOException {
        FXMLLoader loader=fxmLoaderService.getLoader(fxmlresource.getURL());
        Parent parent=loader.load();
        ErrorWindowController errorWindowController=loader.getController();
        errorWindowController.setErrorLabel(error);
        Scene scene=new Scene(parent);
        scene.getStylesheets().add(css.getURL().toExternalForm());
        Stage stage=new Stage();
        stage.setScene(scene);
        stage.showAndWait();
    }



    private void setKeyboard(boolean lowercase){
        int x=0;
        int y=0;
        for(int i=0; i< (keyboardGrid.getChildren().size());){
            keyboardGrid.getChildren().remove(keyboardGrid.getChildren().get(i));
        }
        for(int i=0;i<spacebarHBox.getChildren().size();){
            spacebarHBox.getChildren().remove(spacebarHBox.getChildren().get(i));
        }

        ArrayList<Button> buttonList=buttonCreationService.createKeyboardButtons(lowercase,55,475,30);

        for(int i=0; i<buttonList.size()-2;i++) {
            buttonList.get(i).setOnAction(this::onKeyboardPress);
            this.lowercase = lowercase;
            keyboardGrid.add(buttonList.get(i), x, y);
            if (x > 8) {
                x = 0;
                y++;
            } else {
                x++;
            }
        }
        buttonList.get(40).setOnAction(this::onKeyboardPress);
        buttonList.get(41).setOnAction(this::onKeyboardPress);
        spacebarHBox.getChildren().add(buttonList.get(40));
        spacebarHBox.getChildren().add(buttonList.get(41));
        spacebarHBox.setMargin(buttonList.get(40),new Insets(0,60,0,3));
    }



    public void onKeyboardPress(ActionEvent event) {
        StringProperty stringProperty = null;
        if (currentTextField.equals(amountField)) {
            stringProperty = amount;
        } else if (currentTextField.equals(nameField)) {
            stringProperty = name;
        }else if(currentTextField.equals(cardField)){
            stringProperty = card;
        }else if(currentTextField.equals(expField)){
            stringProperty=exp;
        }else if(currentTextField.equals(bankField)){
            stringProperty=bank;
        }
        if (stringProperty != null) {
            Button button = (Button) event.getSource();
            switch (button.getId()) {
                case "capsKey":
                    setKeyboard(!lowercase);
                    break;
                case "backspaceKey":
                    if (!stringProperty.getValue().equals("")) {
                        stringProperty.set(stringProperty.getValue().substring(0, stringProperty.getValue().length() - 1));
                    }
                    break;
                case "deleteKey":
                    stringProperty.set("");
                    break;
                case "spacebarKey":
                    stringProperty.set(stringProperty.getValue() + " ");
                    break;
                default:
                    stringProperty.set(stringProperty.getValue() + button.getText());
                    break;

            }
        }

    }
    public void setOrderId(Long orderId){
        this.orderId=orderId;
    }
    public Long getOrderId(){
        return this.orderId;
    }

    public Payment getPayment() {
        return payment;
    }

    public CardPayment getCardPayment() {
        return cardPayment;
    }

    public boolean wasSaved(){
        return saved;
    }
    public boolean isCash(){
        return cash;
    }


}

