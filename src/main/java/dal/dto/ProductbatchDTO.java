package dal.dto;

import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

public class ProductbatchDTO implements IProductDTO {

    private int productId;
    private String name;
    private int recipe;
    private List<ICommodityBatchDTO> commodityBatches;
    private UserDTO madeBy;
    private Date productionDate;
    private Date expirationDate;
    private int volume;


    public ProductbatchDTO() {
        this.commodityBatches = new ArrayList<>();
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRecipe() {
        return recipe;
    }

    public void setRecipe(int recipe) {
        this.recipe = recipe;
    }

    public List<ICommodityBatchDTO> getCommodityBatches() {
        return commodityBatches;
    }


    @Override
    public Date getProductionDate() {
        return productionDate;
    }

    @Override
    public void setProductionDate(Date productionDate) {
        this.productionDate = productionDate;
    }

    @Override
    public int getVolume() {
        return volume;
    }

    @Override
    public void setVolume(int volume) {
        this.volume = volume;
    }

    @Override
    public Date getExpirationDate() {
        return expirationDate;
    }

    @Override
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setCommodityBatches(List<ICommodityBatchDTO> commodityBatches) {
        this.commodityBatches = commodityBatches;
    }

    public UserDTO getMadeBy() {
        return madeBy;
    }

    public void setMadeBy(UserDTO madeBy) {
        this.madeBy = madeBy;
    }

    @Override
    public String toString() {
        return "Product [productId=" + productId + ", type=" + name + ", productbatches=" + commodityBatches + ", made by: " + madeBy + "]";
    }
}