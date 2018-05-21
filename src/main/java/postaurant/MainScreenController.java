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
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import postaurant.context.FXMLoaderService;

import postaurant.database.UserDatabase;
import postaurant.model.User;
import postaurant.service.TimeService;

import java.io.IOException;


@Component
public class MainScreenController {


    @Value("/FXML/DubScreen.fxml")
    private Resource dubScreenForm;

    @Value("/FXML/LogIn.fxml")
    private Resource loginForm;

    @Value("/FXML/ManagerScreen.fxml")
    private Resource managerForm;

    @Value("/FXML/ErrorWindow.fxml")
    private Resource accessBlockedForm;

    @Value("/FXML/KitchenScreen.fxml")
    private Resource kitchenForm;

    @Value("/FXML/QCScreen.fxml")
    private Resource qcScreen;

    @Value("/FXML/BarScreen.fxml")
    private Resource barScreen;

    @Value("/FXML/BarQCScreen.fxml")
    private Resource barQCScreen;

    @Value("img/logo.png")
    private Resource logo;



    private final FXMLoaderService loaderService;
    private final TimeService timeService;






    private User user;

    @FXML private Button loginButton;
    @FXML private TextField timeField;
    @FXML private Button timeButton;
    @FXML private ImageView logoImg;

    public MainScreenController(FXMLoaderService loaderService, TimeService timeService, UserDatabase userDatabase){
        this.loaderService = loaderService;
        this.timeService=timeService;

    }

    public void initialize() throws IOException {
        logoImg.setImage(new Image(logo.getURL().toExternalForm()));
        timeButton.setOnAction(e -> {
            try {

                timeService.doTime(timeField);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });



        loginButton.setOnAction(e -> {
            try {
                FXMLLoader loader = loaderService.getLoader(loginForm.getURL());
                Parent root = loader.load();
                Scene scene = new Scene(root);
                scene.getStylesheets().add("POStaurant.css");
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(scene);
                stage.showAndWait();
                LogInController controller = loader.getController();
                user = controller.getUser();
                if (user != null) {
                    if (user.isAccessible()) {
                        try {
                            if (user.getPosition().equals("MANAGER")) {
                                loader = loaderService.getLoader(managerForm.getURL());
                                Parent parent = loader.load();
                                ManagerScreenController managerScreenController=loader.getController();
                                managerScreenController.setUser(user);
                                scene = new Scene(parent);
                                scene.getStylesheets().add("POStaurant.css");
                                stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                                stage.setScene(scene);
                                stage.show();
                            }else if(user.getPosition().equals("KITCHEN")){
                                loader =loaderService.getLoader(kitchenForm.getURL());
                                Parent parent = loader.load();
                                scene = new Scene(parent);
                                scene.getStylesheets().add("POStaurant.css");
                                stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                                stage.setScene(scene);
                                stage.show();
                            }
                            else if(user.getPosition().equals("FOODRUNNER")){
                                loader =loaderService.getLoader(qcScreen.getURL());
                                Parent parent = loader.load();
                                scene = new Scene(parent);
                                scene.getStylesheets().add("POStaurant.css");
                                stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                                stage.setScene(scene);
                                stage.show();
                            }else if(user.getPosition().equals("BAR")){
                                loader=loaderService.getLoader(barScreen.getURL());
                                Parent parent=loader.load();
                                scene= new Scene(parent);
                                scene.getStylesheets().add("POStaurant.css");
                                stage =(Stage)((Node)e.getSource()).getScene().getWindow();
                                stage.setScene(scene);
                                stage.show();
                            }else if(user.getPosition().equals("DRINKRUNNER")){
                                loader=loaderService.getLoader(barQCScreen.getURL());
                                Parent parent=loader.load();
                                scene= new Scene(parent);
                                scene.getStylesheets().add("POStaurant.css");
                                stage =(Stage)((Node)e.getSource()).getScene().getWindow();
                                stage.setScene(scene);
                                stage.show();

                            }
                            else {
                                loader = loaderService.getLoader(dubScreenForm.getURL());
                                Parent parent = loader.load();
                                DubScreenController dubscreen = loader.getController();
                                dubscreen.setUser(user);
                                scene = new Scene(parent);
                                scene.getStylesheets().add("POStaurant.css");
                                stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                                stage.setScene(scene);
                                stage.show();
                            }
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                    else{
                        FXMLLoader loader1=loaderService.getLoader(accessBlockedForm.getURL());
                        Parent parent = loader1.load();
                        ErrorWindowController errorWindowController=loader1.getController();
                        errorWindowController.setErrorLabel("Access for this user is blocked");
                        Scene scene1=new Scene(parent);
                        scene1.getStylesheets().add("POStaurant.css");
                        Stage stage1=new Stage();
                        stage1.initModality(Modality.APPLICATION_MODAL);
                        stage1.initStyle(StageStyle.UNDECORATED);
                        stage1.setScene(scene1);
                        stage1.showAndWait();
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
    }







}
