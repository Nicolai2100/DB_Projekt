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
        try {
            conn.setAutoCommit(false);
            String insertIngList = "INSERT INTO ingredientlist(ingredientlistid, version, ingredientid, amountmg) " +
                    "VALUES(?,?,?,?);";
            PreparedStatement pstmtInsertIngredientList = conn.prepareStatement(insertIngList);
            pstmtInsertIngredientList.setInt(1, recipeDTO.getRecipeId());

            for (IIngredientDTO ingredient : recipeDTO.getIngredientsList()) {
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
            throw new DALException("An error occurred in the database at IngredientListDAO.");
        }
        return ingredientList;
    }

    @Override
    public void updateIngredientList(IRecipeDTO recipeDTO) throws DALException {
        try {
            conn.setAutoCommit(false);
            String updateIngListString = "INSERT INTO ingredientlist(ingredientlistid, version, ingredientid, amountmg) " +
                    "VALUES(?,?,?,?);";
            PreparedStatement pstmtInsertIngredientList = conn.prepareStatement(updateIngListString);
            pstmtInsertIngredientList.setInt(1, recipeDTO.getRecipeId());
            pstmtInsertIngredientList.setInt(2, recipeDTO.getVersion());

            for (IIngredientDTO ingredient : recipeDTO.getIngredientsList()) {
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

    /**
     * Metoden undersøger om der allerede er en ingrediensliste, hvis ikke bliver der skabt en og hvis der er
     * gør der ikke.
     *
     * @param recipeDTO
     */
    @Override
    public void isIngredientListCreated(IRecipeDTO recipeDTO, int version) throws DALException {
        try {
            PreparedStatement pstmtGetIngredientList = conn.prepareStatement(
                    "SELECT COUNT(*) FROM ingredientlist " +
                            "WHERE ingredientlistid = ? " +
                            "AND version = ?;");
            pstmtGetIngredientList.setInt(1, recipeDTO.getRecipeId());
            pstmtGetIngredientList.setInt(2, version);

            ResultSet rs = pstmtGetIngredientList.executeQuery();
            int result = 0;
            if (rs.next()) {
                result = rs.getInt(1);
            }
            if (result > 0) {
                return;
            } else {
                createIngredientList(recipeDTO, version);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException("An error occurred in the database at IngredientListDAO.");
        }
    }
}
