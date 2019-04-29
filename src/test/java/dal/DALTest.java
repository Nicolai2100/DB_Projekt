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
        IRecipeDTO norethisteron_recipe = new RecipeDTO();
        norethisteron_recipe.setRecipeId(2);
        norethisteron_recipe.setName("Norethisteron/estrogen");
        norethisteron_recipe.setMadeBy(userDAO.getUser(2));
        List<IIngredientDTO> norethisteron_ingredients = new ArrayList<>();

        IRecipeDTO sildenafil_recipe = new RecipeDTO();
        sildenafil_recipe.setRecipeId(3);
        sildenafil_recipe.setName("sildenafil");
        sildenafil_recipe.setMadeBy(userDAO.getUser(2));
        List<IIngredientDTO> sildenafil_ingredients = new ArrayList<>();

        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(1);
        ingredientDTO.setName("sildenafil");
        ingredientDTO.setType("active");
        ingredientDAO.createIngredient(ingredientDTO);

        ingredientDTO.setAmount(25);
        sildenafil_ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(2);
        ingredientDTO.setName("calciumhydrogenphosphat_dihydrat");
        ingredientDTO.setType("helper");
        ingredientDAO.createIngredient(ingredientDTO);

        ingredientDTO.setAmount(20);
        sildenafil_ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(3);
        ingredientDTO.setName("cellulose");
        ingredientDTO.setType("helper");
        ingredientDAO.createIngredient(ingredientDTO);

        ingredientDTO.setAmount(25);
        sildenafil_ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(4);
        ingredientDTO.setName("magnesiumstearat");
        ingredientDTO.setType("helper");
        ingredientDAO.createIngredient(ingredientDTO);

        ingredientDTO.setAmount(10);
        sildenafil_ingredients.add(ingredientDTO);
        ingredientDTO.setAmount(15);
        norethisteron_ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(5);
        ingredientDTO.setName("silica");
        ingredientDTO.setType("helper");
        ingredientDAO.createIngredient(ingredientDTO);

        ingredientDTO.setAmount(5);
        sildenafil_ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(6);
        ingredientDTO.setName("croscarmellosenatrium");
        ingredientDTO.setType("helper");
        ingredientDAO.createIngredient(ingredientDTO);

        ingredientDTO.setAmount(6);
        sildenafil_ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(7);
        ingredientDTO.setName("hypromellose");
        ingredientDTO.setType("helper");
        ingredientDAO.createIngredient(ingredientDTO);

        ingredientDTO.setAmount(1.3);
        sildenafil_ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(8);
        ingredientDTO.setName("titandioxid");
        ingredientDTO.setType("helper");
        ingredientDAO.createIngredient(ingredientDTO);

        ingredientDTO.setAmount(0.5);
        sildenafil_ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(9);
        ingredientDTO.setName("macrogol");
        ingredientDTO.setType("helper");
        ingredientDAO.createIngredient(ingredientDTO);

        ingredientDTO.setAmount(20);
        sildenafil_ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(10);
        ingredientDTO.setName("talcum");
        ingredientDTO.setType("helper");
        ingredientDAO.createIngredient(ingredientDTO);

        ingredientDTO.setAmount(1);
        sildenafil_ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(11);
        ingredientDTO.setName("indigotin");
        ingredientDTO.setType("helper");
        ingredientDAO.createIngredient(ingredientDTO);

        ingredientDTO.setAmount(0.02);
        sildenafil_ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(12);
        ingredientDTO.setName("estradiol");
        ingredientDTO.setType("active");
        ingredientDAO.createIngredient(ingredientDTO);

        ingredientDTO.setAmount(1);
        norethisteron_ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(13);
        ingredientDTO.setName("norethisteronacetat");
        ingredientDTO.setType("active");
        ingredientDAO.createIngredient(ingredientDTO);

        ingredientDTO.setAmount(0.5);
        norethisteron_ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(14);
        ingredientDTO.setName("opovidon");
        ingredientDTO.setType("helper");
        ingredientDAO.createIngredient(ingredientDTO);

        ingredientDTO.setAmount(50);
        norethisteron_ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(15);
        ingredientDTO.setName("laktosemonohydrat");
        ingredientDTO.setType("helper");
        ingredientDAO.createIngredient(ingredientDTO);

        ingredientDTO.setAmount(10);
        norethisteron_ingredients.add(ingredientDTO);

        ingredientDTO = new IngredientDTO();
        ingredientDTO.setIngredientId(16);
        ingredientDTO.setName("majsstivelse");
        ingredientDTO.setType("helper");
        ingredientDAO.createIngredient(ingredientDTO);

        ingredientDTO.setAmount(120);
        norethisteron_ingredients.add(ingredientDTO);

        sildenafil_recipe.setIngredientsList(sildenafil_ingredients);
        norethisteron_recipe.setIngredientsList(norethisteron_ingredients);
        ingredientListDAO.createIngredientList(sildenafil_recipe, 1);
        ingredientListDAO.createIngredientList(norethisteron_recipe, 1);
        recipeDAO.createRecipe(sildenafil_recipe);
        recipeDAO.createRecipe(norethisteron_recipe);

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
        IUserDTO testUser = userDAO.getUser(1);
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

//        recipeDAO.archiveRecipe(2, testUser_2);

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