package postaurant;

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
import postaurant.service.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UsersScreenController {
     private final UserService userService;
     private final FXMLoaderService fxmLoaderService;

     private ArrayList<Button> userButtons;
     private int page;

    @Value("FXML/NewUserScreen.fxml")
     private Resource newUserForm;
    @Value("FXML/ManagerScreen.fxml")
    private Resource managerScreenForm;

     @FXML
     private GridPane gridPane;
     @FXML private Button newUserButton;
     @FXML private Button managerScreenButton;


     public UsersScreenController(UserService userService, FXMLoaderService fxmLoaderService){
         this.fxmLoaderService=fxmLoaderService;
         this.userService=userService;
     }

    public void initialize() {
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
                 stage.showAndWait();
                 NewUserScreenController newUserScreenController=loader.getController();
                 if(newUserScreenController.getWasUserSaved()){
                     this.setUserButtons();
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
            System.out.println(userButtons.get((this.page * 16))+" checks : "+ this.page*16);
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;

    }

    public void setUserButtons(){
         this.page=0;
         createUserButtons();
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



    public void createUserButtons() {
        try {
            userButtons=new ArrayList<>();
            List<User> users = userService.getAllActiveUsers();
            for (User u: users) {
                String text =""+u.getFirst_name().substring(0,1)+u.getLast_name()+System.lineSeparator()+u.getPosition();
                Button button = new Button(text);
                button.setPrefHeight(70.0);
                button.setPrefWidth(95.0);

                button.setMnemonicParsing(false);
                button.getStyleClass().add("UserButton");
                if(u.getPosition().equals("DUBDUB")) {
                    button.getStyleClass().add("DubButton");
                }else if (u.getPosition().equals("MANAGER")){
                    button.getStyleClass().add("ManagerButton");
                }
                userButtons.add(button);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
