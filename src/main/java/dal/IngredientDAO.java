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

    public IngredientDAO() {
        this.conn = ConnectionDAO.getConnection();
    }

    public void createIngredient(IIngredientDTO ingredientDTO) throws DALException {
        try {
            conn.setAutoCommit(false);
            String insertIngString = "INSERT INTO ingredient VALUES(?,?,?,?,?);";
            PreparedStatement pstmtInsertIngredient = conn.prepareStatement(insertIngString);
            pstmtInsertIngredient.setInt(1, ingredientDTO.getIngredientId());
            pstmtInsertIngredient.setString(2, ingredientDTO.getName());
            pstmtInsertIngredient.setString(3, ingredientDTO.getType());
            pstmtInsertIngredient.setInt(4,0);
            pstmtInsertIngredient.setBoolean(5, true);
            pstmtInsertIngredient.executeUpdate();
            conn.commit();
            System.out.println("The ingredient was successfully created.");

        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at IngredientDAO.");
        }
    }

    public IIngredientDTO getIngredient(int ingredientId) throws DALException {
        IIngredientDTO ingredientDTO = new IngredientDTO();
        try {
            String getIngString = "SELECT * FROM ingredient WHERE ingredientid = ?;";
            PreparedStatement pstmtGetIngredient = conn.prepareStatement(getIngString);
            pstmtGetIngredient.setInt(1, ingredientId);
            ResultSet rs = pstmtGetIngredient.executeQuery();

            while (rs.next()) {
                ingredientDTO.setIngredientId(rs.getInt(1));
                ingredientDTO.setName(rs.getString(2));
                ingredientDTO.setType(rs.getString(3));
            }
        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at IngredientDAO.");
        }
        return ingredientDTO;
    }

    public List<IIngredientDTO> checkForReorder() throws DALException {
        List<IIngredientDTO> toBeOrdered = new ArrayList<>();
        try {
            String getReorderString = "SELECT * FROM ingredient WHERE reorder = 1;";
            PreparedStatement pstmtGetReorder = conn.prepareStatement(getReorderString);
            ResultSet rs = pstmtGetReorder.executeQuery();

            while (rs.next()) {
                IIngredientDTO ingredientDTO = new IngredientDTO();
                ingredientDTO.setIngredientId(rs.getInt(1));
                ingredientDTO.setName(rs.getString(2));
                ingredientDTO.setType(rs.getString(3));
                toBeOrdered.add(ingredientDTO);
            }
        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at IngredientDAO.");
        }

        return toBeOrdered;
    }

    /**
     * Opretter et ingrediens i DB, hvis der ikke findes et
     * med dets id
     *
     * @param ingredient
     */
    public void isIngredientThere(IIngredientDTO ingredient) throws DALException {
        try {
            String isIngThere = "SELECT COUNT(*) FROM ingredient WHERE ingredientid = ?;";
            PreparedStatement pstmtIsIngThere = conn.prepareStatement(isIngThere);
            pstmtIsIngThere.setInt(1, ingredient.getIngredientId());
            ResultSet rs = pstmtIsIngThere.executeQuery();
            if (rs.next()) {
                int result = rs.getInt(1);
                if (result < 1) {
                    createIngredient(ingredient);
                }
            }
        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at IngredientDAO.");
        }
    }

    /**
     * Finds the total remaining amount af a given ingredient.
     *
     * @param ingredient the given ingredient.
     * @return the total remaining amount across productbatches in kg
     */
    public double getTotalAmount(IIngredientDTO ingredient) throws DALException {
        double totalAmount = 0;

        try {
            String totalAmountString = "SELECT amountinkg FROM commoditybatch WHERE ingredientid=? AND residue = 0";
            PreparedStatement preparedStatement = conn.prepareStatement(totalAmountString);
            preparedStatement.setInt(1, ingredient.getIngredientId());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                totalAmount += resultSet.getDouble("amountinkg");
            }

        } catch (Exception e) {
            throw new DALException("An error occurred in the database at IngredientDAO.");
        }
        return totalAmount;
    }

}
