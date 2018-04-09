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
            if(map.containsKey(item.getCat())){
                map.get(item.getCat()).add(item);
            }else{
                List<Item> recipe=new ArrayList<>();
                recipe.add(item);
                map.put(item.getCat(),recipe);
            }
        }

        return map;
    }

    public Item getItem(String itemID){
        List<Item> list=userDatabase.getItem(itemID);
        for(int i=0;i<list.size();){
            Map.Entry<Ingredient,Integer> entry=list.get(i).getRecipe().entrySet().iterator().next();
            list.get(i).addIngredient(entry.getKey(),entry.getValue());
            list.remove(i+1);
        }
        Item itemWithIngredients=list.get(0);
        return itemWithIngredients;
    }

}
