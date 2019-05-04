package dal;

import dal.dto.*;

import java.sql.*;
import java.util.*;

public class CommodityBatchDAO implements ICommodityBatchDAO {
    private UserDAO userDAO;
    private IngredientDAO ingredientDAO;
    private Connection conn;
    private RecipeDAO recipeDAO;

    public CommodityBatchDAO(UserDAO userDAO, IngredientDAO ingredientDAO, RecipeDAO recipeDAO) throws DALException {
        this.userDAO = userDAO;
        this.ingredientDAO = ingredientDAO;
        this.recipeDAO = recipeDAO;
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

/*
            recipeDAO.checkReorder();
*/
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
                "SET commoditybatchid=?, ingredientid=?, orderedby=?, amountinkg=?, orderdate=?, residue=? " +
                "WHERE commoditybatchid=?";
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

/*
            recipeDAO.checkReorder();
*/
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException("An error occurred in the database at CommodityBatchDAO.");
        }
    }

    public double getTotalCommodityAmountInKG(IIngredientDTO ingredient) throws DALException {
        double totalAmount = 0.0;
        String getTotComAmString = "SELECT sum(amountinkg) " +
                "FROM commoditybatch " +
                "WHERE ingredientid = ? AND residue=0";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(getTotComAmString);
            preparedStatement.setInt(1, ingredient.getIngredientId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                totalAmount = resultSet.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException("An error occurred in the database at CommodityBatchDAO.");
        }
        return totalAmount;
    }

    public List<ICommodityBatchDTO> getCommodityBatchList(IIngredientDTO ingredient) throws DALException {
        List<ICommodityBatchDTO> commodityBatchList = new ArrayList<>();
        //TODO skal det medregnes, hvis det er rest?
        //TODO orderedby tages ikke med for det er wack
        String getComBatListString = "SELECT commoditybatchid, ingredientid, amountinkg, orderdate, residue " +
                "FROM commoditybatch " +
                "WHERE ingdientid = ? AND NOT residue=1";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(getComBatListString);
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
            throw new DALException("An error occurred in the database at CommodityBatchDAO.");
        }
        return commodityBatchList;
    }

    public List<ICommodityBatchDTO> getAllCommodityBatchList() throws DALException {
        List<ICommodityBatchDTO> commodityBatchList = new ArrayList<>();
        String getAllComBat = "SELECT * FROM commoditybatch WHERE residue = 0;";
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

    public void checkForResidue() throws DALException {
        try {
            List<ICommodityBatchDTO> combats = getAllCommodityBatchList();

            String setResidueString = "UPDATE commoditybatch SET residue = 1 WHERE " +
                    "commoditybatchid = ?;";
            PreparedStatement pstmtSetResidue = conn.prepareStatement(setResidueString);

            for (ICommodityBatchDTO combat : combats) {
                if (combat.getAmountInKg()*1000000 < combat.getIngredientDTO().getMinAmountMG()) {
                    pstmtSetResidue.setInt(1, combat.getBatchId());
                    pstmtSetResidue.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException("An error occurred in the database at CommodityBatchDAO.");
        }
    }
}
