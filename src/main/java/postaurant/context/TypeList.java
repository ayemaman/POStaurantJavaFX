package postaurant.context;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)

public class TypeList {
    private static ObservableList<String> itemTypes =FXCollections.observableArrayList();

    public TypeList(){
        itemTypes.add("FOODITEM");
        itemTypes.add("DRINKITEM");
    }

    public static ObservableList<String> getItemTypes() {
        return itemTypes;
    }
}

