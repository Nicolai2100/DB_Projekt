package dal;

import dal.dto.IIngredientDTO;
import dal.dto.RecipeDTO;
import org.junit.Test;

import java.util.HashMap;

public class RecipeDTOTest {
    RecipeDTO recipeDTO = new RecipeDTO();

    @Test
    public void toStringTest() {
        HashMap<String, IIngredientDTO> inlist = new HashMap<>();

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
