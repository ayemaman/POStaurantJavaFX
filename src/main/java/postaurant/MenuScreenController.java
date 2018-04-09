package postaurant;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import postaurant.context.FXMLoaderService;
import postaurant.service.MenuService;

import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MenuScreenController {

    private List<Button> itemButtonList;
    private List<Tab> sectionTabList;

    @FXML
    private TabPane sectionTabPane;

    private MenuService menuService;
    private FXMLoaderService fxmLoaderService;

    public MenuScreenController(MenuService menuService, FXMLoaderService fxmLoaderService){
        this.menuService=menuService;
        this.fxmLoaderService=fxmLoaderService;
    }
    public void initialize(){

    }

    public List<Button> getItemButtonList() {
        return itemButtonList;
    }

    public void setItemButtonList(List<Button> itemButtonList) {
        this.itemButtonList = itemButtonList;
    }

    public List<Tab> getSectionTabList() {
        return sectionTabList;
    }

    public void setSectionTabList(List<Tab> sectionTabList) {
        this.sectionTabList = sectionTabList;
    }
}
