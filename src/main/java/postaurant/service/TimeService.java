package postaurant.service;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


@Component
public class TimeService {


    public String createTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formatDateTime = now.format(formatter);
        return formatDateTime;

    }

    public void doTime(TextField textField){
        textField.setText(createTime());
        Timeline time=new Timeline();
        KeyFrame frame= new KeyFrame(Duration.seconds(3), event -> {
                textField.setText("");
                time.stop();

        });
        time.getKeyFrames().add(frame);
        time.setCycleCount(Timeline.INDEFINITE);
        time.play();

    }

}
