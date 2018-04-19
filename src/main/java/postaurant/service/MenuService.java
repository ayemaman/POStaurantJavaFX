package postaurant.service;

import org.springframework.stereotype.Component;
import postaurant.database.UserDatabase;
import postaurant.model.Ingredient;
import postaurant.model.Item;

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
            for(Map.Entry<Ingredient, Integer> entry: item.getRecipe().entrySet()) {
                System.out.println(entry.getKey() + " " + entry.getValue());
            }
            return item;
        }else{
            return null;
        }
    }

    public List<Ingredient> getAllIngredients(){
        return userDatabase.getAllIngredients();
    }

    public Ingredient getIngredientById(long id){
        return userDatabase.getIngredientById(id);
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
        Ingredient ingredient1=getIngredientByNameAmount(ingredient.getName(), ingredient.getAmount());
        if(ingredient1==null){
            userDatabase.saveNewIngredient(ingredient);
            return getIngredientByNameAmount(ingredient.getName(),ingredient.getAmount());
        }
        return null;
    }

    public Ingredient getIngredientByNameAmount(String name, Integer amount){
        return userDatabase.getIngredientByNameAmount(name,amount);
    }
}
