package postaurant;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import postaurant.model.User;
import postaurant.service.ButtonCreationService;
import postaurant.service.TimeService;

import java.io.IOException;
import java.util.List;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TransferTableController {

    private User user;
    private boolean transfered;
    private Integer page;
    private List<Button> tableButtonList;
    private final FXMLoaderService fxmLoaderService;
    private final ButtonCreationService buttonCreationService;
    private final TimeService timeService;

    @Value("/FXML/POStaurant.fxml")
    private Resource mainScreenForm;
    @Value("/FXML/DubScreen.fxml")
    private Resource dubScreen;
    @Value("/FXML/ConfirmationExitWindow.fxml")
    private Resource confirmationWindow;
    @Value("/FXML/ManagerScreen.fxml")
    private Resource managerScreen;
    @Value("img/logo.png")
    private Resource logo;
    @Value("/POStaurant.css")
    private Resource css;

    @FXML
    private Button timeButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button goBackButton;
    @FXML
    private Button upButton;
    @FXML
    private Button downButton;
    @FXML
    private GridPane tablesGrid;
    @FXML
    private TextField userID;
    @FXML
    private TextField timeField;
    @FXML
    private ImageView logoImg;


    public TransferTableController(FXMLoaderService fxmLoaderService, ButtonCreationService buttonCreationService, TimeService timeService){
        this.fxmLoaderService=fxmLoaderService;
        this.buttonCreationService=buttonCreationService;
        this.timeService=timeService;

    }

    public void initialize() throws IOException {
        logoImg.setImage(new Image(logo.getURL().toExternalForm()));
        timeButton.setOnAction(e->{
            timeService.doTime(timeField);
        });
        goBackButton.setOnAction(e->{
            try {
                FXMLLoader loader=fxmLoaderService.getLoader(dubScreen.getURL());
                Parent parent=loader.load();
                DubScreenController dubScreenController=loader.getController();
                dubScreenController.setUser(this.user);
                Scene scene=new Scene(parent);
                scene.getStylesheets().add("POStaurant.css");
                Stage stage=(Stage)((Node)e.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e1) {
                e1.printStackTrace();
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
                    try {
                        FXMLLoader loader1;
                        Parent parent2;
                        if(user.getPosition().equals("MANAGER")){
                            loader1=fxmLoaderService.getLoader(managerScreen.getURL());
                            parent2=loader1.load();
                            ManagerScreenController managerScreenController=loader1.getController();
                            managerScreenController.setUser(user);
                        }else {
                            loader1 = fxmLoaderService.getLoader(mainScreenForm.getURL());
                            parent2=loader1.load();
                        }
                        Scene scene2=new Scene(parent2);
                        scene2.getStylesheets().add(css.getURL().toString());
                        Stage stage2=(Stage)((Node) e.getSource()).getScene().getWindow();
                        stage2.setScene(scene2);
                        stage2.show();
                    }catch (IOException iOE){
                        iOE.printStackTrace();
                    }
                }
            }catch (IOException iOE){
                iOE.printStackTrace();
            }


        });
        upButton.setOnAction(e-> setTables(tablesGrid,16,false,tableButtonList));

        downButton.setOnAction(e-> setTables(tablesGrid,16,true,tableButtonList));

    }

    public void setUser(User user){
        this.user=user;
        System.out.println(user);
        userID.setText(user.getFirstName());
        this.page=0;
        //todo
        this.tableButtonList=buttonCreationService.createTransferTables(user);
        if(!tableButtonList.isEmpty()) {
            setTables(tablesGrid, 16, true, tableButtonList);

        }
    }
    public void setTables(GridPane gridPane, Integer size, boolean forward, List<Button> list) {
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
                    page++;
                } else {
                    if (buttonCreationService.isNextPage(page + 1, list, size)) {
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
                        page++;
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
                        page++;
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

                    page++;
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
                    GridPane.setMargin(list.get(i), new Insets(2, 2, 2, 2));
                    if (x == 3) {
                        x = 0;
                        y++;
                    } else {
                        x++;
                    }
                }
                page--;
            }

        }
    }

}
