package dal.dto;

public class CommodityBatchDTO implements ICommodityBatchDTO {
    private int batchId;
    private IIngredientDTO ingredientDTO;
    private IUserDTO orderedBy;
    private double amountInKg;
    private String orderDate;
    private boolean residue;

    public CommodityBatchDTO() {
    }

    @Override
    public String toString() {
        return "Commodity-batchID: " + batchId + ", ingredient: " + ingredientDTO.getName() + " amount: " + amountInKg + " kg." +
                "\nOrdered by: " + orderedBy.getUserName() + ", userID: " + orderedBy.getUserId() + ", roles: " + orderedBy.getRoles() +
                ".\nOrder date: " + orderDate.substring(0, 19);
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
    public IIngredientDTO getIngredientDTO() {
        return ingredientDTO;
    }

    @Override
    public void setIngredientDTO(IIngredientDTO ingredientDTO) {
        this.ingredientDTO = ingredientDTO;
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
        return orderedBy;
    }

    @Override
    public void setOrderedBy(IUserDTO userDTO) {
        this.orderedBy = userDTO;
    }

    public boolean isResidue() {
        return residue;
    }

    @Override
    public void setResidue(boolean residue) {
        this.residue = residue;
    }
}
