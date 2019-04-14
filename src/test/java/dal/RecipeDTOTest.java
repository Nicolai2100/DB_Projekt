package dal;

import dal.dto.IngredientDTO;
import dal.dto.RecipeDTO;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.*;

public class RecipeDTOTest {
    RecipeDTO recipeDTO = new RecipeDTO();

    @Test
    public void toStringTest() {
        HashMap<String, IngredientDTO> inlist = new HashMap<>();

        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("estradiol");
        ingredientDTO.setType("active");
        ingredientDTO.setIngredientId(1);
        ingredientDTO.setAmountInMG(1);
        inlist.put(ingredientDTO.getName(), ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("norethisteronacetat");
        ingredientDTO.setType("active");
        ingredientDTO.setIngredientId(2);
        ingredientDTO.setAmountInMG(0.5);
        inlist.put(ingredientDTO.getName(), ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("opovidon");
        ingredientDTO.setType("helper");
        ingredientDTO.setIngredientId(3);
        ingredientDTO.setAmountInMG(50);
        inlist.put(ingredientDTO.getName(), ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("laktosemonohydrat");
        ingredientDTO.setType("helper");
        ingredientDTO.setIngredientId(4);
        ingredientDTO.setAmountInMG(10);
        inlist.put(ingredientDTO.getName(), ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("magnesiumstearat");
        ingredientDTO.setType("helper");
        ingredientDTO.setIngredientId(5);
        ingredientDTO.setAmountInMG(15);
        inlist.put(ingredientDTO.getName(), ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("majsstivelse");
        ingredientDTO.setType("helper");
        ingredientDTO.setIngredientId(6);
        ingredientDTO.setAmountInMG(120);
        inlist.put(ingredientDTO.getName(), ingredientDTO);

        recipeDTO.setIngredients(inlist);
        recipeDTO.setName("norethisteron/estrogen");
        recipeDTO.setRecipeId(2);
        recipeDTO.setMadeBy(10);
        System.out.println(recipeDTO);

    }

/*
        Præparat: Norethisteron/estrogen "PantherPharma"
        Opbevaringstid: 36 måneder
        Aktive stoffer
        Estradiol 1 mg
        Norethisteronacetat 0,5 mg
                Hjælpestoffer
        Tablet:
        Opovidon 50 mg
        Laktosemonohydrat 10 mg
        Magnesiumstearat 15 mg
        Majsstivelse  120 mg


        recipeDTO.setName("Sildenafil");
        recipeDTO.setMadeBy(10);
        recipeDTO.setRecipeId(2);
*/


/*
        recipeDTO.setIngredients();
*/
}
