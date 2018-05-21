package postaurant;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import postaurant.context.FXMLoaderService;
import postaurant.model.User;
import postaurant.service.OrderService;
import postaurant.service.ReportService;
import postaurant.service.TimeService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
public class PickADateController {

    private final ReportService reportService;
    private final TimeService timeService;
    private final OrderService orderService;
    private final FXMLoaderService fxmLoaderService;

    private User user;
    private boolean fullreport=false;
    private  ObservableList<String> observableDays31;
    private ObservableList<String> observableDays30;
    private ObservableList<String> observableDays29;
    private ObservableList<String> observableDays28;

    @Value("/FXML/ErrorWindow.fxml")
    private Resource errorWindow;
    @FXML
    private ChoiceBox<String> dayBox;
    @FXML
    private ChoiceBox<String> monthBox;
    @FXML
    private ChoiceBox<String> yearBox;
    @FXML
    private Button printButton;
    @FXML
    private Button exitButton;

    public PickADateController(ReportService reportService, TimeService timeService, OrderService orderService, FXMLoaderService fxmLoaderService) {
        this.reportService = reportService;
        this.timeService = timeService;
        this.orderService = orderService;
        this.fxmLoaderService = fxmLoaderService;
    }

    public void initialize(){

        exitButton.setOnAction(e->{
            ((Node)e.getSource()).getScene().getWindow().hide();
        });

        printButton.setOnAction(e->{
            String day=dayBox.getSelectionModel().getSelectedItem();
            if(day.length()==1){
                day="0"+day;
            }
            String month=monthBox.getSelectionModel().getSelectedItem();
            if(month.length()==1){
                month="0"+month;
            }

            //yyyy-MM-dd HH:mm"
            String year=yearBox.getSelectionModel().getSelectedItem();
            String buffer=day + "-" + month + "-" + year+" 00:00";
            if(fullreport){
                if(orderService.areAllTablesPaid(buffer)){
                    reportService.createFullReport(buffer);
                    ((Node) e.getSource()).getScene().getWindow().hide();
                }else{
                    try {
                        FXMLLoader loader = fxmLoaderService.getLoader(errorWindow.getURL());
                        Parent parent = loader.load();
                        ErrorWindowController errorWindowController = loader.getController();
                        errorWindowController.setErrorLabel("NOT ALL TABLES ARE PAID!");
                        Scene scene = new Scene(parent);
                        scene.getStylesheets().add("POStaurant.css");
                        Stage stage = new Stage();
                        stage.setScene(scene);
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.showAndWait();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }else {

                if (orderService.areAllTablesPaid(user, buffer)) {
                    reportService.createDubReport(user, buffer);
                    ((Node) e.getSource()).getScene().getWindow().hide();
                } else {
                    try {
                        FXMLLoader loader = fxmLoaderService.getLoader(errorWindow.getURL());
                        Parent parent = loader.load();
                        ErrorWindowController errorWindowController = loader.getController();
                        errorWindowController.setErrorLabel("NOT ALL TABLES ARE PAID!");
                        Scene scene = new Scene(parent);
                        scene.getStylesheets().add("POStaurant.css");
                        Stage stage = new Stage();
                        stage.setScene(scene);
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.showAndWait();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        LinkedList<String> days31=new LinkedList<>();
        for(int i=1;i<32;i++){
            days31.add(""+i);
        }
        LinkedList<String> days30=new LinkedList<>(days31);
        days30.removeLast();
        LinkedList<String> days29=new LinkedList<>(days30);
        days29.removeLast();
        LinkedList<String> days28=new LinkedList<>(days29);
        days28.removeLast();

        observableDays31 = FXCollections.observableArrayList(days31);
        observableDays30 = FXCollections.observableArrayList(days30);
        observableDays29= FXCollections.observableArrayList(days29);
        observableDays28 =FXCollections.observableArrayList(days28);

        dayBox.setValue(""+LocalDateTime.now().getDayOfMonth());
        dayBox.setItems(observableDays31);

        List<String> months=new ArrayList<>();
        for(int i=1;i<13;i++){
            months.add(""+i);
        }
        ObservableList<String> observableMonths=FXCollections.observableArrayList(months);
        monthBox.setValue(""+LocalDateTime.now().getMonthValue());
        monthBox.setItems(observableMonths);

        List<String> years=new ArrayList<>();
        for(int i=2020;i>2017;i--){
            years.add(""+i);
        }
        ObservableList<String> observableYears=FXCollections.observableArrayList(years);
        yearBox.setValue(""+LocalDateTime.now().getYear());
        yearBox.setItems(observableYears);

        monthBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            String buffer=(monthBox.getItems().get((Integer) newValue));
            String dayBuffer=dayBox.getSelectionModel().getSelectedItem();
            Integer yearBuffer=Integer.parseInt(yearBox.getSelectionModel().getSelectedItem());
            if(buffer.equals("1")||buffer.equals("3")||buffer.equals("5")||buffer.equals("7")||buffer.equals("8")||buffer.equals("10")||buffer.equals("12")){
                dayBox.setItems(observableDays31);
                dayBox.setValue(dayBuffer);
            }else if(buffer.equals("2")){
                if(yearBuffer%4!=0){
                    dayBox.setItems(observableDays28);
                    if(Integer.parseInt(dayBuffer)<29){
                        dayBox.setValue(dayBuffer);
                    }else{
                        dayBox.setValue("1");
                    }
                }else {
                    dayBox.setItems(observableDays29);
                    dayBox.setValue(dayBuffer);
                }
            }else {
                dayBox.setItems(observableDays30);
                if(Integer.parseInt(dayBuffer)<31) {
                    dayBox.setValue(dayBuffer);
                }else{
                    dayBox.setValue("1");
                }
            }
        });

        yearBox.getSelectionModel().selectedIndexProperty().addListener((observable,oldValue,newValue)->{
            Integer buffer=Integer.parseInt((yearBox.getItems().get((Integer) newValue)));
            String monthBuffer=monthBox.getSelectionModel().getSelectedItem();
            String dayBuffer=dayBox.getSelectionModel().getSelectedItem();
            if(buffer%4!=0){
                System.out.println("HERE");
                if(monthBuffer.equals("2")){
                    System.out.println("HERE2");
                    dayBox.setItems(observableDays28);
                    if(Integer.parseInt(dayBuffer)>28){
                        dayBox.setValue("1");
                    }else{
                        dayBox.setValue(dayBuffer);
                    }
                }
            }else{
                if(monthBuffer.equals("2")){
                    dayBox.setItems(observableDays29);
                    if(Integer.parseInt(dayBuffer)>29){
                        dayBox.setValue("1");
                    }else{
                        dayBox.setValue(dayBuffer);
                    }
                }
            }
        });

    }

    public void setUser(User user){
        this.user=user;
    }
    public void setFullreport(boolean fullreport){this.fullreport=fullreport;}
}
