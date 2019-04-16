package dal;

import dal.IUserDAO;
import dal.UserDAOImpl;
import dal.dto.IUserDTO;
import dal.dto.UserDTO;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DALTest {
    UserDAOImpl userDAO = new UserDAOImpl();

    @Before
    public void ini() throws IUserDAO.DALException {
    }


    @Test
    public void deleteUser() throws IUserDAO.DALException {
        userDAO.deleteUser(13);
    }

    @Test
    public void dropAllTables() throws IUserDAO.DALException {
        userDAO.dropAllTables(0);
    }

    @Test
    public void initializeDataBase() throws IUserDAO.DALException {
        userDAO.initializeDataBase();
    }

    @Test
    public void createUser() throws IUserDAO.DALException {

        UserDTO testUser = new UserDTO();
        testUser.setUserId(10);
        testUser.setUserName("Puk Hansen");
        testUser.setIni("PH");
        ArrayList<String> roles = new ArrayList();
        roles.add("administrator");
        roles.add("farmaceut");
        testUser.setRoles(roles);
        userDAO.createUser(testUser);

        IUserDTO testUser2 = new UserDTO();
        testUser2.setUserId(5);
        testUser2.setUserName("Pælle Hansen");
        testUser2.setIni("PH");
        ArrayList<String> roles2 = new ArrayList();
        roles.add("administrator");
        roles.add("productleader");
        testUser2.setRoles(roles2);
        userDAO.createUser(testUser2);
    }
    @Test
    public void test() {
        try {
            UserDTO testUser = new UserDTO();
            testUser.setUserId(13);
            testUser.setUserName("Per Hansen");
            testUser.setIni("PH");
            ArrayList<String> roles = new ArrayList();
            roles.add("administrator");
            testUser.setRoles(roles);

            userDAO.createUser(testUser);
            IUserDTO receivedUser = userDAO.getUser(13);
            assertEquals(testUser.getUserName(), receivedUser.getUserName());
            assertEquals(testUser.getIni(), receivedUser.getIni());
            assertEquals(testUser.getRoles().get(0), receivedUser.getRoles().get(0));
            assertEquals(testUser.getRoles().size(), receivedUser.getRoles().size());
            List<IUserDTO> allUsers = userDAO.getUserList();
            boolean found = false;
            for (IUserDTO user : allUsers) {
                if (user.getUserId() == testUser.getUserId()) {
                    assertEquals(testUser.getUserName(), user.getUserName());
                    assertEquals(testUser.getIni(), user.getIni());
                    assertEquals(testUser.getRoles().get(0), user.getRoles().get(0));
                    assertEquals(testUser.getRoles().size(), user.getRoles().size());
                    found = true;
                }
            }
            if (!found) {
                fail();
            }

            testUser.setUserName("Per petersen");
            testUser.setIni("PP");
            roles.remove(0);
            roles.add("pedel");
            testUser.setRoles(roles);
            userDAO.updateUser(testUser);

            receivedUser = userDAO.getUser(13);
            assertEquals(testUser.getUserName(), receivedUser.getUserName());
            assertEquals(testUser.getIni(), receivedUser.getIni());
            assertEquals(testUser.getRoles().get(0), receivedUser.getRoles().get(0));
            assertEquals(testUser.getRoles().size(), receivedUser.getRoles().size());

            userDAO.deleteUser(testUser.getUserId());
            allUsers = userDAO.getUserList();

            for (IUserDTO user : allUsers) {
                if (user.getUserId() == testUser.getUserId()) {
                    fail();
                }
            }
        } catch (IUserDAO.DALException e) {
            e.printStackTrace();
            fail();
        }
    }
}
