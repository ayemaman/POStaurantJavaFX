/**
 * Class that represents Items in this system
 * Implements Comparable interface and has overwritten compareTo(Object o) method
 */

package postaurant.model;

import javafx.beans.property.SimpleStringProperty;

import postaurant.context.StationList;
import postaurant.context.TypeList;
import postaurant.exception.InputValidationException;

import java.time.LocalDateTime;
import java.util.*;

public class Item implements Comparable<Item> {
    private Long id;
    private String name;
    private Double price;
    private String type;
    private String section;
    private String station;
    private Map<Ingredient,Integer> recipe;
    private int availability;
    private String kitchenStatus;
    private Date dateCreated;
    private LocalDateTime dateOrdered;

    public Item(){
    recipe=new TreeMap<>();
    }

    public Item(long id, String name, Double price, String type, String section, String station, int availability, Date dateCreated) throws InputValidationException{
        setId(id);
        setName(name);
        setPrice(price);
        setType(type);
        setStation(station);
        setSection(section);
        setAvailability(availability);
        setDateCreated(dateCreated);
        setKitchenStatus("");
        this.recipe=new TreeMap<>();
        setDateOrdered(null);
    }
    public Item(long id, String name, Double price, String type, String section, String station, int availability, Date dateCreated, LocalDateTime dateOrdered ) throws InputValidationException {
        this(id,name,price,type,section,station,availability,dateCreated);
        setDateOrdered(dateOrdered);
    }

    public Item(long id, String name, Double price, String type, String section, String station, int availability, Map<Ingredient, Integer> recipe, Date dateCreated) throws InputValidationException{
        this(id, name, price, type, section, station, availability,dateCreated);
        setRecipe(recipe);
        setKitchenStatus("");
    }


    public Item (long id, String name, Double price, String type, String section, String station, int availability, Map<Ingredient,Integer> recipe, String kitchenStatus, Date dateCreated, LocalDateTime dateOrdered)throws InputValidationException{
        setId(id);
        setName(name);
        setPrice(price);
        setType(type);
        setStation(station);
        setSection(section);
        setAvailability(availability);
        setRecipe(recipe);
        setKitchenStatus(kitchenStatus);
        setDateCreated(dateCreated);
        setDateOrdered(dateOrdered);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id=id;
    }

    public String getName() {
        return name;
    }
    public SimpleStringProperty getNameProperty(){
        return new SimpleStringProperty(getName());
    }
    /**
     * Item's name Setter
     * Checks using regular expressions if name String consists of ASCII characters and it's length is larger than 1 and smaller than 31;
     * Then removes all Spaces from string and sets it to UPPERCASE
     * @throws InputValidationException if not
     * @param name name of Item;
     */
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

    /**
     * Item's price Setter
     * Doesn't let price to be set to null
     * @throws InputValidationException if price==null
     * @param price Item's price
     */
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

    /**
     * Item's Type Setter
     * Checks if given String matches any of pre-defined Item Type's
     * @see TypeList
     * @throws InputValidationException if not
     * @param type Item's Type
     */
    public void setType(String type) throws InputValidationException {
        boolean match=false;
        for(String s: TypeList.getItemTypes()) {
            if (type.matches(s)) {
                match = true;
            }
        }
        if(match){
            this.type=type;
        } else {
            throw new InputValidationException();
        }
    }

    public String getStation(){
        return station;
    }

    /**
     * Item Station Setter
     * Sets String to upperCase
     * Checks if given String matches any of predefined Stations
     * @see StationList
     * @throws InputValidationException if not
     * @param station Items Station Name
     *
     */
    public void setStation(String station) throws InputValidationException {
        String buffer = station.toUpperCase();
        boolean match=false;
        for(String s: StationList.getStationTypes()){
            if(buffer.matches(s)){
                match=true;
            }
        }
        if(match){
            this.station=buffer;
        } else {
            throw new InputValidationException();
        }
    }
    public String getSection() {
        return section;
    }

    /**
     * Item's section Setter
     * Sets it to UPPERCASE
     * Checks using regular expressions if name String consists of ASCII characters and it's length is larger than 1 and smaller than 31;
     * @throws InputValidationException if not
     * @param section Item's Section name
     */
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

    /**
     * Item recipe Setter
     * Checks if given map is not empty
     * @throws InputValidationException if map is empty
     * @param recipe Item's recipe Map
     */
    public void setRecipe(Map<Ingredient,Integer> recipe) throws InputValidationException{
        if(!recipe.isEmpty()) {
            this.recipe = recipe;
        }else{
            throw new InputValidationException();
        }
    }

    /**
     * Checks if given ingredient EntryKey already exists.
     * If yes -> adds amount to EntryValue
     * If no-> adds new EntryKey with specified EntryValue
     * @param ingredient Ingredient being added to recipe
     * @param amount amount of Ingredient being added to recipe
     */
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

    /**
     * item's availability Setter
     * Checks if availability equals 68 or 86 or 85
     * @throws InputValidationException if not
     * @param availability number code for Item availability
     */
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

    public LocalDateTime getDateOrdered(){
        return this.dateOrdered;
    }
    public void setDateOrdered(LocalDateTime dateOrdered){
        this.dateOrdered=dateOrdered;
    }


    public String toString(){
        StringBuilder buffer= new StringBuilder();
        buffer.append("Name:").append(getName());
        if(getId()!=null) {
            buffer.append("\n ID: ").append(getId());
        }
        buffer.append("\n Section: ").append(getSection());
        buffer.append("\n Type: ").append(getType());
        buffer.append("\n Station: ").append(getStation());
        buffer.append("\n Availability: ").append(getAvailability()).append("\n");

        for (Map.Entry<Ingredient,Integer > entry : getRecipe().entrySet()){
            buffer.append("\nIngr:").append(entry.getKey()).append(" ID:").append(entry.getKey().getId()).append(" Amount:").append(entry.getValue()).append("/ ");
        }
        if(this.dateOrdered!=null) {
            buffer.append("\nDateOrdered").append(getDateOrdered());
        }
        return buffer.toString();
    }

    /**
     * Equals method that is being used in MenuService to distinguish different items
     * Checks if name/price/type/section/station/recipe are equal
     * if no -> return false
     *
     * If those fields are equal, checks if any of dateOrdered are equal to null
     * if yes -> return true
     *
     * If non dateOrdered are equal to null, returns result of their equals
     * @param item to be compared to
     * @return boolean if items are equal or not
     */

    public boolean specialEquals (Item item) {
        if (!this.name.equals(item.getName())) {
            return false;
        }
        if (!this.price.equals(item.getPrice())) {
            return false;
        }
        if (!this.type.equals(item.getType())) {
            return false;
        }
        if (!this.section.equals(item.getSection())) {
            return false;
        }
        if(!this.station.equals(item.getStation())){
            return false;
        }
        if (this.getRecipe().keySet().size() != item.getRecipe().keySet().size()) {
            return false;
        }
        for (Map.Entry<Ingredient, Integer> entry : this.getRecipe().entrySet()) {
            if (!item.getRecipe().containsKey(entry.getKey())) {
                return false;
            } else {
                if (!entry.getValue().equals(item.getRecipe().get(entry.getKey()))) {
                    return false;
                }
            }
        }
        if (this.dateOrdered == null ||item.getDateOrdered() == null ) {
            return true;
        }

        return (this.getDateOrdered().equals(item.getDateOrdered()));

    }

    /**
     * Compare method, that compares Items by id and Date Ordered
     * @param item item that is being compared to
     * @return A number representing whether it is before or after another Item
     */
    @Override
    public int compareTo(Item item) {
        int idCmp=Long.compare(getId(),item.getId());
        if(idCmp==0) {
            if (this.dateOrdered != null) {
                return getDateOrdered().compareTo(item.getDateOrdered());
            }
        }
        return idCmp;
    }

}
