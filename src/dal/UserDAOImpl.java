package dal;

import dto.UserDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements IUserDAO {

    public UserDAOImpl() {
    }
    public Connection createConnection() throws DALException {
        try {
            return DriverManager.getConnection("jdbc:mysql://ec2-52-30-211-3.eu-west-1.compute.amazonaws.com/s185020",
                    "s185020", "iEFSqK2BFP60YWMPlw77I");
        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
    }

    @Override
    public UserDTO getUser(int userId) throws DALException, SQLException {
        boolean empty = true;
        UserDTO returnUser = new UserDTO();
        Connection conn = null;
        try {
            conn = createConnection();
            PreparedStatement pSmtSelectUser = conn.prepareStatement(
                    "SELECT * " +
                            "FROM user_table " +
                            "WHERE userid = ?");

            pSmtSelectUser.setInt(1, userId);
            ResultSet rs = pSmtSelectUser.executeQuery();
            while (rs.next()) {
                empty = false;
                returnUser.setUserId(rs.getInt(1));
                returnUser.setUserName(rs.getString(2));
                returnUser.setIni(rs.getString(3));
                returnUser.setRoles(getUserRoleList(conn, userId));
            }
            if (empty) {
                System.out.println("No such user in the database!");
                return null;
            }
            rs.close();
            pSmtSelectUser.close();

        } catch (SQLException e) {
            System.out.println("Error! " + e.getMessage());
        }finally {
            conn.close();
        }
        return returnUser;
    }

    @Override
    public void createUser(UserDTO user) throws DALException {
        Connection conn = null;
        PreparedStatement pSmtInsertUser = null;
        try {
            conn = createConnection();
            pSmtInsertUser = conn.prepareStatement(
                    "INSERT INTO user_table " +
                            "VALUES(?,?,?)");

            pSmtInsertUser.setInt(1, user.getUserId());
        } catch (Exception e) {
            System.out.println("The user cannot be created without a valid ID");
        }
        try {
            pSmtInsertUser.setString(2, user.getUserName());
            pSmtInsertUser.setString(3, user.getIni());

            // Bør gøres atomic - da bruger ellers oprettes uden roller!!!
            pSmtInsertUser.executeUpdate();
            //Først oprettes brugeren - så indsættes rollerne.
            setUserRoles(conn, user);
            System.out.println("The user was successfully created in the database system");

        } catch (SQLException e) {
            System.out.println("Error! " + e.getMessage());
        }
    }

    @Override
    public List<UserDTO> getUserList() throws DALException {
        List<UserDTO> userList = new ArrayList<>();
        Connection conn = null;

        try {
            conn = createConnection();
            PreparedStatement pSmtSelectAllTable = conn.prepareStatement(
                    "SELECT * " +
                            "FROM user_table");
            UserDTO returnUser;
            ResultSet rs = pSmtSelectAllTable.executeQuery();

            while (rs.next()) {
                returnUser = new UserDTO();
                int userIDReturn = rs.getInt("userID");
                returnUser.setUserId(userIDReturn);
                returnUser.setUserName(rs.getString("username"));
                returnUser.setIni(rs.getString("ini"));
                userList.add(returnUser);
            }
        } catch (SQLException e) {
            System.out.println("Error " + e.getMessage());
        } finally {
            //Først hentes brugerne ned, så hentes deres roller og tilføjes hver bruger.
            getAllUsersRoles(conn, userList);
        }
        return userList;
    }

    @Override
    public void updateUser(UserDTO user) throws DALException {
        try {
            Connection conn = createConnection();
            if (!peekUser(conn, user.getUserId())) {
                System.out.println("No such user in the database!");
            } else {
                PreparedStatement pSmtUpdateUser = conn.prepareStatement(
                        "UPDATE user_table " +
                                "SET " +
                                "username = ?, " +
                                "ini = ? " +
                                "WHERE userid = ? ");
                pSmtUpdateUser.setString(1, user.getUserName());
                pSmtUpdateUser.setString(2, user.getIni());
                pSmtUpdateUser.setInt(3, user.getUserId());
                pSmtUpdateUser.executeUpdate();
                //Hvis brugeren har fået nye roller oprettes de - men sletter ikke, hvis han har fået dem fjernet!
                setUserRoles(conn, user);
                System.out.println("The user was successfully updated!");
            }
        } catch (SQLException e) {
            System.out.println("Error! " + e.getMessage());
        }
    }

    @Override
    public void deleteUser(int userId) throws DALException {
        int result = 0;
        try {
            Connection conn = createConnection();
            PreparedStatement pSmtDeleteUser = conn.prepareStatement(
                    "DELETE FROM user_table " +
                            "WHERE userid = ?");

            pSmtDeleteUser.setInt(1, userId);
            result = pSmtDeleteUser.executeUpdate();
            if (result == 1) {
                System.out.println("The user with user-ID: " + userId + " was successfully deleted from the system.");
            } else {
                System.out.println("Error no such user exists in the database!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<String> getUserRoleList(Connection conn, int userID) throws DALException {
        List<String> userRoleList = new ArrayList<>();
        try {
            PreparedStatement pSmtSelectUserRoles = conn.prepareStatement(
                    "SELECT role_table.role " +
                            "FROM (user_roles " +
                            "JOIN role_table ON user_roles.roleid = role_table.roleid)" +
                            "WHERE userid = ?");

            pSmtSelectUserRoles.setInt(1, userID);
            ResultSet rs = pSmtSelectUserRoles.executeQuery();
            while (rs.next()) {
                userRoleList.add(rs.getString("role"));
            }

            pSmtSelectUserRoles.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return userRoleList;
    }

    public void getAllUsersRoles(Connection conn, List<UserDTO> userDTOList) throws DALException {

        for (UserDTO user : userDTOList) {
            List<String> userRoleList = getUserRoleList(conn, user.getUserId());
            user.setRoles(userRoleList);
        }
    }

    public void setUserRoles(Connection conn, UserDTO user) throws DALException {
        try {
            PreparedStatement pSmtInsertUserRole = conn.prepareStatement(
                    "INSERT INTO user_roles " +
                            "VALUES(?,?)");

            pSmtInsertUserRole.setInt(1, user.getUserId());

            for (int i = 0; i < user.getRoles().size(); i++) {
                String role = user.getRoles().get(i);
                int roleInt = getRoleID(conn, role);
                pSmtInsertUserRole.setInt(2, roleInt);
                pSmtInsertUserRole.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error in user_roles " + e.getMessage());
        }
    }

    public int getRoleID(Connection conn, String role) throws DALException {
        int returnInt = 0;
        try {
            conn = createConnection();
            PreparedStatement pStmtSelectRoleId = conn.prepareStatement(
                    "SELECT  roleid  " +
                            "FROM role_table " +
                            "WHERE role = ?");
            pStmtSelectRoleId.setString(1, role);

            ResultSet rs = pStmtSelectRoleId.executeQuery();
            if (rs.next()) {
                returnInt = rs.getInt("roleid");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if (returnInt == 0) {
            // Hvis den returnerer 0 betyder det at rollen ikke findes, og derfor skal den oprettes.
            returnInt = createNewRole(conn, role);
        }
        return returnInt;
    }

    public int createNewRole(Connection conn, String userRole) {
        int newRoleID = 0;
        try {
            PreparedStatement pSmtInsertUserRole = conn.prepareStatement(
                    "INSERT INTO role_table " +
                            "VALUES(?,?)");
            newRoleID = getMaxFromRoleTable(conn);
            pSmtInsertUserRole.setInt(1, newRoleID);
            pSmtInsertUserRole.setString(2, userRole);
            pSmtInsertUserRole.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return newRoleID;
    }

    public int getMaxFromRoleTable(Connection conn) {
        int resultInt = 0;
        try {
            PreparedStatement prep = conn.prepareStatement(
                    "SELECT MAX(roleid) AS maxid " +
                            "FROM role_table ");
            ResultSet rs = prep.executeQuery();
            if (rs.next())
                resultInt = rs.getInt("maxid");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return resultInt + 1;
    }

    public boolean peekUser(Connection conn, int userID) throws DALException {
        int returnInt = 0;
        try {
            PreparedStatement prep = conn.prepareStatement(
                    "SELECT COUNT(*) AS uservalidity " +
                            "FROM user_table " +
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
    public void deleteAllRoles(Connection conn, UserDTO user){
        try {
          /*  List<String> rolesDB =getUserRoleList(conn, user.getUserId());

            for (String role:
                 ) {

            }*/
            PreparedStatement prep = conn.prepareStatement(
                    "DELETE FROM user_roles " +
                            "WHERE userid = ?" );
            prep.setInt(1, user.getUserId());
            prep.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}


/* public int getNumOfUserRoles(int userId) {
        int numOfUserRoles = 0;
        try {
            Connection conn = createConnection();
            PreparedStatement pSmtSelectNumOfUserRoles = conn.prepareStatement(
                    "SELECT count(userRole)" +
                            "AS numOfRoles" +
                            "FROM user_roles " +
                            "WHERE userID = ?");
            pSmtSelectNumOfUserRoles.setInt(1, userId);

            ResultSet rs = pSmtSelectNumOfUserRoles.executeQuery();
            rs.next();
            numOfUserRoles = rs.getInt("numOfRoles");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return numOfUserRoles;
    }*/