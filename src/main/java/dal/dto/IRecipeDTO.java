package dal.dto;

import java.util.List;

public interface IRecipeDTO {

    int getRecipeId();

    void setRecipeId(int recipeId);

    String getName();

    void setName(String name);

    IUserDTO getMadeBy();

    void setMadeBy(IUserDTO madeBy);

    List<IIngredientDTO> getIngredientsList();

    void setIngredientsList(List<IIngredientDTO> ingredientsList);

    int getEdition();

    void setEdition(int edition);
}
