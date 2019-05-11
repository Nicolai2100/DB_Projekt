package dal;

import dal.dto.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DALTest {
    ConnectionDAO connectionDAO;
    UserDAO userDAO;
    CommodityBatchDAO commoditybatchDAO;
    IngredientDAO ingredientDAO;
    RecipeDAO recipeDAO;
    IngredientListDAO ingredientListDAO;
    ProductBatchDAO productBatchDAO;

    @Before
    public void initialize() throws DALException {
        connectionDAO = new ConnectionDAO();
        userDAO = new UserDAO();
        ingredientDAO = new IngredientDAO();
        ingredientListDAO = new IngredientListDAO(ingredientDAO);
        recipeDAO = new RecipeDAO(ingredientDAO, ingredientListDAO, userDAO);
        commoditybatchDAO = new CommodityBatchDAO(userDAO, ingredientDAO);
        productBatchDAO = new ProductBatchDAO(recipeDAO, commoditybatchDAO, userDAO);
    }

    @After
    public void close() throws DALException {
        connectionDAO.closeConn();
    }

    @Test
    public void name() throws DALException {
        //connectionDAO.dropAllTables(0);
        connectionDAO.initializeDataBase();
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
        norethisteron_recipe.setExpirationInMonths(36);
        List<IIngredientDTO> norethisteron_ingredients = new ArrayList<>();

        IRecipeDTO sildenafil_recipe = new RecipeDTO();
        sildenafil_recipe.setRecipeId(3);
        sildenafil_recipe.setMinBatchSize(11000);
        sildenafil_recipe.setName("sildenafil");
        sildenafil_recipe.setMadeBy(userDAO.getUser(2));
        sildenafil_recipe.setExpirationInMonths(30);
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
        /**
         * Liste over råvarer der skal bestilles
         */
        List<IIngredientDTO> ingredientDTOS = ingredientDAO.getReorders();
        assertTrue(ingredientDTOS.size() == 0);
        System.out.println("\n" + ingredientDTOS.size() + " commodities to be ordered: ");
        for (IIngredientDTO ing : ingredientDTOS) {
            System.out.println("" + (ingredientDTOS.indexOf(ing) + 1) + ": IngredientID: " + ing.getIngredientId()
                    + "-" + ing.getName());
        }
        sildenafil_recipe.setIngredientsList(sildenafil_ingredients);
        norethisteron_recipe.setIngredientsList(norethisteron_ingredients);

        //Når en opskrift er blevet oprettet kaldes en metode der opdaterer de forskellige ingrediensers
        // minimums størrelse. Disse bruges af en trigger til at vurdere om en råvare skal markeres som
        // "skal bestilles" - derfor opdateres opskrifterne med det samme de er blevet oprettet
        recipeDAO.createRecipe(sildenafil_recipe);
        recipeDAO.updateRecipe(recipeDAO.getActiveRecipe(sildenafil_recipe.getRecipeId()));
        recipeDAO.createRecipe(norethisteron_recipe);
        recipeDAO.updateRecipe(recipeDAO.getActiveRecipe(norethisteron_recipe.getRecipeId()));
        /**
         * Liste over råvarer der skal bestilles
         */
        ingredientDTOS = ingredientDAO.getReorders();
        assertTrue(ingredientDTOS.size() == 16);
        System.out.println("\n" + ingredientDTOS.size() + " commodities to be ordered: ");
        for (IIngredientDTO ing : ingredientDTOS) {
            System.out.println("" + (ingredientDTOS.indexOf(ing) + 1) + ": IngredientID: " + ing.getIngredientId()
                    + "-" + ing.getName());
        }
        /**
         *Der bestilles et råvare batch
         */
        ICommodityBatchDTO commodityBatch = new CommodityBatchDTO();
        IUserDTO testUser = userDAO.getUser(1);
        commodityBatch.setOrderedBy(testUser);
        commodityBatch.setBatchId(4);
        commodityBatch.setAmountInKg(1);
        commodityBatch.setIngredientDTO(ingredientDAO.getIngredient(4));
        commodityBatch.setOrderDate(LocalDateTime.now().toString());
        commoditybatchDAO.createCommodityBatch(commodityBatch);

        commodityBatch.setOrderedBy(testUser);
        commodityBatch.setBatchId(12);
        commodityBatch.setAmountInKg(1);
        commodityBatch.setIngredientDTO(ingredientDAO.getIngredient(12));
        commodityBatch.setOrderDate(LocalDateTime.now().toString());
        commoditybatchDAO.createCommodityBatch(commodityBatch);

        commodityBatch.setOrderedBy(testUser);
        commodityBatch.setBatchId(13);
        commodityBatch.setAmountInKg(1);
        commodityBatch.setIngredientDTO(ingredientDAO.getIngredient(13));
        commodityBatch.setOrderDate(LocalDateTime.now().toString());
        commoditybatchDAO.createCommodityBatch(commodityBatch);

        commodityBatch.setOrderedBy(testUser);
        commodityBatch.setBatchId(14);
        commodityBatch.setAmountInKg(1);
        commodityBatch.setIngredientDTO(ingredientDAO.getIngredient(14));
        commodityBatch.setOrderDate(LocalDateTime.now().toString());
        commoditybatchDAO.createCommodityBatch(commodityBatch);

        commodityBatch.setOrderedBy(testUser);
        commodityBatch.setBatchId(15);
        commodityBatch.setAmountInKg(1);
        commodityBatch.setIngredientDTO(ingredientDAO.getIngredient(15));
        commodityBatch.setOrderDate(LocalDateTime.now().toString());
        commoditybatchDAO.createCommodityBatch(commodityBatch);

        commodityBatch.setOrderedBy(testUser);
        commodityBatch.setBatchId(16);
        commodityBatch.setAmountInKg(3);
        commodityBatch.setIngredientDTO(ingredientDAO.getIngredient(16));
        commodityBatch.setOrderDate(LocalDateTime.now().toString());
        commoditybatchDAO.createCommodityBatch(commodityBatch);
        /**
         *Liste over råvarer der skal bestilles
         */
        ingredientDTOS = ingredientDAO.getReorders();
        assertTrue(ingredientDTOS.size() == 10);
        System.out.println("\n" + ingredientDTOS.size() + " commodities to be ordered: ");
        for (IIngredientDTO ing : ingredientDTOS) {
            System.out.println("" + (ingredientDTOS.indexOf(ing) + 1) + ": IngredientID: " + ing.getIngredientId()
                    + "-" + ing.getName());
        }
        /**
         *Der oprettes et produkt -batch
         */
        IProductBatchDTO productbatchDTO = new ProductBatchDTO();
        IUserDTO testUser2 = userDAO.getUser(1);
        productbatchDTO.setOrderedBy(testUser2); //Produktionslederen indsættes som et bruger-objekt.
        productbatchDTO.setName("Sildenafil"); //Produktets navn indsættes.
        productbatchDTO.setProductId(1); //Et unikt id vælges.
        productbatchDTO.setRecipe(recipeDAO.getActiveRecipe(2)); //Id'et til opskriften, som produktet skal produceres ud fra, indsættes.
        //Produktionsdatoen indsættes i formatet java.sql.Date.
        productbatchDTO.setVolume(10000); //Mængden af piller indsættes.
        //Råvare-batches tilknyttes. De skal have samme id, som den tilsvarende ingrediens i opskriften.
        productbatchDTO.getCommodityBatches().add(commoditybatchDAO.getCommodityBatch(4));
        productbatchDTO.getCommodityBatches().add(commoditybatchDAO.getCommodityBatch(12));
        productbatchDTO.getCommodityBatches().add(commoditybatchDAO.getCommodityBatch(13));
        productbatchDTO.getCommodityBatches().add(commoditybatchDAO.getCommodityBatch(14));
        productbatchDTO.getCommodityBatches().add(commoditybatchDAO.getCommodityBatch(15));
        productbatchDTO.getCommodityBatches().add(commoditybatchDAO.getCommodityBatch(16));

        //Et produkt oprettes som en bestilling
        productBatchDAO.createProductbatch(productbatchDTO);
        List<IProductBatchDTO> orderedProducts = productBatchDAO.getProductsOrdered();
        System.out.println(orderedProducts);
        assertTrue(orderedProducts.size() == 1);
        //Et produktets status ændres til under produktion
        productBatchDAO.initiateProduction(productbatchDTO, testUser_2);
        List<IProductBatchDTO> underProductionProducts = productBatchDAO.getProductsUnderProduction();
        System.out.println(underProductionProducts);
        assertTrue(underProductionProducts.size() == 1);


        //Laboranten, som producerer produktet, indsættes og produktbatchen produceres færdigt
        productbatchDTO.setProducedBy(testUser_4);
        productBatchDAO.produceProductBatch(productbatchDTO, testUser_4);
        List<IProductBatchDTO> completedProducts = productBatchDAO.getProductsCompleted();
        System.out.println(completedProducts);
        assertTrue(completedProducts.size() == 1);

        ingredientDTOS = ingredientDAO.getReorders();
        assertTrue(ingredientDTOS.size() == 12);
        System.out.println("\n" + ingredientDTOS.size() + " commodities to be ordered: ");
        for (IIngredientDTO ing : ingredientDTOS) {
            System.out.println("" + (ingredientDTOS.indexOf(ing) + 1) + ": IngredientID: " + ing.getIngredientId()
                    + "-" + ing.getName());
        }
        /**
         *En opskrift opdateres
         */
        IRecipeDTO recipeDTO = recipeDAO.getActiveRecipe(3);
        recipeDTO.setName("Opdateret 2 " + recipeDTO.getName());
        recipeDTO.getIngredientsList().get(1).setMinAmountMG(0.002);
        recipeDAO.updateRecipe(recipeDTO);
        /**
         *Liste over arkiverede opskrifter
         **/
        System.out.println("\nOld recipes: ");
        List<IRecipeDTO> oldRecipes = recipeDAO.getListOfOldRecipes();
        for (IRecipeDTO oldRecipe : oldRecipes) {
            System.out.println(oldRecipe);
        }
        assertTrue(oldRecipes.size() == 3);
        /**
         *Hamstring af cellulose
         **/
        for (int i = 0; i < 3; i++) {
            ICommodityBatchDTO majsstivelse = new CommodityBatchDTO();
            majsstivelse.setOrderedBy(testUser);
            majsstivelse.setBatchId(50 + i);
            majsstivelse.setAmountInKg(2.5);
            majsstivelse.setIngredientDTO(ingredientDAO.getIngredient(3));
            majsstivelse.setOrderDate(LocalDateTime.now().toString());
            commoditybatchDAO.createCommodityBatch(majsstivelse);
        }
        /**
         *Liste over den resterende mængde af råvarer for hver ingrediens for opskriften sildenafil i kg
         *som ikke er markeret som rest.
         **/
        for (IIngredientDTO ing : sildenafil_ingredients
        ) {
            double amountOnStock = commoditybatchDAO.getTotalCommodityAmountInKG(ing);
            System.out.println(amountOnStock);
            if (ing.getIngredientId() == 3) {
                assertTrue(amountOnStock == 7.5);
            }
        }

        ingredientDTOS = ingredientDAO.getReorders();
        assertTrue(ingredientDTOS.size() == 12);
        System.out.println("\n" + ingredientDTOS.size() + " commodities to be ordered: ");
        for (IIngredientDTO ing : ingredientDTOS) {
            System.out.println("" + (ingredientDTOS.indexOf(ing) + 1) + ": IngredientID: " + ing.getIngredientId()
                    + "-" + ing.getName());
        }
    }
}
