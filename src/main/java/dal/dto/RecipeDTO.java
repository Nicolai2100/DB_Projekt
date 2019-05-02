package dal.dto;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

public class RecipeDTO implements IRecipeDTO {
    private int recipeId;
    private String name;
    private IUserDTO madeBy;
    private int version;
    private List<IIngredientDTO> ingredientsList;
    private Timestamp expired;
    private int minBatchSize;

    public RecipeDTO() {
        ingredientsList = new ArrayList<>();
    }

    @Override
    public String toString() {
        String returnString = "";
        if (expired == null) {
            returnString = "RecipeID " + recipeId + ", version: " + version + ", of type " + name + " made by: " + madeBy + ". " +
                    "\nIngredients: \n";
            for (IIngredientDTO ingredient : ingredientsList) {
                returnString += "[" + ingredient + "]\n";
            }
        } else {
            returnString = "Expired recipe - RecipeID " + recipeId + ", version: " + version + ", of type " + name + " made by: " + madeBy + ". " +
                    "\nIngredients: \n";
            for (IIngredientDTO ingredient : ingredientsList) {
                returnString += "[" + ingredient + "]\n";
            }
        }
        return returnString;
    }

    @Override
    public int getRecipeId() {
        return recipeId;
    }

    @Override
    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public IUserDTO getMadeBy() {
        return madeBy;
    }

    @Override
    public void setMadeBy(IUserDTO madeBy) {
        this.madeBy = madeBy;
    }

    @Override
    public List<IIngredientDTO> getIngredientsList() {
        return ingredientsList;
    }

    @Override
    public void setIngredientsList(List<IIngredientDTO> ingredientsList) {
        this.ingredientsList = ingredientsList;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public int getMinBatchSize() {
        return minBatchSize;
    }

    @Override
    public void setMinBatchSize(int minBatchSize) {
        this.minBatchSize = minBatchSize;
    }

    @Override
    public Timestamp getExpired() {
        return expired;
    }

    @Override
    public void setExpired(Timestamp expired) {
        this.expired = expired;
    }
}
