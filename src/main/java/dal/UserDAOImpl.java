package dal;

import dal.dto.IUserDTO;
import dal.dto.UserDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserDAOImpl implements IUserDAO {
    private Connection conn;

    public UserDAOImpl() {
        try {
            conn = createConnection();
        } catch (DALException e) {
            e.printStackTrace();
        }
    }

    public Connection getConn() {
        return conn;
    }

    public Connection createConnection() throws DALException {
        String dataBase = "jdbc:mysql://ec2-52-30-211-3.eu-west-1.compute.amazonaws.com/jekala";
        String user = "jekala";
        String password = "d0czCtqcu5015NhwwP5zl";
        try {
            return DriverManager.getConnection(dataBase, user, password);
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    @Override
    public void createUser(IUserDTO user) throws DALException {
        if (user.getUserId() < 1) {
            System.out.println("Error in userID!");
            return;
        }
        try {
            conn.setAutoCommit(false);
            PreparedStatement pSmtInsertUser = conn.prepareStatement(
                    "INSERT INTO user " +
                            "VALUES(?,?,?,?)");
            pSmtInsertUser.setInt(1, user.getUserId());
            pSmtInsertUser.setString(2, user.getUserName());
            pSmtInsertUser.setString(3, user.getIni());
            Integer adminId;
            if (user.getAdmin() != null) {
                adminId = user.getAdmin().getUserId();
                pSmtInsertUser.setInt(4, adminId);
            } else {
                pSmtInsertUser.setNull(4, Types.INTEGER);

            }
            pSmtInsertUser.executeUpdate();
            setUserRoles(conn, user);

            conn.commit();
            System.out.println("The user was successfully created in the database system");

        } catch (SQLException e) {
            System.out.println("Error! " + e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public IUserDTO getUser(int userId) throws DALException {
        boolean empty = true;
        UserDTO returnUser = new UserDTO();
        try {
            conn = createConnection();
            PreparedStatement pSmtSelectUser = conn.prepareStatement(
                    "SELECT * " +
                            "FROM user " +
                            "WHERE userid = ?");

            pSmtSelectUser.setInt(1, userId);
            ResultSet rs = pSmtSelectUser.executeQuery();
            while (rs.next()) {
                empty = false;
                returnUser.setUserId(rs.getInt(1));
                returnUser.setUserName(rs.getString(2));
                returnUser.setIni(rs.getString(3));
                returnUser.setRoles(getUserRoleList(userId));
            }
            if (empty) {
                System.out.println("No such user in the database!");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error! " + e.getMessage());
        }
        return returnUser;
    }

    @Override
    public List<IUserDTO> getUserList() throws DALException {
        List<IUserDTO> userList = new ArrayList<>();
        try {
            conn = createConnection();
            PreparedStatement pSmtSelectAllTable = conn.prepareStatement(
                    "SELECT * " +
                            "FROM user");
            UserDTO returnUser;
            ResultSet rs = pSmtSelectAllTable.executeQuery();

            while (rs.next()) {
                returnUser = new UserDTO();
                int userIDReturn = rs.getInt("userid");
                returnUser.setUserId(userIDReturn);
                returnUser.setUserName(rs.getString("name"));
                returnUser.setIni(rs.getString("ini"));
                userList.add(returnUser);
            }
            getAllUsersRoles(userList);
        } catch (SQLException e) {
            System.out.println("Error " + e.getMessage());
        }
        return userList;
    }

    @Override
    public void updateUser(IUserDTO user) throws DALException {
        try {
            conn = createConnection();

            if (!peekUser(user.getUserId())) {
                System.out.println("No such user in the database!");
            } else {
                conn.setAutoCommit(false);
                PreparedStatement pSmtUpdateUser = conn.prepareStatement(
                        "UPDATE user " +
                                "SET " +
                                "name = ?, " +
                                "ini = ? " +
                                "WHERE userid = ? ");
                pSmtUpdateUser.setString(1, user.getUserName());
                pSmtUpdateUser.setString(2, user.getIni());
                pSmtUpdateUser.setInt(3, user.getUserId());
                pSmtUpdateUser.executeUpdate();

                roleTransAct(user);
                System.out.println("The user was successfully updated!");
                conn.commit();
            }
        } catch (SQLException e) {
            System.out.println("Error! " + e.getMessage());
        }
    }

    @Override
    public void deleteUser(int userId) throws DALException {
        int result;
        try {
            PreparedStatement pSmtDeleteUser = conn.prepareStatement(
                    "DELETE FROM user " +
                            "WHERE userid = ?");
            pSmtDeleteUser.setInt(1, userId);
            result = pSmtDeleteUser.executeUpdate();
            if (result == 1) {
                System.out.println("The user with user-ID: " + userId + " was successfully deleted from the system.");
            } else {
                System.out.println("Error no such user exists in the database!");
            }
            pSmtDeleteUser.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
    }

    /**
     * Metoden henter en specifik brugers roller og returnerer dem i en liste.
     */
    public List<String> getUserRoleList(int userID) {
        List<String> userRoleList = new ArrayList<>();
        try {
            PreparedStatement pSmtSelectUserRoles = conn.prepareStatement(
                    "SELECT role " +
                            "FROM userrole " +
                            "WHERE userid = ?");

            pSmtSelectUserRoles.setInt(1, userID);
            ResultSet rs = pSmtSelectUserRoles.executeQuery();
            while (rs.next()) {
                userRoleList.add(rs.getString("role"));
            }
            Collections.reverse(userRoleList);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return userRoleList;
    }

    /**
     * Metoden henter og gemmer roller for alle brugere i en liste.
     */
    public void getAllUsersRoles(List<IUserDTO> userDTOList) {

        for (IUserDTO user : userDTOList) {
            List<String> userRoleList = getUserRoleList(user.getUserId());
            user.setRoles(userRoleList);
        }
    }

    /**
     * Metoden opretter og gemmer roller for en bruger i userrole.
     */
    public void setUserRoles(Connection conn, IUserDTO user) throws DALException {
        try {
            conn.setAutoCommit(false);
            PreparedStatement pSmtInsertUserRole = conn.prepareStatement(
                    "INSERT INTO userrole " +
                            "VALUES(?,?)");
            pSmtInsertUserRole.setInt(1, user.getUserId());
            for (int i = 0; i < user.getRoles().size(); i++) {
                String role = user.getRoles().get(i);
                pSmtInsertUserRole.setString(2, role);
                pSmtInsertUserRole.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            System.out.println("Error in creating userrole " + e.getMessage());
        }
    }

    /**
     * Metoden sletter alle roller for en specifik bruger.
     * Dette bruges når brugeren bliver opdateret, for at undgå at
     * brugeren får flere roller end denne bør have.
     */

    public void roleTransAct(IUserDTO user) {
        List<String> newUserRoles = user.getRoles();
        try {
            PreparedStatement deleteRolesFromDB = conn.prepareStatement(
                    "DELETE FROM userrole " +
                            "WHERE userid = ?;");
            deleteRolesFromDB.setInt(1, user.getUserId());

            PreparedStatement insertRolesInDB = conn.prepareStatement(
                    "INSERT INTO userrole " +
                            "VALUES(?,?)");
            insertRolesInDB.setInt(1, user.getUserId());

            conn.setAutoCommit(false);
            deleteRolesFromDB.setInt(1, user.getUserId());
            deleteRolesFromDB.executeUpdate();

            for (String role : newUserRoles) {
                insertRolesInDB.setString(2, role);
                int success = insertRolesInDB.executeUpdate();
                if (success < 1) {
                    conn.rollback();
                }
            }
            conn.commit();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Metoden bruges til at undersøge om der eksisterer en bruger med et
     * specifikt userid i user_table. Den returnerer false, hvis der ikke gør.
     */
    public boolean peekUser(int userID) throws DALException {
        int returnInt = 0;
        try {
            PreparedStatement prep = conn.prepareStatement(
                    "SELECT COUNT(*) AS uservalidity " +
                            "FROM user " +
                            "WHERE userid = ?");
            prep.setInt(1, userID);

            ResultSet rs = prep.executeQuery();
            while (rs.next()) {
                returnInt = rs.getInt("uservalidity");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (returnInt == 0) {
            return false;
        } else {
            return true;
        }
    }

    public void initializeDataBase() {
        try {
            conn.setAutoCommit(false);
            PreparedStatement createTableUser = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS user " +
                            "(userid INT, " +
                            "name VARCHAR(30) NOT NULL, " +
                            "ini VARCHAR(5), " +
                            "admin INT NULL, " +
                            "PRIMARY KEY (userid), " +
                            "FOREIGN KEY (admin) " +
                            "REFERENCES user (userid));");

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
                            "ingredientid INT, " +
                            "amountmg FLOAT, " +
                            "PRIMARY KEY (ingredientlistid, ingredientid));");

            PreparedStatement createTableRecipe = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS recipe " +
                            "(recipeid INT," +
                            "name VARCHAR(50), " +
                            "madeby INT, " +
                            "ingredientlistidid INT, " +
                            "PRIMARY KEY (recipeid), " +
                            "FOREIGN KEY (madeby) REFERENCES user (userid));");

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

            //Nødvendig?
       /*     PreparedStatement createTableCommodityStock = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS commoditystock " +
                            "(commoditybatchid INT, " +
                            "amountinkg INT, " +
                            "expirationdate VARCHAR(60), " +
                            "PRIMARY KEY (commoditybatchid), " +
                            "FOREIGN KEY (commoditybatchid) REFERENCES commoditybatch (commoditybatchid) " +
                            "ON DELETE CASCADE);");*/

            PreparedStatement createTableProduct = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS product " +
                            "(productid INT, " +
                            "name VARCHAR(50) NOT NULL, " +
                            "madeby INT, " +
                            "recipe INT, " +
                            "PRIMARY KEY (productid), " +
                            "FOREIGN KEY (recipe) " +
                            "REFERENCES recipe(recipeid));");

            PreparedStatement createTableOldRecipe = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS oldrecipe " +
                            "(recipeid INT, " +
                            "name VARCHAR(50) NOT NULL, " +
                            "madeby INT, " +
                            "ingredientlist int, " +
                            "outdated VARCHAR(50) NOT NULL, " +
                            "PRIMARY KEY (recipeid), " +
                            "FOREIGN KEY (madeby) " +
                            "REFERENCES user (userid), " +
                            "FOREIGN KEY (recipeid) " +
                            "REFERENCES ingredientlist(ingredientlistid));");

            //rækkefølgen er vigtig!
            createTableUser.execute();
            createTableUserRole.execute();
            createTableingredient.execute();
            createTableingredientlist.execute();
            createTableRecipe.execute();
            createTableCommodityBatch.execute();
            createTableProduct.execute();
            createTableOldRecipe.execute();
            //createTableCommodityStock.execute();

            //createTableProperty.execute();
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dropAllTables(int deleteTable) {
        try {
            PreparedStatement dropTableUser = conn.prepareStatement(
                    "drop table user;");
            PreparedStatement dropTableUserRole = conn.prepareStatement(
                    "drop table userrole;");
            PreparedStatement dropTableIngredientList = conn.prepareStatement(
                    "DROP TABLE ingredientlist;");
            PreparedStatement dropTableIngredient = conn.prepareStatement(
                    "DROP TABLE ingredient;");
            PreparedStatement dropTableRecipe = conn.prepareStatement(
                    "DROP TABLE recipe;");
            PreparedStatement dropTableOldRecipe = conn.prepareStatement(
                    "DROP TABLE oldrecipe;");
            PreparedStatement dropTableProduct = conn.prepareStatement(
                    "DROP TABLE product;");
            PreparedStatement dropTableCommodityBatch = conn.prepareStatement(
                    "DROP TABLE commoditybatch;");
            PreparedStatement dropTableCommodityStock = conn.prepareStatement(
                    "DROP TABLE commoditystock;");

            if (deleteTable == 0) {
                dropTableCommodityStock.execute();
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
