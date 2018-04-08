package postaurant.model;

import javafx.beans.property.SimpleStringProperty;
import org.springframework.stereotype.Component;
import postaurant.exception.InputValidationException;

import java.util.List;

@Component
public class User {
    private String userID;
    private String firstName;
    private String lastName;
    private String position;
    private List<Order> userOpenOrders;

    public User(){
    }

    public User(String firstName, String lastName, String position)throws InputValidationException{
        setFirstName(firstName);
        setLastName(lastName);
        setPosition(position);
    }
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) throws InputValidationException {
        if(firstName.matches("(\\p{Alpha}){2,30}")) {
            this.firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
        }
        else{
            throw new InputValidationException();
        }
    }

    public String getLastName(){
        return lastName;
    }

    public void setLastName(String lastName) throws InputValidationException {
        if (lastName.matches("([A-Za-z]){2,30}")) {
            this.lastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
        } else {
            throw new InputValidationException();
        }
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

    public SimpleStringProperty getIDProperty(){
        return new SimpleStringProperty(getUserID());
    }
    public SimpleStringProperty getNameProperty(){
        return new SimpleStringProperty(getFirstName());
    }
    public SimpleStringProperty getSurnameProperty(){
        return new SimpleStringProperty(getLastName());
    }
    public SimpleStringProperty getPositionProperty(){
        return new SimpleStringProperty(getPosition());
    }

    public String toString(){
        return getFirstName()+getLastName()+getUserID()+getPosition();
    }
}
