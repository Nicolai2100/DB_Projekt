package dal;

import dal.dto.IIngredientDTO;
import dal.dto.IngredientDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IngredientDAO implements IIngredientDAO {
    private Connection conn;

    public IngredientDAO() throws DALException {
        this.conn = ConnectionDAO.getConnection();
    }

    @Override
    public void createIngredient(IIngredientDTO ingredientDTO) throws DALException {
        try {
            conn.setAutoCommit(false);
            String insertIngString = "INSERT INTO ingredient VALUES(?,?,?,?,?);";
            PreparedStatement pstmtInsertIngredient = conn.prepareStatement(insertIngString);
            pstmtInsertIngredient.setInt(1, ingredientDTO.getIngredientId());
            pstmtInsertIngredient.setString(2, ingredientDTO.getName());
            pstmtInsertIngredient.setString(3, ingredientDTO.getType());
            pstmtInsertIngredient.setInt(4,0);
            pstmtInsertIngredient.setBoolean(5, false);
            pstmtInsertIngredient.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at IngredientDAO.");
        }
    }

    @Override
    public IIngredientDTO getIngredient(int ingredientId) throws DALException {
        IIngredientDTO ingredientDTO = new IngredientDTO();
        String getIngString = "SELECT * FROM ingredient WHERE ingredientid = ?;";
        try {
            PreparedStatement pstmtGetIngredient = conn.prepareStatement(getIngString);
            pstmtGetIngredient.setInt(1, ingredientId);
            ResultSet rs = pstmtGetIngredient.executeQuery();
            while (rs.next()) {
                ingredientDTO.setIngredientId(rs.getInt(1));
                ingredientDTO.setName(rs.getString(2));
                ingredientDTO.setType(rs.getString(3));
                ingredientDTO.setMinAmountMG(rs.getInt(4));
            }
        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at IngredientDAO.");
        }
        return ingredientDTO;
    }

    @Override
    public List<IIngredientDTO> checkForReorder() throws DALException {
        List<IIngredientDTO> toBeOrdered = new ArrayList<>();
        String getReorderString = "SELECT * FROM ingredient WHERE reorder = 1;";
        try {
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

    @Override
    public void setReorder(List<IIngredientDTO> ingList) throws DALException {
        String getReorderString = "UPDATE ingredient SET reorder = 1 WHERE ingredientid = ?;";
        try {
            PreparedStatement pstmtGetReorder = conn.prepareStatement(getReorderString);

            for (IIngredientDTO ing: ingList) {
                pstmtGetReorder.setInt(1,ing.getIngredientId());
                pstmtGetReorder.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at IngredientDAO.");
        }
    }
    /**
     * Finds the total remaining amount af a given ingredient.
     * @param ingredient the given ingredient.
     * @return the total remaining amount across productbatches in kg
     */
    @Override
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
