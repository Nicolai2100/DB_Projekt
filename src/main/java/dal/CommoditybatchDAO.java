package dal;

import dal.dto.*;

import java.sql.*;

public class CommoditybatchDAO {
    private UserDAO userDAO;
    private Connection conn;

    public CommoditybatchDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.conn = ConnectionDAO.getConnection();
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

            boolean hasResult = false;
            ResultSet rs = pstmtGetCommodityBatch.executeQuery();
            while (rs.next()) {

                ingredientDTO.setIngredientId(rs.getInt("ingredientid"));
                ingredientDTO.setName(rs.getString("ingredient.name"));
                ingredientDTO.setType(rs.getString("type"));
                hasResult = true;
                ingredientDTO.setIngredientId(rs.getInt(6));
                ingredientDTO.setName(rs.getString(7));
                ingredientDTO.setType(rs.getString(8));

                userDTO.setUserId(rs.getInt("userid"));
                userDTO.setUserName(rs.getString("user.name"));
                userDTO.setIni(rs.getString("ini"));
                userDTO.setRoles(userDAO.getUserRoleList(userDTO.getUserId()));

                commodityBatch.setIngredientDTO(ingredientDTO);
                commodityBatch.setOrderedBy(userDTO);
                commodityBatch.setAmountInKg(rs.getDouble("amountinkg"));
                commodityBatch.setOrderDate(rs.getString("orderdate"));
            }
            if (!hasResult){
                System.out.println("No commodity-batch with that batchID!");
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commodityBatch;
    }
}
