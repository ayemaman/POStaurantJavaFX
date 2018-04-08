package postaurant;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import postaurant.model.User;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DeleteConfirmationController {
    private User user;


    public void setUser(User user){
        this.user=user;
    }
    public void initialize(){

    }
}
