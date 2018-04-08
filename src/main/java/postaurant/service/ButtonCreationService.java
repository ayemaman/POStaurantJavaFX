package postaurant.service;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import postaurant.context.KeyboardList;
import postaurant.model.Order;
import postaurant.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Component
public class ButtonCreationService {


    @Value("/img/upArrow.png")
    private Resource upArrow;
    private final UserService userService;
    private final KeyboardList keyboardList;

    public ButtonCreationService(UserService userService, KeyboardList keyboardList) {
        this.userService = userService;
        this.keyboardList=keyboardList;
    }

    public ArrayList<Button> createTableButtons(User user) {
        ArrayList<Button> tableButtonList = new ArrayList<>();
        try {
            List<Order> tables = userService.getUserOrders(user);
            for (Order o : tables) {
                String text = "" + o.getTableNo();
                Button button = new Button(text);
                button.setPrefHeight(70.0);
                button.setPrefWidth(95.0);
                button.setMnemonicParsing(false);
                tableButtonList.add(button);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tableButtonList;
    }

    public ArrayList<Button> createKeyboardButtons(boolean lowercase) {

        ArrayList<String> list= (keyboardList.getQwerty());
        ArrayList<Button> keyboardButtonList = new ArrayList<>();
        for(int i=0;i<10;i++) {
            Button button = new Button();
            button.setText(list.get(i));
            button.setMinWidth(55.0);
            button.setMnemonicParsing(false);
            keyboardButtonList.add(button);
        }
        if (lowercase) {
            for (int i = 10; i < 40; i++) {
                Button button = new Button();
                button.setMinWidth(55.0);
                button.setMnemonicParsing(false);
                if (list.get(i).equals("")) {
                    ImageView caps = new ImageView();
                    caps.setFitHeight(20);
                    caps.setFitWidth(30);
                    try {
                        Image image = new Image(upArrow.getURL().toString());
                        caps.setImage(image);
                        button.setGraphic(caps);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    button.setText(list.get(i));
                }
                keyboardButtonList.add(button);
            }
        }
        else {
            for (int i = 40; i < list.size(); i++) {
                Button button = new Button(list.get(i));
                button.setMinWidth(55.0);
                button.setMnemonicParsing(false);
                if (list.get(i).equals("")) {
                    ImageView caps = new ImageView();
                    caps.setFitHeight(20);
                    caps.setFitWidth(30);
                    try {
                        Image image = new Image(upArrow.getURL().toString());
                        caps.setImage(image);
                        button.setGraphic(caps);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    button.setText(list.get(i));
                }
                keyboardButtonList.add(button);
            }
        }
        Button delete=new Button("DELETE");
        delete.setMinHeight(35);
        keyboardButtonList.add(delete);

        Button spacebar=new Button();
        spacebar.setMinWidth(475);
        spacebar.setMinHeight(35);
        spacebar.setMnemonicParsing(false);
        keyboardButtonList.add(spacebar);

        return keyboardButtonList;
    }




}
