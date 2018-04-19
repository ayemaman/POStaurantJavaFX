package postaurant.model;

public class ItemIngredient {
    private  Integer itemId;
    private  Integer ingredientId;
    private  Integer amount;

    public ItemIngredient(Integer itemId, Integer ingredientId, Integer amount) {
        this.itemId = itemId;
        this.ingredientId = ingredientId;
        this.amount = amount;
    }

    public Integer getItemId() {
        return itemId;
    }


    public Integer getIngredientId() {
        return ingredientId;
    }


    public Integer getAmount() {
        return amount;
    }

}

