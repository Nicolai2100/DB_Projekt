package dal;

import dal.dto.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class CommodityBatchDAOTest {
    ConnectionDAO connectionDAO;
    UserDAO userDAO;
    CommodityBatchDAO commoditybatchDAO;
    IngredientDAO ingredientDAO;
    RecipeDAO recipeDAO;
    IngredientListDAO ingredientListDAO;

    @Before
    public void initialize() throws DALException {
        connectionDAO = new ConnectionDAO();
        userDAO = new UserDAO();
        ingredientDAO = new IngredientDAO();
        recipeDAO = new RecipeDAO(ingredientDAO, ingredientListDAO, userDAO);
        commoditybatchDAO = new CommodityBatchDAO(userDAO, ingredientDAO, recipeDAO);
        ingredientListDAO = new IngredientListDAO(ingredientDAO);
    }

    @After
    public void close() throws DALException {
        connectionDAO.closeConn();
    }

    @Test
    public void getCommodityBatch() throws DALException {
        ICommodityBatchDTO batchFromDB = commoditybatchDAO.getCommodityBatch(4);
        System.out.println(batchFromDB);
    }


    @Test
    public void getAllCommodityBatch() throws DALException {
       /* for (ICommodityBatchDTO comBat : commoditybatchDAO.getAllCommodityBatchList()) {
            System.out.println(comBat);
        }*/

        List<IIngredientDTO> listen = ingredientListDAO.getIngredientList(recipeDAO.getActiveRecipe(2));
        System.out.println(listen.size());
        for (IIngredientDTO ing : listen) {
            System.out.println(commoditybatchDAO.getTotalCommodityAmountInKG(ing));
        }
    }


    @Test
    public void createCommodityBatch() throws DALException {
       /* ICommodityBatchDTO commodityBatch = new CommodityBatchDTO();
        IUserDTO testUser = userDAO.getUser(1);
        commodityBatch.setOrderedBy(testUser);
        commodityBatch.setBatchId(52);
        commodityBatch.setAmountInKg(2.5);
        commodityBatch.setIngredientDTO(ingredientDAO.getIngredient(16));
        commodityBatch.setOrderDate(LocalDateTime.now().toString());
        commoditybatchDAO.createCommodityBatch(commodityBatch);*/

        /**
         * Hamstring af majsstivelse
         **/
        for (int i = 0; i < 3; i++) {
            ICommodityBatchDTO majsstivelse = new CommodityBatchDTO();
            majsstivelse.setOrderedBy(userDAO.getUser(1));
            majsstivelse.setBatchId(50 + i);
            majsstivelse.setAmountInKg(2.5);
            majsstivelse.setIngredientDTO(ingredientDAO.getIngredient(16));
            majsstivelse.setOrderDate(LocalDateTime.now().toString());
            commoditybatchDAO.createCommodityBatch(majsstivelse);
        }
        /**
         * Liste over den resterende mængde af råvarer for hver ingrediens i norethisteron i kg
         **/

    }

    @Test
    public void updateBatch() throws DALException {
        ICommodityBatchDTO comBat = commoditybatchDAO.getCommodityBatch(4);
        commoditybatchDAO.updateCommodityBatch(comBat);
    }
}