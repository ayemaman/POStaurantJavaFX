package postaurant.context;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Component;

@Component
public class StationList {
    private static ObservableList<String> stationTypes= FXCollections.observableArrayList();

    public StationList(){
        stationTypes.add("FRY");
        stationTypes.add("GRILL");
        stationTypes.add("PLATE/SAUTE");
        stationTypes.add("DESSERTS");
        stationTypes.add("BAR");
    }
    public static ObservableList<String> getStationTypes(){
        return stationTypes;
    }
}
