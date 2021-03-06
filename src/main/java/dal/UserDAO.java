package dal;

import dal.dto.IUserDTO;
import dal.dto.UserDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IUserDAO {
    private Connection conn;

    public UserDAO() throws DALException {
        this.conn = ConnectionDAO.getConnection();
    }

    @Override
    public void createUser(IUserDTO admin, IUserDTO user) throws DALException {
        if (user.getUserId() < 1) {
            throw new DALException("Error in userID!");
        }
        //Brugeren må kun angives som argument for metodekaldet, hvis brugeren har "admin" som rolle
        if (admin.equals(user) && !user.getRoles().contains("admin")) {
            throw new DALException("User not authorized to proceed!");
        }
        if (!admin.getIsActive()) {
            throw new DALException("Admin is not active");
        }
        user.setAdmin(admin);
        String insertString = "INSERT INTO user VALUES(?,?,?,?,?);";
        try {
            conn.setAutoCommit(false);
            PreparedStatement pSmtInsertUser = conn.prepareStatement(insertString);
            pSmtInsertUser.setInt(1, user.getUserId());
            pSmtInsertUser.setString(2, user.getUserName());
            pSmtInsertUser.setString(3, user.getIni());
            pSmtInsertUser.setBoolean(4, true);
            Integer adminId;
            if (user.getAdmin() != null) {
                adminId = user.getAdmin().getUserId();
                pSmtInsertUser.setInt(5, adminId);
            } else {
                pSmtInsertUser.setNull(5, Types.INTEGER);
            }
            int result = pSmtInsertUser.executeUpdate();

            if (result > 0) {
                setUserRoles(conn, user);
                conn.commit();
                System.out.println("The user was successfully created in the database system");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException("An error occurred in the database at UserDAO.");
        }
    }

    @Override
    public IUserDTO getUser(int userId) throws DALException {
        String selectString = "SELECT * FROM user NATURAL JOIN userrole WHERE user_id = ?;";
        boolean empty = true;
        IUserDTO returnUser = new UserDTO();
        try {
            PreparedStatement pSmtSelectUser = conn.prepareStatement(selectString);
            pSmtSelectUser.setInt(1, userId);
            ResultSet rs = pSmtSelectUser.executeQuery();
            while (rs.next()) {
                empty = false;
                returnUser.setUserId(rs.getInt(1));
                returnUser.setUserName(rs.getString(2));
                returnUser.setIni(rs.getString(3));
                returnUser.setIsActive(rs.getBoolean(4));
                int adminId = rs.getInt(5);
                returnUser.addRole(rs.getString(6));
                IUserDTO admin;
                if (returnUser.getUserId() == adminId) {
                    returnUser.setAdmin(returnUser);
                } else {
                    admin = getUser(adminId);
                    returnUser.setAdmin(admin);
                }
            }
            if (empty) {
                return null;
            }
        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at UserDAO.");
        }
        return returnUser;
    }

    @Override
    public List<IUserDTO> getUserList() throws DALException {
        List<IUserDTO> userList = new ArrayList<>();
        String getUserListString = "SELECT * FROM user NATURAL JOIN userrole";
        try {
            PreparedStatement pSmtSelectAllTable = conn.prepareStatement(getUserListString);
            UserDTO returnUser;
            ResultSet rs = pSmtSelectAllTable.executeQuery();
            while (rs.next()) {
                returnUser = new UserDTO();
                returnUser.setUserId(rs.getInt(1));
                returnUser.setUserName(rs.getString(2));
                returnUser.setIni(rs.getString(3));
                returnUser.setIsActive(rs.getBoolean(4));
                returnUser.setAdmin(getUser(rs.getInt(5)));
                returnUser.addRole(rs.getString(6));

                boolean duplicate = false;
                for (IUserDTO user : userList) {
                    if (user.getUserId() == returnUser.getUserId()) {
                        duplicate = true;
                    }
                }
                //Tilføjer roller til den allerede eksisterende bruger
                if (duplicate) {
                    for (IUserDTO user : userList) {
                        if (user.getUserId() == returnUser.getUserId()) {
                            if (!user.getRoles().contains(returnUser.getRoles().get(0)))
                                user.addRole(returnUser.getRoles().get(0));
                        }
                    }
                }
                if (!duplicate) {
                    userList.add(returnUser);
                }
            }
        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at UserDAO.");
        }
        return userList;
    }

    @Override
    public void updateUser(IUserDTO admin, IUserDTO user) throws DALException {
        if (user.getUserId() < 1) {
            throw new DALException("Error! User need to have a userID specified!");
        }
        if (!admin.getIsActive()) {
            throw new DALException("Admin is not active");
        }
        String updateUserString = "UPDATE user SET name = ?, initials = ?, active_status = ? WHERE user_id = ?; ";
        try {
            conn.setAutoCommit(false);
            IUserDTO returnUser = getUser(user.getUserId());
            PreparedStatement pSmtUpdateUser = conn.prepareStatement(updateUserString);
            if (user.getUserName() != null) {
                pSmtUpdateUser.setString(1, user.getUserName());
            } else {
                pSmtUpdateUser.setString(1, returnUser.getUserName());
            }
            if (user.getIni() != null) {
                pSmtUpdateUser.setString(2, user.getIni());
            } else {
                pSmtUpdateUser.setString(2, returnUser.getIni());
            }
            pSmtUpdateUser.setBoolean(3, user.getIsActive());
            pSmtUpdateUser.setInt(4, user.getUserId());

            int result = pSmtUpdateUser.executeUpdate();
            if (result < 1) {
                System.out.println("Error! The user was not updated!");
            } else {
                roleTransAct(user);
                System.out.println("The user was successfully updated!");
                conn.commit();
            }
        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at UserDAO.");
        }
    }

    @Override
    public void deleteUser(IUserDTO admin, int userId) throws DALException {
        if (userId < 1) {
            throw new DALException("Error! Improper userID!");
        }
        if (!admin.getIsActive()) {
            throw new DALException("Admin is not active");
        }
        int result;
        String inactivateString = "UPDATE user SET active_status = 0 WHERE user_id = ? ";
        try {
            PreparedStatement psmtInactivateUser = conn.prepareStatement(inactivateString);
            psmtInactivateUser.setInt(1, userId);
            result = psmtInactivateUser.executeUpdate();
            if (result == 1) {
                System.out.println("The user with user-ID: " + userId + " have been inactivated.");
            } else {
                System.out.println("Error no such user exists in the database!");
            }
        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at UserDAO.");
        }
    }

    /**
     * Metoden opretter og gemmer roller for en bruger i userrole.
     */
    private void setUserRoles(Connection conn, IUserDTO user) throws DALException {
        String insertUserRoleString = "INSERT INTO userrole VALUES(?,?);";
        try {
            conn.setAutoCommit(false);
            PreparedStatement pSmtInsertUserRole = conn.prepareStatement(insertUserRoleString);
            pSmtInsertUserRole.setInt(1, user.getUserId());
            for (int i = 0; i < user.getRoles().size(); i++) {
                String role = user.getRoles().get(i);
                pSmtInsertUserRole.setString(2, role);
                pSmtInsertUserRole.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException("An error occurred in the database at UserDAO.");
        }
    }

    /**
     * Metoden sletter alle roller for en specifik bruger.
     * Dette bruges når brugeren bliver opdateret, for at undgå at
     * brugeren får flere roller end denne bør have.
     */
    private void roleTransAct(IUserDTO user) throws DALException {
        List<String> newUserRoles = user.getRoles();
        String deleteRoleString = "DELETE FROM userrole WHERE user_id = ?;";
        String insertRoleString = "INSERT INTO userrole VALUES(?,?);";
        try {
            PreparedStatement deleteRolesFromDB = conn.prepareStatement(deleteRoleString);
            deleteRolesFromDB.setInt(1, user.getUserId());
            PreparedStatement insertRolesInDB = conn.prepareStatement(insertRoleString);
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
            throw new DALException("An error occurred in the database at UserDAO.");
        }
    }
}
