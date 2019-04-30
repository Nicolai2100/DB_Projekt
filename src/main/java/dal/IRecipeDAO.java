package dal;

import dal.dto.IRecipeDTO;
import dal.dto.IUserDTO;

public interface IRecipeDAO {
    void createRecipe(IRecipeDTO recipeDTO) throws DALException;

    IRecipeDTO getActiveRecipe(int recipeId) throws DALException;

    void updateRecipe(IRecipeDTO recipeDTO) throws DALException;

    void archiveRecipe(int recipeId, IUserDTO userDTO) throws DALException;
}
