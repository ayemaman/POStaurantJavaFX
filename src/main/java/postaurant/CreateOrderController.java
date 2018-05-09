package postaurant;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import postaurant.context.FXMLoaderService;
import postaurant.service.OrderService;
import postaurant.serviceWindowsControllers.ErrorWindowController;

import java.net.URL;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CreateOrderController {

    private final FXMLoaderService fxmlLoaderService;
    private final OrderService orderService;

    @Value("/FXML/ErrorWindow.fxml")
    private Resource tableAlreadyExists;


    private StringProperty buffer = new SimpleStringProperty("");
    private int tableNum=-1;


    @FXML
    private TextField tableNo;
    @FXML
    private GridPane gridPane;
    @FXML
    private Button buttonCREATE;

    public CreateOrderController(FXMLoaderService fxmlLoaderService, OrderService orderService) {
        this.fxmlLoaderService=fxmlLoaderService;
        this.orderService = orderService;
    }

    public void initialize() {
        tableNo.textProperty().bind(buffer);
        for (Node n : gridPane.getChildren()) {
            Button b = (Button) n;
            b.setOnAction(event -> {
                try {
                    buttonAction(b);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

     buttonCREATE.setOnAction(e-> {
        try {
            boolean tableExists = orderService.tableExists(buffer.getValue());
            if (tableExists ) {
                try {
                    FXMLLoader loader=fxmlLoaderService.getLoader(tableAlreadyExists.getURL());
                    Parent root = loader.load();
                    ErrorWindowController errorWindowController=loader.getController();
                    errorWindowController.setErrorLabel("THIS TABLE IS OCCUPIED");
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add("mainScreen.css");
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setScene(scene);
                    stage.showAndWait();
                    buffer.setValue("");
                } catch (Exception e2) {
                    e2.printStackTrace();

                }
            } else{
                setTableNum(Integer.parseInt(buffer.getValue()));
                buttonCREATE.getScene().getWindow().hide();
            }
        } catch (Exception e2) {
            e2.printStackTrace();

        }
    });
}

    private void buttonAction(Button button) {
        if (button.getText().equals("DELETE")) {
            buffer.setValue("");
        } else if (button.getText().equals("EXIT")) {
            button.getScene().getWindow().hide();
        } else {
            if (buffer.getValue().length() < 5) {
                String string = button.getText();
                buffer.set(buffer.getValue() + string);
            }
        }

    }

    public void setTableNum(int i){
        this.tableNum=i;
    }
    public int getTableNum(){
        return this.tableNum;
    }
}
