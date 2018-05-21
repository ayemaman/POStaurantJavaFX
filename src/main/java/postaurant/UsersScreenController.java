package postaurant;

import javafx.event.ActionEvent;
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
     private final TimeService timeService;

     private User user;
     private ArrayList<Button> userButtons;
     private int page;

    @Value("FXML/NewUserScreen.fxml")
     private Resource newUserForm;
    @Value("FXML/ManagerScreen.fxml")
    private Resource managerScreenForm;
    @Value("FXML/UserInfoScreen.fxml")
    private Resource userInfoForm;
    @Value("img/logo.png")
    private Resource logo;

     @FXML
     private GridPane gridPane;
     @FXML private Button newUserButton;
     @FXML private Button managerScreenButton;
     @FXML private Button upButton;
     @FXML private Button downButton;
     @FXML private Button timeButton;
     @FXML private TextField timeField;
     @FXML private ImageView logoImg;


     public UsersScreenController(UserService userService, FXMLoaderService fxmLoaderService, ButtonCreationService buttonCreationService, TimeService timeService){
         this.fxmLoaderService=fxmLoaderService;
         this.userService=userService;
         this.buttonCreationService=buttonCreationService;
         this.timeService = timeService;
     }

    public void initialize() throws IOException {
         logoImg.setImage(new Image(logo.getURL().toExternalForm()));
         timeButton.setOnAction(e-> {
             timeService.doTime(timeField);
         });
         upButton.setOnAction(e-> setTables(gridPane,16,false,userButtons));
         downButton.setOnAction(e-> setTables(gridPane,16,true,userButtons));
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
                     page--;
                     setUserButtons();
                 }

             }catch (IOException ioe1){
                 ioe1.printStackTrace();
             }
         });

         managerScreenButton.setOnAction(e->{
             try{
                 FXMLLoader loader=fxmLoaderService.getLoader(managerScreenForm.getURL());

                 Parent root=loader.load();
                 ManagerScreenController managerScreenController=loader.getController();
                 managerScreenController.setUser(user);
                 Scene scene= new Scene(root);
                 scene.getStylesheets().add("POStaurant.css");
                 Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                 stage.setScene(scene);
             }catch (IOException ioe2){
                 ioe2.printStackTrace();
             }
         });
    }


    public void setUserButtons(){
         this.page=0;
         userButtons=buttonCreationService.createUserButtons();
         for(Button b:userButtons){
             b.setOnAction(this::handleUserButtons);
         }
         setTables(gridPane,16,true,userButtons);
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

         public void setUser(User user){
         this.user=user;
         }

    }

