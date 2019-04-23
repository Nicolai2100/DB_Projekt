package dal;

import dal.dto.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CommodityDAO {
    private UserDAO userDAO;
    private Connection conn;

    public CommodityDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.conn = ConnectionDAO.createConnection();
    }

    public void createCommodityBatch(ICommodityBatchDTO commodityBatch) {
        IUserDTO userDTO = commodityBatch.getOrderedBy();
        if (!userDTO.getRoles().contains("productleader")) {
            System.out.println("User not authorized to proceed!");
            return;
        }
        try {
            PreparedStatement pstmtInsertCommodityBatch = conn.prepareStatement(
                    "INSERT INTO commoditybatch " +
                            "VALUES(?,?,?,?,?,?)");
            pstmtInsertCommodityBatch.setInt(1, commodityBatch.getBatchId());
            pstmtInsertCommodityBatch.setInt(2, commodityBatch.getIngredientDTO().getIngredientId());
            pstmtInsertCommodityBatch.setInt(3, commodityBatch.getOrderedBy().getUserId());
            pstmtInsertCommodityBatch.setDouble(4, commodityBatch.getAmountInKg());
            pstmtInsertCommodityBatch.setString(5, commodityBatch.getOrderDate());
            pstmtInsertCommodityBatch.setBoolean(6, false);
            pstmtInsertCommodityBatch.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ICommodityBatchDTO getCommodityBatch(int commodityBatchId) {
        ICommodityBatchDTO commodityBatch = new CommodityBatchDTO();
        commodityBatch.setBatchId(commodityBatchId);
        IIngredientDTO ingredientDTO = new IngredientDTO();
        IUserDTO userDTO = new UserDTO();
        try {
            PreparedStatement pstmtGetCommodityBatch = conn.prepareStatement(
                    "SELECT * FROM commoditybatch " +
                            "JOIN ingredient ON ingredient.ingredientid " +
                            "JOIN user " +
                            "ON orderedby = userid " +
                            "WHERE commoditybatchid = ?;");

            pstmtGetCommodityBatch.setInt(1, commodityBatchId);

            ResultSet rs = pstmtGetCommodityBatch.executeQuery();
            while (rs.next()) {

                ingredientDTO.setIngredientId(rs.getInt(6));
                ingredientDTO.setName(rs.getString(7));
                ingredientDTO.setType(rs.getString(8));

                userDTO.setUserId(rs.getInt(9));
                userDTO.setUserName(rs.getString(10));
                userDTO.setIni(rs.getString(11));
                userDTO.setRoles(userDAO.getUserRoleList(userDTO.getUserId()));

                commodityBatch.setIngredientDTO(ingredientDTO);
                commodityBatch.setOrderedBy(userDTO);
                commodityBatch.setAmountInKg(rs.getDouble(4));
                commodityBatch.setOrderDate(rs.getString(5));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commodityBatch;
    }
}
