package dal;

import dal.dto.IIngredientDTO;
import dal.dto.IRecipeDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IngredientListDAO implements IIngredientListDAO {
    private Connection conn;
    private IngredientDAO ingredientDAO;

    public IngredientListDAO(IngredientDAO ingredientDAO) throws DALException {
        this.ingredientDAO = ingredientDAO;
        this.conn = ConnectionDAO.getConnection();
    }

    @Override
    public void createIngredientList(IRecipeDTO recipeDTO, int version) throws DALException {
        String insertIngList = "INSERT INTO ingredientlist(ingredientlist_id, version_id, ingredient_id, amount_mg) " +
                "VALUES(?,?,?,?);";
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmtInsertIngredientList = conn.prepareStatement(insertIngList);
            for (IIngredientDTO ingredient : recipeDTO.getIngredientsList()) {
                pstmtInsertIngredientList.setInt(1, recipeDTO.getRecipeId());
                pstmtInsertIngredientList.setInt(2, version);
                pstmtInsertIngredientList.setInt(3, ingredient.getIngredientId());
                pstmtInsertIngredientList.setDouble(4, ingredient.getAmount());
                pstmtInsertIngredientList.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException("An error occurred in the database at IngredientListDAO.");
        }
    }

    @Override
    public List<IIngredientDTO> getIngredientList(IRecipeDTO recipeDTO) throws DALException {
        List<IIngredientDTO> ingredientList = new ArrayList<>();
        String getMaxVersionInt = "SELECT MAX(version_id) FROM ingredientlist WHERE ingredientlist_id = ?";
        String getIngListString = "SELECT * FROM ingredientlist WHERE ingredientlist_id = ? AND version_id = ?;";
        try {
            PreparedStatement pstmtGetMaxIngVersion = conn.prepareStatement(getMaxVersionInt);
            pstmtGetMaxIngVersion.setInt(1, recipeDTO.getRecipeId());
            ResultSet rs1 = pstmtGetMaxIngVersion.executeQuery();
            int maxVersion = 1;
            if (rs1.next()) {
                maxVersion = rs1.getInt(1);
            }
            PreparedStatement pstmtGetIngredientList = conn.prepareStatement(getIngListString);
            pstmtGetIngredientList.setInt(1, recipeDTO.getRecipeId());
            pstmtGetIngredientList.setInt(2, maxVersion);
            ResultSet rs2 = pstmtGetIngredientList.executeQuery();
            while (rs2.next()) {
                IIngredientDTO ingredientDTO = ingredientDAO.getIngredient(rs2.getInt(3));
                ingredientDTO.setAmount(rs2.getDouble(4));
                ingredientList.add(ingredientDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException("An error occurred in the database at IngredientListDAO.");
        }
        return ingredientList;
    }
}
