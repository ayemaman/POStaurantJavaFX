package postaurant.service;

import javafx.scene.control.Button;
import org.springframework.stereotype.Component;
import postaurant.model.Order;
import postaurant.model.User;

import java.util.ArrayList;
import java.util.List;
@Component
public class ButtonCreationService {
    private UserService userService;

    public ButtonCreationService(UserService userService) {
        this.userService = userService;
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

}
