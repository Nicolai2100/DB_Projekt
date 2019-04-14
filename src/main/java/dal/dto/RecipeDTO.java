package dal.dto;

import java.util.ArrayList;
import java.util.List;

public class RecipeDTO {
    private int	recipeId;
    private String name;
    private int recipe;
    private UserDTO madeBy;
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
}
