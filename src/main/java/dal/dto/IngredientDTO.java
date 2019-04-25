package dal.dto;

public class IngredientDTO implements IIngredientDTO {
    private int ingredientId;
    private String name;
    private String type;
    private int minAmountInMG;
    private boolean reorder;


    public IngredientDTO() {
    }

    public IngredientDTO(int ingredientId, String name, String type, int minAmountInMG, boolean reorder) {
        this.ingredientId = ingredientId;
        this.name = name;
        this.type = type;
        this.minAmountInMG = minAmountInMG;
        this.reorder = reorder;
    }

    @Override
    public int getIngredientId() {
        return ingredientId;
    }

    @Override
    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
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
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void setMinAmountMG(int minAmount) {
        this.minAmountInMG = minAmount;
    }

    @Override
    public boolean getReorder() {
        return reorder;
    }

    @Override
    public void setReorder(boolean status) {
        this.reorder = status;
    }

    @Override
    public int getMinAmountMG() {
        return minAmountInMG;
    }

    @Override
    public String toString() {
        return name + ", of type: " + type + " minamount: " + minAmountInMG + " mg.";
    }
}

