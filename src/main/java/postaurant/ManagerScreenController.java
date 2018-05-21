package postaurant;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ManagerScreenController {

    private User user;

    private final FXMLoaderService fxmLoaderService;
    private final ButtonCreationService buttonCreationService;
    private final TimeService timeService;

    @Value("/FXML/UsersScreen.fxml")
    private Resource usersForm;

    @Value("/FXML/POStaurant.fxml")
    private Resource postaurantForm;

    @Value("/FXML/MenuScreen.fxml")
    private Resource menuForm;

    @Value("/FXML/DubScreen.fxml")
    private Resource dubScreen;

    @Value("/FXML/ConfirmationExitWindow.fxml")
    private Resource confirmationWindow;

    @Value("/FXML/PickADate.fxml")
    private Resource pickDateForm;

    @Value("POStaurant.css")
    private Resource css;

    @Value("img/logo.png")
    private Resource logo;

    @FXML
    private Button usersButton;
    @FXML
    private Button exitButton;
    @FXML
    private Button menuButton;
    @FXML
    private Button timeButton;
    @FXML
    private Button reportButton;
    @FXML
    private Button ordersButton;
    @FXML
    private Button shutDownButton;
    @FXML
    private TextField timeField;
    @FXML
    private ImageView logoImage;

    public ManagerScreenController(FXMLoaderService fxmLoaderService, ButtonCreationService buttonCreationService, TimeService timeService){
        this.fxmLoaderService=fxmLoaderService;
        this.buttonCreationService=buttonCreationService;
        this.timeService = timeService;
    }
    public void initialize() throws IOException {
        logoImage.setImage(new Image(logo.getURL().toExternalForm()));

        ordersButton.setOnAction(e->{
            try {
            FXMLLoader loader=fxmLoaderService.getLoader(dubScreen.getURL());
                Parent parent=loader.load();
                DubScreenController dubScreenController=loader.getController();
                dubScreenController.setUser(user);
                Scene scene=new Scene(parent);
                scene.getStylesheets().add("POStaurant.css");
                Stage stage=(Stage)((Node) e.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        });

        shutDownButton.setOnAction(e->{
            try{
                FXMLLoader loader=fxmLoaderService.getLoader(confirmationWindow.getURL());
                Parent parent=loader.load();
                ConfirmationExitWindowController confirmationExitWindowController =loader.getController();
                confirmationExitWindowController.setConfirmationLabel("Are you sure you want to shutdown?");
                Scene scene=new Scene(parent);
                scene.getStylesheets().add("POStaurant.css");
                Stage stage=new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(scene);
                stage.showAndWait();
                if(confirmationExitWindowController.confirmed()) {
                    ((Node) e.getSource()).getScene().getWindow().hide();
                }
            }catch (IOException iOE) {
                iOE.printStackTrace();
            }

        });
        timeButton.setOnAction(e->{
            timeService.doTime(timeField);
        });
        usersButton.setOnAction(e-> {
            try {
                FXMLLoader loader = fxmLoaderService.getLoader(usersForm.getURL());
                Parent parent = loader.load();
                UsersScreenController usersScreenController= loader.getController();
                usersScreenController.setUserButtons();
                usersScreenController.setUser(user);
                Scene scene = new Scene(parent);
                scene.getStylesheets().add(""+css.getURL());
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }catch (Exception e1){
                System.out.println(e1);
        }
        });

        menuButton.setOnAction(e->{
            try{
                FXMLLoader loader=fxmLoaderService.getLoader(menuForm.getURL());
                Parent parent=loader.load();
                MenuScreenController menuScreenController=loader.getController();
                menuScreenController.setUser(user);
                Scene scene= new Scene(parent);
                scene.getStylesheets().add(""+css.getURL());
                Stage stage= (Stage)((Node) e.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }catch (Exception e1){
               e1.printStackTrace();
            }
        });

        reportButton.setOnAction(e->{
            try {
                FXMLLoader loader = fxmLoaderService.getLoader(pickDateForm.getURL());
                Parent parent = loader.load();
                PickADateController pickADateController=loader.getController();
                pickADateController.setUser(user);
                pickADateController.setFullreport(true);
                Scene scene = new Scene(parent);
                scene.getStylesheets().add(css.getURL().toString());
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(scene);
                stage.showAndWait();
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
                        FXMLLoader loader1 = fxmLoaderService.getLoader(postaurantForm.getURL());
                        Parent parent1 = loader1.load();
                        Scene scene1 = new Scene(parent1);
                        scene1.getStylesheets().add("" + css.getURL());
                        Stage stage1 = (Stage) ((Node) e.getSource()).getScene().getWindow();
                        stage1.setScene(scene1);
                        stage1.show();
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                }
            }catch (IOException iOE){
                iOE.printStackTrace();
            }

        });
    }

    public void setUser(User user){
        this.user=user;
    }

}
