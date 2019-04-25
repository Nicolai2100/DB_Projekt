package dal;

import dal.dto.*;

import java.sql.*;

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

    public void createProductbatch(ProductbatchDTO product) {
        //kontroller om han er aktiv i systemet
        if (!product.getMadeBy().getRoles().contains("productionleader") || !product.getMadeBy().getIsActive()) {
            System.out.println("User not authorized to proceed!");
            return;
        }
        try {
            conn.setAutoCommit(false);

            PreparedStatement pstmtInsertProduct = conn.prepareStatement(
                    "INSERT INTO productbatch " +
                            "VALUES(?,?,?,?,?,?,?,?)");

            PreparedStatement pstmtInsertCommodityRelation = conn.prepareStatement(
                    "INSERT INTO productbatch_commodity_relationship " +
                            "VALUES(?,?)");

            pstmtInsertProduct.setInt(1, product.getProductId());
            pstmtInsertProduct.setString(2, product.getName());
            pstmtInsertProduct.setInt(3, product.getMadeBy().getUserId());
            pstmtInsertProduct.setInt(4, product.getRecipe());
            pstmtInsertProduct.setDate(5, product.getProductionDate());
            pstmtInsertProduct.setInt(6, product.getVolume());
            pstmtInsertProduct.setDate(7, product.getExpirationDate());
            pstmtInsertProduct.setString(8, product.getBatchState());
            pstmtInsertProduct.executeUpdate();

            for (ICommodityBatchDTO c : product.getCommodityBatches()) {
                pstmtInsertCommodityRelation.setInt(1, product.getProductId());
                pstmtInsertCommodityRelation.setInt(2, c.getBatchId());
                pstmtInsertCommodityRelation.executeUpdate();
            }


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


}