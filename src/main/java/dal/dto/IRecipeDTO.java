package dal.dto;

import java.sql.Timestamp;
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

    int getVersion();

    void setVersion(int version);

    int getMinBatchSize();

    void setMinBatchSize(int size);

    Timestamp getExpired();

    void setExpired(Timestamp expired);

    int getExpirationInMonths();

    void setExpirationInMonths(int expirationInMonths);

}
