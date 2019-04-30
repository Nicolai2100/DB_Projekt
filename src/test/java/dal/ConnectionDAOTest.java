package dal;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;

public class ConnectionDAOTest {

    ConnectionDAO connectionDAO = new ConnectionDAO();

    public ConnectionDAOTest() throws DALException {
    }

/*    @Before
    public void initialize() throws DALException {
        connectionDAO = new ConnectionDAO();
    }*/

    @After
    public void close() throws DALException {
        connectionDAO.closeConn();
    }

    @Test
    public void dropAllTables() throws DALException {
        connectionDAO.dropAllTables(0);
    }

    @Test
    public void initializeDataBase() throws DALException {
        connectionDAO.initializeDataBase();
    }

    @Test
    public void initializeItAll() throws DALException {
        dropAllTables();
        initializeDataBase();
    }

    @Test
    public void deleteFromAllTables() throws DALException {
        connectionDAO.deleteTables();
    }
}
