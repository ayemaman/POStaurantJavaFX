package postaurant.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Item {
    private String id;
    private String name;
    private double price;
    private String type;
    private String cat;
    private Map<Ingredient,Integer> recipe=new HashMap<>();
    private int availability;
    private String kitchen_status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public Map<Ingredient,Integer> getRecipe() {
        return recipe;
    }

    public void setRecipe(HashMap<Ingredient,Integer> recipe) {
        this.recipe = recipe;
    }

    public void addIngredient(Ingredient ingredient, Integer amount){
        getRecipe().put(ingredient,amount);
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public String getKitchen_status() {
        return kitchen_status;
    }

    public void setKitchen_status(String kitchen_status) {
        this.kitchen_status = kitchen_status;
    }

    public String toString(){
        String buffer="Name:"+ getName()+" ID: "+getId()+" Section: "+getCat()+" ";
        for (Map.Entry<Ingredient,Integer > entry : getRecipe().entrySet()){
            buffer+="Ingr:"+entry.getKey()+" Amount:"+entry.getValue()+"/ ";
        }
        return buffer;
    }
}
