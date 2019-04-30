package dal;

import dal.dto.RecipeDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OldRecipeDAO {
    private Connection conn;
    private RecipeDAO recipeDAO;

    public OldRecipeDAO(RecipeDAO recipeDAO) {
        this.conn = ConnectionDAO.getConnection();
        this.recipeDAO = recipeDAO;
    }

    public int haveOldRecipe(int recipeId) {
        int edition = 0;
        try {
            String haveOldRecipeString = "SELECT MAX(edition) FROM oldrecipe WHERE recipeid = ?;";
            PreparedStatement pstmtHaveOldRecipe = conn.prepareStatement(haveOldRecipeString);
            pstmtHaveOldRecipe.setInt(1, recipeId);
            ResultSet rs = pstmtHaveOldRecipe.executeQuery();
            if (rs.next()) {
                edition = rs.getInt(1);
                System.out.println(edition);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return edition;
    }

    public void getAllOldRecipes() {
        PreparedStatement pstmtGetAllOld = null;
        try {
            String getAllOldString = "SELECT * FROM oldrecipe;";
            pstmtGetAllOld = conn.prepareStatement(getAllOldString);

            ResultSet rs = pstmtGetAllOld.executeQuery();
            while (rs.next()) {
                RecipeDTO recipeDTO = new RecipeDTO();
                recipeDTO.setName(rs.getString(3));
                recipeDTO.setEdition(rs.getInt(2));
                System.out.println(recipeDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
