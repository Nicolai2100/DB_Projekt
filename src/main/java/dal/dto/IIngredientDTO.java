package dal.dto;

public interface IIngredientDTO {

    int getIngredientId();

    void setIngredientId(int ingredientId);

    String getName();

    void setName(String name);

    String getType();

    void setType(String type);

    void setAmount(double amount);

    double getAmount();

}
