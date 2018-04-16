package postaurant.context;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)

public class AllergenList {
    private static ObservableList<String> allergens=FXCollections.observableArrayList();

    public AllergenList(){
        allergens.add("CELERIAC");
        allergens.add("GLUTEN");
        allergens.add("CRUSTACEANS");
        allergens.add("EGGS");
        allergens.add("FISH");
        allergens.add("LUPIN");
        allergens.add("LACTOSE");
        allergens.add("MOLLUSCS");
        allergens.add("MUSTARD");
        allergens.add("NUTS");
        allergens.add("SESAME SEEDS");
        allergens.add("SOYA");
        allergens.add("SULPHITES");
        Collections.sort(allergens);
        allergens.add(0,"NOALLERGY");
    }

    public static ObservableList<String> getAllergens() {
        return allergens;
    }
}
