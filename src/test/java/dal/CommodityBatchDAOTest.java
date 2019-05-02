package dal;

import dal.dto.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

public class CommodityBatchDAOTest {
    ConnectionDAO connectionDAO;
    UserDAO userDAO;
    CommodityBatchDAO commoditybatchDAO;
    IngredientDAO ingredientDAO;

    @Before
    public void initialize() throws DALException {
        connectionDAO = new ConnectionDAO();
        userDAO = new UserDAO();
        ingredientDAO = new IngredientDAO();
        commoditybatchDAO = new CommodityBatchDAO(userDAO, ingredientDAO);
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

        for (ICommodityBatchDTO comBat: commoditybatchDAO.getAllCommodityBatchList()) {
            System.out.println(comBat);
        }
    }


    @Test
    public void createCommodityBatch() throws DALException {

        ICommodityBatchDTO commodityBatch = new CommodityBatchDTO();
        IUserDTO testUser = userDAO.getUser(1);
        commodityBatch.setOrderedBy(testUser);
        commodityBatch.setBatchId(50);
        commodityBatch.setAmountInKg(2.5);
        commodityBatch.setIngredientDTO(ingredientDAO.getIngredient(16));
        commodityBatch.setOrderDate(LocalDateTime.now().toString());
        commoditybatchDAO.createCommodityBatch(commodityBatch);
    }
}