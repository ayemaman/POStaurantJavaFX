package postaurant;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import postaurant.service.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UsersScreenController {
     private final UserService userService;
     private final FXMLoaderService fxmLoaderService;
     private final ButtonCreationService buttonCreationService;

     private ArrayList<Button> userButtons;
     private int page;

    @Value("FXML/NewUserScreen.fxml")
     private Resource newUserForm;
    @Value("FXML/ManagerScreen.fxml")
    private Resource managerScreenForm;
    @Value("FXML/UserInfoScreen.fxml")
    private Resource userInfoForm;

     @FXML
     private GridPane gridPane;
     @FXML private Button newUserButton;
     @FXML private Button managerScreenButton;
     @FXML private Button upButton;
     @FXML private Button downButton;


     public UsersScreenController(UserService userService, FXMLoaderService fxmLoaderService, ButtonCreationService buttonCreationService){
         this.fxmLoaderService=fxmLoaderService;
         this.userService=userService;
         this.buttonCreationService=buttonCreationService;
     }

    public void initialize() {
         upButton.setOnAction(e-> setUsers(false));
         downButton.setOnAction(e-> setUsers(true));
         newUserButton.setOnAction(e->{
             try {
                 FXMLLoader loader=fxmLoaderService.getLoader(newUserForm.getURL());
                 Parent root =loader.load();
                 Scene scene = new Scene(root);
                 scene.getStylesheets().add("POStaurant.css");
                 Stage stage = new Stage();
                 stage.initModality(Modality.APPLICATION_MODAL);
                 stage.initStyle(StageStyle.UNDECORATED);
                 stage.setScene(scene);
                 NewUserScreenController newUserScreenController=loader.getController();
                 stage.showAndWait();
                 if(newUserScreenController.getWasUserSaved()) {
                     setUserButtons();
                 }

             }catch (IOException ioe1){
                 ioe1.printStackTrace();
             }
         });

         managerScreenButton.setOnAction(e->{
             try{
                 Parent root=fxmLoaderService.getLoader(managerScreenForm.getURL()).load();
                 Scene scene= new Scene(root);
                 scene.getStylesheets().add("POStaurant.css");
                 Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                 stage.setScene(scene);
             }catch (IOException ioe2){
                 ioe2.printStackTrace();
             }
         });
    }

    private boolean isNextPage() {
        try {
           userButtons.get((this.page * 16));
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;

    }

    public void setUserButtons(){
         this.page=0;
         userButtons=buttonCreationService.createUserButtons();
         for(Button b:userButtons){
             b.setOnAction(this::handleUserButtons);
         }
         setUsers(true);
    }


    public void setUsers(boolean forward) {
        int start;
        int x = 0;
        int y = 0;
        if (forward) {
            if (this.page == 0) {
                start = 0;
            } else {
                start = this.page * 16;
            }
            if (isNextPage()) {
                for (int i = 0; i < (gridPane.getChildren()).size(); ) {
                    gridPane.getChildren().remove(gridPane.getChildren().get(i));
                }
                if (userButtons.size() - start > 15) {
                    for (int i = start; i < (start + 16); i++) {
                        gridPane.add(userButtons.get(i), x, y);
                        GridPane.setMargin(userButtons.get(i), new Insets(2, 2, 2, 2));
                        if (x == 3) {
                            x = 0;
                            y++;
                        } else {
                            x++;
                        }

                    }
                } else {
                    for (int i = start; i < userButtons.size(); i++) {
                        gridPane.add(userButtons.get(i), x, y);
                        GridPane.setMargin(userButtons.get(i), new Insets(2, 2, 2, 2));
                        if (x == 3) {
                            x = 0;
                            y++;
                        } else {
                            x++;
                        }

                    }
                }
                page++;
            }
        } else {
            if (this.page > 1) {
                if (this.page == 2) {
                    start = 0;
                } else {
                    start = (this.page - 2) * 16;
                }
                for (int i = start; i < (start + 16); i++) {
                    gridPane.add(userButtons.get(i), x, y);
                    GridPane.setMargin(userButtons.get(i), new Insets(2, 2, 2, 2));
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



    public void handleUserButtons(ActionEvent event){
             try {
                 FXMLLoader loader = fxmLoaderService.getLoader(userInfoForm.getURL());
                 Parent root = loader.load();
                 UserInfoScreenController userInfoScreenController = loader.getController();
                 Button button=(Button) event.getSource();
                 User user=userService.getUser(button.getText().substring(0,4));
                 userInfoScreenController.setUser(user);
                 Scene scene = new Scene(root);
                 scene.getStylesheets().add("POStaurant.css");
                 Stage stage = new Stage();
                 stage.initModality(Modality.APPLICATION_MODAL);
                 stage.initStyle(StageStyle.UNDECORATED);
                 stage.setScene(scene);
                 stage.showAndWait();
                 if(userInfoScreenController.wasDeleted()){
                     setUserButtons();
                 }
             } catch (Exception e1) {
                 e1.printStackTrace();
             }

         }

    }

