package postaurant.model;


import postaurant.exception.InputValidationException;

import java.util.Comparator;
import java.util.Date;
import java.util.function.ToIntFunction;

public class Ingredient implements Comparable<Ingredient> {
    private long id;
    private String name;
    private int amount;
    private double price;
    private int availability;
    private String allergy;
    private Date dateCreated;

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

    public void setName(String name) throws InputValidationException {
        if(name.matches("(\\p{ASCII}){2,30}")) {
            this.name = name;
        }else{
            throw new InputValidationException();
        }
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Double getPrice(Double price){
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

    public Date getDateCreated(){
        return this.dateCreated;
    }
    public void setDateCreated(Date dateCreated){
        this.dateCreated=dateCreated;
    }

    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(Ingredient o) {
        int nameCmp=this.getName().compareTo(o.getName());
        if(nameCmp!=0){
            return nameCmp;
        }
        return this.getAmount()-(o.getAmount());
    }

}




