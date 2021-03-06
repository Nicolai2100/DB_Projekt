package dal;

import dal.dto.IIngredientDTO;
import dal.dto.IRecipeDTO;

import java.util.List;

public interface IIngredientListDAO {

    void createIngredientList(IRecipeDTO recipeDTO, int edition) throws DALException;

    List<IIngredientDTO> getIngredientList(IRecipeDTO recipeDTO) throws DALException;

}

