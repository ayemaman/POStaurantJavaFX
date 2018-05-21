package postaurant.service;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

import javax.swing.text.DateFormatter;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


@Component
public class TimeService {


    public String createTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return now.format(formatter);

    }

    public String createTimeOnly(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return now.format(formatter);
    }

    public String createDateOnly(LocalDateTime date){
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String buffer=date.format(formatter);
        return buffer.substring(0,11);

    }


    public LocalDateTime createNextDayLocalDateTimeFromString(String string){
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime time=LocalDateTime.parse(string,formatter);
        LocalDateTime newTime=time.plusDays(1);
        return newTime;

    }



    public void doTime(TextField textField){
        textField.setText(createTime());
        Timeline time=new Timeline();
        KeyFrame frame= new KeyFrame(Duration.seconds(3), event -> {
                textField.setText("");
                time.stop();

        });
        time.getKeyFrames().add(frame);
        time.play();

    }

}
