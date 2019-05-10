package dal;

import dal.dto.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductBatchDAO implements IProductBatchDAO {
    private Connection conn;
    private RecipeDAO recipeDAO;
    private CommodityBatchDAO commoditybatchDAO;
    private UserDAO userDAO;

    public ProductBatchDAO(RecipeDAO recipeDAO, CommodityBatchDAO commoditybatchDAO, UserDAO userDAO) throws DALException {
        this.conn = ConnectionDAO.getConnection();
        this.commoditybatchDAO = commoditybatchDAO;
        this.recipeDAO = recipeDAO;
        this.userDAO = userDAO;
    }

    @Override
    public void createProductbatch(IProductBatchDTO productbatch) throws DALException {
        if (!productbatch.getMadeBy().getRoles().contains("productionleader") || !productbatch.getMadeBy().getIsActive()) {
            throw new DALException("User not authorized to proceed!");
        }
        if (productbatch.getVolume() < productbatch.getRecipe().getMinBatchSize()){
            throw new DALException("Productbatch size is too small!");
        }
        //Stadiet indsættes som ENUM. Det kan være enten ORDERED, UNDER_PRODUCTION eller COMPLETED.
        productbatch.setBatchState(IProductBatchDTO.State.ORDERED);
        String selectVersionString = "SELECT version from recipe where recipeid = ? AND in_use = 1";
        String insertString = "INSERT INTO productbatch VALUES(?,?,?,?,?,?,?,?,?,?)";
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmtSelectVersionNum = conn.prepareStatement(selectVersionString);
            pstmtSelectVersionNum.setInt(1, productbatch.getRecipe().getRecipeId());
            ResultSet rs = pstmtSelectVersionNum.executeQuery();
            int versionNum = 0;
            if (rs.next()) {
                versionNum = rs.getInt(1);
            }
            PreparedStatement pstmtInsertProduct = conn.prepareStatement(insertString);
            pstmtInsertProduct.setInt(1, productbatch.getProductId());
            pstmtInsertProduct.setString(2, productbatch.getName());
            pstmtInsertProduct.setInt(3, productbatch.getMadeBy().getUserId());
            pstmtInsertProduct.setInt(4, productbatch.getRecipe().getRecipeId());
            pstmtInsertProduct.setInt(5, versionNum);
            pstmtInsertProduct.setDate(6, productbatch.getProductionDate());
            pstmtInsertProduct.setInt(7, productbatch.getVolume());
            pstmtInsertProduct.setDate(8, productbatch.getExpirationDate());
            pstmtInsertProduct.setString(9, productbatch.getBatchState());
            if (productbatch.getProducedBy() == null) {
                pstmtInsertProduct.setNull(10, Types.INTEGER);
            } else {
                pstmtInsertProduct.setInt(10, productbatch.getProducedBy().getUserId());
            }

            int result = pstmtInsertProduct.executeUpdate();
            createRelations(productbatch.getCommodityBatches(), productbatch.getProductId());
            conn.commit();
            if (result > 0) {
                System.out.println("The product was successfully ordered.");
            } else {
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException("An error occurred in the database at ProductBatchDAO.");
        }
    }

    @Override
    public ProductBatchDTO getProductbatch(int productBatchID) throws DALException {
        ProductBatchDTO productbatchDTO = new ProductBatchDTO();
        String getProdBatchString =
                "SELECT productbatchid, name, madeby, recipe, production_date, volume, expiration_date, " +
                        "batch_state, commodity_batch_id FROM productbatch " +
                        "NATURAL JOIN productbatch_commodity_relationship WHERE productbatchid = ?;";
        try {
            PreparedStatement pstmtSelectProductBatch = conn.prepareStatement(getProdBatchString);
            pstmtSelectProductBatch.setInt(1, productBatchID);
            ResultSet rs = pstmtSelectProductBatch.executeQuery();
            int i = 0;
            while (rs.next()) {
                if (i < 1) {
                    productbatchDTO.setProductId(productBatchID);
                    productbatchDTO.setName(rs.getString("name"));
                    int recipeID = rs.getInt("recipe");
                    int recipeVersion = rs.getInt("recipeversion");
                    productbatchDTO.setRecipe(recipeDAO.getRecipeFromVersionNumber(recipeID,recipeVersion));
                    productbatchDTO.setMadeBy(userDAO.getUser(rs.getInt("madeby")));
                    productbatchDTO.setProductionDate(rs.getDate("production_date"));
                    productbatchDTO.setExpirationDate(rs.getDate("expiration_date"));
                    productbatchDTO.setVolume(rs.getInt("volume"));
                    productbatchDTO.setBatchState(IProductBatchDTO.State.valueOf(rs.getString("batch_state")));
                    i++;
                }
                productbatchDTO.getCommodityBatches().add(commoditybatchDAO.getCommodityBatch(rs.getInt("commodity_batch_id")));
            }
        } catch (Exception e) {
            throw new DALException("An error occurred in the database at ProductBatchDAO.");
        }
        return productbatchDTO;
    }

    @Override
    public void updateProductBatch(IProductBatchDTO productbatch, IUserDTO user) throws DALException {
        if ((!user.getRoles().contains("laborant") || !user.getRoles().contains("productionleader")) && !user.getIsActive()) {
            throw new DALException("User not authorized to proceed!");
        }
        String delString = "DELETE FROM productbatch_commodity_relationship WHERE product_batch_id = ?";
        String updateProcString = "UPDATE productbatch SET name = ?, madeby = ?, recipe = ?, recipeversion = ?, " +
                "production_date = ?, volume = ?, expiration_date = ?, batch_state = ?, producedby = ? " +
                "WHERE productbatchid = ?";
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmtDeleteRelations = conn.prepareStatement(delString);
            pstmtDeleteRelations.setInt(1, productbatch.getProductId());
            pstmtDeleteRelations.executeUpdate();

            PreparedStatement pstmtUpdateProduct = conn.prepareStatement(updateProcString);
            pstmtUpdateProduct.setString(1, productbatch.getName());
            pstmtUpdateProduct.setInt(2, productbatch.getMadeBy().getUserId());
            pstmtUpdateProduct.setInt(3, productbatch.getRecipe().getRecipeId());
            pstmtUpdateProduct.setInt(4,productbatch.getRecipe().getVersion());
            if (productbatch.getProductionDate() == null) {
                pstmtUpdateProduct.setNull(5, java.sql.Types.DATE);
            } else {
                pstmtUpdateProduct.setDate(5, productbatch.getProductionDate());
            }
            pstmtUpdateProduct.setDate(5, productbatch.getProductionDate());
            pstmtUpdateProduct.setInt(6, productbatch.getVolume());
            if (productbatch.getExpirationDate() == null) {
                pstmtUpdateProduct.setNull(7, java.sql.Types.DATE);
            } else {
                pstmtUpdateProduct.setDate(7, productbatch.getExpirationDate());
            }
            pstmtUpdateProduct.setString(8, productbatch.getBatchState());
            if (productbatch.getProducedBy() == null) {
                pstmtUpdateProduct.setInt(9, 0);
            } else {
                pstmtUpdateProduct.setInt(9, productbatch.getProducedBy().getUserId());
            }
            pstmtUpdateProduct.setInt(10, productbatch.getProductId());

            pstmtUpdateProduct.executeUpdate();
            createRelations(productbatch.getCommodityBatches(), productbatch.getProductId());
            conn.commit();
        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at ProductBatchDAO.");
        }
    }

    @Override
    public void initiateProduction(IProductBatchDTO productbatch, IUserDTO user) throws DALException {
        if ((!user.getRoles().contains("laborant") || !user.getRoles().contains("productionleader")) && !user.getIsActive()) {
            throw new DALException("User not authorized to proceed!");
        }
        try {
            conn.setAutoCommit(false);
            productbatch.setBatchState(IProductBatchDTO.State.UNDER_PRODUCTION);
            updateProductBatch(productbatch, user);

            List<ICommodityBatchDTO> commodityBatchDTOS = new ArrayList<>();
            double newAmount;
            for (IIngredientDTO i : recipeDAO.getActiveRecipe(productbatch.getRecipe().getRecipeId()).getIngredientsList()) {
                ICommodityBatchDTO commoditybatch = commoditybatchDAO.getCommodityBatch(i.getIngredientId());
                newAmount = (commoditybatch.getAmountInKg() - i.getAmount() / 1000000 * productbatch.getVolume());
                if (newAmount >= 0) {
                    commoditybatch.setAmountInKg(newAmount);
                    commodityBatchDTOS.add(commoditybatch);
                } else {
                    throw new DALException("Not enough of commodity in stock!");
                }
            }
            //Sørger for at ingen commoditybatches bliver opdateret
            //når bare en af dem går i minus
            for (ICommodityBatchDTO commoditybatch : commodityBatchDTOS) {
                commoditybatchDAO.updateCommodityBatch(commoditybatch);
                commoditybatchDAO.checkForResidue();
            }
            conn.commit();
            System.out.println("The product was successfully initiated.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void produceProductBatch(IProductBatchDTO productbatch, IUserDTO user) throws DALException {
        if (!user.getRoles().contains("laborant") && !user.getIsActive()) {
            throw new DALException("User not authorized to proceed!");
        }
        try {
            conn.setAutoCommit(false);
            productbatch.setProducedBy(user);
            productbatch.setBatchState(IProductBatchDTO.State.COMPLETED);
            Date now = (new Date(System.currentTimeMillis()));
            LocalDate ld = now.toLocalDate();
            LocalDate expirationDate = ld.plusMonths(productbatch.getRecipe().getExpirationInMonths());
            java.sql.Date sqlExpirationDate = java.sql.Date.valueOf(expirationDate);
            productbatch.setProductionDate(now);
            productbatch.setExpirationDate(sqlExpirationDate);
            updateProductBatch(productbatch, user);
            conn.commit();
            System.out.println("The product was successfully produced.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createRelations(List<ICommodityBatchDTO> commodityBatchList, int productbatchId) throws DALException {
        PreparedStatement pstmtInsertCommodityRelation;
        String createRelationString = "INSERT INTO productbatch_commodity_relationship VALUES(?,?)";
        try {
            pstmtInsertCommodityRelation = conn.prepareStatement(createRelationString);
            for (ICommodityBatchDTO c : commodityBatchList) {
                pstmtInsertCommodityRelation.setInt(1, productbatchId);
                pstmtInsertCommodityRelation.setInt(2, c.getBatchId());
                pstmtInsertCommodityRelation.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at ProductBatchDAO.");
        }
    }

    @Override
    public List<IProductBatchDTO> getProductsOrdered() throws DALException {
        return getProductList("ORDERED");
    }

    @Override
    public List<IProductBatchDTO> getProductsUnderProduction() throws DALException {
        return getProductList("UNDER_PRODUCTION");
    }

    @Override
    public List<IProductBatchDTO> getProductsCompleted() throws DALException {
        return getProductList("COMPLETED");
    }

    private List<IProductBatchDTO> getProductList(String status) throws DALException {
        String selectString = "SELECT * FROM productbatch WHERE batch_state = ?;";
        List<IProductBatchDTO> products = new ArrayList<>();
        try {
            PreparedStatement pstmtSelect = conn.prepareStatement(selectString);
            pstmtSelect.setString(1, status);
            ResultSet resultSet = pstmtSelect.executeQuery();

            while (resultSet.next()) {
                IProductBatchDTO productBatchDTO = new ProductBatchDTO();
                productBatchDTO.setProductId(resultSet.getInt(1));
                productBatchDTO.setName(resultSet.getString(2));
                productBatchDTO.setMadeBy(userDAO.getUser(resultSet.getInt(3)));

                int recipeID = resultSet.getInt("recipe");
                int recipeVersion = resultSet.getInt("recipeversion");
                productBatchDTO.setRecipe(recipeDAO.getRecipeFromVersionNumber(recipeID,recipeVersion));

                productBatchDTO.setProductionDate(resultSet.getDate(6));
                productBatchDTO.setVolume(resultSet.getInt(7));
                productBatchDTO.setExpirationDate(resultSet.getDate(8));
                productBatchDTO.setBatchState(IProductBatchDTO.State.valueOf(resultSet.getString(9)));
                productBatchDTO.setProducedBy(userDAO.getUser(resultSet.getInt(10)));
                products.add(productBatchDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException("An error occurred in the database at ProductBatchDAO.");
        }
        return products;
    }
}