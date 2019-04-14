package dal.dto;

import java.util.ArrayList;
import java.util.List;

public class RecipeDTO {
    private int	recipeId;
    private String name;
    private int recipe;
    private int madeBy;
    private List<String> ingredients;

    public RecipeDTO(){
        ingredients = new ArrayList<>();
    }

    @Override
    public String toString() {
        String returnString = "Recipe " + recipeId + "of type " + name + "made by " + madeBy +
        "\nIngredients: ";

        for (String ingredient: ingredients) {
            returnString += "["+ingredient+"]";
        }
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

    public int getRecipe() {
        return recipe;
    }

    public void setRecipe(int recipe) {
        this.recipe = recipe;
    }

    public int getMadeBy() {
        return madeBy;
    }

    public void setMadeBy(int madeBy) {
        this.madeBy = madeBy;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
