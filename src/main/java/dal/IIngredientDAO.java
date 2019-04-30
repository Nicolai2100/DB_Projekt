package dal;

import dal.dto.IIngredientDTO;

import java.util.List;

public interface IIngredientDAO {
    void createIngredient(IIngredientDTO ingredientDTO) throws DALException;

    IIngredientDTO getIngredient(int ingredientId) throws DALException;

    void isIngredientThere(IIngredientDTO ingredient) throws DALException;

    List<IIngredientDTO> checkForReorder() throws DALException;

    double getTotalAmount(IIngredientDTO ingredient) throws DALException;
}
