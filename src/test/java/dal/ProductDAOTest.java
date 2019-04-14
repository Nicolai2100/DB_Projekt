package dal;

import dal.dto.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ProductDAOTest {
    ProductDAO productDAO = new ProductDAO();
    UserDAOImpl userDAO = new UserDAOImpl();


    @After
    public void closeAll() throws SQLException {
        userDAO.getConn().close();
        productDAO.getConn().close();
    }

    @Test
    public void createConnection() {


    }

    @Test
    public void createProduct() throws IUserDAO.DALException {


        ProductDTO productDTO = new ProductDTO();

        UserDTO userDTO = (UserDTO) userDAO.getUser(10);
        productDTO.setMadeBy(userDTO);
        productDTO.setName("Ost");
        productDTO.setProductId(1);
        productDTO.setRecipe(1);
        /*productDTO.setProductBatches();
         */
        productDAO.createProduct(productDTO);
    }

    @Test
    public void createUser() throws IUserDAO.DALException {

        UserDTO testUser = new UserDTO();
        testUser.setUserId(10);
        testUser.setUserName("Puk Hansen");
        testUser.setIni("PH");
        ArrayList<String> roles = new ArrayList();
        roles.add("administrator");
        roles.add("farmaceut");
        testUser.setRoles(roles);
        userDAO.createUser(testUser);
    }

    @Test
    public void getRecipe() {
        RecipeDTO recipeDTO = productDAO.getRecipe(2);
        System.out.println(recipeDTO);
    }

    @Test
    public void createRecipe() throws IUserDAO.DALException {
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setRecipeId(2);
        recipeDTO.setName("Norethisteron/estrogen");
        recipeDTO.setMadeBy(userDAO.getUser(10));
        recipeDTO.setIngredientsList(productDAO.getIngredientList(recipeDTO));
        productDAO.createRecipe(recipeDTO);
    }

    @Test
    public void createIngredientList() throws IUserDAO.DALException {
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setName("norethisteron/estrogen");
        recipeDTO.setRecipeId(2);
        recipeDTO.setMadeBy(userDAO.getUser(10));
        List<IIngredientDTO> ingredients = new ArrayList<>();

        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(1);
        ingredientDTO.setAmount(1);
        ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("norethisteronacetat");
        ingredientDTO.setType("active");
        ingredientDTO.setIngredientId(2);
        ingredientDTO.setAmount(0.5);
        ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("opovidon");
        ingredientDTO.setType("helper");
        ingredientDTO.setIngredientId(3);
        ingredientDTO.setAmount(50);
        ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("laktosemonohydrat");
        ingredientDTO.setType("helper");
        ingredientDTO.setIngredientId(4);
        ingredientDTO.setAmount(10);
        ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("magnesiumstearat");
        ingredientDTO.setType("helper");
        ingredientDTO.setIngredientId(5);
        ingredientDTO.setAmount(15);
        ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("majsstivelse");
        ingredientDTO.setType("helper");
        ingredientDTO.setIngredientId(6);
        ingredientDTO.setAmount(120);
        ingredients.add(ingredientDTO);

//        recipeDTO.setIngredients(ingredients);
        recipeDTO.setIngredientsList(ingredients);

        productDAO.createIngredientList(recipeDTO);
    }

    @Test
    public void getIngredientList() throws IUserDAO.DALException {
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setRecipeId(2);
        List<IIngredientDTO> ingrediendts = productDAO.getIngredientList(recipeDTO);

        System.out.println(ingrediendts);
    }


    @Test
    public void createIngredient() throws IUserDAO.DALException {
        List<IngredientDTO> ingredients = new ArrayList<>();

        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("estradiol");
        ingredientDTO.setType("active");
        ingredientDTO.setIngredientId(1);
        ingredientDTO.setAmount(1);
        ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("norethisteronacetat");
        ingredientDTO.setType("active");
        ingredientDTO.setIngredientId(2);
        ingredientDTO.setAmount(0.5);
        ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("opovidon");
        ingredientDTO.setType("helper");
        ingredientDTO.setIngredientId(3);
        ingredientDTO.setAmount(50);
        ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("laktosemonohydrat");
        ingredientDTO.setType("helper");
        ingredientDTO.setIngredientId(4);
        ingredientDTO.setAmount(10);
        ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("magnesiumstearat");
        ingredientDTO.setType("helper");
        ingredientDTO.setIngredientId(5);
        ingredientDTO.setAmount(15);
        ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("majsstivelse");
        ingredientDTO.setType("helper");
        ingredientDTO.setIngredientId(6);
        ingredientDTO.setAmount(120);
        ingredients.add(ingredientDTO);

        for (IngredientDTO ingredient : ingredients) {
            productDAO.createIngredient(ingredient);
        }
    }

    @Test
    public void getIngredient() throws IUserDAO.DALException {
        IIngredientDTO ingredientDTO = productDAO.getIngredient(1);
        assertEquals(ingredientDTO.getType(), "active");
        System.out.println(ingredientDTO);
    }
}