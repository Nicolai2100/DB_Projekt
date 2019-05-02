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
    UserDAO userDAO = new UserDAO();
    IngredientDAO ingredientDAO = new IngredientDAO();
    IngredientListDAO ingredientListDAO = new IngredientListDAO(ingredientDAO);
    CommodityBatchDAO commoditybatchDAO = new CommodityBatchDAO(userDAO,ingredientDAO);
    RecipeDAO recipeDAO = new RecipeDAO(ingredientListDAO, ingredientDAO, userDAO, commoditybatchDAO);
    ProductBatchDAO productBatchDAO = new ProductBatchDAO(recipeDAO, commoditybatchDAO, userDAO);

    public DALTest() throws DALException {
    }

    /* @Before
     public void initialize() {
         connectionDAO = new ConnectionDAO();
         productBatchDAO = new ProductBatchDAO(connectionDAO);
         userDAO = new UserDAO(connectionDAO);
         ingredientDAO = new IngredientDAO(connectionDAO);
         ingredientListDAO = new IngredientListDAO(connectionDAO, userDAO, ingredientDAO);
         commoditybatchDAO = new CommodityBatchDAO(connectionDAO, userDAO);
         recipeDAO = new RecipeDAO(connectionDAO, ingredientListDAO, userDAO, oldRecipeDAO);

         userDAOTest = new UserDAOTest();
     }
 */
    @After
    public void close() throws DALException {
        connectionDAO.closeConn();
    }

    @Test
    public void cleanTables() throws DALException {
        connectionDAO.deleteTables();
    }

    @Test
    public void temp() throws DALException {
        /**
         * Alt slettes
         */
        connectionDAO.deleteTables();

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

        UserDTO testUser_3 = new UserDTO();
        testUser_3.setUserId(3);
        testUser_3.setUserName("John Peder");
        testUser_3.setIni("JP");
        testUser_3.addRole("laborant");
        testUser_3.setAdmin(userDAO.getUser(2));
        userDAO.createUser(testUser_1, testUser_3);
        /**
         * Ingredienser og opskrift oprettes
         */
        IRecipeDTO norethisteron_recipe = new RecipeDTO();
        norethisteron_recipe.setRecipeId(1);
        norethisteron_recipe.setName("Norethisteron/estrogen");
        norethisteron_recipe.setMadeBy(userDAO.getUser(2));
        norethisteron_recipe.setMinBatchSize(10000);
        List<IIngredientDTO> norethisteron_ingredients = new ArrayList<>();

        IRecipeDTO sildenafil_recipe = new RecipeDTO();
        sildenafil_recipe.setRecipeId(2);
        sildenafil_recipe.setName("sildenafil");
        sildenafil_recipe.setMadeBy(userDAO.getUser(2));
        sildenafil_recipe.setMinBatchSize(11000);
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


        System.out.println("To be ordered: ");
        List<IIngredientDTO> ingredientDTOS1 = ingredientDAO.checkForReorder();

        for (IIngredientDTO ing : ingredientDTOS1) {
            System.out.println("" + (ingredientDTOS1.indexOf(ing) + 1) + ": IngredientID: " + ing.getIngredientId()
                    + "-" + ing.getName());
        }

        sildenafil_recipe.setIngredientsList(sildenafil_ingredients);
        norethisteron_recipe.setIngredientsList(norethisteron_ingredients);
        recipeDAO.createRecipe(sildenafil_recipe);
        recipeDAO.createRecipe(norethisteron_recipe);

        System.out.println("To be ordered: ");
        ingredientDTOS1 = ingredientDAO.checkForReorder();

        for (IIngredientDTO ing : ingredientDTOS1) {
            System.out.println("" + (ingredientDTOS1.indexOf(ing) + 1) + ": IngredientID: " + ing.getIngredientId()
                    + "-" + ing.getName());
        }
    }

    @Test
    public void testItAll() throws DALException {

        /**
         * Alt slettes
         */
        connectionDAO.deleteTables();

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

        UserDTO testUser_3 = new UserDTO();
        testUser_3.setUserId(3);
        testUser_3.setUserName("Puk Larsen");
        testUser_3.setIni("PL");
        testUser_3.addRole("farmaceut");
        testUser_3.setAdmin(userDAO.getUser(2));
        testUser_3.setIsActive(true);
        userDAO.createUser(testUser_1, testUser_3);

        UserDTO testUser_4 = new UserDTO();
        testUser_4.setUserId(4);
        testUser_4.setUserName("John Peder");
        testUser_4.setIni("JP");
        testUser_4.addRole("laborant");
        testUser_4.setAdmin(userDAO.getUser(2));
        testUser_4.setIsActive(true);
        userDAO.createUser(testUser_1, testUser_4);
        /**
         * Ingredienser og opskrift oprettes
         */
        IRecipeDTO norethisteron_recipe = new RecipeDTO();
        norethisteron_recipe.setRecipeId(2);
        norethisteron_recipe.setMinBatchSize(10000);
        norethisteron_recipe.setName("Norethisteron/estrogen");
        norethisteron_recipe.setMadeBy(userDAO.getUser(2));
        List<IIngredientDTO> norethisteron_ingredients = new ArrayList<>();

        IRecipeDTO sildenafil_recipe = new RecipeDTO();
        sildenafil_recipe.setRecipeId(3);
        sildenafil_recipe.setMinBatchSize(11000);
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

        //Ny opskrift
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

        System.out.println("To be ordered: ");
        List<IIngredientDTO> ingredientDTOS = ingredientDAO.checkForReorder();

        for (IIngredientDTO ing : ingredientDTOS) {
            System.out.println("" + (ingredientDTOS.indexOf(ing) + 1) + ": IngredientID: " + ing.getIngredientId()
                    + "-" + ing.getName());
        }

        sildenafil_recipe.setIngredientsList(sildenafil_ingredients);
        norethisteron_recipe.setIngredientsList(norethisteron_ingredients);
        recipeDAO.createRecipe(sildenafil_recipe);
        recipeDAO.createRecipe(norethisteron_recipe);

        System.out.println("To be ordered: ");
        List<IIngredientDTO> ingredientDTOS1 = ingredientDAO.checkForReorder();

        for (IIngredientDTO ing : ingredientDTOS1) {
            System.out.println("" + (ingredientDTOS1.indexOf(ing) + 1) + ": IngredientID: " + ing.getIngredientId()
                    + "-" + ing.getName());
        }


        /**
         * Der bestilles et råvare batch
         */
        ICommodityBatchDTO commodityBatch = new CommodityBatchDTO();
        IUserDTO testUser = userDAO.getUser(1);
        commodityBatch.setOrderedBy(testUser);
        commodityBatch.setBatchId(4);
        commodityBatch.setAmountInKg(0.5);
        commodityBatch.setIngredientDTO(ingredientDAO.getIngredient(4));
        commodityBatch.setOrderDate(LocalDateTime.now().toString());
        commoditybatchDAO.createCommodityBatch(commodityBatch);

        commodityBatch.setOrderedBy(testUser);
        commodityBatch.setBatchId(12);
        commodityBatch.setAmountInKg(0.5);
        commodityBatch.setIngredientDTO(ingredientDAO.getIngredient(12));
        commodityBatch.setOrderDate(LocalDateTime.now().toString());
        commoditybatchDAO.createCommodityBatch(commodityBatch);

        commodityBatch.setOrderedBy(testUser);
        commodityBatch.setBatchId(13);
        commodityBatch.setAmountInKg(0.5);
        commodityBatch.setIngredientDTO(ingredientDAO.getIngredient(13));
        commodityBatch.setOrderDate(LocalDateTime.now().toString());
        commoditybatchDAO.createCommodityBatch(commodityBatch);

        commodityBatch.setOrderedBy(testUser);
        commodityBatch.setBatchId(14);
        commodityBatch.setAmountInKg(0.5);
        commodityBatch.setIngredientDTO(ingredientDAO.getIngredient(14));
        commodityBatch.setOrderDate(LocalDateTime.now().toString());
        commoditybatchDAO.createCommodityBatch(commodityBatch);

        commodityBatch.setOrderedBy(testUser);
        commodityBatch.setBatchId(15);
        commodityBatch.setAmountInKg(0.5);
        commodityBatch.setIngredientDTO(ingredientDAO.getIngredient(15));
        commodityBatch.setOrderDate(LocalDateTime.now().toString());
        commoditybatchDAO.createCommodityBatch(commodityBatch);

        commodityBatch.setOrderedBy(testUser);
        commodityBatch.setBatchId(16);
        commodityBatch.setAmountInKg(1.2);
        commodityBatch.setIngredientDTO(ingredientDAO.getIngredient(16));
        commodityBatch.setOrderDate(LocalDateTime.now().toString());
        commoditybatchDAO.createCommodityBatch(commodityBatch);
        /**
         * Liste over råvarer der skal bestilles
         */
        System.out.println("To be ordered: ");
        List<IIngredientDTO> ingredientDTOS2 = ingredientDAO.checkForReorder();

        for (IIngredientDTO ing : ingredientDTOS2) {
            System.out.println("" + (ingredientDTOS2.indexOf(ing) + 1) + ": IngredientID: " + ing.getIngredientId()
                    + "-" + ing.getName());
        }

        /**
         * Der oprettes et produkt-batch
         */
        ProductBatchDTO productbatchDTO = new ProductBatchDTO();
        UserDTO testUser2 = (UserDTO) userDAO.getUser(1);
        productbatchDTO.setMadeBy(testUser2); //Produktionslederen indsættes som et bruger-objekt.
        productbatchDTO.setName("Sildenafil"); //Produktets navn indsættes.
        productbatchDTO.setProductId(1); //Et unikt id vælges.
        productbatchDTO.setRecipe(2); //Id'et til opskriften, som produktet skal produceres ud fra, indsættes.
        productbatchDTO.setProductionDate(new Date(System.currentTimeMillis())); //Produktionsdatoen indsættes i formatet java.sql.Date.
        productbatchDTO.setExpirationDate(new Date(System.currentTimeMillis())); //Udløbsdatoen indsættes i formatet java.sql.Date.
        productbatchDTO.setVolume(10000); //Mængden af piller indsættes.
        productbatchDTO.getCommodityBatches().add(commoditybatchDAO.getCommodityBatch(4)); //Råvare-batches tilknyttes. De skal have samme id, som den tilsvarende ingrediens i opskriften.
        productbatchDTO.getCommodityBatches().add(commoditybatchDAO.getCommodityBatch(12));
        productbatchDTO.getCommodityBatches().add(commoditybatchDAO.getCommodityBatch(13));
        productbatchDTO.getCommodityBatches().add(commoditybatchDAO.getCommodityBatch(14));
        productbatchDTO.getCommodityBatches().add(commoditybatchDAO.getCommodityBatch(15));
        productbatchDTO.getCommodityBatches().add(commoditybatchDAO.getCommodityBatch(16));
        productbatchDTO.setProducedBy(testUser_4); //Laboranten, som producerer produktet, indsættes.
        productbatchDTO.setBatchState(IProductBatchDTO.State.ORDERED); //Stadiet indsættes som ENUM. Det kan være enten ORDERED, UNDER_PRODUCTION eller COMPLETED.

        productBatchDAO.createProductbatch(productbatchDTO);

        productBatchDAO.initiateProduction(productbatchDTO, testUser_2);

        productbatchDTO.setName("Amfetamin");

        productBatchDAO.produceProductBatch(productbatchDTO, testUser_4);

        System.out.println("Read product succesful:" + productBatchDAO.getProductbatch(1).toString());

        IRecipeDTO recipeDTO = recipeDAO.getActiveRecipe(3);
        recipeDTO.setName("Opdateret 2 " + recipeDTO.getName());
        recipeDAO.updateRecipe(recipeDTO);

        /**
         * Liste over råvarer der skal bestilles
         **/
        System.out.println("To be ordered: ");
        List<IIngredientDTO> ingredientDTOS3 = ingredientDAO.checkForReorder();

        for (IIngredientDTO ing : ingredientDTOS3) {
            System.out.println("" + (ingredientDTOS3.indexOf(ing) + 1) + ": IngredientID: " + ing.getIngredientId()
                    + "-" + ing.getName());
        }
    }
}
