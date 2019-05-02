package dal;

import dal.dto.*;
import org.junit.After;
import org.junit.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductBatchDAOTest {
    ConnectionDAO connectionDAO = new ConnectionDAO();
    UserDAO userDAO = new UserDAO();
    IngredientDAO ingredientDAO = new IngredientDAO();
    IngredientListDAO ingredientListDAO = new IngredientListDAO(ingredientDAO);
    CommodityBatchDAO commoditybatchDAO = new CommodityBatchDAO(userDAO, ingredientDAO);
    RecipeDAO recipeDAO = new RecipeDAO(ingredientListDAO,ingredientDAO, userDAO, commoditybatchDAO);
    ProductBatchDAO productBatchDAO = new ProductBatchDAO(recipeDAO, commoditybatchDAO, userDAO);
    UserDAOTest userDAOTest = new UserDAOTest();

    public ProductBatchDAOTest() throws DALException {
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
    public void cleanTables() throws DALException {
        connectionDAO.deleteTables();
    }

    @Test
    public void createProduct() throws DALException {
        IUserDTO testUser_4 = userDAO.getUser(4);
        IUserDTO testUser_2 = userDAO.getUser(1);

        ProductBatchDTO productbatchDTO = new ProductBatchDTO();
        UserDTO testUser2 = (UserDTO) userDAO.getUser(1);
        productbatchDTO.setMadeBy(testUser2); //Produktionslederen indsættes som et bruger-objekt.
        productbatchDTO.setName("Sildenafil"); //Produktets navn indsættes.
        productbatchDTO.setProductId(3); //Et unikt id vælges.
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
    }


    @Test
    public void checkForReorder() throws DALException {
        List<IIngredientDTO> ingredientDTOS = ingredientDAO.checkForReorder();

        for (IIngredientDTO ing : ingredientDTOS) {
            System.out.println(ing);
        }
    }

    @Test
    public void getCommodityBatch() throws DALException {
        ICommodityBatchDTO batchFromDB = commoditybatchDAO.getCommodityBatch(2);
        System.out.println(batchFromDB);
    }

    @Test
    public void createCommodityBatch() throws DALException {

        ICommodityBatchDTO commodityBatch = new CommodityBatchDTO();
        IUserDTO testUser = userDAO.getUser(2);
        commodityBatch.setOrderedBy(testUser);
        commodityBatch.setBatchId(3);
        commodityBatch.setAmountInKg(2.5);
        commodityBatch.setIngredientDTO(ingredientDAO.getIngredient(2));
        commodityBatch.setOrderDate(LocalDate.now().toString());

        commoditybatchDAO.createCommodityBatch(commodityBatch);
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

    @Test
    public void createIngredientList() throws DALException {
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
    public void getIngredientList() throws DALException {
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setRecipeId(2);
        List<IIngredientDTO> ingredients = ingredientListDAO.getIngredientList(recipeDTO);

        System.out.println(ingredients);
    }

    @Test
    public void createIngredient() throws DALException {
        List<IngredientDTO> ingredients = new ArrayList<>();

        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("estradiol");
        ingredientDTO.setType("active");
        ingredientDTO.setIngredientId(1);
        ingredientDTO.setAmount(1);
        ingredientDTO.setMinAmountMG(100000);
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
    public void testItAll() throws DALException {

        /**
         * Alt slettes
         */

        connectionDAO.deleteTables();


        /**
         * Brugerne oprettes
         */
        IUserDTO admin = userDAO.getUser(1);
        IUserDTO testUser_2 = new UserDTO();
        testUser_2.setUserId(2);
        testUser_2.setUserName("Pelle Hansen");
        testUser_2.setIni("PH");
        ArrayList<String> roles2 = new ArrayList();
        roles2.add("admin");
        roles2.add("productionleader");
        testUser_2.setRoles(roles2);
        testUser_2.setIsActive(true);
        userDAO.createUser(admin, testUser_2);

        UserDTO testUser_3 = new UserDTO();
        testUser_3.setUserId(3);
        testUser_3.setUserName("Puk Larsen");
        testUser_3.setIni("PL");
        testUser_3.addRole("farmaceut");
        testUser_3.setAdmin(userDAO.getUser(2));
        testUser_3.setIsActive(true);
        userDAO.createUser(admin, testUser_3);
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

/*
        recipeDAO.archiveRecipe(2, testUser_3);
*/
        createProduct();

    }
}