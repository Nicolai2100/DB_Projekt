package dal;

import dal.dto.IUserDTO;
import dal.dto.UserDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UserDAOTest {

    ConnectionDAO connectionDAO = new ConnectionDAO();
    UserDAO userDAO = new UserDAO();

    public UserDAOTest() throws DALException {
    }

    @Before
    public void initialize() {
        /*connectionDAO = new ConnectionDAO();
        userDAO = new UserDAO(connectionDAO);
        productDAOTest = new ProductBatchDAOTest();*/
    }

    @After
    public void close() throws DALException {
        connectionDAO.closeConn();
    }

    @Test
    public void deleteUser() throws DALException {
        IUserDTO admin = userDAO.getUser(7);
        userDAO.deleteUser(admin, 10);
    }

    @Test
    void getUser() throws DALException {
        IUserDTO testUser = userDAO.getUser(7);
        System.out.println(testUser);
    }

    @Test
    void getAllUsers() throws DALException {
        for (IUserDTO user : userDAO.getUserList()) {
            System.out.println(user);
        }
    }


    @Test
    void getUserWithRoles() throws DALException {

       /* for (IUserDTO user:userDAO.getUserList()) {
            System.out.println(user);
        }*/
    }

    @Test
    public void updateUser() throws DALException {
        IUserDTO testUser2 = new UserDTO();
        testUser2.setUserId(7);
        testUser2.setIsActive(false);

        UserDTO testUser = new UserDTO();
        testUser.setUserId(10);
        testUser.addRole("laborant");
        userDAO.updateUser(testUser2, testUser);
    }

    @Test
    public void createUser() throws DALException {
        IUserDTO testUser2 = new UserDTO();
        testUser2.setUserId(7);
        testUser2.setUserName("Karl Mørk");
        testUser2.setIni("PH");
        testUser2.addRole("admin");
        testUser2.addRole("productionleader");
        userDAO.createUser(testUser2, testUser2);

        UserDTO testUser = new UserDTO();
        testUser.setUserId(10);
        testUser.setUserName("Puk Larsen");
        testUser.setIni("PL");
        testUser.addRole("farmaceut");
        userDAO.createUser(testUser2, testUser);
    }

    @Test
    public void test() {
        try {
            IUserDTO admin = userDAO.getUser(1);
            UserDTO testUser = new UserDTO();
            testUser.setUserId(13);
            testUser.setUserName("Per Hansen");
            testUser.setIni("PH");
            ArrayList<String> roles = new ArrayList();
            roles.add("admin");
            testUser.setRoles(roles);

            userDAO.createUser(admin, testUser);
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
            userDAO.updateUser(admin, testUser);

            receivedUser = userDAO.getUser(14);
            assertEquals(testUser.getUserName(), receivedUser.getUserName());
            assertEquals(testUser.getIni(), receivedUser.getIni());
            assertEquals(testUser.getRoles().get(0), receivedUser.getRoles().get(0));
            assertEquals(testUser.getRoles().size(), receivedUser.getRoles().size());

            System.out.println(testUser.getUserId());
            userDAO.deleteUser(admin, testUser.getUserId());
            allUsers = userDAO.getUserList();

            for (IUserDTO user : allUsers) {
                if (user.getUserId() == testUser.getUserId()) {
                    fail();
                }
            }
        } catch (DALException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    void ss() {
      /*  Calendar calendar = Calendar.getInstance();
        System.out.println("Current Date = " + calendar.getTime());
        // Add 8 months to current date
        calendar.add(Calendar.MONTH, 8);
        System.out.println("Updated Date = " + calendar.getTime());
*/
        Date now = (new Date(System.currentTimeMillis()));
        System.out.println(now);
        LocalDate ld = now.toLocalDate();
        LocalDate expirationDate = ld.plusMonths(3);
        java.sql.Date sqlExpirationDate = java.sql.Date.valueOf(expirationDate);
        System.out.println(sqlExpirationDate);
    }
}
