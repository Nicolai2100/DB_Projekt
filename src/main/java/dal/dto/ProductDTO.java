package dal.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProductDTO implements Serializable{

    private int	productId;
    private String name;
    private int recipe;
    private List<String> productBatches;
    private UserDTO madeBy;

    public ProductDTO() {
        this.productBatches = new ArrayList<>();
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public UserDTO getMadeby() {
        return madeBy;
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

    public List<String> getProductBatches() {
        return productBatches;
    }

    public void setProductBatches(List<String> productBatches) {
        this.productBatches = productBatches;
    }

    public UserDTO getMadeBy() {
        return madeBy;
    }

    public void setMadeBy(UserDTO madeBy) {
        this.madeBy = madeBy;
    }

    @Override
    public String toString() {
        return "Product [productId=" + productId + ", type=" + name + ", productbatches=" + productBatches + ", made by: " + madeBy + "]";
    }
}