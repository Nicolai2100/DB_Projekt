package dal;

import dal.dto.*;
import org.junit.After;
import org.junit.Test;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DALTest {
    ConnectionDAO connectionDAO = new ConnectionDAO();
    ProductBatchDAO productBatchDAO = new ProductBatchDAO();
    UserDAO userDAO = new UserDAO();
    IngredientDAO ingredientDAO = new IngredientDAO();
    IngredientListDAO ingredientListDAO = new IngredientListDAO(ingredientDAO);
    CommoditybatchDAO commoditybatchDAO = new CommoditybatchDAO(userDAO);
    RecipeDAO recipeDAO = new RecipeDAO(ingredientListDAO, userDAO);
    OldRecipeDAO oldRecipeDAO = recipeDAO.getOldRecipeDAO();
    UserDAOTest userDAOTest = new UserDAOTest();

    /* @Before
     public void initialize() {
         connectionDAO = new ConnectionDAO();
         productBatchDAO = new ProductBatchDAO(connectionDAO);
         userDAO = new UserDAO(connectionDAO);
         ingredientDAO = new IngredientDAO(connectionDAO);
         ingredientListDAO = new IngredientListDAO(connectionDAO, userDAO, ingredientDAO);
         commoditybatchDAO = new CommoditybatchDAO(connectionDAO, userDAO);
         oldRecipeDAO = new OldRecipeDAO(connectionDAO, recipeDAO);
         recipeDAO = new RecipeDAO(connectionDAO, ingredientListDAO, userDAO, oldRecipeDAO);

         userDAOTest = new UserDAOTest();
     }
 */
    @After
    public void close() {
        connectionDAO.closeConn();
    }

    @Test
    public void cleanTables() {
        connectionDAO.cleanTables();
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
        IUserDTO testUser_1 = new UserDTO();
        testUser_1.setUserId(1);
        testUser_1.setUserName("Pelle Hansen");
        testUser_1.setIni("PH");
        testUser_1.addRole("admin");
        testUser_1.addRole("productionleader");
        userDAO.createUser(testUser_1, testUser_1);

        UserDTO testUser_2 = new UserDTO();
        testUser_2.setUserId(2);
        testUser_2.setUserName("Puk Larsen");
        testUser_2.setIni("PL");
        testUser_2.addRole("farmaceut");
        userDAO.createUser(testUser_1, testUser_2);
        /**
         * Ingredienser og opskrift oprettes
         */
        IRecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setRecipeId(2);
        recipeDTO.setName("Norethisteron/estrogen");
        recipeDTO.setMadeBy(userDAO.getUser(2));

        List<IIngredientDTO> ingredients = new ArrayList<>();
        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(1);
        ingredientDTO.setName("estradiol");
        ingredientDTO.setType("active");
        ingredientDTO.setAmount(1);
        ingredients.add(ingredientDTO);
        ingredientDAO.createIngredient(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(2);
        ingredientDTO.setName("norethisteronacetat");
        ingredientDTO.setType("active");
        ingredientDTO.setAmount(0.5);
        ingredients.add(ingredientDTO);
        ingredientDAO.createIngredient(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(3);
        ingredientDTO.setName("opovidon");
        ingredientDTO.setType("helper");
        ingredientDTO.setAmount(50);
        ingredients.add(ingredientDTO);
        ingredientDAO.createIngredient(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(4);
        ingredientDTO.setName("laktosemonohydrat");
        ingredientDTO.setType("helper");
        ingredientDTO.setAmount(10);
        ingredients.add(ingredientDTO);
        ingredientDAO.createIngredient(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(5);
        ingredientDTO.setName("magnesiumstearat");
        ingredientDTO.setType("helper");
        ingredientDTO.setAmount(15);
        ingredients.add(ingredientDTO);
        ingredientDAO.createIngredient(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(6);
        ingredientDTO.setName("majsstivelse");
        ingredientDTO.setType("helper");
        ingredientDTO.setAmount(120);
        ingredients.add(ingredientDTO);
        ingredientDAO.createIngredient(ingredientDTO);

        recipeDTO.setIngredientsList(ingredients);
        ingredientListDAO.createIngredientList(recipeDTO, 1);
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
        commodityBatch.setBatchId(2);
        commodityBatch.setAmountInKg(2.5);
        commodityBatch.setIngredientDTO(ingredientDAO.getIngredient(2));
        commodityBatch.setOrderDate(LocalDateTime.now().toString());

        commoditybatchDAO.createCommodityBatch(commodityBatch);
        commodityBatch.setOrderedBy(testUser);
        commodityBatch.setBatchId(3);
        commodityBatch.setAmountInKg(2.5);
        commodityBatch.setIngredientDTO(ingredientDAO.getIngredient(2));
        commodityBatch.setOrderDate(LocalDateTime.now().toString());

        commoditybatchDAO.createCommodityBatch(commodityBatch);

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

//        recipeDAO.deleteRecipe(2, testUser_2);

        //      oldRecipeDAO.getAllOldRecipes();

        ProductbatchDTO productbatchDTO = new ProductbatchDTO();
        UserDTO testUser2 = (UserDTO) userDAO.getUser(1);
        productbatchDTO.setMadeBy(testUser2);
        productbatchDTO.setName("Ost");
        productbatchDTO.setProductId(1);
        productbatchDTO.setRecipe(2);
        productbatchDTO.setProductionDate(new Date(System.currentTimeMillis()));
        productbatchDTO.setExpirationDate(new Date(System.currentTimeMillis()));
        productbatchDTO.setVolume(100);
        productbatchDTO.getCommodityBatches().add(commoditybatchDAO.getCommodityBatch(2));
        productbatchDTO.getCommodityBatches().add(commoditybatchDAO.getCommodityBatch(3));

        productbatchDTO.setBatchState(IProductDTO.State.UNDER_PRODUCTION);

        productBatchDAO.createProductbatch(productbatchDTO);

        productbatchDTO.setName("Amfetamin");

        productBatchDAO.updateProductBatch(productbatchDTO);

        System.out.println("Read product succesful:" + productBatchDAO.getProductbatch(1).toString());

    }
}