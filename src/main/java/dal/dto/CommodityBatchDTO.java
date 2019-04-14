package dal.dto;

public class CommodityBatchDTO implements ICommodityBatch{
    private int batchId;
    private int ingredientId;
    private double amountInKg;
    private String orderDate;

    //User
    private int orderedBy;
    public CommodityBatchDTO(){
    }

    @Override
    public String toString() {
        return "Commodity batch: " + batchId + "ingredient: " + ingredientId + "amount: " + amountInKg + "ordered by " + orderedBy;
    }

    @Override
    public int getBatchId() {
        return batchId;
    }

    @Override
    public void setBatchId(int batchId) {
        this.batchId = batchId;
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
    public double getAmountInKg() {
        return amountInKg;
    }

    @Override
    public void setAmountInKg(double amount) {
        this.amountInKg = amount;
    }

    @Override
    public String getOrderDate() {
        return orderDate;
    }

    @Override
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    @Override
    public IUserDTO getOrderedBy() {
        return null;
    }

    @Override
    public void setOrderedBy(IUserDTO userDTO) {

    }
}
