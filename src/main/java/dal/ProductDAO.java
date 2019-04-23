package dal;

import dal.dto.*;

import java.sql.*;
import java.util.*;

public class ProductDAO {
    private Connection conn;
    private UserDAOImpl userDAO;

    public ProductDAO() {
        userDAO = new UserDAOImpl();
        try {
            conn = createConnection();
        } catch (IUserDAO.DALException e) {
            e.printStackTrace();
        }

    }

    public Connection getConn() {
        return conn;
    }

    public Connection createConnection() throws IUserDAO.DALException {
        String dataBase = "jdbc:mysql://ec2-52-30-211-3.eu-west-1.compute.amazonaws.com/jekala";
        String user = "jekala";
        String password = "d0czCtqcu5015NhwwP5zl";
        try {
            return DriverManager.getConnection(dataBase, user, password);
        } catch (SQLException e) {
            throw new IUserDAO.DALException(e.getMessage());
        }
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