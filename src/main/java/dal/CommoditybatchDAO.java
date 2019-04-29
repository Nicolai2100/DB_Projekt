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
        String insertString = "INSERT INTO commoditybatch VALUES(?,?,?,?,?,?);";
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmtInsertCommodityBatch = conn.prepareStatement(insertString);
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
        String selectComBatch = "SELECT * FROM commoditybatch " +
                "LEFT JOIN ingredient " +
                "ON ingredient.ingredientid = commoditybatch.ingredientid " +
                "LEFT JOIN user " +
                "ON orderedby = userid " +
                "WHERE commoditybatchid = ?;";
        try {
            PreparedStatement pstmtSelectCommodityBatch = conn.prepareStatement(selectComBatch);

            pstmtSelectCommodityBatch.setInt(1, commodityBatchId);
            boolean hasResult = false;
            ResultSet rs = pstmtSelectCommodityBatch.executeQuery();
            while (rs.next()) {
                hasResult = true;
                ingredientDTO.setIngredientId(rs.getInt(7));
                ingredientDTO.setName(rs.getString(8));
                ingredientDTO.setType(rs.getString(9));
                commodityBatch.setIngredientDTO(ingredientDTO);
                commodityBatch.setOrderedBy(userDTO);
                commodityBatch.setAmountInKg(rs.getDouble(4));
                commodityBatch.setOrderDate(rs.getString(5));
                commodityBatch.setOrderedBy(userDAO.getUser(rs.getInt(3)));
            }
            if (!hasResult) {
                System.out.println("No commodity-batch with that batchID!");
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IUserDAO.DALException e) {
            e.printStackTrace();
        }
        return commodityBatch;
    }

    public void deleteCommodityBatch (int commodityBatchId) throws SQLException {
        try {
            conn.setAutoCommit(false);
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "DELETE FROM commoditybatch WHERE commoditybatchid=?"
            );
            preparedStatement.setInt(1, commodityBatchId);

            preparedStatement.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCommodityBatch (ICommodityBatchDTO commodityBatch) throws SQLException {
        try {
            conn.setAutoCommit(false);
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "UPDATE commoditybatch " +
                            "SET commoditybatchid=?, ingredientid=?, orderedby=?, amountinkg=?, orderdate=?, residue=? " +
                            "WHERE commoditybatchid=?"
            );
            preparedStatement.setInt(1, commodityBatch.getBatchId());
            preparedStatement.setInt(2, commodityBatch.getIngredientDTO().getIngredientId());
            preparedStatement.setInt(3, commodityBatch.getOrderedBy().getUserId());
            preparedStatement.setDouble(4, commodityBatch.getAmountInKg()); //TODO er sat til int i databasen
            preparedStatement.setString(5, commodityBatch.getOrderDate());
            preparedStatement.setBoolean(6, commodityBatch.isResidue());
            preparedStatement.setInt(7, commodityBatch.getBatchId());

            preparedStatement.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
