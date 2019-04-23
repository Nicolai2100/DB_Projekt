package dal;

import dal.dto.IIngredientDTO;
import dal.dto.IRecipeDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IngredientListDAO {
    private Connection conn;
    private IngredientDAO ingredientDAO;
    private ConnectionDAO connectionDAO;

    public IngredientListDAO(ConnectionDAO connectionDAO, UserDAO userDAO, IngredientDAO ingredientDAO) {
        this.connectionDAO = connectionDAO;
        this.ingredientDAO = ingredientDAO;
        this.conn = connectionDAO.getConn();
    }


    public List<IIngredientDTO> getIngredientList(IRecipeDTO recipeDTO) {
        List<IIngredientDTO> ingredientList = new ArrayList<>();

        try {
            PreparedStatement pstmtGetIngredientList = conn.prepareStatement(
                    "SELECT * FROM ingredientlist " +
                            "WHERE ingredientlistid = ?;");

            pstmtGetIngredientList.setInt(1, recipeDTO.getRecipeId());
            ResultSet rs = pstmtGetIngredientList.executeQuery();

            while (rs.next()) {
                IIngredientDTO ingredientDTO = ingredientDAO.getIngredient(rs.getInt(3));
                ingredientDTO.setAmount(rs.getDouble(4));
                ingredientList.add(ingredientDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredientList;

    }

    /**
     * Metoden undersøger om der allerede er en ingrediensliste, hvis ikke bliver der skabt en og hvis der er
     * gør der ikke.
     *
     * @param recipeDTO
     */
    public void isIngredientListCreated(IRecipeDTO recipeDTO, int edition) {
        try {
            PreparedStatement pstmtGetIngredientList = conn.prepareStatement(
                    "SELECT COUNT(*) FROM ingredientlist " +
                            "WHERE ingredientlistid = ? " +
                            "AND edition = ?;");
            pstmtGetIngredientList.setInt(1, recipeDTO.getRecipeId());
            pstmtGetIngredientList.setInt(2, edition);

            ResultSet rs = pstmtGetIngredientList.executeQuery();
            int result = 0;
            if (rs.next()) {
                result = rs.getInt(1);
            }
            if (result > 0) {
                return;
            } else {
                createIngredientList(recipeDTO, edition);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateIngredientList(IRecipeDTO recipeDTO, int edition) {
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmtInsertIngredientList = conn.prepareStatement(
                    "INSERT INTO ingredientlist(ingredientlistid, edition, ingredientid, amountmg) " +
                            "VALUES(?,?,?,?)");
            pstmtInsertIngredientList.setInt(1, recipeDTO.getRecipeId());
            pstmtInsertIngredientList.setInt(2, edition);

            for (IIngredientDTO ingredient : recipeDTO.getIngredientsList()) {
                pstmtInsertIngredientList.setInt(3, ingredient.getIngredientId());
                pstmtInsertIngredientList.setDouble(4, ingredient.getAmount());
                pstmtInsertIngredientList.executeUpdate();
            }
            conn.commit();
            System.out.println("The ingredientlist was successfully updated.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createIngredientList(IRecipeDTO recipeDTO, int edition) {
        try {
            for (IIngredientDTO ingredient : recipeDTO.getIngredientsList()) {
                ingredientDAO.isIngredientThere(ingredient);
            }
            conn.setAutoCommit(false);
            PreparedStatement pstmtInsertIngredientList = conn.prepareStatement(
                    "INSERT INTO ingredientlist(ingredientlistid, edition, ingredientid, amountmg) " +
                            "VALUES(?,?,?,?)");
            pstmtInsertIngredientList.setInt(1, recipeDTO.getRecipeId());

            for (IIngredientDTO ingredient : recipeDTO.getIngredientsList()) {
                pstmtInsertIngredientList.setInt(2, edition);
                pstmtInsertIngredientList.setInt(3, ingredient.getIngredientId());
                pstmtInsertIngredientList.setDouble(4, ingredient.getAmount());
                pstmtInsertIngredientList.executeUpdate();
            }
            conn.commit();
            System.out.println("The ingredientlist was successfully created.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
