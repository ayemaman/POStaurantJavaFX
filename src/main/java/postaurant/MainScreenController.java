package postaurant;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import postaurant.context.FXMLoaderService;
import postaurant.model.Item;
import postaurant.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Component
public class MainScreenController {


    @Value("/FXML/DubScreen.fxml")
    private Resource dubScreenForm;

    @Value("/FXML/LogIn.fxml")
    private Resource loginForm;

    @Value("/FXML/ManagerScreen.fxml")
    private Resource managerForm;

    @Value("/FXML/AccessBlocked.fxml")
    private Resource accessBlockedForm;



    private final FXMLoaderService loaderService;
    private User user;

    @FXML private Button loginButton;
    @FXML private TextField textField;
    @FXML private BorderPane borderPane;
    @FXML private Button timeButton;
    private final Integer startTime = 1;
    private Integer seconds = startTime;

    public MainScreenController(FXMLoaderService loaderService){
        this.loaderService = loaderService;
    }

    public void initialize() {
        timeButton.setOnAction(e -> {
            try {
                doTime();
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
                                scene = new Scene(parent);
                                scene.getStylesheets().add("POStaurant.css");
                                stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                                stage.setScene(scene);
                                stage.show();
                            } else {
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
                        Parent parent = loaderService.getLoader(accessBlockedForm.getURL()).load();
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
    public String createTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formatDateTime = now.format(formatter);
        return formatDateTime;

    }

    public void doTime(){
        VBox newVBox=(VBox) borderPane.getChildren().get(0);
        TextField newText= (TextField)newVBox.getChildren().get(1);
        newText.setText(createTime());
        ((VBox) borderPane.getChildren().get(0)).getChildren().remove(1);
        ((VBox) borderPane.getChildren().get(0)).getChildren().add(1,newText);


        Timeline time=new Timeline();
        KeyFrame frame= new KeyFrame(Duration.seconds(3), event -> {
            seconds--;
            if(seconds<=0) {
                VBox newnewVBox=(VBox) borderPane.getChildren().get(0);
                TextField newnewText=(TextField)newnewVBox.getChildren().get(1);
                newnewText.setText("");
                ((VBox) borderPane.getChildren().get(0)).getChildren().remove(1);
                ((VBox) borderPane.getChildren().get(0)).getChildren().add(1,newnewText);


                ((VBox) borderPane.getChildren().get(0)).getChildren().remove(1);
                ((VBox) borderPane.getChildren().get(0)).getChildren().add(1,textField);
                time.stop();


            }
        });
        time.getKeyFrames().add(frame);
        time.setCycleCount(Timeline.INDEFINITE);
        time.play();
    }



}
