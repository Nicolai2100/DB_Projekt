package dal;

import dal.dto.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommodityBatchDAO implements ICommodityBatchDAO {
    private UserDAO userDAO;
    private Connection conn;

    public CommodityBatchDAO(UserDAO userDAO) throws DALException {
        this.userDAO = userDAO;
        this.conn = ConnectionDAO.getConnection();
    }

    @Override
    public void createCommodityBatch(ICommodityBatchDTO commodityBatch) throws DALException {
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
            throw new DALException("An error occurred in the database at CommodityBatchDAO.");
        }
    }

    @Override
    public ICommodityBatchDTO getCommodityBatch(int commodityBatchId) throws DALException {
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
            throw new DALException("An error occurred in the database at CommodityBatchDAO.");
        }
        return commodityBatch;
    }

    @Override
    public void updateCommodityBatch(ICommodityBatchDTO commodityBatch) throws DALException {
        String updateComBatchString = "UPDATE commoditybatch " +
                "SET commoditybatchid=?, ingredientid=?, orderedby=?, amountinkg=?, orderdate=?, residue=? " +
                "WHERE commoditybatchid=?";
        try {
            conn.setAutoCommit(false);
            PreparedStatement preparedStatement = conn.prepareStatement(updateComBatchString);
            preparedStatement.setInt(1, commodityBatch.getBatchId());
            preparedStatement.setInt(2, commodityBatch.getIngredientDTO().getIngredientId());
            preparedStatement.setInt(3, commodityBatch.getOrderedBy().getUserId());
            preparedStatement.setDouble(4, commodityBatch.getAmountInKg()); //TODO er sat til int i databasen - nej den er float
            preparedStatement.setString(5, commodityBatch.getOrderDate());
            preparedStatement.setBoolean(6, commodityBatch.isResidue());
            preparedStatement.setInt(7, commodityBatch.getBatchId());
            preparedStatement.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);

        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at CommodityBatchDAO.");
        }
    }

    @Override
    public void deleteCommodityBatch(int commodityBatchId) throws DALException {
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
            throw new DALException("An error occurred in the database at CommodityBatchDAO.");
        }
    }

    public double getTotalCommodityAmountInKG (IIngredientDTO ingredient) throws SQLException {
        double totalAmount = 0;

        try {
            PreparedStatement preparedStatement = conn.prepareStatement( //TODO skal det medregnes, hvis det er rest?
                    "SELECT sum(amountinkg) " +
                            "FROM commoditybatch " +
                            "WHERE ingredientid=? AND NOT residue=1"
            );
            preparedStatement.setInt(1, ingredient.getIngredientId());

            ResultSet resultSet = preparedStatement.executeQuery();

            totalAmount = resultSet.getDouble(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalAmount;
    }

    public List<ICommodityBatchDTO> getCommodityBatchList (IIngredientDTO ingredient) throws SQLException {
        List<ICommodityBatchDTO> commodityBatchList = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = conn.prepareStatement( //TODO skal det medregnes, hvis det er rest?
                    "SELECT commoditybatchid, ingredientid, amountinkg, orderdate, residue " + //TODO orderedby tages ikke med for det er wack
                            "FROM commoditybatch " +
                            "WHERE ingdientid=? AND NOT residue=1"
            );
            preparedStatement.setInt(1, ingredient.getIngredientId());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ICommodityBatchDTO batch = new CommodityBatchDTO();
                batch.setBatchId(resultSet.getInt("commoditybatchid"));
                batch.setIngredientDTO(ingredient);
                batch.setAmountInKg(resultSet.getInt("amountinkg"));
                batch.setOrderDate(resultSet.getString("orderdate"));
                batch.setResidue(resultSet.getBoolean("residue"));
                commodityBatchList.add(batch);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commodityBatchList;
    }
}
