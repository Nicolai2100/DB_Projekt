package dal;

import dal.dto.IUserDTO;

import java.util.List;

public interface IUserDAO {
    //Create
    void createUser(IUserDTO admin, IUserDTO user) throws DALException;
    //Read
    IUserDTO getUser(int userId) throws DALException;
    List<IUserDTO> getUserList() throws DALException;
    //Update
    void updateUser(IUserDTO admin, IUserDTO user) throws DALException;
    //Delete
    void deleteUser(IUserDTO admin, int userId) throws DALException;

}