/**
 * Service, that works with database data related to Menu;
 * @see postaurant.model.Item
 * @see postaurant.model.Ingredient
 */
package postaurant.service;

import org.springframework.stereotype.Component;
import postaurant.database.UserDatabase;
import postaurant.model.Ingredient;
import postaurant.model.Item;
import postaurant.context.OrderInfo;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class MenuService {
    private final UserDatabase userDatabase;

    public MenuService(UserDatabase userDatabase){
        this.userDatabase=userDatabase;
    }

    /**
     * Creates a list of all distinct items and filters it by distinct name;
     * @return a Map, that holds Items grouped by Section;
     */
    public Map<String, List<Item>> getSectionsWithItems() {
        List<Item> fullItemList = userDatabase.getMenu().stream().filter(distinctByKey(Item::getName)).collect(Collectors.toList());
        return fullItemList.stream().collect(Collectors.groupingBy(Item::getSection));
    }

    /**
     * Creates a list of all food items and filters it by distinct name;
     * @return a Map, that holds food items grouped by Section;
     */
    public Map<String, List<Item>> getFoodSectionsWithItems(){
        List<Item> foodItemList = userDatabase.getFoodMenu().stream().filter(distinctByKey(Item::getName)).collect(Collectors.toList());
        return foodItemList.stream().collect(Collectors.groupingBy(Item::getSection));
    }

    /**
     * Creates a list of all drink items and filters it by distinct name;
     * @return a Map, that holds drink items grouped by Section;
     */
    public Map<String, List<Item>> getDrinkSectionsWithItems(){
        List<Item> foodItemList = userDatabase.getDrinkMenu().stream().filter(distinctByKey(Item::getName)).collect(Collectors.toList());
        return foodItemList.stream().collect(Collectors.groupingBy(Item::getSection));
    }

    /**
     * Creates a list of all Ingredients and filters it by distinct name;
     * @return a Map<String, List<Ingredient>> that holds Ingredients grouped by First Letter of it's name;
     */
    public Map<String, List<Ingredient>> getAZSectionsWithIngredients(){
        List<Ingredient> fullIngredientList = userDatabase.getAllIngredients().stream().filter(distinctByKey(Ingredient::getName)).collect(Collectors.toList());
        Map<Character,List<Ingredient>> map=fullIngredientList.stream().collect(Collectors.groupingBy(Ingredient::getFirstLetter));
        Map<String,List<Ingredient>> map2=new HashMap<>();
        map2.put("A-C", new ArrayList<>());
        map2.put("D-F", new ArrayList<>());
        map2.put("G-I", new ArrayList<>());
        map2.put("J-L", new ArrayList<>());
        map2.put("M-O", new ArrayList<>());
        map2.put("P-S", new ArrayList<>());
        map2.put("T-V", new ArrayList<>());
        map2.put("W-Z", new ArrayList<>());
        for(Map.Entry<Character,List<Ingredient>> entry: map.entrySet()) {
            switch (entry.getKey()) {
                case 'A':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("A-C").add(ingr);
                    }
                    break;
                case 'B':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("A-C").add(ingr);
                    }
                    break;
                case 'C':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("A-C").add(ingr);
                    }
                    break;
                case 'D':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("D-F").add(ingr);
                    }
                    break;
                case 'E':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("D-F").add(ingr);
                    }
                    break;
                case 'F':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("D-F").add(ingr);
                    }
                    break;
                case 'G':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("G-I").add(ingr);
                    }
                    break;
                case 'H':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("G-I").add(ingr);
                    }
                    break;
                case 'I':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("G-I").add(ingr);
                    }
                    break;
                case 'J':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("J-L").add(ingr);
                    }
                    break;
                case 'K':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("J-L").add(ingr);
                    }
                    break;
                case 'L':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("J-L").add(ingr);
                    }
                    break;
                case 'M':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("M-O").add(ingr);
                    }
                    break;
                case 'N':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("M-O").add(ingr);
                    }
                    break;
                case 'O':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("M-O").add(ingr);
                    }
                    break;
                case 'P':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("P-S").add(ingr);
                    }
                    break;
                case 'Q':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("P-S").add(ingr);
                    }
                    break;
                case 'R':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("P-S").add(ingr);
                    }
                    break;
                case 'S':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("P-S").add(ingr);
                    }
                    break;
                case 'T':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("T-V").add(ingr);
                    }
                    break;
                case 'U':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("T-V").add(ingr);
                    }
                    break;
                case 'V':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("T-V").add(ingr);
                    }
                    break;
                case 'W':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("W-Z").add(ingr);
                    }
                    break;
                case 'X':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("W-Z").add(ingr);
                    }
                    break;
                case 'Y':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("W-Z").add(ingr);
                    }
                    break;
                case 'Z':
                    for (Ingredient ingr : entry.getValue()) {
                        map2.get("W-Z").add(ingr);
                    }
                    break;
            }
        }
        return map2;
    }

    /**
     *Method, that is used in filtering List to get distinct Items by name
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * Method, that retrieves Item with specific id
     * @param itemID item's id number
     * @return Item that is specific to param ID
     */
    public Item getItemById(long itemID) {
        return userDatabase.getItemById(itemID);
    }

    /**
     * Method, that retrieves last saved Item from database with given name
     * @param name item's name
     * @return last saved Item from database with given name
     */
    public Item getLatestSavedItemByName(String name){
        return userDatabase.getItemByName(name);
    }

    /**
     * Method that returns all custom items that are specific to this name
     * @param name item's name
     * @return List<Item> of all custom items that has given name
     */
    public List<Item> getCustomItemsByName(String name){
        return userDatabase.getCustomItemsByName(name);

    }

    /**
     * Method that returns all Ingredients saved in database;
     * @return List<Ingredient> that holds all Ingredients;
     */
    public List<Ingredient> getAllIngredients(){
        return userDatabase.getAllIngredients();
    }

    /**
     * Method that returns Ingredient with given id from database;
     * @param id Ingredient's id;
     * @return Ingredient with given id;
     */
    public Ingredient getIngredientById(long id){
        return userDatabase.getIngredientById(id);
    }

    /**
     * Method that saves new custom Item to database and set's all previous Custom items availability with given name to "86" ->unavailable
     * @param item that's about to get saved
     * @return saved Item
     */
    public Item saveNewCustomItem(Item item) {
        List<Item> previousCustomItems = getCustomItemsByName(item.getName());
        for (Item i : previousCustomItems) {
            if (item.specialEquals(i)) {
                return i;
            }
        }
        userDatabase.saveNewCustomItem(item);
        Item itemSaved = getLatestSavedItemByName(item.getName());
        userDatabase.setNewItem(itemSaved);
        return getItemById(itemSaved.getId());
    }

    /**
     * Gets BAR info on all drink Items that are currently ordered
     * @return List<OrderInfo> of all currently ordered drink Items
     */
    public List<OrderInfo> getAllOrderItemsForBarQC(){
        List<OrderInfo> items=userDatabase.getQCBarOrderInfo();
        return items;
    }

    /**
     * Gets BAR QC info on all drink Items that are currently ordered
     * @return List<OrderInfo> of all currently ordered drink Items
     */
    public List<OrderInfo> getAllOrderedItemsForBar(){
        List<OrderInfo> items=userDatabase.getBarOrderInfo();
        return items;
    }

    /**
     * Method that returns QC info on all food Items that are currently ordered
     * @return List<OrderInfo> of all currently ordered food Items
     */
    public List<OrderInfo> getAllOrderedItemsForQC(){
        List<OrderInfo> items=userDatabase.getQCOrderInfo();
        return items;
    }

    /**
     * Method that returns Kitchen info on all food Items that are currently ordered
     * @return List<OrderInfo> of all currently ordered food Items
     */
    public List<OrderInfo> getAllOrderedItemsForKitchen(){
        List<OrderInfo> items=userDatabase.getKitchenOrderInfo();
        return items;
    }


    /**
     * Merhod that save's new Item and connect it to Ingredients
     * @param item that is beeing saved to Database
     * @return saved Item
     */
    public Item saveNewItem(Item item) {
        Item item1 = getLatestSavedItemByName(item.getName());
        if (item1 != null) {
            if (item.specialEquals(item1)) {
                if (item.getAvailability() == item1.getAvailability()) {
                    return getItemById(item1.getId());
                }
                else {
                    userDatabase.changeItemAvailability(item1, item.getAvailability());
                    return getItemById(item1.getId());
                }
            }
            else {
                userDatabase.saveNewItem(item);
                Item itemSaved = getLatestSavedItemByName(item.getName());
                userDatabase.setNewItem(itemSaved);
                return getItemById(itemSaved.getId());
            }
        }
        else {
            userDatabase.saveNewItem(item);
            Item itemSaved = getLatestSavedItemByName(item.getName());
            userDatabase.setNewItem(itemSaved);
            return getItemById(itemSaved.getId());
        }
    }

    /**
     * Method that returns all sections that are used in database
     * @return List<String> of section names
     */
    public List<String> getSections(){
        return userDatabase.getSections();
    }

    /**
     * Method that saves new Ingredient to database
     * @param ingredient to be saved
     * @return saved Ingredient
     */
    public Ingredient saveNewIngredient(Ingredient ingredient){
        Ingredient ingredient1=getIngredientByNameAmountPrice(ingredient.getName(), ingredient.getAmount(), ingredient.getPrice());
        if(ingredient1!=null) {
            if (ingredient.getAvailability() != ingredient1.getAvailability()) {
                userDatabase.changeIngredientAvailability(ingredient1,ingredient.getAvailability());
            }
            if(!ingredient.getAllergy().equals(ingredient1.getAllergy())){
                userDatabase.changeIngredientAllergy(ingredient1, ingredient.getAllergy());
            }
            return getIngredientById(ingredient1.getId());
        }else{
            userDatabase.saveNewIngredient(ingredient);
            return getIngredientByNameAmountPrice(ingredient.getName(),ingredient.getAmount(), ingredient.getPrice());
        }
    }

    /**
     * Method get specific Ingredient from database with given parameters
     * @param name of Ingredient
     * @param amount of Ingredient
     * @param price of Ingredient
     * @return specific Ingredient
     */
    public Ingredient getIngredientByNameAmountPrice(String name, Integer amount, Double price){
        return userDatabase.getIngredientByNameAmountPrice(name,amount,price);
    }

    /**
     * Method, that sets ordered items status to 'SEEN'
     * @param orderId that holds this item
     * @param itemId that is being modified
     * @param timeOrdered time this item was ordered
     */
    public void setKitchenStatusToSeen(Long orderId, Long itemId, LocalDateTime timeOrdered){
        userDatabase.setKitchenStatusToSeen(orderId, itemId, timeOrdered);
    }

    /**
     * Method, that sets ordered items status to 'READY'
     * @param orderId that holds this item
     * @param itemId that is being modified
     * @param timeOrdered time this item was ordered
     */
    public void setKitchenStatusToReady(Long orderId, Long itemId, LocalDateTime timeOrdered){
        userDatabase.setKitchenStatusToReady(orderId, itemId, timeOrdered);
    }

    /**
     * Method, that sets ordered items status to 'BUMPED'
     * @param orderId that holds this item
     * @param itemId that is being modified
     * @param timeOrdered time this item was ordered
     */
    public void setKitchenStatusToBumped(Long orderId, Long itemId, LocalDateTime timeOrdered){
        userDatabase.setKitchenStatusToBumped(orderId,itemId,timeOrdered);
    }
}
