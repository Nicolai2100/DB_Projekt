package dal.dto;

import java.util.*;

public class RecipeDTO {
    private int recipeId;
    private String name;
    private IUserDTO madeBy;
    private List<IIngredientDTO> ingredientsList;

    public RecipeDTO() {
        ingredientsList = new ArrayList<>();
    }

    @Override
    public String toString() {
        String returnString = "RecipeID " + recipeId + " of type " + name + " made by userID: " + madeBy +
                "\nIngredients: \n";

        for (IIngredientDTO ingredient: ingredientsList) {
            returnString += "["+ingredient+"]\n";
        }
      /*  Iterator it = ingredients.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            returnString += "[" + pair.getValue() + "]\n";
            it.remove(); // avoids a ConcurrentModificationException
        }*/
        return returnString;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IUserDTO getMadeBy() {
        return madeBy;
    }

    public void setMadeBy(IUserDTO madeBy) {
        this.madeBy = madeBy;
    }


    public List<IIngredientDTO> getIngredientsList() {
        return ingredientsList;
    }

    public void setIngredientsList(List<IIngredientDTO> ingredientsList) {
        this.ingredientsList = ingredientsList;
    }
}
