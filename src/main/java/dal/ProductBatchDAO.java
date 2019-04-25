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

    //todo
    // Vi kunne godt tænke os at det er muligt at udsøge produktbatches,
    // der er hhv. bestilt, under produktion og færdiggjort.

    public void createProduct(ProductbatchDTO product) {
        //kontroller om han er aktiv i systemet
        if (!product.getMadeBy().getRoles().contains("productionleader") || !product.getMadeBy().getIsActive()) {
            System.out.println("User not authorized to proceed!");
            return;
        }
        try {
            conn.setAutoCommit(false);

            PreparedStatement pstmtInsertProduct = conn.prepareStatement(
                    "INSERT INTO product " +
                            "VALUES(?,?,?,?,?,?,?)");

            PreparedStatement pstmtInsertCommodityRelation = conn.prepareStatement(
                    "INSERT INTO product_commodity_relationship " +
                            "VALUES(?,?)");

            pstmtInsertProduct.setInt(1, product.getProductId());
            pstmtInsertProduct.setString(2, product.getName());
            pstmtInsertProduct.setInt(3, product.getMadeBy().getUserId());
            pstmtInsertProduct.setInt(4, product.getRecipe());
            pstmtInsertProduct.setDate(5, product.getProductionDate());
            pstmtInsertProduct.setInt(6, product.getVolume());
            pstmtInsertProduct.setDate(7, product.getExpirationDate());
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
}