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
                            "(user_id INT, " +
                            "name VARCHAR(30) NOT NULL, " +
                            "initials VARCHAR(5)," +
                            "active_status BIT, " +
                            "administrator_id INT NULL, " +
                            "PRIMARY KEY (user_id), " +
                            "FOREIGN KEY (administrator_id) " +
                            "REFERENCES user (user_id)" +
                            "ON DELETE CASCADE);");

            PreparedStatement createTableUserRole = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS userrole " +
                            "(user_id int, " +
                            "role VARCHAR(30), " +
                            "PRIMARY KEY (user_id, role), " +
                            "FOREIGN KEY (user_id) REFERENCES user (user_id) " +
                            "ON DELETE CASCADE);");

            PreparedStatement createTableingredient = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS ingredient " +
                            "(ingredient_id INT, " +
                            "name VARCHAR(50), " +
                            "type VARCHAR(15), " +
                            "min_amount_mg INT, " +
                            "reorder_status BIT, " +
                            "PRIMARY KEY (ingredient_id));");

            PreparedStatement createTableingredientlist = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS ingredientlist " +
                            "(ingredientlist_id INT, " +
                            "version_id INT, " +
                            "ingredient_id INT, " +
                            "amount_mg FLOAT, " +
                            "PRIMARY KEY (ingredientlist_id, version_id, ingredient_id), " +
                            "FOREIGN KEY (ingredient_id) " +
                            "REFERENCES ingredient (ingredient_id));");

            PreparedStatement createTableRecipe = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS recipe " +
                            "(recipe_id INT, " +
                            "version_id INT, " +
                            "name VARCHAR(50), " +
                            "creator_id INT, " +
                            "ingredientlist_id INT, " +
                            "in_use BIT, " +
                            "last_used_date DATETIME, " +
                            "min_batch_size int, " +
                            "expiration int, " +
                            "PRIMARY KEY (recipe_id, version_id), " +
                            "FOREIGN KEY (ingredientlist_id) " +
                            "REFERENCES ingredientlist (ingredientlist_id), " +
                            "FOREIGN KEY (creator_id) " +
                            "REFERENCES user (user_id));");

            PreparedStatement createTableCommodityBatch = conn.prepareStatement(
                    "CREATE TABLE if NOT EXISTS commoditybatch " +
                            "(commoditybatch_id INT, " +
                            "ingredient_id INT, " +
                            "orderer_id INT, " +
                            "amount_kg FLOAT, " +
                            "order_date VARCHAR(50), " +
                            "residue_status BIT, " +
                            "PRIMARY KEY (commoditybatch_id), " +
                            "FOREIGN KEY (orderer_id) " +
                            "REFERENCES user (user_id), " +
                            "FOREIGN KEY (ingredient_id) " +
                            "REFERENCES ingredient(ingredient_id));");

            PreparedStatement createTableProductBatch = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS productbatch " +
                            "(productbatch_id INT, " +
                            "name VARCHAR(50) NOT NULL, " +
                            "recipe_id INT, " +
                            "recipe_version INT, " +
                            "volume INT, " +
                            "production_date DATE, " +
                            "expiration_date DATE, " +
                            "batch_state VARCHAR(20), " +
                            "orderer_id INT, " +
                            "producer_id INT, " +
                            "PRIMARY KEY (productbatch_id), " +
                            "FOREIGN KEY (recipe_id) " +
                            "REFERENCES recipe (recipe_id), " +
                            "FOREIGN KEY(orderer_id) " +
                            "REFERENCES user (user_id));");

            PreparedStatement createTableProductbatchCommodityRelationship = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS productbatch_commodity_relationship " +
                            "(product_batch_id INT, " +
                            "commodity_batch_id INT, " +
                            "PRIMARY KEY (product_batch_id, commodity_batch_id), " +
                            "FOREIGN KEY (product_batch_id) " +
                            "REFERENCES productbatch(productbatch_id)" +
                            "ON DELETE CASCADE, " +
                            "FOREIGN KEY (commodity_batch_id) " +
                            "REFERENCES commoditybatch(commoditybatch_id) " +
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
            PreparedStatement pstmtRemoveConstraints = conn.prepareStatement("SET FOREIGN_KEY_CHECKS=0;");
            PreparedStatement pstmtReplaceConstraints = conn.prepareStatement("SET FOREIGN_KEY_CHECKS=1;");
            PreparedStatement pstmtDeleteProductbatchCommodityRelation = conn.prepareStatement("delete from productbatch_commodity_relationship;");
            PreparedStatement pstmtDeleteCommodityBatch = conn.prepareStatement("delete from commoditybatch;");
            PreparedStatement pstmtDeleteProductbatch = conn.prepareStatement("delete from productbatch;");
            PreparedStatement pstmtDeleteRecipe = conn.prepareStatement("delete from recipe;");
            PreparedStatement pstmtDeleteIngredientLists = conn.prepareStatement("delete from ingredientlist;");
            PreparedStatement pstmtDeleteIngredients = conn.prepareStatement("delete from ingredient;");
            PreparedStatement pstmtDeleteUsers = conn.prepareStatement("delete from user;");
            PreparedStatement pstmtDeleteUserRoles = conn.prepareStatement("delete from userrole;");
            pstmtRemoveConstraints.execute();
            pstmtDeleteProductbatchCommodityRelation.execute();
            pstmtDeleteProductbatch.execute();
            pstmtDeleteCommodityBatch.execute();
            pstmtDeleteRecipe.execute();
            pstmtDeleteIngredientLists.execute();
            pstmtDeleteIngredients.execute();
            deleteUsers();
            pstmtDeleteUsers.execute();
            pstmtDeleteUserRoles.execute();
            pstmtReplaceConstraints.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException("An error occurred in the database at ConnectionDAO.");
        }
    }

    @Override
    public void deleteUsers() throws DALException {
        String deleteUserString = "DELETE FROM user WHERE user_id = ?;";
        try {
            PreparedStatement deleteNonAdmins = conn.prepareStatement(deleteUserString);

            for (IUserDTO user : userDAO.getUserList()) {
                if (user.getUserId() != user.getAdmin().getUserId()) {
                    deleteNonAdmins.setInt(1, user.getUserId());
                    deleteNonAdmins.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException("An error occurred in the database at ConnectionDAO.");
        }
    }

    //@Override
    public void createTriggers() throws DALException {
        createTriggerReorderInsertCom();
        createTriggerReorderUpdateCom();
    }

    public void createTriggerReorderUpdateCom() throws DALException {
        try {
            String createTrigReorderString =
                    "CREATE TRIGGER set_reorder_afterupdatecombatch AFTER UPDATE ON commoditybatch " +
                            "FOR EACH ROW BEGIN " +
                            "DECLARE needamount float; " +
                            "DECLARE haveamount float; " +
                            "SET needamount = (select min_amount_mg from ingredient " +
                            "    where ingredient.ingredient_id = new.ingredient_id); " +
                            "SET haveamount = (select max(amount_kg) * 1000000 from commoditybatch " +
                            "    join ingredient on ingredient.ingredient_id = commoditybatch.ingredient_id " +
                            "    where commoditybatch.residue = 0 and new.ingredient_id = ingredient.ingredient_id); " +
                            "IF needamount > haveamount THEN " +
                            "UPDATE ingredient SET ingredient.reorder_status = 1 " +
                            "WHERE new.ingredient_id = ingredient.ingredient_id; " +
                            "ELSE " +
                            "UPDATE ingredient SET ingredient.reorder_status = 0 " +
                            "WHERE new.ingredient_id =  ingredient.ingredient_id; " +
                            "END IF; " +
                            "END;";
            PreparedStatement pstmtCreateTriggerReorder = conn.prepareStatement(createTrigReorderString);
            pstmtCreateTriggerReorder.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException("An error occurred in the database at ConnectionDAO.");
        }
    }

    public void createTriggerReorderInsertCom() throws DALException {
        try {
            String createTrigReorderString =
                    "CREATE TRIGGER set_reorder_afterinsertcombatch AFTER INSERT ON commoditybatch " +
                            "FOR EACH ROW BEGIN " +
                            "DECLARE needamount float; " +
                            "DECLARE haveamount float; " +
                            "SET needamount = (select min_amount_mg from ingredient " +
                            "    where ingredient.ingredient_id = new.ingredient_id); " +
                            "SET haveamount = (select max(amount_kg) * 1000000 from commoditybatch " +
                            "    join ingredient on ingredient.ingredient_id = commoditybatch.ingredient_id " +
                            "    where commoditybatch.residue_status = 0 and new.ingredient_id = ingredient.ingredient_id); " +
                            "IF needamount > haveamount THEN " +
                            "UPDATE ingredient SET ingredient.reorder = 1 " +
                            "WHERE new.ingredient_id = ingredient.ingredient_id; " +
                            "ELSE " +
                            "UPDATE ingredient SET ingredient.reorder_status = 0 " +
                            "WHERE new.ingredient_id =  ingredient.ingredient_id; " +
                            "END IF; " +
                            "END;";
            PreparedStatement pstmtCreateTriggerReorder = conn.prepareStatement(createTrigReorderString);
            pstmtCreateTriggerReorder.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException("An error occurred in the database at ConnectionDAO.");
        }
    }

    public void dropAllTables(int deleteTable) throws DALException {
        try {
            PreparedStatement pstmtRemoveConstraints = conn.prepareStatement("SET FOREIGN_KEY_CHECKS=0;");
            PreparedStatement pstmtReplaceConstraints = conn.prepareStatement("SET FOREIGN_KEY_CHECKS=1;");
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
                pstmtRemoveConstraints.execute();
                dropTableProductbatchCommodityRelation.execute();
                dropTableProductbatch.execute();
                dropTableCommodityBatch.execute();
                dropTableRecipe.execute();
                dropTableIngredientList.execute();
                dropTableIngredient.execute();
                dropTableUserRole.execute();
                dropTableUser.execute();
                pstmtReplaceConstraints.execute();
            }
        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at ConnectionDAO.");
        }
    }
}