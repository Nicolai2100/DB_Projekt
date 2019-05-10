package dal;

import dal.dto.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommodityBatchDAO implements ICommodityBatchDAO {
    private UserDAO userDAO;
    private IngredientDAO ingredientDAO;
    private Connection conn;

    public CommodityBatchDAO(UserDAO userDAO, IngredientDAO ingredientDAO) throws DALException {
        this.userDAO = userDAO;
        this.ingredientDAO = ingredientDAO;
        this.conn = ConnectionDAO.getConnection();
    }

    @Override
    public void createCommodityBatch(ICommodityBatchDTO commodityBatch) throws DALException {
        IUserDTO userDTO = commodityBatch.getOrderedBy();
        if (!userDTO.getRoles().contains("productionleader")) {
            throw new DALException("User not authorized to proceed!");
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
                "ON ingredient.ingredient_id = commoditybatch.ingredient_id " +
                "LEFT JOIN user " +
                "ON orderer_id = user_id " +
                "WHERE commoditybatch_id = ?;";
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
                commodityBatch.setOrderedBy(userDAO.getUser(rs.getInt(3)));
                commodityBatch.setAmountInKg(rs.getDouble(4));
                commodityBatch.setOrderDate(rs.getString(5));
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
                "SET commoditybatch_id=?, ingredient_id=?, orderer_id=?, amount_kg=?, order_date=?, residue_status=? " +
                "WHERE commoditybatch_id=?";
        try {
            conn.setAutoCommit(false);
            PreparedStatement preparedStatement = conn.prepareStatement(updateComBatchString);
            preparedStatement.setInt(1, commodityBatch.getBatchId());
            preparedStatement.setInt(2, commodityBatch.getIngredientDTO().getIngredientId());
            preparedStatement.setInt(3, commodityBatch.getOrderedBy().getUserId());
            preparedStatement.setDouble(4, commodityBatch.getAmountInKg());
            preparedStatement.setString(5, commodityBatch.getOrderDate());
            preparedStatement.setBoolean(6, commodityBatch.isResidue());
            preparedStatement.setInt(7, commodityBatch.getBatchId());
            preparedStatement.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at CommodityBatchDAO.");
        }
    }

    public double getTotalCommodityAmountInKG(IIngredientDTO ingredient) throws DALException {
        double totalAmount;
        //TODO skal det medregnes, hvis det er rest?
        String getTotComAmString = "SELECT sum(amount_kg) " +
                "FROM commoditybatch " +
                "WHERE ingredient_id=? AND NOT residue_status=1";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(getTotComAmString);
            preparedStatement.setInt(1, ingredient.getIngredientId());
            ResultSet resultSet = preparedStatement.executeQuery();
            totalAmount = resultSet.getDouble(1);
        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at CommodityBatchDAO.");
        }
        return totalAmount;
    }

    public List<ICommodityBatchDTO> getCommodityBatchList(IIngredientDTO ingredient) throws DALException {
        List<ICommodityBatchDTO> commodityBatchList = new ArrayList<>();
        //TODO skal det medregnes, hvis det er rest?
        //TODO orderedby tages ikke med for det er wack
        String getComBatListString = "SELECT commoditybatch_id, ingredient_id, amount_kg, order_date, residue_status " +
                "FROM commoditybatch " +
                "WHERE ingredient_id = ? AND NOT residue_status=1";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(getComBatListString);
            preparedStatement.setInt(1, ingredient.getIngredientId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ICommodityBatchDTO batch = new CommodityBatchDTO();
                batch.setBatchId(resultSet.getInt("commoditybatch_id"));
                batch.setIngredientDTO(ingredient);
                batch.setAmountInKg(resultSet.getInt("amount_kg"));
                batch.setOrderDate(resultSet.getString("order_date"));
                batch.setResidue(resultSet.getBoolean("residue_status"));
                commodityBatchList.add(batch);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException("An error occurred in the database at CommodityBatchDAO.");
        }
        return commodityBatchList;
    }

    public List<ICommodityBatchDTO> getAllCommodityBatchList() throws DALException {
        List<ICommodityBatchDTO> commodityBatchList = new ArrayList<>();
        String getAllComBat = "SELECT * FROM commoditybatch WHERE residue_status = 0;";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(getAllComBat);
            ResultSet resultSet = preparedStatement.executeQuery();

            ICommodityBatchDTO batch;
            while (resultSet.next()) {
                batch = new CommodityBatchDTO();
                batch.setBatchId(resultSet.getInt(1));
                int ingInt = resultSet.getInt(2);
                IIngredientDTO ing = ingredientDAO.getIngredient(ingInt);
                batch.setIngredientDTO(ing);
                batch.setOrderedBy(userDAO.getUser(resultSet.getInt(3)));
                batch.setAmountInKg(resultSet.getDouble(4));
                batch.setOrderDate(resultSet.getString(5));
                commodityBatchList.add(batch);
            }
        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at CommodityBatchDAO.");
        }
        return commodityBatchList;
    }
}
