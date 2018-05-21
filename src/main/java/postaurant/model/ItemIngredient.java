/**
 * Class that represents item connection to ingredients
 */
package postaurant.model;

public class ItemIngredient {
    private  Long itemId;
    private  Long ingredientId;
    private  Integer amount;

    public ItemIngredient(Long itemId, Long ingredientId, Integer amount) {
        this.itemId = itemId;
        this.ingredientId = ingredientId;
        this.amount = amount;
    }

    public Long getItemId() {
        return itemId;
    }


    public Long getIngredientId() {
        return ingredientId;
    }


    public Integer getAmount() {
        return amount;
    }

}

