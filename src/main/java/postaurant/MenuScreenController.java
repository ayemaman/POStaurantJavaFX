package postaurant;


import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import postaurant.context.FXMLoaderService;
import postaurant.service.ButtonCreationService;
import postaurant.service.MenuService;
import javafx.fxml.FXML;


import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.ArrayList;


@Component
@Scope(value=ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MenuScreenController {

    private List<Button> itemButtonList;
    private List<Tab> sectionTabList;
    private int page;


    private final MenuService menuService;
    private final FXMLoaderService fxmLoaderService;
    private final ButtonCreationService buttonCreationService;

    @FXML
    private TabPane sectionTabPane;
    @FXML
    private Button testButton;

    public MenuScreenController(MenuService menuService, FXMLoaderService fxmLoaderService, ButtonCreationService buttonCreationService){
        this.menuService=menuService;
        this.fxmLoaderService=fxmLoaderService;
        this.buttonCreationService=buttonCreationService;
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

    public void setSectionTabs(){
        for(Tab t: sectionTabList) {
            t.setOnSelectionChanged(event-> {
                        AnchorPane anchorPane = (AnchorPane) t.getContent();
                        GridPane gridPane = (GridPane) anchorPane.getChildren().get(0);
                        setItemButtonList(buttonCreationService.createItemButtonsForSection(t.getText()));
                        this.page=0;
                        setItemButtonsForSection(gridPane, true);
                    });
            sectionTabPane.getTabs().add(t);
        }
        AnchorPane anchorPane=(AnchorPane)sectionTabPane.getTabs().get(0).getContent();
        GridPane gridPane =(GridPane)anchorPane.getChildren().get(0);
        setItemButtonsForSection(gridPane, true);

    }
    private boolean isNextPage() {
        try {
            System.out.println(itemButtonList.get((this.page * 16)));
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;

    }


    public void setItemButtonsForSection(GridPane gridPane, boolean forward){
        int start;
        int x=0;
        int y=0;
        if(forward){
            if (this.page==0){
                start = 0;
            }else{
                start=this.page*16;
            }
            if(isNextPage()){
                for(int i=0; i<gridPane.getChildren().size();){
                    gridPane.getChildren().remove(gridPane.getChildren().get(i));
                }
            }
            if(itemButtonList.size()-start >15){
                for(int i=start; i< (start + 16);i++){
                    gridPane.add(itemButtonList.get(i),x, y);
                    gridPane.setMargin(itemButtonList.get(i), new Insets(2,2,2,2));
                    if(x==3) {
                        x = 0;
                        y++;
                    }else{
                        x++;
                    }
                }
            }else{
                for(int i = start; i<itemButtonList.size();i++){
                    gridPane.add(itemButtonList.get(i), x, y);
                    gridPane.setMargin(itemButtonList.get(i), new Insets(2,2,2,2));
                    if(x==3){
                        x=0;
                        y++;
                    }else{
                        x++;
                    }
                }
            }
            page++;
        }else{
            if(this.page>1){
                if(this.page==2){
                    start = 0;
                }
                else{
                    start = (this.page-2)*16;
                }
                for(int i=start; i<(start+16);i++){
                    gridPane.add(itemButtonList.get(i),x,y);
                    gridPane.setMargin(itemButtonList.get(i), new Insets(2,2,2,2));
                    if(x==3){
                        x=0;
                        y++;
                    }else{
                        x++;
                    }
                }
            }
            page--;
        }

    }
}
