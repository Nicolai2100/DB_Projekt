package dal;

import java.sql.Connection;

public interface IConnectionDAO {
    void initializeDataBase() throws DALException;

    void closeConn() throws DALException;

    void deleteTables() throws DALException;

    void deleteUsers() throws DALException;

    void createTriggers() throws DALException;
}
