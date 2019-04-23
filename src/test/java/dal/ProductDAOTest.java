package dal;

import dal.dto.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ProductDAOTest {
    private ProductDAO productDAO;
    private UserDAO userDAO;
    private DALTest dalTest;
    private ConnectionDAO connectionDAO;
    private IngredientListDAO ingredientListDAO;
    private IngredientDAO ingredientDAO;
    private CommodityDAO commodityDAO;
    private RecipeDAO recipeDAO;
    private OldRecipeDAO oldRecipeDAO;

    @Before
    public void initialize() {
        connectionDAO = new ConnectionDAO();
        productDAO = new ProductDAO(connectionDAO);
        userDAO = new UserDAO(connectionDAO);
        ingredientDAO = new IngredientDAO(connectionDAO);
        ingredientListDAO = new IngredientListDAO(connectionDAO, userDAO, ingredientDAO);
        commodityDAO = new CommodityDAO(connectionDAO, userDAO);
        oldRecipeDAO = new OldRecipeDAO(connectionDAO, recipeDAO);
        recipeDAO = new RecipeDAO(connectionDAO, ingredientListDAO, userDAO, oldRecipeDAO);

        dalTest = new DALTest();
    }

    @After
    public void closeAll() throws SQLException {
        connectionDAO.getConn().close();
    }


    @Test
    public void cleanTables() {
        connectionDAO.cleanTables();
    }

    @Test
    public void createProduct() throws IUserDAO.DALException {


        ProductDTO productDTO = new ProductDTO();
        UserDTO testUser = (UserDTO) userDAO.getUser(10);
        testUser.addRole("productionleader");
        productDTO.setMadeBy(testUser);
        productDTO.setName("Ost");
        productDTO.setProductId(1);
        productDTO.setRecipe(1);
        productDTO.setProductionDate(new Date(System.currentTimeMillis()));
        productDTO.setExpirationDate(new Date(System.currentTimeMillis() + 432 * 10 ^ 7));
        productDTO.setVolume(100);

        /*productDTO.setCommodityBatches();
         */
        productDAO.createProduct(productDTO);
    }


    @Test
    public void checkForReorder() {
        List<IIngredientDTO> ingredientDTOS = ingredientDAO.checkForReorder();

        for (IIngredientDTO ing : ingredientDTOS) {
            System.out.println(ing);
        }
    }

    @Test
    public void getCommodityBatch() throws IUserDAO.DALException {
        ICommodityBatchDTO batchFromDB = commodityDAO.getCommodityBatch(2);
        System.out.println(batchFromDB);
    }

    @Test
    public void createCommodityBatch() throws IUserDAO.DALException {

        ICommodityBatchDTO commodityBatch = new CommodityBatchDTO();
        IUserDTO testUser = userDAO.getUser(5);
        commodityBatch.setOrderedBy(testUser);
        commodityBatch.setBatchId(3);
        commodityBatch.setAmountInKg(2.5);
        commodityBatch.setIngredientDTO(ingredientDAO.getIngredient(2));
        commodityBatch.setOrderDate(LocalDateTime.now().toString());

        commodityDAO.createCommodityBatch(commodityBatch);
    }

    @Test
    public void createTriggers() {
/*
        productDAO.dropTriggers();
*/
        connectionDAO.createTriggerOldRecipe();
        connectionDAO.createTriggerReorder();
    }

    @Test
    public void deleteRecipe() throws IUserDAO.DALException {
        IUserDTO testUser = new UserDTO();
        testUser.addRole("farmaceut");
        testUser.setIsActive(true);
        recipeDAO.deleteRecipe(2, testUser);
    }

    @Test
    public void updateRecipe() {
        IRecipeDTO recipeDTO = recipeDAO.getRecipe(2);
        recipeDTO.setName("snillert");
        recipeDAO.updateRecipe(recipeDTO);
    }


    @Test
    public void getRecipe() {
        IRecipeDTO recipeDTO = recipeDAO.getRecipe(2);
        System.out.println(recipeDTO);
    }

    @Test
    public void createRecipe() throws IUserDAO.DALException {
        IRecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setRecipeId(2);
        recipeDTO.setName("Norethisteron/estrogen");
        recipeDTO.setMadeBy(userDAO.getUser(10));
/*
        recipeDTO.setIngredientsList(productDAO.getIngredientList(recipeDTO));
*/
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

    @Test
    public void createIngredientList() throws IUserDAO.DALException {
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setName("norethisteron/estrogen");
        recipeDTO.setRecipeId(2);
        recipeDTO.setMadeBy(userDAO.getUser(10));
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
        ingredientListDAO.createIngredientList(recipeDTO, 1);
    }

    @Test
    public void getIngredientList() throws IUserDAO.DALException {
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setRecipeId(2);
        List<IIngredientDTO> ingrediendts = ingredientListDAO.getIngredientList(recipeDTO);

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
            ingredientDAO.createIngredient(ingredient);
        }
    }

    @Test
    public void getIngredient() throws IUserDAO.DALException {
        IIngredientDTO ingredientDTO = ingredientDAO.getIngredient(1);
        assertEquals(ingredientDTO.getType(), "active");
        System.out.println(ingredientDTO);
    }

    @Test
    public void testItAll() throws IUserDAO.DALException {

        /**
         * Alt slettes
         */
        connectionDAO.cleanTables();

        /**
         * Brugerne oprettes
         */
        IUserDTO testUser_2 = new UserDTO();
        testUser_2.setUserId(2);
        testUser_2.setUserName("Pælle Hansen");
        testUser_2.setIni("PH");
        ArrayList<String> roles2 = new ArrayList();
        roles2.add("admin");
        roles2.add("productleader");
        testUser_2.setRoles(roles2);
        testUser_2.setIsActive(true);
        userDAO.createUser(testUser_2);

        UserDTO testUser_3 = new UserDTO();
        testUser_3.setUserId(3);
        testUser_3.setUserName("Puk Larsen");
        testUser_3.setIni("PL");
        testUser_3.addRole("farmaceut");
        testUser_3.setAdmin(userDAO.getUser(2));
        testUser_3.setIsActive(true);
        userDAO.createUser(testUser_3);
        /**
         * Ingredienser og opskrift oprettes
         */
        IRecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setRecipeId(2);
        recipeDTO.setName("Norethisteron/estrogen");
        recipeDTO.setMadeBy(userDAO.getUser(3));

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

        /**
         * Liste over råvarer der skal bestilles
         */
        System.out.println("To be ordered: ");
        List<IIngredientDTO> ingredientDTOS = ingredientDAO.checkForReorder();

        for (IIngredientDTO ing : ingredientDTOS) {
            System.out.println("" + (ingredientDTOS.indexOf(ing) + 1) + " " + ing);
        }
        /**
         * Der bestilles et råvare batch
         */
        ICommodityBatchDTO commodityBatch = new CommodityBatchDTO();
        IUserDTO testUser = userDAO.getUser(2);
        commodityBatch.setOrderedBy(testUser);
        commodityBatch.setBatchId(3);
        commodityBatch.setAmountInKg(2.5);
        commodityBatch.setIngredientDTO(ingredientDAO.getIngredient(2));
        commodityBatch.setOrderDate(LocalDateTime.now().toString());

        commodityDAO.createCommodityBatch(commodityBatch);

        /**
         * Liste over råvarer der skal bestilles
         */
        System.out.println("To be ordered: ");
        List<IIngredientDTO> ingredientDTOS2 = ingredientDAO.checkForReorder();

        for (IIngredientDTO ing2 : ingredientDTOS2) {
            System.out.println("" + (ingredientDTOS2.indexOf(ing2) + 1) + " " + ing2);
        }

        /**
         * Der oprettes et produkt-batch
         */
        //todo få testUser_2 til at oprette et product-batch

        recipeDAO.deleteRecipe(2, testUser_3);

        oldRecipeDAO.getAllOldRecipes();
    }
}