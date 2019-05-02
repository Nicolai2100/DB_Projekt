package dal;

import dal.dto.IIngredientDTO;

import java.util.List;

public interface IIngredientDAO {

    void createIngredient(IIngredientDTO ingredientDTO) throws DALException;

    IIngredientDTO getIngredient(int ingredientId) throws DALException;

    List<IIngredientDTO> checkForReorder() throws DALException;

    double getTotalAmount(IIngredientDTO ingredient) throws DALException;

    void setReorder(List<IIngredientDTO> ingList) throws DALException;
}
