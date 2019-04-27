package dal.dto;

public class IngredientDTO implements IIngredientDTO {
    private int ingredientId;
    private String name;
    private String type;
    private double amountInMG;
    private int minAmountInMG;


    public IngredientDTO() {
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
    public void setAmount(double amount) {
        this.amountInMG = amount;
    }

    @Override
    public double getAmount() {
        return amountInMG;
    }

    @Override
    public void setMinAmountMG(int minAmount) {
        this.minAmountInMG = minAmount;
    }

    @Override
    public int getMinAmountMG() {
        return minAmountInMG;
    }

    @Override
    public String toString() {
        return "\n"+name + ", of type: " + type + " amount: " + amountInMG + " mg.";
    }
}

