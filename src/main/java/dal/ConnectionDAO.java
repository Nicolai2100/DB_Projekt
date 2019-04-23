package dal;

import dal.dto.*;

import java.sql.*;

public class ConnectionDAO {
    private Connection conn;
    private UserDAO userDAO;

    public ConnectionDAO() {
        userDAO = new UserDAO(this);
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

    public void createTriggerReorder() {
        try {
            PreparedStatement pstmtCreateTriggerReorder = conn.prepareStatement(
                    "CREATE TRIGGER set_reorder " +
                            "AFTER INSERT ON commoditybatch " +
                            "FOR EACH ROW " +
                            "BEGIN " +
                            "UPDATE ingredient " +
                            "SET ingredient.reorder = 0 " +
                            "WHERE ingredient.ingredientid = new.ingredientid; " +
                            "END;");

            pstmtCreateTriggerReorder.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTriggerOldRecipe() {
        try {
            PreparedStatement pstmtCreateTriggerSaveDeletedRecipe = conn.prepareStatement(
                    "CREATE TRIGGER save_recipe_delete " +
                            "BEFORE DELETE ON recipe " +
                            "FOR EACH ROW " +
                            "BEGIN " +
                            "INSERT INTO oldrecipe " +
                            "VALUES " +
                            "(old.recipeid,  " +
                            "old.edition, " +
                            "old.name, " +
                            "old.madeby, " +
                            "old.ingredientlistid, " +
                            "NOW()); " +
                            "END;");

            PreparedStatement pstmtCreateTriggerSaveUpdatedRecipe = conn.prepareStatement(
                    "CREATE TRIGGER save_recipe_update " +
                            "BEFORE UPDATE ON recipe " +
                            "FOR EACH ROW " +
                            "BEGIN " +
                            "INSERT INTO oldrecipe " +
                            "VALUES " +
                            "(old.recipeid,  " +
                            "old.edition, " +
                            "old.name, " +
                            "old.madeby, " +
                            "old.ingredientlistid, " +
                            "NOW()); " +
                            "END;");

            pstmtCreateTriggerSaveUpdatedRecipe.execute();
            pstmtCreateTriggerSaveDeletedRecipe.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dropTriggers() {
        try {

            PreparedStatement pstmtDropTriggerReorder = conn.prepareStatement(
                    "DROP TRIGGER IF EXISTS set_reorder;");

            PreparedStatement pstmtDropSaveRecipeDeleteTrigger = conn.prepareStatement(
                    "DROP TRIGGER IF EXISTS save_recipe_delete;");

            PreparedStatement pstmtDropSaveRecipeUpdateTrigger = conn.prepareStatement(
                    "DROP TRIGGER IF EXISTS save_recipe_update"
            );
            pstmtDropSaveRecipeDeleteTrigger.execute();
            pstmtDropSaveRecipeUpdateTrigger.execute();
            pstmtDropTriggerReorder.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cleanTables() {
        try {
            PreparedStatement pstmtDeleteRecipe = conn.prepareStatement("DELETE FROM recipe;");
            PreparedStatement pstmtDeleteOldRecipe = conn.prepareStatement("DELETE FROM oldrecipe;");
            PreparedStatement pstmtDeleteIngredientLists = conn.prepareStatement("DELETE FROM ingredientlist;");
            PreparedStatement pstmtDeleteIngredients = conn.prepareStatement("DELETE FROM ingredient;");
            PreparedStatement pstmtDeleteUsers = conn.prepareStatement("DELETE FROM user;");

            pstmtDeleteRecipe.execute();
            pstmtDeleteOldRecipe.execute();
            pstmtDeleteIngredientLists.execute();
            pstmtDeleteIngredients.execute();

            for (IUserDTO user : userDAO.getUserList()) {
                if (user.getAdmin() != null) ;
                userDAO.deleteUser(user.getUserId());
            }

            pstmtDeleteUsers.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IUserDAO.DALException e) {
            e.printStackTrace();
        }
    }

    public void initializeDataBase() {
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

/*
            Hvis brugere slettes - hvad gør vi så med commodity batch ect?
            PreparedStatement createTableOldUser = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS olduser " +
                            "(userid INT, " +
                            "name VARCHAR(30) NOT NULL, " +
                            "ini VARCHAR(5), " +
                            "PRIMARY KEY (userid), " +
                            "FOREIGN KEY (admin) " +
                            "REFERENCES user (userid));");
*/
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
                            "reorder BIT, " +
                            "PRIMARY KEY (ingredientid));");

            PreparedStatement createTableingredientlist = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS ingredientlist " +
                            "(ingredientlistid INT, " +
                            "edition INT, " +
                            "ingredientid INT, " +
                            "amountmg FLOAT, " +
                            "PRIMARY KEY (ingredientlistid, edition, ingredientid), " +
                            "FOREIGN KEY (ingredientid) " +
                            "REFERENCES ingredient (ingredientid));");

            PreparedStatement createTableRecipe = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS recipe " +
                            "(recipeid INT, " +
                            "edition INT, " +
                            "name VARCHAR(50), " +
                            "madeby INT, " +
                            "ingredientlistid INT, " +
                            "PRIMARY KEY (recipeid), " +
                            "FOREIGN KEY (ingredientlistid) " +
                            "REFERENCES ingredientlist (ingredientlistid), " +
                            "FOREIGN KEY (madeby) " +
                            "REFERENCES user (userid));");

            PreparedStatement createTableOldRecipe = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS oldrecipe " +
                            "(recipeid INT, " +
                            "edition INT, " +
                            "name VARCHAR(50) NOT NULL, " +
                            "madeby INT, " +
                            "ingredientlistid INT, " +
                            "outdated TIMESTAMP NOT NULL, " +
                            "PRIMARY KEY (recipeid, edition), " +
                            "FOREIGN KEY (ingredientlistid) " +
                            "REFERENCES ingredientlist (ingredientlistid) " +
                            "ON DELETE CASCADE, " +
                            "FOREIGN KEY (madeby) " +
                            "REFERENCES user (userid));");

            PreparedStatement createTableCommodityBatch = conn.prepareStatement(
                    "CREATE TABLE if NOT EXISTS commoditybatch " +
                            "(commoditybatchid INT, " +
                            "ingredientid INT, " +
                            "orderedby INT, " +
                            "amountinkg INT, " +
                            "orderdate VARCHAR(25), " +
                            "residue BIT, " +
                            "PRIMARY KEY (commoditybatchid), " +
                            "FOREIGN KEY (orderedby) " +
                            "REFERENCES user (userid), " +
                            "FOREIGN KEY (ingredientid) " +
                            "REFERENCES ingredient(ingredientid));");

            PreparedStatement createTableProduct = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS product " +
                            "(productid INT, " +
                            "name VARCHAR(50) NOT NULL, " +
                            "madeby INT, " +
                            "recipe INT, " +
                            "PRIMARY KEY (productid), " +
                            "FOREIGN KEY (recipe) " +
                            "REFERENCES recipe(recipeid));");


            //rækkefølgen er vigtig!
            createTableUser.execute();
            createTableUserRole.execute();
            createTableingredient.execute();
            createTableingredientlist.execute();
            createTableRecipe.execute();
            createTableCommodityBatch.execute();
            createTableProduct.execute();
            createTableOldRecipe.execute();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dropAllTables(int deleteTable) {
        try {
            PreparedStatement dropTableUser = conn.prepareStatement(
                    "drop table IF EXISTS user;");
            PreparedStatement dropTableUserRole = conn.prepareStatement(
                    "drop table IF EXISTS userrole;");
            PreparedStatement dropTableIngredientList = conn.prepareStatement(
                    "DROP TABLE IF EXISTS ingredientlist;");
            PreparedStatement dropTableIngredient = conn.prepareStatement(
                    "DROP TABLE IF EXISTS ingredient;");
            PreparedStatement dropTableRecipe = conn.prepareStatement(
                    "DROP TABLE IF EXISTS recipe;");
            PreparedStatement dropTableOldRecipe = conn.prepareStatement(
                    "DROP TABLE IF EXISTS oldrecipe;");
            PreparedStatement dropTableProduct = conn.prepareStatement(
                    "DROP TABLE IF EXISTS product;");
            PreparedStatement dropTableCommodityBatch = conn.prepareStatement(
                    "DROP TABLE IF EXISTS commoditybatch;");


            if (deleteTable == 0) {
                dropTableOldRecipe.execute();
                dropTableProduct.execute();
                dropTableCommodityBatch.execute();
                dropTableRecipe.execute();
                dropTableIngredientList.execute();
                dropTableIngredient.execute();
                dropTableUserRole.execute();
                dropTableUser.execute();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}