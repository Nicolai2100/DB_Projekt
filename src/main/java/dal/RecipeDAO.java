package dal;

import dal.dto.IRecipeDTO;
import dal.dto.IUserDTO;
import dal.dto.RecipeDTO;

import java.sql.*;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RecipeDAO implements IRecipeDAO {
    private Connection conn;
    private IngredientListDAO ingredientListDAO;
    private UserDAO userDAO;

    public RecipeDAO(IngredientListDAO ingredientListDAO, UserDAO userDAO) throws DALException {
        this.ingredientListDAO = ingredientListDAO;
        this.userDAO = userDAO;
        this.conn = ConnectionDAO.getConnection();

    }

    @Override
    public void createRecipe(IRecipeDTO recipeDTO) throws DALException {
        //Først undersøges det om brugeren, der står på til at have oprettet opskriften har
        //den rette rolle til at kunne gøre det.
        IUserDTO userDTO = recipeDTO.getMadeBy();
        if (!userDTO.getRoles().contains("farmaceut") || !userDTO.getIsActive()) {
            System.out.println("User not authorized to proceed!");
            return;
        }

        int version;
        if (recipeDTO.getVersion() == 0) {
            version = 1;
        } else {
            version = recipeDTO.getVersion();
        }
        try {
            //Hvis en opskriften oprettes med det samme id som ligger i oldrecipe,
            //må man gå ud fra at den nye opskrift er en ny udgave af den fra oldrecipe.
            //Derfor får den automatisk et korrekt version-nummer.

            conn.setAutoCommit(false);
            String insertRecipeString = "INSERT INTO recipe (recipeid, version, name, madeby, ingredientlistid, in_use, minbatchsize) VALUES(?,?,?,?,?,?,?)";
            PreparedStatement pstmtInsertRecipe = conn.prepareStatement(insertRecipeString);
            pstmtInsertRecipe.setInt(1, recipeDTO.getRecipeId());
            pstmtInsertRecipe.setInt(2, version);
            pstmtInsertRecipe.setString(3, recipeDTO.getName());
            pstmtInsertRecipe.setInt(4, recipeDTO.getMadeBy().getUserId());
            pstmtInsertRecipe.setInt(5, recipeDTO.getRecipeId());
            pstmtInsertRecipe.setBoolean(6, true);
            pstmtInsertRecipe.setInt(7, recipeDTO.getMinBatchSize());
            //Opretter ingrediensliste todo slet og lav ordentlig
            ingredientListDAO.isIngredientListCreated(recipeDTO, version);
            pstmtInsertRecipe.executeUpdate();
            conn.commit();
            System.out.println("The recipe was successfully created.");
            updateMinAmounts();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException("An error occurred in the database at RecipeDAO.");
        }
    }

    @Override
    public IRecipeDTO getActiveRecipe(int recipeId) throws DALException {
        IRecipeDTO recipeDTO = new RecipeDTO();
        try {
            String getRecipeString = "SELECT * FROM recipe WHERE recipeid = ? AND in_use = 1;";
            PreparedStatement pstmtGetRecipe = conn.prepareStatement(getRecipeString);
            pstmtGetRecipe.setInt(1, recipeId);
            ResultSet rs = pstmtGetRecipe.executeQuery();
            while (rs.next()) {
                recipeDTO.setRecipeId(recipeId);
                recipeDTO.setName(rs.getString(3));
                recipeDTO.setMadeBy(userDAO.getUser(rs.getInt(4)));
                recipeDTO.setIngredientsList(ingredientListDAO.getIngredientList(recipeDTO));
                recipeDTO.setMinBatchSize(rs.getInt("minbatchsize"));
            }
        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at RecipeDAO.");
        }
        return recipeDTO;
    }

    @Override
    public void updateRecipe(IRecipeDTO recipeDTO) throws DALException {
        IUserDTO userDTO = recipeDTO.getMadeBy();
        if (!userDTO.getRoles().contains("farmaceut") || !userDTO.getIsActive()) {
            System.out.println("User not authorized to proceed!");
            return;
        }
        try {
            conn.setAutoCommit(true);
            String selectEditionString = "SELECT * FROM recipe WHERE recipeid = ? AND in_use = 1;";
            PreparedStatement pstmtGetEdition = conn.prepareStatement(selectEditionString);
            pstmtGetEdition.setInt(1, recipeDTO.getRecipeId());

            ResultSet rs = pstmtGetEdition.executeQuery();
            int versionInt = 1;
            if (rs.next()) {
                versionInt += rs.getInt(2);
                recipeDTO.setVersion(versionInt);
                createRecipe(recipeDTO);
            }
            ingredientListDAO.createIngredientList(recipeDTO, versionInt);

            Instant instant = Instant.now();
            Timestamp timestamp = Timestamp.from(instant);

            String updateRecipeString = "UPDATE recipe SET in_use = ?, last_used_date = ? WHERE recipeid = ? and version = ?;";
            PreparedStatement pstmtUpdateRecipe = conn.prepareStatement(updateRecipeString);
            pstmtUpdateRecipe.setBoolean(1, false);
            pstmtUpdateRecipe.setTimestamp(2, timestamp);
            pstmtUpdateRecipe.setInt(3, recipeDTO.getRecipeId());
            pstmtUpdateRecipe.setInt(4, versionInt);
            pstmtUpdateRecipe.executeUpdate();

            conn.commit();

            updateMinAmounts();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException("An error occurred in the database at RecipeDAO.");
        }
    }

    @Override
    public void archiveRecipe(int recipeId, IUserDTO userDTO) throws DALException {
        if (!userDTO.getRoles().contains("farmaceut") || !userDTO.getIsActive()) {
            System.out.println("User not authorized to proceed!");
            return;
        }
        try {
            String deleteRecipeString = "UPDATE recipe SET in_use = 0 WHERE recipeid = ?;";
            PreparedStatement pstmtDeleteRecipe = conn.prepareStatement(deleteRecipeString);
            pstmtDeleteRecipe.setInt(1, recipeId);
            int result = pstmtDeleteRecipe.executeUpdate();
            if (result < 1) {
                System.out.println("The recipe was not archived.");
            } else {
                System.out.println("The recipe with id: " + recipeId + " was successfully archived.");
            }

            updateMinAmounts();

        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at RecipeDAO.");
        }
    }

    public List<IRecipeDTO> getListOfOldRecipes() throws DALException {
        List<IRecipeDTO> oldRecipes = new ArrayList<>();
        try {
            String getOldRecipesString = "SELECT * FROM recipe WHERE in_use = 0;";
            PreparedStatement pstmtGetOldRecipe = conn.prepareStatement(getOldRecipesString);
            ResultSet rs = pstmtGetOldRecipe.executeQuery();
            while (rs.next()) {
                IRecipeDTO recipeDTO = new RecipeDTO();
                recipeDTO.setRecipeId(rs.getInt(1));
                recipeDTO.setVersion(rs.getInt(2));
                recipeDTO.setName(rs.getString(3));
                recipeDTO.setMadeBy(userDAO.getUser(rs.getInt(4)));
                recipeDTO.setIngredientsList(ingredientListDAO.getIngredientList(recipeDTO));
                ((RecipeDTO) recipeDTO).setExpired(rs.getTimestamp(7));
                recipeDTO.setMinBatchSize(rs.getInt(8));
            }
        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at RecipeDAO.");
        }
        return oldRecipes;

    }

    private void updateMinAmounts() throws SQLException {

        try {
            conn.setAutoCommit(false);

            //Dette query returnerer ingredientid, mindste mængde forekommende(ingrediens) og minimumamount
            PreparedStatement preparedStatementAmounts = conn.prepareStatement(
                    "SELECT ingredientlist.ingredientid, min(amountmg*minbatchsize) AS amount, minamountinmg " +
                            "FROM ingredientlist JOIN recipe ON ingredientlist.ingredientlistid = recipe.ingredientlistid " +
                            "JOIN ingredient ON ingredient.ingredientid = ingredientlist.ingredientid WHERE in_use = 1 " + //TODO in_use skal slettes, når nicolai er færdig med lege
                            "GROUP BY ingredientid ASC"
            );

            ResultSet resultSet = preparedStatementAmounts.executeQuery();

            while (resultSet.next()) {
                if (resultSet.getDouble("amount") < resultSet.getDouble("minamountinmg")) {
                    PreparedStatement preparedStatementNewMin = conn.prepareStatement(
                            "UPDATE ingredient " +
                                    "SET minamountinmg = ? " +
                                    "WHERE ingredientid = ?"
                    );
                    preparedStatementNewMin.setInt(1, resultSet.getInt("amount"));
                    preparedStatementNewMin.setInt(2, resultSet.getInt("ingredientid"));
                    preparedStatementNewMin.executeUpdate();
                }
            }

            conn.commit();
            conn.setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
