package dal;

import dal.dto.IngredientDTO;
import dal.dto.ProductDTO;
import dal.dto.RecipeDTO;
import dal.dto.UserDTO;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ProductDAOTest {
    ProductDAO productDAO = new ProductDAO();
    UserDAOImpl userDAO = new UserDAOImpl();

/*
    @Before
*/

    @Test
    public void createConnection() {


    }

    @Test
    public void createUser() throws IUserDAO.DALException {

        UserDTO testUser = new UserDTO();
        testUser.setUserId(9);
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
        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(2);
        ingredientDTO.setType("Calciumhydrogenphosphat dihydrat");
        productDAO.createIngredient(ingredientDTO);
    }

    @Test
    public void createIngredientList(){

/*
        ingredientDTO.setType("Calciumhydrogenphosphat dihydrat");
*/


        RecipeDTO recipeDTO = new RecipeDTO();
        /*
        recipeDTO.setIngredients();
*/
    }


    @Test
    public void createRecipe(){
        RecipeDTO recipeDTO = new RecipeDTO();
        List<String> ingredientList = new ArrayList<>();
        ingredientList.add("Calciumhydrogenphosphat dihydrat");
        ingredientList.add("Sildenafil");
        recipeDTO.setIngredients(ingredientList);

        recipeDTO.setMadeBy(10);

        productDAO.createRecipe(recipeDTO, 10);
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