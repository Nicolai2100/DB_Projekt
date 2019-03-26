package dal;

import dto.UserDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImplTest {
    UserDAOImpl dbCon;
    List<String> roller;
    Connection connection;

    @Before
    public void initialize() throws IUserDAO.DALException {
        dbCon = new UserDAOImpl();
        roller = new ArrayList<>();
        try {
            connection = DriverManager.getConnection("jdbc:mysql://ec2-52-30-211-3.eu-west-1.compute.amazonaws.com/s185020",
                    "s185020", "iEFSqK2BFP60YWMPlw77I");
        } catch (SQLException e) {
            throw new IUserDAO.DALException(e.getMessage());

        }
    }

    @org.junit.Test
    public void deleteAllRoles(){
        UserDTO user = new UserDTO();
        user.setUserId(13);
        dbCon.deleteAllRoles(connection, user );
    }
    @org.junit.Test
    public void getUser() throws IUserDAO.DALException, SQLException {
        System.out.println(dbCon.getUser(2));
    }

    @org.junit.Test
    public void createUser() throws IUserDAO.DALException {

        UserDTO user = new UserDTO();
        user.setUserId(2);

        user.setRoles(roller);
        dbCon.createUser(user);
    }

    @Test
    public void getRoleID() throws IUserDAO.DALException {


    }

    @Test
    public void createNewRole() throws IUserDAO.DALException {
    }

    @Test
    public void getMaxFromRoleTable(){
        System.out.println(dbCon.getMaxFromRoleTable(connection));
    }

    @org.junit.Test
    public void getUserList() throws IUserDAO.DALException {
        List<UserDTO> users = dbCon.getUserList();
        for (UserDTO user : users) {
            System.out.println(user);
        }
        System.out.println(users.size());
    }

    @org.junit.Test
    public void getUserRoleList() throws IUserDAO.DALException {
        List<String> roller = dbCon.getUserRoleList(connection,1);

        for (String rolle : roller) {
            System.out.println(rolle);
        }
        System.out.println(roller.size());
    }

    @org.junit.Test
    public void getAllUserRoles() throws IUserDAO.DALException {
        List<UserDTO> userListen = dbCon.getUserList();
        dbCon.getAllUsersRoles(connection, userListen);
        for (UserDTO user : userListen) {
            System.out.println(user);
        }
    }


    @org.junit.Test
    public void updateUser() throws IUserDAO.DALException, SQLException {
        System.out.println(dbCon.getUser(1));
        UserDTO user = new UserDTO();
        user.setUserId(1);
        user.setUserName("Lars Barfoed");
        roller.add("grøffer");
        user.setRoles(roller);
        dbCon.updateUser(user);
        System.out.println(dbCon.getUser(1));
    }

    @org.junit.Test
    public void deleteUser() throws IUserDAO.DALException {

    }

    @After
    public void closeConn() {
    }

    @Test
    public void userRoleInt() {
/*
        int tal = dbCon.userRoleInt("forsyner");
        System.out.println(tal);
*/
    }

    @Test
    public void setUserRoles() throws IUserDAO.DALException, SQLException {
        UserDTO user3 = dbCon.getUser(1);
        List<String> roles = new ArrayList<>();
        roles.add("sergeant");
        roles.add("forsyner");
        user3.setRoles(roles);
        dbCon.setUserRoles(connection, user3);
    }

    @Test
    public void peekUser() {
    }

/*
    @org.junit.Test
    public void getNumOfUserRoles() {
        int num = dbCon.getNumOfUserRoles(1);
        System.out.println(num);
    }
*/
}

/* public void createUserTable() throws SQLException, DALException {
        Connection conn = createConnection();
        Statement stmt = conn.createStatement();
        String sqlStmt = "CREATE TABLE IF NOT EXISTS user_table" +
                "(userID INT,"+
                "username VARCHAR(255),"+
                "ini VARCHAR(255), "+
                "numOfRoles INT, "+
                "PRIMARY KEY (userID))";
        stmt.executeUpdate(sqlStmt);
        conn.close();
    }
    public void createRoleTable() throws SQLException, DALException {
        Connection conn = createConnection();
        Statement stmt = conn.createStatement();
        String sqlStmt = "CREATE TABLE IF NOT EXISTS user_roles" +
                "(userID INT,"+
                "role VARCHAR(255)";
        stmt.executeUpdate(sqlStmt);
        conn.close();
    }*/

