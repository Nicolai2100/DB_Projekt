package dal;

public interface IConnectionDAO {
    void initializeDataBase() throws DALException;

    void closeConn() throws DALException;

    void deleteTables() throws DALException;

    void deleteUsers() throws DALException;
}
