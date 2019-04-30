package dal.dto;

import java.util.ArrayList;
import java.util.List;

public class OldRecipeDTO implements IOldrecipeDTO{
    private String name;
    //User
    private int madeBy;
    private String outDated;
    private List<IngredientDTO> ingredients;
    private int minBatchSize;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMadeBy() {
        return madeBy;
    }

    public void setMadeBy(int madeBy) {
        this.madeBy = madeBy;
    }

    public String getOutDated() {
        return outDated;
    }

    public void setOutDated(String outDated) {
        this.outDated = outDated;
    }

    public List<IngredientDTO> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientDTO> ingredients) {
        this.ingredients = ingredients;
    }

    public OldRecipeDTO(){
        ingredients = new ArrayList<>();
    }
    /*  "(recipeid int, " +
        "name varchar(50) not null, " +
        "madeby int, " +
        "ingredientlist int, " +
        "outdated varchar(50) not null, " +
        "primary key (recipeid), " +
        "FOREIGN KEY (madeby) " +
        "REFERENCES user (userid), " +
        "foreign key (recipeid) " +
        "references ingredientlist(ingredie*/

    public int getMinBatchSize() {
        return minBatchSize;
    }

    public void setMinBatchSize(int minBatchSize) {
        this.minBatchSize = minBatchSize;
    }
}
