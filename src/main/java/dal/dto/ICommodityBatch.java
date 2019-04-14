package dal.dto;

public interface ICommodityBatch {

    int getBatchId();

    void setBatchId(int batchId);

    int getIngredientId();

    void setIngredientId(int ingredientId);

    double getAmountInKg();

    void setAmountInKg(double amount);

    String getOrderDate();

    void setOrderDate(String orderData);
    //User
    IUserDTO getOrderedBy();

    void setOrderedBy(IUserDTO userDTO);


}
