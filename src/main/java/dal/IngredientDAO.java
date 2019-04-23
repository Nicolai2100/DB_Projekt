package dal;

import dal.dto.IIngredientDTO;
import dal.dto.IngredientDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IngredientDAO {
    private Connection conn;

    public IngredientDAO(){
        this.conn = ConnectionDAO.createConnection();
    }

    public void createIngredient(IIngredientDTO ingredientDTO) {
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmtInsertIngredient = conn.prepareStatement(
                    "INSERT INTO ingredient " +
                            "VALUES(?,?,?,?)");
            pstmtInsertIngredient.setInt(1, ingredientDTO.getIngredientId());
            pstmtInsertIngredient.setString(2, ingredientDTO.getName());
            pstmtInsertIngredient.setString(3, ingredientDTO.getType());
            pstmtInsertIngredient.setInt(4, 1);
            pstmtInsertIngredient.executeUpdate();
            conn.commit();
            System.out.println("The ingredient was successfully created.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public IIngredientDTO getIngredient(int ingredientId) {
        IIngredientDTO ingredientDTO = new IngredientDTO();
        try {
            PreparedStatement pstmtGetIngredient = conn.prepareStatement(
                    "SELECT * FROM ingredient " +
                            "WHERE ingredientid = ?;");
            pstmtGetIngredient.setInt(1, ingredientId);
            ResultSet rs = pstmtGetIngredient.executeQuery();

            while (rs.next()) {
                ingredientDTO.setIngredientId(rs.getInt(1));
                ingredientDTO.setName(rs.getString(2));
                ingredientDTO.setType(rs.getString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredientDTO;
    }

    public List<IIngredientDTO> checkForReorder() {
        List<IIngredientDTO> toBeOrdered = new ArrayList<>();
        try {
            PreparedStatement pstmtGetReorder = conn.prepareStatement(
                    "SELECT * " +
                            "FROM ingredient " +
                            "WHERE reorder = 1;");
            ResultSet rs = pstmtGetReorder.executeQuery();

            while (rs.next()) {
                IIngredientDTO ingredientDTO = new IngredientDTO();
                ingredientDTO.setIngredientId(rs.getInt(1));
                ingredientDTO.setName(rs.getString(2));
                ingredientDTO.setType(rs.getString(3));
                toBeOrdered.add(ingredientDTO);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return toBeOrdered;
    }

    /**
     * Opretter et ingrediens i DB, hvis der ikke findes et
     * med dets id
     *
     * @param ingredient
     */
    public void isIngredientThere(IIngredientDTO ingredient) {
        try {
            PreparedStatement pstmtIsIngThere = conn.prepareStatement(
                    "SELECT COUNT(*) " +
                            "FROM ingredient " +
                            "WHERE ingredientid = ?;");
            pstmtIsIngThere.setInt(1, ingredient.getIngredientId());
            ResultSet rs = pstmtIsIngThere.executeQuery();
            if (rs.next()) {
                int result = rs.getInt(1);
                if (result < 1) {
                    createIngredient(ingredient);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
