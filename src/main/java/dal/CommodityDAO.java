package dal;

import dal.dto.*;

import java.sql.*;

public class CommodityDAO {
    private UserDAO userDAO;
    private Connection conn;

    public CommodityDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.conn = ConnectionDAO.createConnection();
    }

    public void createCommodityBatch(ICommodityBatchDTO commodityBatch) {
        IUserDTO userDTO = commodityBatch.getOrderedBy();
        if (!userDTO.getRoles().contains("productionleader")) {
            System.out.println("User not authorized to proceed!");
            return;
        }
        try {
            conn.setAutoCommit(false);
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
            conn.commit();
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

                ingredientDTO.setIngredientId(rs.getInt("ingredientid"));
                ingredientDTO.setName(rs.getString("ingredient.name"));
                ingredientDTO.setType(rs.getString("type"));

                userDTO.setUserId(rs.getInt("userid"));
                userDTO.setUserName(rs.getString("user.name"));
                userDTO.setIni(rs.getString("ini"));
                userDTO.setRoles(userDAO.getUserRoleList(userDTO.getUserId()));

                commodityBatch.setIngredientDTO(ingredientDTO);
                commodityBatch.setOrderedBy(userDTO);
                commodityBatch.setAmountInKg(rs.getDouble("amountinkg"));
                commodityBatch.setOrderDate(rs.getString("orderdate"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commodityBatch;
    }
}
