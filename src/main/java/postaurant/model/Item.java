package postaurant.model;

import postaurant.exception.InputValidationException;

import java.text.SimpleDateFormat;
import java.util.*;

public class Item implements Comparable<Item> {
    private long id;
    private String name;
    private Double price;
    private String type;
    private String section;
    private Map<Ingredient,Integer> recipe;
    private int availability;
    private String kitchenStatus;
    private Date dateCreated;
    private Date dateOrdered;

    public Item(){
    recipe=new TreeMap<>();
    }

    public Item(long id, String name, Double price, String type, String section, int availability, Date dateCreated) throws InputValidationException{
        setId(id);
        setName(name);
        setPrice(price);
        setType(type);
        setSection(section);
        setAvailability(availability);
        setDateCreated(dateCreated);
    }

    public Item(long id, String name, Double price, String type, String section, int availability, Map<Ingredient, Integer> recipe, Date dateCreated) throws InputValidationException{
        setId(id);
        setName(name);
        setPrice(price);
        setType(type);
        setSection(section);
        setAvailability(availability);
        setRecipe(recipe);
        setDateCreated(dateCreated);
    }


    public Item (long id, String name, Double price, String type, String section, int availability, Map<Ingredient,Integer> recipe, String kitchenStatus, Date dateCreated, Date dateOrdered)throws InputValidationException{
        setId(id);
        setName(name);
        setPrice(price);
        setType(type);
        setSection(section);
        setAvailability(availability);
        setRecipe(recipe);
        setKitchenStatus(kitchenStatus);
        setDateCreated(dateCreated);
        setDateOrdered(dateOrdered);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws InputValidationException {
        if(name.matches("(\\p{ASCII}){2,30}")) {
            String noSpace= name.replaceAll(" ", "");
            this.name = noSpace.toUpperCase();
        }else{
            throw new InputValidationException();
        }
    }

    public Double getPrice(){
        return price;
    }

    public void setPrice(Double price) throws InputValidationException {
        if(price!=null) {
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
            this.type = type.toUpperCase();
        } else {
            throw new InputValidationException();
        }
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) throws InputValidationException {
        if (section.matches("(\\p{Alpha}){2,30}")) {
            this.section = section.toUpperCase();
        } else {
            throw new InputValidationException();
        }
    }

    public Map<Ingredient,Integer> getRecipe() {
        return recipe;
    }

    public void setRecipe(Map<Ingredient,Integer> recipe) throws InputValidationException{
        if(!recipe.isEmpty()) {
            this.recipe = recipe;
        }else{
            throw new InputValidationException();
        }
    }

    public void addIngredient(Ingredient ingredient, Integer amount){
        if(!getRecipe().isEmpty()) {
            Ingredient buffer=null;
            for (Map.Entry<Ingredient, Integer> entry : getRecipe().entrySet()) {
                if (entry.getKey().getId().equals(ingredient.getId())) {
                    buffer=entry.getKey();
                }
            }
            if(buffer!=null) {
                    recipe.put(buffer, recipe.get(buffer) + amount);
                } else {
                    recipe.put(ingredient, amount);
                }

        }else{
            recipe.put(ingredient, amount);
        }
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) throws InputValidationException {
        if((availability!=68) && (availability!=86) && (availability!=85)) {
            throw new InputValidationException();
        }else{
            this.availability=availability;
        }
    }

    public String getKitchenStatus() {
        return kitchenStatus;
    }

    public void setKitchenStatus(String kitchenStatus) {
        this.kitchenStatus = kitchenStatus;
    }

    public Date getDateCreated(){
        return this.dateCreated;
    }

    public void setDateCreated(Date dateCreated){
        this.dateCreated = dateCreated;
    }

    public Date getDateOrdered(){
        return this.dateOrdered;
    }
    public void setDateOrdered(Date dateOrdered){
        this.dateOrdered=dateOrdered;
    }

    public String testString(){
        StringBuilder buffer= new StringBuilder();
        buffer.append("Name:").append(getName()).append("\n ID: ").append(getId()).append("\n Section: ").append(getSection()).append("\n TYPE: ").append(getType()).append("\n AVAIL: ").append(getAvailability());
        for (Map.Entry<Ingredient,Integer > entry : getRecipe().entrySet()){
            buffer.append("\nIngr:").append(entry.getKey()).append(" ID:").append(entry.getKey().getId()).append(" Amount:").append(entry.getValue()).append("/ ");
        }
        return buffer.toString();
    }

    public String toString(){
        StringBuilder buffer= new StringBuilder("Name:" + getName() + "\n ID: " + getId() + "\n Section: " + getSection() + "\n ");
        for (Map.Entry<Ingredient,Integer > entry : getRecipe().entrySet()){
            buffer.append("\nIngr:").append(entry.getKey()).append(" ID:").append(entry.getKey().getId()).append(" Amount:").append(entry.getValue()).append("/ ");
        }
        SimpleDateFormat ft = new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
        if(this.dateOrdered!=null) {
            buffer.append("\nDateOrdered").append(ft.format(getDateOrdered()));
        }
        return buffer.toString();
    }


    public boolean equals (Item item) {
        if (!this.name.equals(item.getName())) {
            return false;
        }
        if(!this.price.equals(item.getPrice())){
            return false;
        }
        if(!this.type.equals(item.getType())){
            return false;
        }
        if(!this.section.equals(item.getSection())){
            return false;
        }
        if(this.getRecipe().keySet().size()!=item.getRecipe().keySet().size()){
            return false;
        }
        for(Map.Entry<Ingredient, Integer>entry:this.getRecipe().entrySet()){
            if(!item.getRecipe().containsKey(entry.getKey())){
                return false;
            }else{
                if(!entry.getValue().equals(item.getRecipe().get(entry.getKey()))){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int compareTo(Item o) {
        int idCmp=Long.compare(this.getId(),o.getId());
        if(idCmp!=0){
            return idCmp;
        }
        return this.getDateOrdered().compareTo(o.getDateOrdered());
    }
}
