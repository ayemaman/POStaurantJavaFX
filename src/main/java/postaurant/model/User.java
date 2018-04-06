package postaurant.model;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class User {
    private String userID;
    private String first_name;
    private String last_name;
    private String position;
    private List<Order> userOpenOrders;

    public User(){

    }
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public List<Order> getUserOpenOrders() {
        return userOpenOrders;
    }

    public void setUserOpenOrders(List<Order> userOpenOrders) {
        this.userOpenOrders = userOpenOrders;
    }

    public String toString(){
        return getFirst_name()+getLast_name()+getUserID()+getPosition();
    }
}
