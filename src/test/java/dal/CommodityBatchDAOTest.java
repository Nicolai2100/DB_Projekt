package dal;

import dal.dto.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

public class CommodityBatchDAOTest {
    ConnectionDAO connectionDAO;
    UserDAO userDAO;
    CommodityDAO commodityDAO;
    IngredientDAO ingredientDAO;

    @Before
    public void initialize() {
        connectionDAO = new ConnectionDAO();
        userDAO = new UserDAO();
        commodityDAO = new CommodityDAO(userDAO);
        ingredientDAO = new IngredientDAO();
    }

    @After
    public void close() {
        connectionDAO.closeConn();
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
}