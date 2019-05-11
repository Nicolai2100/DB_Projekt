package dal.dto;

import java.sql.Date;
import java.util.List;

public interface IProductBatchDTO {

    int getProductId();

    void setProductId(int productId);

    String getName();

    void setName(String name);

    IRecipeDTO getRecipe();

    void setRecipe(IRecipeDTO recipe);

    List<ICommodityBatchDTO> getCommodityBatches();

    void setCommodityBatches(List<ICommodityBatchDTO> commodityBatches);

    Date getProductionDate();

    void setProductionDate(Date productionDate);

    int getVolume();

    void setVolume(int volume);

    Date getExpirationDate();

    void setExpirationDate(Date expirationDate);

    IUserDTO getOrderedBy();

    void setOrderedBy(IUserDTO orderedBy);

    IUserDTO getProducedBy();

    void setProducedBy(IUserDTO producedBy);

    void setBatchState(State state);

    String getBatchState();

    enum State {
        ORDERED, UNDER_PRODUCTION, COMPLETED
    }
}
