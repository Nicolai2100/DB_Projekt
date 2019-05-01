package dal;

import dal.dto.*;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RecipeDAOTest {
    ConnectionDAO connectionDAO = new ConnectionDAO();
    UserDAO userDAO = new UserDAO();
    IngredientDAO ingredientDAO = new IngredientDAO();
    IngredientListDAO ingredientListDAO = new IngredientListDAO(ingredientDAO);
    RecipeDAO recipeDAO = new RecipeDAO(ingredientListDAO, userDAO);

    public RecipeDAOTest() throws DALException {
    }

    /* @Before
     public void initialize() {
         connectionDAO = new ConnectionDAO();
         productBatchDAO = new ProductBatchDAO(connectionDAO);
         userDAO = new UserDAO(connectionDAO);
         ingredientDAO = new IngredientDAO(connectionDAO);
         ingredientListDAO = new IngredientListDAO(connectionDAO, userDAO, ingredientDAO);
         commoditybatchDAO = new CommodityBatchDAO(connectionDAO, userDAO);
         oldRecipeDAO = new OldRecipeDAO(connectionDAO, recipeDAO);
         recipeDAO = new RecipeDAO(connectionDAO, ingredientListDAO, userDAO, oldRecipeDAO);

         userDAOTest = new UserDAOTest();
     }
 */
    @After
    public void close() throws DALException {
        connectionDAO.closeConn();
    }

    @Test
    public void archiveRecipe() throws DALException {
        IUserDTO testUser = new UserDTO();
        testUser.addRole("farmaceut");
        testUser.setIsActive(true);
        recipeDAO.archiveRecipe(2, testUser);
    }

    @Test
    public void getOldRecipes() throws DALException {
        for (IRecipeDTO oldRecipe : recipeDAO.getListOfOldRecipes()) {
            System.out.println(oldRecipe);
        }

    }

    @Test
    public void updateRecipe() throws DALException {
        IRecipeDTO recipeDTO = recipeDAO.getActiveRecipe(2);
        System.out.println(recipeDTO);
/*
        recipeDTO.setName("Opdateret 2 " + recipeDTO.getName());
*/
        recipeDAO.updateRecipe(recipeDTO);
    }

    @Test
    public void getRecipe() throws DALException {
        IRecipeDTO recipeDTO = recipeDAO.getActiveRecipe(2);
        System.out.println(recipeDTO);
    }

    @Test
    public void createRecipe() throws DALException {
        IRecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setRecipeId(1);
        recipeDTO.setName("Norethisteron/estrogen");
        recipeDTO.setMadeBy(userDAO.getUser(10));

        recipeDTO.setIngredientsList(ingredientListDAO.getIngredientList(recipeDTO));

        List<IIngredientDTO> ingredients = new ArrayList<>();
        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(1);
        ingredientDTO.setName("estradiol");
        ingredientDTO.setType("active");
        ingredientDTO.setAmount(1);
        ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(2);
        ingredientDTO.setName("norethisteronacetat");
        ingredientDTO.setType("active");
        ingredientDTO.setAmount(0.5);
        ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(3);
        ingredientDTO.setName("opovidon");
        ingredientDTO.setType("helper");
        ingredientDTO.setAmount(50);
        ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(4);
        ingredientDTO.setName("laktosemonohydrat");
        ingredientDTO.setType("helper");
        ingredientDTO.setAmount(10);
        ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(5);
        ingredientDTO.setName("magnesiumstearat");
        ingredientDTO.setType("helper");
        ingredientDTO.setAmount(15);
        ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(6);
        ingredientDTO.setName("majsstivelse");
        ingredientDTO.setType("helper");
        ingredientDTO.setAmount(120);
        ingredients.add(ingredientDTO);

        recipeDTO.setIngredientsList(ingredients);
        recipeDAO.createRecipe(recipeDTO);
    }
}