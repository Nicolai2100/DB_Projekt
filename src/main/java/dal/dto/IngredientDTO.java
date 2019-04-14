package dal.dto;

public class IngredientDTO {
    private int	ingredientId;
    private String type;

    public IngredientDTO() {

    }

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "IngredientId : " +ingredientId + " type: " + type;
    }
}

