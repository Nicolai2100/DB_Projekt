package dal;

import org.junit.After;
import org.junit.jupiter.api.Test;

public class ConnectionDAOTest {


    ConnectionDAO connectionDAO = new ConnectionDAO();

//    @Before
//    public void initialize() {
//        connectionDAO = new ConnectionDAO();
//    }

    @After
    public void close() {
        connectionDAO.closeConn();
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
    void createTriggers() {
        connectionDAO.createTriggerOldRecipe();
        connectionDAO.createTriggerReorder();
    }

    @Test
    void dropTriggers() {
        connectionDAO.dropTriggers();
    }

    @Test
    public void initializeItAll() throws IUserDAO.DALException {
        dropAllTables();
        initializeDataBase();
    }

    @Test
    public void deleteFromAllTables() throws IUserDAO.DALException {
        connectionDAO.deleteTables();
    }

}
