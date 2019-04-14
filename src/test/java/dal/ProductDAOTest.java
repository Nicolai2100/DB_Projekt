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
    public void createIngredient() throws IUserDAO.DALException {
        List<IngredientDTO> ingredients = new ArrayList<>();

        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("estradiol");
        ingredientDTO.setType("active");
        ingredientDTO.setIngredientId(1);
        ingredientDTO.setAmountInMG(1);
        ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("norethisteronacetat");
        ingredientDTO.setType("active");
        ingredientDTO.setIngredientId(2);
        ingredientDTO.setAmountInMG(0.5);
        ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("opovidon");
        ingredientDTO.setType("helper");
        ingredientDTO.setIngredientId(3);
        ingredientDTO.setAmountInMG(50);
        ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("laktosemonohydrat");
        ingredientDTO.setType("helper");
        ingredientDTO.setIngredientId(4);
        ingredientDTO.setAmountInMG(10);
        ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("magnesiumstearat");
        ingredientDTO.setType("helper");
        ingredientDTO.setIngredientId(5);
        ingredientDTO.setAmountInMG(15);
        ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("majsstivelse");
        ingredientDTO.setType("helper");
        ingredientDTO.setIngredientId(6);
        ingredientDTO.setAmountInMG(120);
        ingredients.add(ingredientDTO);

        for (IngredientDTO ingredient : ingredients) {
            productDAO.createIngredient(ingredient);
        }
    }

    @Test
    public void createIngredientList() {
        RecipeDTO recipeDTO = new RecipeDTO();
/*

        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("estradiol");
        ingredientDTO.setType("active");
        ingredientDTO.setIngredientId(1);
        ingredientDTO.setAmountInMG(1);
        ingredients.put(ingredientDTO.getName(), ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("norethisteronacetat");
        ingredientDTO.setType("active");
        ingredientDTO.setIngredientId(2);
        ingredientDTO.setAmountInMG(0.5);
        ingredients.put(ingredientDTO.getName(), ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("opovidon");
        ingredientDTO.setType("helper");
        ingredientDTO.setIngredientId(3);
        ingredientDTO.setAmountInMG(50);
        ingredients.put(ingredientDTO.getName(), ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("laktosemonohydrat");
        ingredientDTO.setType("helper");
        ingredientDTO.setIngredientId(4);
        ingredientDTO.setAmountInMG(10);
        ingredients.put(ingredientDTO.getName(), ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("magnesiumstearat");
        ingredientDTO.setType("helper");
        ingredientDTO.setIngredientId(5);
        ingredientDTO.setAmountInMG(15);
        ingredients.put(ingredientDTO.getName(), ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("majsstivelse");
        ingredientDTO.setType("helper");
        ingredientDTO.setIngredientId(6);
        ingredientDTO.setAmountInMG(120);
        ingredients.put(ingredientDTO.getName(), ingredientDTO);

        recipeDTO.setIngredients(ingredients);
        recipeDTO.setName("norethisteron/estrogen");
        recipeDTO.setRecipeId(2);
        recipeDTO.setMadeBy(10);
*/


    }


    @Test
    public void createRecipe() {
        RecipeDTO recipeDTO = new RecipeDTO();
        HashMap<String, IngredientDTO> ingredients = new HashMap<>();
       /* IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("estradiol");
        ingredientDTO.setType("active");
        ingredientDTO.setIngredientId(1);
        ingredientDTO.setAmountInMG(1);
        ingredients.put(ingredientDTO.getName(), ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("norethisteronacetat");
        ingredientDTO.setType("active");
        ingredientDTO.setIngredientId(2);
        ingredientDTO.setAmountInMG(0.5);
        ingredients.put(ingredientDTO.getName(), ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("opovidon");
        ingredientDTO.setType("helper");
        ingredientDTO.setIngredientId(3);
        ingredientDTO.setAmountInMG(50);
        ingredients.put(ingredientDTO.getName(), ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("laktosemonohydrat");
        ingredientDTO.setType("helper");
        ingredientDTO.setIngredientId(4);
        ingredientDTO.setAmountInMG(10);
        ingredients.put(ingredientDTO.getName(), ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("magnesiumstearat");
        ingredientDTO.setType("helper");
        ingredientDTO.setIngredientId(5);
        ingredientDTO.setAmountInMG(15);
        ingredients.put(ingredientDTO.getName(), ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("majsstivelse");
        ingredientDTO.setType("helper");
        ingredientDTO.setIngredientId(6);
        ingredientDTO.setAmountInMG(120);
        ingredients.put(ingredientDTO.getName(), ingredientDTO);

        recipeDTO.setIngredients(ingredients);
        recipeDTO.setName("norethisteron/estrogen");
        recipeDTO.setRecipeId(2);
        recipeDTO.setMadeBy(10);*/
        productDAO.createIngredientList(recipeDTO);

/*
        productDAO.createRecipe(recipeDTO);
*/
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

}