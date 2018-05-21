/**
 * Service that works with "time" Strings
 */
package postaurant.service;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



@Component
public class TimeService {

    /**
     * Creates formatted String of present date and time
     * @return formatted String of present date and time
     */
    public String createTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return now.format(formatter);

    }

    /**
     * Creates formatted String of present time
     * @return formatted String of present time
     */
    public String createTimeOnly(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return now.format(formatter);
    }

    /**
     * Creates formatted String of present date and time from a given LocalDateTime
     * @param date to be formatted
     * @return formatted String of present date and time
     */
    public String createDateOnly(LocalDateTime date){
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String buffer=date.format(formatter);
        return buffer.substring(0,11);

    }

    /**
     * Creates LocalDateTime of the next day from given "time" String
     * @param string String that represents time
     * @return LocalDateTime of the next day from given "time" String
     */
    public LocalDateTime createNextDayLocalDateTimeFromString(String string){
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime time=LocalDateTime.parse(string,formatter);
        LocalDateTime newTime=time.plusDays(1);
        return newTime;

    }


    /**
     * Sets specified TextField text to current time
     * @param textField that is being modified
     */
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
