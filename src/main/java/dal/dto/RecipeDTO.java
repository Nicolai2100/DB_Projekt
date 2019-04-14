package dal.dto;

import java.util.*;

public class RecipeDTO {
    private int recipeId;
    private String name;
    private int madeBy;
    private HashMap<String, IngredientDTO> ingredients;
    private List<IngredientDTO> ingredientsList;

    public RecipeDTO() {
        ingredients = new HashMap<>();
        ingredientsList = new ArrayList<>();
    }

    @Override
    public String toString() {
        String returnString = "RecipeID " + recipeId + " of type " + name + " made by userID: " + madeBy +
                "\nIngredients: \n";

        Iterator it = ingredients.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            returnString += "[" + pair.getValue() + "]\n";
            it.remove(); // avoids a ConcurrentModificationException
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

    public int getMadeBy() {
        return madeBy;
    }

    public void setMadeBy(int madeBy) {
        this.madeBy = madeBy;
    }

    public HashMap<String, IngredientDTO> getIngredients() {
        return ingredients;
    }

    public void setIngredients(HashMap<String, IngredientDTO> ingredients) {
        this.ingredients = ingredients;
    }

    public List<IngredientDTO> getIngredientsList() {
        return ingredientsList;
    }

    public void setIngredientsList(List<IngredientDTO> ingredientsList) {
        this.ingredientsList = ingredientsList;
    }
}
