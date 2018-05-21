package postaurant.context;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;
import postaurant.model.KitchenOrderInfo;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;


@Component
public class QCBox extends VBox implements Printable {



    public QCBox(){

    }




    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        int x=50;
        int y=50;
        ObservableList<KitchenOrderInfo> list=((ListView)getChildren().get(1)).getItems();
        graphics.drawString(""+list.get(0).getTableNo(), x, y);
        for(KitchenOrderInfo k:list) {
            y=y+50;
            graphics.drawString(k.getItem()+" "+k.getQty(), x, y);
        }
        return PAGE_EXISTS;
    }
}
