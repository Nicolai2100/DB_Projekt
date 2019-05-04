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
    RecipeDAO recipeDAO = new RecipeDAO(ingredientDAO, ingredientListDAO, userDAO);
    CommodityBatchDAO commoditybatchDAO = new CommodityBatchDAO(userDAO, ingredientDAO, recipeDAO);
    ProductBatchDAO productBatchDAO = new ProductBatchDAO(recipeDAO, commoditybatchDAO, userDAO);

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
    public void createProduct() throws DALException {
        IUserDTO testUser_1 = userDAO.getUser(1);
        IUserDTO testUser_4 = userDAO.getUser(4);

      /*  ProductBatchDTO productbatchDTO = new ProductBatchDTO();
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
        */
        IProductBatchDTO productbatchDTO = productBatchDAO.getProductbatch(1);
        System.out.println(productbatchDTO.getMadeBy());
        productBatchDAO.initiateProduction(productbatchDTO, testUser_1);

        /*

        productbatchDTO.setName("Amfetamin");

        productBatchDAO.produceProductBatch(productbatchDTO, testUser_4);

        System.out.println("Read product succesful:" + productBatchDAO.getProductbatch(1).toString());*/
    }


    @Test
    public void checkForReorder() throws DALException {
        List<IIngredientDTO> ingredientDTOS = ingredientDAO.getReorders();

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
}