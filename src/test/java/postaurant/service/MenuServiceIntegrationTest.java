package postaurant.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import postaurant.POStaurant;
import postaurant.model.Ingredient;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = POStaurant.class)
public class MenuServiceIntegrationTest {

    @Resource
    private MenuService menuService;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Transactional
    @Test // simple test to check that when retrieving ingredients from from database it creates ingredient object with values from database table.
    public void getIngredients_ReturnsIngredientItems() {
        // ARRANGE - set up the test test state you want to test in an easy to check way
        int id = 9999;
        String name = "test_ingredient";
        Integer amount = 25;
        Double price = 25.0;
        int availability = 50;
        String allergy = "test_allergy";
        jdbcTemplate.update("insert into " +
                "ingredients (ingredient_id, ingredient_name, ingredient_amount, ingredient_price, ingredient_availability, ingredient_allergy, ingredient_date_created) " +
                "values (?, ?, ?, ?, ?, ?, ?)", id, name, amount, price, availability, allergy, new Date());

        // ACT - execute the code that you're wanting to test works fine
        List<Ingredient> ingredients = menuService.getAllIngredients();

        // ASSERT - check that the values are what you expect them to be
        Ingredient testIngredient = ingredients.stream().filter(ing -> ing.getId() == id).findFirst().get();
        Assert.assertEquals(name, testIngredient.getName());
        Assert.assertEquals(amount, testIngredient.getAmount());
        Assert.assertEquals(price, testIngredient.getPrice());
        Assert.assertEquals(availability, testIngredient.getAvailability());
        Assert.assertEquals(allergy, testIngredient.getAllergy());
    }
}
