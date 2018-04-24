package postaurant.service;

import org.springframework.stereotype.Component;
import postaurant.database.UserDatabase;
import postaurant.model.Ingredient;
import postaurant.model.Item;
import postaurant.model.Order;
import postaurant.model.User;

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


    public Map<String, List<Item>> getSectionsWithItems() {
        List<Item> fullItemList = userDatabase.getMenu().stream().filter(distinctByKey(Item::getName)).collect(Collectors.toList());
        return fullItemList.stream().collect(Collectors.groupingBy(Item::getSection));
    }

    public Map<String, List<Item>> getFoodSectionsWithItems(){
        List<Item> foodItemList = userDatabase.getFoodMenu().stream().filter(distinctByKey(Item::getName)).collect(Collectors.toList());
        return foodItemList.stream().collect(Collectors.groupingBy(Item::getSection));
    }

    public Map<String, List<Item>> getDrinkSectionsWithItems(){
        List<Item> foodItemList = userDatabase.getDrinkMenu().stream().filter(distinctByKey(Item::getName)).collect(Collectors.toList());
        return foodItemList.stream().collect(Collectors.groupingBy(Item::getSection));
    }
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

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public Item getItemById(long itemID) {
        List<Item> list = userDatabase.getItemById(itemID);
        if (!list.isEmpty()) {
            Item item = list.get(0);
           for(int i=0;i<list.size()-1;){
               Map.Entry<Ingredient,Integer> entry=list.get(i+1).getRecipe().entrySet().iterator().next();
               list.get(i).addIngredient(entry.getKey(),entry.getValue());
               list.remove(i+1);
           }
            return item;
        } else {
            return null;
        }
    }

    public Item getLatestSavedItemByName(String name){
        List<Item> list= userDatabase.getItemByName(name);
        if(!list.isEmpty()) {
            Item item = list.get(0);
            Date newestEntry = list.get(0).getDateCreated();
            for (int i = 1; i < list.size(); ) {
                if (list.get(i).getDateCreated().getTime()==(newestEntry.getTime())) {
                    Map.Entry<Ingredient, Integer> entry = list.get(i).getRecipe().entrySet().iterator().next();
                    item.addIngredient(entry.getKey(), entry.getValue());
                    list.remove(i);
                } else {
                    i++;
                }
            }

            return item;
        }else{
            return null;
        }
    }

    public List<Item> getCustomItemsByName(String name){
        return userDatabase.getCustomItemsByName(name);

    }

    public List<Ingredient> getAllIngredients(){
        return userDatabase.getAllIngredients();
    }

    public Ingredient getIngredientById(long id){
        return userDatabase.getIngredientById(id);
    }
    public Item saveNewCustomItem(Item item) {
        List<Item> previousCustomItems = getCustomItemsByName(item.getName());
        for (Item i : previousCustomItems) {
            if (item.equals(i)) {
                return i;
            }
        }
        userDatabase.saveNewCustomItem(item);
        Item itemSaved = getLatestSavedItemByName(item.getName());
        userDatabase.setNewItem(itemSaved);
        return getItemById(itemSaved.getId());
    }



    public Item saveNewItem(Item item) {
        Item item1 = getLatestSavedItemByName(item.getName());
        if (item1 != null) {
            if (item.equals(item1)) {
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

    public List<String> getSections(){
        return userDatabase.getSections();
    }

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

    public Ingredient getIngredientByNameAmountPrice(String name, Integer amount, Double price){
        return userDatabase.getIngredientByNameAmountPrice(name,amount,price);
    }
}
