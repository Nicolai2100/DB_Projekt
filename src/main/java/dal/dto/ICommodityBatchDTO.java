package dal.dto;

public interface ICommodityBatchDTO {

    int getBatchId();

    void setBatchId(int batchId);

    IIngredientDTO getIngredientDTO();

    void setIngredientDTO(IIngredientDTO ingredientDTO);

    double getAmountInKg();

    void setAmountInKg(double amount);

    String getOrderDate();

    void setOrderDate(String orderData);

    IUserDTO getOrderedBy();

    void setOrderedBy(IUserDTO userDTO);

    boolean isResidue();

    void setResidue(boolean status);

}
