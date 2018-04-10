package postaurant.model;

import postaurant.exception.InputValidationException;

import java.util.HashMap;
import java.util.Map;

public class Item {
    private String id;
    private String name;
    private double price;
    private String type;
    private String section;
    private Map<Ingredient,Integer> recipe;
    private int availability;
    private String kitchen_status;

    public Item(){
        recipe=new HashMap<Ingredient,Integer>();

    }
    public Item (String name, Double price, String type, String section, int availability, HashMap<Ingredient,Integer> recipe)throws InputValidationException{
        setName(name);
        setPrice(price);
        setType(type);
        setSection(section);
        setAvailability(availability);
        setRecipe(recipe);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws InputValidationException {
        if(name.matches("(\\p{ASCII}){2,30}")){
        this.name = name;
        }else{
            throw new InputValidationException();
        }
    }

    public double getPrice(){
        return price;
    }

    public void setPrice(double price) throws InputValidationException {
        if(price>0) {
            this.price = price;
        }else{
            throw new InputValidationException();
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) throws InputValidationException {
        if (type.matches("(\\p{Alpha}){2,30}")) {
            this.type = type;
        } else {
            throw new InputValidationException();
        }
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) throws InputValidationException {
        if (section.matches("(\\p{Alpha}){2,30}")) {
            this.section = section;
        } else {
            throw new InputValidationException();
        }
    }

    public Map<Ingredient,Integer> getRecipe() {
        return recipe;
    }

    public void setRecipe(HashMap<Ingredient,Integer> recipe) throws InputValidationException{
        if(!recipe.isEmpty()) {
            this.recipe = recipe;
        }else{
            throw new InputValidationException();
        }
    }

    public void addIngredient(Ingredient ingredient, Integer amount){
        getRecipe().put(ingredient,amount);
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) throws InputValidationException {
        if(availability!=1 && availability!=0) {
            throw new InputValidationException();
        }else{
            this.availability=availability;
        }
    }

    public String getKitchen_status() {
        return kitchen_status;
    }

    public void setKitchen_status(String kitchen_status) {
        this.kitchen_status = kitchen_status;
    }

    public String toString(){
        String buffer="Name:"+ getName()+" ID: "+getId()+" Section: "+ getSection()+" ";
        for (Map.Entry<Ingredient,Integer > entry : getRecipe().entrySet()){
            buffer+="Ingr:"+entry.getKey()+" Amount:"+entry.getValue()+"/ ";
        }
        return buffer;
    }
}
