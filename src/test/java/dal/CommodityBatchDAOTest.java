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
        commoditybatchDAO = new CommodityBatchDAO(userDAO);
        ingredientDAO = new IngredientDAO();
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
    public void createCommodityBatch() throws DALException {

        ICommodityBatchDTO commodityBatch = new CommodityBatchDTO();
        IUserDTO testUser = userDAO.getUser(2);
        commodityBatch.setOrderedBy(testUser);
        commodityBatch.setBatchId(4);
        commodityBatch.setAmountInKg(2.5);
        commodityBatch.setIngredientDTO(ingredientDAO.getIngredient(5));
        commodityBatch.setOrderDate(LocalDateTime.now().toString());
        commoditybatchDAO.createCommodityBatch(commodityBatch);
    }
}