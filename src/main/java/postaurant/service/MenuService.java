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

    public Map<String, List<Item>> getSectionsWithItems(){
        Map<String, List<Item>> map=new TreeMap<>();
        List<Item> fullItemList=userDatabase.getMenu();

        for(int itemInt=0;itemInt<fullItemList.size()-1;)
            if (fullItemList.get(itemInt).getId()==(fullItemList.get(itemInt + 1).getId())){
                Map.Entry<Ingredient,Integer> entry = fullItemList.get(itemInt + 1).getRecipe().entrySet().iterator().next();
                fullItemList.get(itemInt).addIngredient(entry.getKey(),entry.getValue());
                fullItemList.remove(itemInt + 1);
            }
            else{
                itemInt++;
            }

        for(Item item:fullItemList){
            if(map.containsKey(item.getSection())){
                map.get(item.getSection()).add(item);
            }else{
                List<Item> recipe=new ArrayList<>();
                recipe.add(item);
                map.put(item.getSection(),recipe);
            }
        }
        return map;
    }

    public Item getItem(long itemID) {
        List<Item> list = userDatabase.getItemById(itemID);
        if (!list.isEmpty()) {
            Item item = list.get(0);
            Date newestEntry = list.get(0).getDateCreated();
            for (int i = 1; i < list.size(); ) {
                if (list.get(i).getDateCreated() == newestEntry) {
                    Map.Entry<Ingredient, Integer> entry = list.get(0).getRecipe().entrySet().iterator().next();
                    item.addIngredient(entry.getKey(), entry.getValue());
                    list.remove(i);
                } else {
                    i++;
                }
            }
            return item;
        } else {
            return null;
        }
    }

    public List<Ingredient> getAllIngredients(){
        return userDatabase.getAllIngredients();
    }

    public Ingredient getIngredient(long id){
        return userDatabase.getIngredient(id);
    }

    public Item saveNewItem(Item item){
        if(getItem(item.getId())==null){
            userDatabase.saveNewItem(item);
            return getItem(item.getId());
        }else{
            return null;
        }

    }

    public List<String> getSections(){
        return userDatabase.getSections();
    }

}
