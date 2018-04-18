package postaurant.service;

import org.springframework.stereotype.Component;
import postaurant.database.UserDatabase;
import postaurant.model.Ingredient;
import postaurant.model.Item;

import java.util.*;

@Component
public class MenuService {
    private final UserDatabase userDatabase;

    public MenuService(UserDatabase userDatabase){
        this.userDatabase=userDatabase;
    }

    public Map<String, List<Item>> getSectionsWithItems() {
        Map<String, List<Item>> map = new TreeMap<>();
        List<Item> fullItemList = userDatabase.getMenu();
        for (int itemInt = 0; itemInt < fullItemList.size() - 1; )
            if (fullItemList.get(itemInt).getId() == (fullItemList.get(itemInt + 1).getId())) {
                Map.Entry<Ingredient, Integer> entry = fullItemList.get(itemInt + 1).getRecipe().entrySet().iterator().next();
                fullItemList.get(itemInt).addIngredient(entry.getKey(), entry.getValue());
                fullItemList.remove(itemInt + 1);
            } else {
                itemInt++;
            }
        for (Item item : fullItemList) {
            if (map.containsKey(item.getSection())) {
                map.get(item.getSection()).add(item);
            } else {
                List<Item> itemsForSection = new ArrayList<>();
                itemsForSection.add(item);
                map.put(item.getSection(), itemsForSection);
            }
        }
        for(Map.Entry<String, List<Item>> entry:map.entrySet()) {
            List<Item> list = entry.getValue();
            for (int i = 0; i < list.size()-1;i++ ) {
                if (list.get(i).getId() == list.get(i + 1).getId()) {
                    i++;
                } else {
                    for (int j = i + 1; j < list.size(); ) {
                        if (list.get(i).getName().equals(list.get(j).getName())) {
                            list.remove(j);
                        } else {
                            j++;
                        }
                    }
                }
            }
        }
        return map;
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

    public Ingredient getIngredient(long id){
        return userDatabase.getIngredient(id);
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

}
