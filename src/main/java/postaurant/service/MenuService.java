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
