package dal;

import dal.dto.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public void updateCommodityBatch (ICommodityBatchDTO commodityBatch) throws IUserDAO.DALException {
        try {
            conn.setAutoCommit(false);
            PreparedStatement preparedStatementUpdate = conn.prepareStatement(
                    "UPDATE commoditybatch " +
                            "SET commoditybatchid=?, ingredientid=?, orderedby=?, amountinkg=?, orderdate=?, residue=? " +
                            "WHERE commoditybatchid=?"
            );
            preparedStatementUpdate.setInt(1, commodityBatch.getBatchId());
            preparedStatementUpdate.setInt(2, commodityBatch.getIngredientDTO().getIngredientId());
            preparedStatementUpdate.setInt(3, commodityBatch.getOrderedBy().getUserId());
            preparedStatementUpdate.setDouble(4, commodityBatch.getAmountInKg()); //TODO er sat til int i databasen
            preparedStatementUpdate.setString(5, commodityBatch.getOrderDate());
            preparedStatementUpdate.setBoolean(6, commodityBatch.isResidue());
            preparedStatementUpdate.setInt(7, commodityBatch.getBatchId());

            preparedStatementUpdate.executeUpdate();
            conn.commit();

            //check whether or not the batch now contains less than the amount required to produce two product
            // batches of the "most-expensive" recipe
            if (checkForReorder(commodityBatch)) {
                PreparedStatement preparedStatementReorder = conn.prepareStatement(
                        "UPDATE ingredient " +
                                "SET reorder=?, " +
                                "WHERE ingredientid=?"
                );
                preparedStatementReorder.setInt(1, 1); //set reorder status to true
                preparedStatementReorder.setInt(2, commodityBatch.getIngredientDTO().getIngredientId());

                preparedStatementReorder.executeUpdate();
                conn.commit(); //TODO ikke atomic, men kan det nogensinde blive det?
            }

            conn.setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
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
                    "SELECT commoditybatchid, ingredientid, amountinkg, orderdate, residue " + //TODO orderedby tages ikke med fordi det er wack
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

    public boolean checkForReorder (ICommodityBatchDTO commodityBatch) throws IUserDAO.DALException {
        double maxAmount = 0;
        boolean reorder = false;

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(
                    "SELECT amountmg, minbatchsize " +
                            "FROM ingredientlist, recipe " +
                            "WHERE ingredientlist.ingredientlistid = recipe.ingredientlistid " +
                            "AND ingredientlist.ingredientlistid = 2;"
            );
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                double amount = resultSet.getInt("amountmg") * resultSet.getInt("minbatchsize");
                if (amount > maxAmount) {
                    maxAmount = amount;
                }
            }

            if (commodityBatch.getAmountInKg()/1000000 < maxAmount*2) {
                reorder = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reorder;
    }

}
