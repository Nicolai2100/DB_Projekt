package dal.dto;

import java.util.ArrayList;
import java.util.List;

public interface IProductDTO {

    int getProductId();

    void setProductId(int productId);

    String getName();

    void setName(String name);

    int getRecipe();

    void setRecipe(int recipe);

    List<String> getProductBatches();

    void setProductBatches(List<String> productBatches);

    UserDTO getMadeBy();

    void setMadeBy(UserDTO madeBy);
}
