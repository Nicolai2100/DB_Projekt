package dal;

import dal.dto.*;

import java.sql.*;

public class ConnectionDAO implements IConnectionDAO {
    private static Connection conn;
    private UserDAO userDAO;

    public ConnectionDAO() throws DALException {
        userDAO = new UserDAO();
    }

    public static Connection getConnection() throws DALException {
        try {
            if (conn == null || conn.isClosed()) {
                String dataBase = "jdbc:mysql://ec2-52-30-211-3.eu-west-1.compute.amazonaws.com/jekala";
                String user = "jekala";
                String password = "d0czCtqcu5015NhwwP5zl";
                conn = DriverManager.getConnection(dataBase, user, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException("An error occurred in the database at ConnectionDAO.");
        }
        return conn;
    }

    @Override
    public void initializeDataBase() throws DALException {
        try {
            conn.setAutoCommit(false);
            PreparedStatement createTableUser = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS user " +
                            "(userid INT, " +
                            "name VARCHAR(30) NOT NULL, " +
                            "ini VARCHAR(5)," +
                            "active BIT, " +
                            "admin INT NULL, " +
                            "PRIMARY KEY (userid), " +
                            "FOREIGN KEY (admin) " +
                            "REFERENCES user (userid)" +
                            "ON DELETE CASCADE);");

            PreparedStatement createTableUserRole = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS userrole " +
                            "(userid int, " +
                            "role VARCHAR(30), " +
                            "PRIMARY KEY (userid, role), " +
                            "FOREIGN KEY (userid) REFERENCES user (userid) " +
                            "ON DELETE CASCADE);");

            PreparedStatement createTableingredient = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS ingredient " +
                            "(ingredientid INT, " +
                            "name VARCHAR(50), " +
                            "type VARCHAR(15), " +
                            "minamountinmg INT, " +
                            "reorder BIT, " +
                            "PRIMARY KEY (ingredientid));");

            PreparedStatement createTableingredientlist = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS ingredientlist " +
                            "(ingredientlistid INT, " +
                            "version INT, " +
                            "ingredientid INT, " +
                            "amountmg FLOAT, " +
                            "PRIMARY KEY (ingredientlistid, version, ingredientid), " +
                            "FOREIGN KEY (ingredientid) " +
                            "REFERENCES ingredient (ingredientid));");

            PreparedStatement createTableRecipe = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS recipe " +
                            "(recipeid INT, " +
                            "version INT, " +
                            "name VARCHAR(50), " +
                            "madeby INT, " +
                            "ingredientlistid INT, " +
                            "in_use BIT, " +
                            "last_used_date DATETIME, " +
                            "minbatchsize int, " +
                            "expiration int, " +
                            "PRIMARY KEY (recipeid, version), " +
                            "FOREIGN KEY (ingredientlistid) " +
                            "REFERENCES ingredientlist (ingredientlistid), " +
                            "FOREIGN KEY (madeby) " +
                            "REFERENCES user (userid));");

            PreparedStatement createTableCommodityBatch = conn.prepareStatement(
                    "CREATE TABLE if NOT EXISTS commoditybatch " +
                            "(commoditybatchid INT, " +
                            "ingredientid INT, " +
                            "orderedby INT, " +
                            "amountinkg FLOAT, " +
                            "orderdate VARCHAR(50), " +
                            "residue BIT, " +
                            "PRIMARY KEY (commoditybatchid), " +
                            "FOREIGN KEY (orderedby) " +
                            "REFERENCES user (userid), " +
                            "FOREIGN KEY (ingredientid) " +
                            "REFERENCES ingredient(ingredientid));");

            PreparedStatement createTableProductBatch = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS productbatch " +
                            "(productbatchid INT, " +
                            "name VARCHAR(50) NOT NULL, " +
                            "madeby INT, " +
                            "recipe INT, " +
                            "recipeversion INT, " +
                            "production_date DATE, " +
                            "volume INT, " +
                            "expiration_date DATE, " +
                            "batch_state VARCHAR(20), " +
                            "producedby INT, " +
                            "PRIMARY KEY (productbatchid), " +
                            "FOREIGN KEY (recipe) " +
                            "REFERENCES recipe (recipeid));");

            PreparedStatement createTableProductbatchCommodityRelationship = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS productbatch_commodity_relationship " +
                            "(product_batch_id INT, " +
                            "commodity_batch_id INT, " +
                            "PRIMARY KEY (product_batch_id, commodity_batch_id), " +
                            "FOREIGN KEY (product_batch_id) " +
                            "REFERENCES productbatch(productbatchid)" +
                            "ON DELETE CASCADE, " +
                            "FOREIGN KEY (commodity_batch_id) " +
                            "REFERENCES commoditybatch(commoditybatchid) " +
                            "ON DELETE CASCADE);");

            //rækkefølgen er vigtig!
            createTableUser.execute();
            createTableUserRole.execute();
            createTableingredient.execute();
            createTableingredientlist.execute();
            createTableRecipe.execute();
            createTableCommodityBatch.execute();
            createTableProductBatch.execute();
            createTableProductbatchCommodityRelationship.execute();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException("An error occurred in the database at ConnectionDAO.");
        }
    }

    @Override
    public void closeConn() throws DALException {
        try {
            conn.close();
        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at ConnectionDAO.");
        }
    }

    @Override
    public void deleteTables() throws DALException {
        try {
            PreparedStatement pstmtDeleteProductbatchCommodityRelation = conn.prepareStatement("delete from productbatch_commodity_relationship;");
            PreparedStatement pstmtDeleteCommodityBatch = conn.prepareStatement("delete from commoditybatch;");
            PreparedStatement pstmtDeleteProductbatch = conn.prepareStatement("delete from productbatch;");
            PreparedStatement pstmtDeleteRecipe = conn.prepareStatement("delete from recipe;");
            PreparedStatement pstmtDeleteIngredientLists = conn.prepareStatement("delete from ingredientlist;");
            PreparedStatement pstmtDeleteIngredients = conn.prepareStatement("delete from ingredient;");
            PreparedStatement pstmtDeleteUsers = conn.prepareStatement("delete from user;");
            pstmtDeleteProductbatchCommodityRelation.execute();
            pstmtDeleteProductbatch.execute();
            pstmtDeleteCommodityBatch.execute();
            pstmtDeleteRecipe.execute();
            pstmtDeleteIngredientLists.execute();
            pstmtDeleteIngredients.execute();
            deleteUsers();
            pstmtDeleteUsers.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException("An error occurred in the database at ConnectionDAO.");
        }
    }

    @Override
    public void deleteUsers() throws DALException {
        String deleteUserString = "DELETE FROM user WHERE userid = ?;";
        try {
            PreparedStatement deleteNonAdmins = conn.prepareStatement(deleteUserString);

            for (IUserDTO user : userDAO.getUserList()) {
                if (user.getUserId() != user.getAdmin().getUserId()) {
                    deleteNonAdmins.setInt(1, user.getUserId());
                    deleteNonAdmins.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at ConnectionDAO.");
        }
    }

    public void dropAllTables(int deleteTable) throws DALException {
        try {
            PreparedStatement dropTableUser = conn.prepareStatement(
                    "DROP table IF EXISTS user;");
            PreparedStatement dropTableUserRole = conn.prepareStatement(
                    "DROP table IF EXISTS userrole;");
            PreparedStatement dropTableIngredientList = conn.prepareStatement(
                    "DROP TABLE IF EXISTS ingredientlist;");
            PreparedStatement dropTableIngredient = conn.prepareStatement(
                    "DROP TABLE IF EXISTS ingredient;");
            PreparedStatement dropTableRecipe = conn.prepareStatement(
                    "DROP TABLE IF EXISTS recipe;");
            PreparedStatement dropTableProductbatch = conn.prepareStatement(
                    "DROP TABLE IF EXISTS productbatch;");
            PreparedStatement dropTableCommodityBatch = conn.prepareStatement(
                    "DROP TABLE IF EXISTS commoditybatch;");
            PreparedStatement dropTableProductbatchCommodityRelation = conn.prepareStatement(
                    "DROP TABLE IF EXISTS productbatch_commodity_relationship;");

            if (deleteTable == 0) {
                dropTableProductbatchCommodityRelation.execute();
                dropTableProductbatch.execute();
                dropTableCommodityBatch.execute();
                dropTableRecipe.execute();
                dropTableIngredientList.execute();
                dropTableIngredient.execute();
                dropTableUserRole.execute();
                dropTableUser.execute();
            }
        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at ConnectionDAO.");
        }
    }
}