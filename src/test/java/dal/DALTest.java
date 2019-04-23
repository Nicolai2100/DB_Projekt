package dal;

import dal.dto.IUserDTO;
import dal.dto.UserDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DALTest {

    ConnectionDAO connectionDAO;
    UserDAO userDAO;
    ProductDAOTest productDAOTest;

    @Before
    public void ini() throws IUserDAO.DALException {
        connectionDAO = new ConnectionDAO();
        userDAO = new UserDAO(connectionDAO);
        productDAOTest = new ProductDAOTest();
    }

    @After
    public void closeConn() throws SQLException {
        connectionDAO.getConn().close();
    }

    @Test
    public void deleteUser() throws IUserDAO.DALException {
        userDAO.deleteUser(13);
    }

    @Test
    public void dropAllTables() throws IUserDAO.DALException {
        connectionDAO.dropAllTables(0);
    }

    @Test
    public void initializeDataBase() throws IUserDAO.DALException {
        connectionDAO.initializeDataBase();
    }

    @Test
    public void initializeItAll() throws IUserDAO.DALException {
        dropAllTables();
        initializeDataBase();

/*
        productDAOTest.createTriggers();
*/
        createUser();

    }

    @Test
    void getUser() throws IUserDAO.DALException {
        IUserDTO testUser = userDAO.getUser(10);
        System.out.println(testUser);
    }

    @Test
    public void createUser() throws IUserDAO.DALException {
        IUserDTO testUser2 = new UserDTO();
        testUser2.setUserId(5);
        testUser2.setUserName("Pælle Hansen");
        testUser2.setIni("PH");
        ArrayList<String> roles2 = new ArrayList();
        roles2.add("admin");
        roles2.add("productleader");
        testUser2.setRoles(roles2);
        userDAO.createUser(testUser2);

        UserDTO testUser = new UserDTO();
        testUser.setUserId(10);
        testUser.setUserName("Puk Larsen");
        testUser.setIni("PL");
        testUser.addRole("farmaceut");
        testUser.setAdmin(userDAO.getUser(5));
        userDAO.createUser(testUser);
    }

    @Test
    public void test() {
        try {
            UserDTO testUser = new UserDTO();
            testUser.setUserId(13);
            testUser.setUserName("Per Hansen");
            testUser.setIni("PH");
            ArrayList<String> roles = new ArrayList();
            roles.add("admin");
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
