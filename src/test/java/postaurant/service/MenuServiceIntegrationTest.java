package postaurant.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;
import postaurant.POStaurant;
import postaurant.context.DataSourceBeans;
import postaurant.model.Ingredient;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class MenuServiceIntegrationTest {

            @Resource
            private MenuService menuService;

            @Resource
            private JdbcTemplate jdbcTemplate;

            @Transactional
            @Test
            // simple test to check that when retrieving ingredients from database it creates ingredient object with values from database table.
            public void getIngredients_ReturnsIngredientItems() {
                // ARRANGE - set up the test test state you want to test in an easy to check way

                String name = "test_ingredient";
                Integer amount = 25;
                Double price = 25.0;
                int availability = 50;
                String allergy = "test_allergy";

                jdbcTemplate.update("insert into " +
                        "ingredients (ingredient_name, ingredient_amount, ingredient_price, ingredient_availability, ingredient_allergy, ingredient_date_created) " +
                        "values (?, ?, ?, ?, ?, ?)", name, amount, price, availability, allergy, LocalDateTime.now());

                // ACT - execute the code that you're wanting to test works fine
                List<Ingredient> ingredients = menuService.getAllIngredients();

                // ASSERT - check that the values are what you expect them to be
                Ingredient testIngredient = ingredients.stream().filter(ing -> ing.getName().equals(name)).findFirst().get();
                Assert.assertEquals(name, testIngredient.getName());
                Assert.assertEquals(amount, testIngredient.getAmount());
                Assert.assertEquals(price, testIngredient.getPrice());
                Assert.assertEquals(availability, testIngredient.getAvailability());
                Assert.assertEquals(allergy, testIngredient.getAllergy());
            }
        }

