package postaurant.service;

import org.springframework.stereotype.Component;
import postaurant.database.UserDatabase;
import postaurant.model.Ingredient;
import postaurant.model.Item;
import postaurant.model.User;

import java.sql.SQLException;
import java.util.*;

@Component
public class MenuService {
    private final UserDatabase userDatabase;

    public MenuService(UserDatabase userDatabase){
        this.userDatabase=userDatabase;
    }

    public Map<String, List<Item>> getSectionsWithItems(){
        Map<String, List<Item>> map=new HashMap<>();
        List<Item> fullItemList=userDatabase.getMenu();
        int itemInt=0;
        while(itemInt<fullItemList.size()-1) {
            if (fullItemList.get(itemInt).getId().equals(fullItemList.get(itemInt + 1).getId())){
                Map.Entry<Ingredient,Integer> entry = fullItemList.get(itemInt + 1).getRecipe().entrySet().iterator().next();
                fullItemList.get(itemInt).addIngredient(entry.getKey(),entry.getValue());
                fullItemList.remove(itemInt + 1);
            }
            else{
                itemInt++;
            }
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

    public Item getItem(String itemID){
        List<Item> list=userDatabase.getItem(itemID);
        Item item=new Item();
        if(!list.isEmpty()){
            for (Item i : list) {
                for (Map.Entry<Ingredient, Integer> entry : i.getRecipe().entrySet()) {
                    list.get(0).addIngredient(entry.getKey(), entry.getValue());
                }
                item=list.get(0);
            }
            return item;
        }
        else{
            return null;
        }

    }

    public List<Ingredient> getAllIngredients(){
        return userDatabase.getAllIngredients();
    }

    public Ingredient getIngredient(String id){
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
}
