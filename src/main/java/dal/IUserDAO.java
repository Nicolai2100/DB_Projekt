package dal;

import dal.dto.IUserDTO;

import java.util.List;

public interface IUserDAO {
    void createUser(IUserDTO admin, IUserDTO user) throws DALException;

    IUserDTO getUser(int userId) throws DALException;

    List<IUserDTO> getUserList() throws DALException;

    void updateUser(IUserDTO admin, IUserDTO user) throws DALException;

    void deleteUser(IUserDTO admin, int userId) throws DALException;
}