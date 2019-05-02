package dal;

import dal.dto.*;

import java.sql.*;
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
    public void createProductbatch(ProductBatchDTO productbatch) throws DALException {
        if (!productbatch.getMadeBy().getRoles().contains("productionleader") || !productbatch.getMadeBy().getIsActive()) {
            System.out.println("User not authorized to proceed!");
            return;
        }
        try {
            conn.setAutoCommit(false);

            PreparedStatement pstmtSelectVersionNum = conn.prepareStatement("SELECT version from recipe where " +
                    "recipeid = ? AND in_use = 1");
            pstmtSelectVersionNum.setInt(1, productbatch.getRecipe());

            ResultSet rs = pstmtSelectVersionNum.executeQuery();
            int versionNum = 0;

            if (rs.next()) {
                versionNum = rs.getInt(1);
            }
            System.out.println(versionNum);

            PreparedStatement pstmtInsertProduct = conn.prepareStatement(
                    "INSERT INTO productbatch " +
                            "VALUES(?,?,?,?,?,?,?,?,?,?)");

            pstmtInsertProduct.setInt(1, productbatch.getProductId());
            pstmtInsertProduct.setString(2, productbatch.getName());
            pstmtInsertProduct.setInt(3, productbatch.getMadeBy().getUserId());
            pstmtInsertProduct.setInt(4, productbatch.getRecipe());
            pstmtInsertProduct.setInt(5, versionNum);
            pstmtInsertProduct.setDate(6, productbatch.getProductionDate());
            pstmtInsertProduct.setInt(7, productbatch.getVolume());
            pstmtInsertProduct.setDate(8, productbatch.getExpirationDate());
            pstmtInsertProduct.setString(9, productbatch.getBatchState());
            pstmtInsertProduct.setInt(10, productbatch.getProducedBy().getUserId());
            pstmtInsertProduct.executeUpdate();
            createRelations(productbatch.getCommodityBatches(), productbatch.getProductId());
            conn.commit();
            System.out.println("The product was successfully created.");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException("An error occurred in the database at ProductBatchDAO.");
        }
    }

    @Override
    public ProductBatchDTO getProductbatch(int productBatch) throws DALException {
        ProductBatchDTO productbatchDTO = new ProductBatchDTO();

        try {
            PreparedStatement pstmtSelectProductBatch = conn.prepareStatement(
                    "SELECT productbatchid, name, madeby, recipe, production_date, volume, expiration_date, batch_state, commodity_batch_id FROM productbatch NATURAL JOIN productbatch_commodity_relationship WHERE productbatchid = ?;");

            pstmtSelectProductBatch.setInt(1, productBatch);
            ResultSet rs = pstmtSelectProductBatch.executeQuery();
            int i = 0;
            while (rs.next()) {
                if (i < 1) {
                    productbatchDTO.setProductId(productBatch);
                    productbatchDTO.setName(rs.getString("name"));
                    productbatchDTO.setRecipe(rs.getInt("recipe"));
                    productbatchDTO.setMadeBy((UserDTO) userDAO.getUser(rs.getInt("madeby")));
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
    public void updateProductBatch(ProductBatchDTO productbatch, IUserDTO user) throws DALException {
        if ((!user.getRoles().contains("laborant") || !user.getRoles().contains("productionleader")) && !user.getIsActive()) {
            System.out.println("User not authorized to proceed!");
            return;
        }

        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmtDeleteRelations = conn.prepareStatement(
                    "DELETE FROM productbatch_commodity_relationship WHERE product_batch_id = ?");
            pstmtDeleteRelations.setInt(1, productbatch.getProductId());
            pstmtDeleteRelations.executeUpdate();

            PreparedStatement pstmtUpdateProduct = conn.prepareStatement(
                    "UPDATE productbatch SET name = ?, madeby = ?, recipe = ?, production_date = ?, volume = ?, expiration_date = ?, batch_state = ?, producedby = ? WHERE productbatchid = ?");

            pstmtUpdateProduct.setString(1, productbatch.getName());
            pstmtUpdateProduct.setInt(2, productbatch.getMadeBy().getUserId());
            pstmtUpdateProduct.setInt(3, productbatch.getRecipe());
            pstmtUpdateProduct.setDate(4, productbatch.getProductionDate());
            pstmtUpdateProduct.setInt(5, productbatch.getVolume());
            pstmtUpdateProduct.setDate(6, productbatch.getExpirationDate());
            pstmtUpdateProduct.setString(7, productbatch.getBatchState());
            pstmtUpdateProduct.setInt(8, productbatch.getProductId());
            pstmtUpdateProduct.setInt(9, productbatch.getProducedBy().getUserId());

            pstmtUpdateProduct.executeUpdate();

            createRelations(productbatch.getCommodityBatches(), productbatch.getProductId());
            conn.commit();
            System.out.println("The product was successfully updated.");
        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at ProductBatchDAO.");
        }
    }

    @Override
    public void initiateProduction(ProductBatchDTO productbatch, IUserDTO user) throws DALException {
        productbatch.setBatchState(IProductBatchDTO.State.UNDER_PRODUCTION);
        updateProductBatch(productbatch, user);
    }

    @Override
    public void produceProductBatch(ProductBatchDTO productbatch, IUserDTO user) throws DALException {
        if (!user.getRoles().contains("laborant") && !user.getIsActive()) {
            System.out.println("User not authorized to proceed!");
            return;
        }
        productbatch.setProducedBy(user);
        productbatch.setBatchState(IProductBatchDTO.State.COMPLETED);
        productbatch.setProductionDate(new Date(System.currentTimeMillis()));

        for (IIngredientDTO i : recipeDAO.getActiveRecipe(productbatch.getRecipe()).getIngredientsList()) {
            ICommodityBatchDTO commoditybatch = commoditybatchDAO.getCommodityBatch(i.getIngredientId());
            double newAmount = (commoditybatch.getAmountInKg() - i.getAmount() / 1000000 * productbatch.getVolume());
            if (newAmount >= 0){
                commoditybatch.setAmountInKg(newAmount);
                System.out.println("Commodity-BatchID: " + commoditybatch.getBatchId() + " new amount " + newAmount);
                commoditybatchDAO.updateCommodityBatch(commoditybatch);
            }else{
                throw new DALException("Not enough of commodity in stock!");
            }
        }
        updateProductBatch(productbatch, user);
    }

    @Override
    public void createRelations(List<ICommodityBatchDTO> commodityBatchList, int productbatchId) throws DALException {
        PreparedStatement pstmtInsertCommodityRelation;
        try {
            pstmtInsertCommodityRelation = conn.prepareStatement(
                    "INSERT INTO productbatch_commodity_relationship " +
                            "VALUES(?,?)");

            for (ICommodityBatchDTO c : commodityBatchList) {
                pstmtInsertCommodityRelation.setInt(1, productbatchId);
                pstmtInsertCommodityRelation.setInt(2, c.getBatchId());
                pstmtInsertCommodityRelation.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at ProductBatchDAO.");
        }
    }
}