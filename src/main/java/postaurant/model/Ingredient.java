/**
 * Class that represents Ingredients in this system
 * Implements Comparable interface and has overwritten compareTo(Object o) method
 */
package postaurant.model;



import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import postaurant.exception.InputValidationException;


import java.util.Date;


public class Ingredient implements Comparable<Ingredient> {
    private long id;
    private String name;
    private Integer amount;
    private Double price;
    private int availability;
    private String allergy;
    private Date dateCreated;

    public Ingredient(){
    }

    public Ingredient(long id, String name, int amount, double price, int availability, String allergy, Date dateCreated) throws InputValidationException{
        setId(id);
        setName(name);
        setAmount(amount);
        setPrice(price);
        setAvailability(availability);
        setAllergy(allergy);
        setDateCreated(dateCreated);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    /**
     * Ingredient's name Setter
     * Checks using regular expressions if name String consists of ASCII characters and it's length is larger than 1 and smaller than 31;
     * @throws InputValidationException if not
     * @param name name of Ingredient
     */
    public void setName(String name) throws InputValidationException {
        if(name.matches("(\\p{ASCII}){2,30}")) {
            this.name = name.toUpperCase();

        }else{
            throw new InputValidationException();
        }
    }
    public char getFirstLetter(){
        return this.name.charAt(0);
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Double getPrice(){
        return this.price;
    }
    public void setPrice(Double price){
        this.price=price;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public String getAllergy() {
        return allergy;
    }

    public void setAllergy(String allergy) {
        this.allergy = allergy;
    }


    public void setDateCreated(Date dateCreated){
        this.dateCreated=dateCreated;
    }

    public String toString() {
        return getName();
    }

    public String toFullString() {
        StringBuilder buffer = new StringBuilder();
        if (getId() != 0) {
            buffer.append("ID: ").append(getId());
        }
        buffer.append("\nNAME: ").append(getName());
        buffer.append("\nAMOUNT: ").append(getAmount());
        buffer.append("\nPrice: ").append(getPrice());
        buffer.append("\nAvailability: ").append(getAvailability());
        buffer.append("\nAllergy: ").append(getAllergy());
        return buffer.toString();
    }

    /**
     * Compare method, that compares Ingredients by Name and Amount
     * @param ingredient Ingredient that is being compared to
     * @return A number representing whether it is before or after another Ingredient
     */
    @Override
    public int compareTo(Ingredient ingredient) {
        int nameCmp=this.getName().compareTo(ingredient.getName());
        if(nameCmp!=0){
            return nameCmp;
        }
        return this.getAmount()-(ingredient.getAmount());
    }

}




