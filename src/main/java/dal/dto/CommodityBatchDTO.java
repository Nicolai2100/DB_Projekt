package dal.dto;

public class CommodityBatchDTO {
    private int batchId;
    private int ingredientId;
    private int amountInKg;

    //User
    private int orderedBy;

    public CommodityBatchDTO(){

    }

    @Override
    public String toString() {
        return "Commodity batch: " + batchId + "ingredient: " + ingredientId + "amount: " + amountInKg + "ordered by " + orderedBy;
    }
}
