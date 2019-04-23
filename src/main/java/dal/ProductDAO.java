package dal;

import dal.dto.*;

import java.sql.*;

public class ProductDAO {
    private Connection conn;
    private UserDAO userDAO;
    private ConnectionDAO connectionDAO;

    public ProductDAO(ConnectionDAO connectionDAO) {
        this.connectionDAO = connectionDAO;
/*
        userDAO = new UserDAO();
*/
        conn = connectionDAO.getConn();
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

    public void createProduct(ProductDTO product) {
        //kontroller om han er aktiv i systemet
        if (!product.getMadeBy().getRoles().contains("productionleader") || !product.getMadeBy().getIsActive()) {
            System.out.println("User not authorized to proceed!");
            return;
        }
        try {
            conn.setAutoCommit(false);


            conn.commit();
            System.out.println("The product was successfully created.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}