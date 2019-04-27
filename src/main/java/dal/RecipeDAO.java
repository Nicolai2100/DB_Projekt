package dal;

import dal.dto.IRecipeDTO;
import dal.dto.IUserDTO;
import dal.dto.RecipeDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RecipeDAO {
    private Connection conn;
    private IngredientListDAO ingredientListDAO;
    private UserDAO userDAO;
    private OldRecipeDAO oldRecipeDAO;

    public OldRecipeDAO getOldRecipeDAO() {
        return oldRecipeDAO;
    }

    public RecipeDAO(IngredientListDAO ingredientListDAO, UserDAO userDAO) {
        this.oldRecipeDAO = new OldRecipeDAO(this);
        this.ingredientListDAO = ingredientListDAO;
        this.userDAO = userDAO;
        this.conn = ConnectionDAO.getConnection();

    }

    public void updateRecipe(IRecipeDTO recipeDTO) {
        IUserDTO userDTO = recipeDTO.getMadeBy();
        if (!userDTO.getRoles().contains("farmaceut") || !userDTO.getIsActive()) {
            System.out.println("User not authorized to proceed!");
            return;
        }
        try {
            conn.setAutoCommit(false);
            String selectEditionString = "SELECT edition FROM recipe WHERE recipeid = ?;";
            PreparedStatement pstmtGetEdition = conn.prepareStatement(selectEditionString);
            pstmtGetEdition.setInt(1, recipeDTO.getRecipeId());
            ResultSet rs = pstmtGetEdition.executeQuery();
            int edition = 1;
            if (rs.next()) {
                edition += rs.getInt(1);
            }
            String uddateRecipeString = "UPDATE recipe SET edition = ?, name = ?, madeby = ?;";
            PreparedStatement pstmtUpdateRecipe = conn.prepareStatement(uddateRecipeString);

            pstmtUpdateRecipe.setInt(1, edition);
            pstmtUpdateRecipe.setString(2, recipeDTO.getName());
            pstmtUpdateRecipe.setInt(3, recipeDTO.getMadeBy().getUserId());

            pstmtUpdateRecipe.executeUpdate();
            //Hver liste af ingredienser bliver oprettet med opskriftens id som id... !?
            ingredientListDAO.updateIngredientList(recipeDTO, edition);
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createRecipe(IRecipeDTO recipeDTO) {
        //Først undersøges det om brugeren, der står på til at have oprettet opskriften har
        //den rette rolle til at kunne gøre det.
        IUserDTO userDTO = recipeDTO.getMadeBy();
        if (!userDTO.getRoles().contains("farmaceut") || !userDTO.getIsActive()) {
            System.out.println("User not authorized to proceed!");
            return;
        }
        int edition = 1;
        try {
            //Hvis en opskriften oprettes med det samme id som ligger i oldrecipe,
            //må man gå ud fra at den nye opskrift er en ny udgave af den fra oldrecipe.
            //Derfor får den automatisk et korrekt edition-nummer.
            int returnEdition = oldRecipeDAO.haveOldRecipe(recipeDTO.getRecipeId());
            if (returnEdition != 0) {
                edition += returnEdition;
            }
            conn.setAutoCommit(false);
            String insertRecipeString = "INSERT INTO recipe VALUES(?,?,?,?,?)";
            PreparedStatement pstmtInsertRecipe = conn.prepareStatement(insertRecipeString);
            pstmtInsertRecipe.setInt(1, recipeDTO.getRecipeId());
            pstmtInsertRecipe.setInt(2, edition);
            pstmtInsertRecipe.setString(3, recipeDTO.getName());
            pstmtInsertRecipe.setInt(4, recipeDTO.getMadeBy().getUserId());
            pstmtInsertRecipe.setInt(5, recipeDTO.getRecipeId());
            //Opretter ingrediensliste
            ingredientListDAO.isIngredientListCreated(recipeDTO, edition);
            pstmtInsertRecipe.executeUpdate();
            conn.commit();
            System.out.println("The recipe was successfully created.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public IRecipeDTO getRecipe(int recipeId) {
        IRecipeDTO recipeDTO = new RecipeDTO();
        try {
            String getRecipeString = "SELECT * FROM recipe WHERE recipeid = ?;";
            PreparedStatement pstmtGetRecipe = conn.prepareStatement(getRecipeString);
            pstmtGetRecipe.setInt(1, recipeId);
            ResultSet rs = pstmtGetRecipe.executeQuery();
            while (rs.next()) {
                recipeDTO.setRecipeId(recipeId);
                recipeDTO.setName(rs.getString(3));
                recipeDTO.setMadeBy(userDAO.getUser(rs.getInt(4)));
                recipeDTO.setIngredientsList(ingredientListDAO.getIngredientList(recipeDTO));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IUserDAO.DALException e) {
            e.printStackTrace();
        }
        return recipeDTO;
    }

    public void deleteRecipe(int recipeId, IUserDTO userDTO) {
        if (!userDTO.getRoles().contains("farmaceut") || !userDTO.getIsActive()) {
            System.out.println("User not authorized to proceed!");
            return;
        }
        try {
            String deleteRecipeString = "DELETE FROM recipe WHERE recipeid = ?;";
            PreparedStatement pstmtDeleteRecipe = conn.prepareStatement(deleteRecipeString);
            pstmtDeleteRecipe.setInt(1, recipeId);
            int result = pstmtDeleteRecipe.executeUpdate();
            if (result < 1) {
                System.out.println("Error! No such recipe in the system!");
            } else {
                System.out.println("The recipe with id: " + recipeId + " was successfully deleted.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("The recipe was not deleted.");
        }
    }
}
