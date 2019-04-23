package dal.dto;

import java.sql.Date;
import java.util.List;

public interface IProductDTO {

    int getProductId();

    void setProductId(int productId);

    String getName();

    void setName(String name);

    int getRecipe();

    void setRecipe(int recipe);

    List<String> getCommodityBatches();

    void setCommodityBatches(List<String> commodityBatches);

    Date getProductionDate();

    void setProductionDate(Date productionDate);

    int getVolume();

    void setVolume(int volume);

    Date getExpirationDate();

    void setExpirationDate(Date expirationDate);

    UserDTO getMadeBy();

    void setMadeBy(UserDTO madeBy);

}
