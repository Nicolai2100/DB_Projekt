package dal;

import dal.dto.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductBatchDAO {
    private Connection conn;

    public ProductBatchDAO() {
        this.conn = ConnectionDAO.getConnection();
    }

    /*Systemet skal således understøtte
  Oprettelse og administration af opskrifter med indholdsstoffer (Farmaceut)
  Oprettelse og administration af råvarebatches (Produktionsleder)
  Oprettelse og igangsætning af produktbatches (Produktionsleder)
  Produktion af produktbatches (Laborant)
  Lagerstatus af råvarer og råvarebatches (Produktionsleder)
  */

    public void createProductbatch(ProductbatchDTO productbatch) {
        //kontroller om han er aktiv i systemet
        if (!productbatch.getMadeBy().getRoles().contains("productionleader") || !productbatch.getMadeBy().getIsActive()) {
            System.out.println("User not authorized to proceed!");
            return;
        }
        try {
            conn.setAutoCommit(false);

            PreparedStatement pstmtInsertProduct = conn.prepareStatement(
                    "INSERT INTO productbatch " +
                            "VALUES(?,?,?,?,?,?,?,?)");


            pstmtInsertProduct.setInt(1, productbatch.getProductId());
            pstmtInsertProduct.setString(2, productbatch.getName());
            pstmtInsertProduct.setInt(3, productbatch.getMadeBy().getUserId());
            pstmtInsertProduct.setInt(4, productbatch.getRecipe());
            pstmtInsertProduct.setDate(5, productbatch.getProductionDate());
            pstmtInsertProduct.setInt(6, productbatch.getVolume());
            pstmtInsertProduct.setDate(7, productbatch.getExpirationDate());
            pstmtInsertProduct.setString(8, productbatch.getBatchState());
            pstmtInsertProduct.executeUpdate();


            createRelations(productbatch.getCommodityBatches(), productbatch.getProductId());

            conn.commit();
            System.out.println("The product was successfully created.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ProductbatchDTO getProductbatch(int productBatch) {

        ProductbatchDTO productbatchDTO = new ProductbatchDTO();
        UserDAO userDAO = new UserDAO();
        CommoditybatchDAO commoditybatchDAO = new CommoditybatchDAO(userDAO);

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
                    productbatchDTO.setBatchState(IProductDTO.State.valueOf(rs.getString("batch_state")));
                    i++;
                }

                productbatchDTO.getCommodityBatches().add(commoditybatchDAO.getCommodityBatch(rs.getInt("commodity_batch_id")));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return productbatchDTO;
    }

    public void updateProductBatch(ProductbatchDTO productbatch) {
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmtDeleteRelations = conn.prepareStatement(
                    "DELETE FROM productbatch_commodity_relationship WHERE product_batch_id = ?");
            pstmtDeleteRelations.setInt(1, productbatch.getProductId());
            pstmtDeleteRelations.executeUpdate();

            PreparedStatement pstmtUpdateProduct = conn.prepareStatement(
                    "UPDATE productbatch SET name = ?, madeby = ?, recipe = ?, production_date = ?, volume = ?, expiration_date = ?, batch_state = ? WHERE productbatchid = ?");

            pstmtUpdateProduct.setString(1, productbatch.getName());
            pstmtUpdateProduct.setInt(2, productbatch.getMadeBy().getUserId());
            pstmtUpdateProduct.setInt(3, productbatch.getRecipe());
            pstmtUpdateProduct.setDate(4, productbatch.getProductionDate());
            pstmtUpdateProduct.setInt(5, productbatch.getVolume());
            pstmtUpdateProduct.setDate(6, productbatch.getExpirationDate());
            pstmtUpdateProduct.setString(7, productbatch.getBatchState());
            pstmtUpdateProduct.setInt(8, productbatch.getProductId());

            pstmtUpdateProduct.executeUpdate();

            createRelations(productbatch.getCommodityBatches(), productbatch.getProductId());
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createRelations(List<ICommodityBatchDTO> commodityBatchList, int productbatchId) throws SQLException {
        PreparedStatement pstmtInsertCommodityRelation = conn.prepareStatement(
                "INSERT INTO productbatch_commodity_relationship " +
                        "VALUES(?,?)");

        for (ICommodityBatchDTO c : commodityBatchList) {
            pstmtInsertCommodityRelation.setInt(1, productbatchId);
            pstmtInsertCommodityRelation.setInt(2, c.getBatchId());
            pstmtInsertCommodityRelation.executeUpdate();
        }
    }
}