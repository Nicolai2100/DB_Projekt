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

        try {
            PreparedStatement pstmtSelectProductBatch = conn.prepareStatement(
                    "SELCET * FROM productbatch " +
                            "WHERE productbatchid = ?");


            pstmtSelectProductBatch.setInt(1, productBatch);
            ResultSet rs = pstmtSelectProductBatch.getResultSet();
            while (rs.next()) {
                productbatchDTO.setProductId(productBatch);
                productbatchDTO.setName(rs.getString("name"));
                productbatchDTO.setRecipe(rs.getInt("recipe"));
                productbatchDTO.setMadeBy((UserDTO) userDAO.getUser(rs.getInt("madeby")));
                productbatchDTO.setProductionDate(rs.getDate("production_date"));
                productbatchDTO.setExpirationDate(rs.getDate("expiration_date"));
                productbatchDTO.setVolume(rs.getInt("volume"));
                productbatchDTO.setBatchState(IProductDTO.State.valueOf(rs.getString("batch_state")));

            }



        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}