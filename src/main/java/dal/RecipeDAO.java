package dal;

import dal.dto.*;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RecipeDAO implements IRecipeDAO {
    private Connection conn;
    private IngredientListDAO ingredientListDAO;
    private UserDAO userDAO;
    private CommodityBatchDAO commodityBatchDAO;
    private IngredientDAO ingredientDAO;

    public RecipeDAO(IngredientListDAO ingredientListDAO, IngredientDAO ingredientDAO, UserDAO userDAO, CommodityBatchDAO commodityBatchDAO) throws DALException {
        this.ingredientListDAO = ingredientListDAO;
        this.userDAO = userDAO;
        this.commodityBatchDAO = commodityBatchDAO;
        this.ingredientDAO = ingredientDAO;
        this.conn = ConnectionDAO.getConnection();
    }

    @Override
    public void createRecipe(IRecipeDTO recipeDTO) throws DALException {
        IUserDTO userDTO = recipeDTO.getMadeBy();
        if (!userDTO.getRoles().contains("farmaceut") || !userDTO.getIsActive()) {
            throw new DALException("User not authorized to proceed!");
        }
        int version;
        if (recipeDTO.getVersion() == 0) {
            version = 1;
        } else {
            version = recipeDTO.getVersion();
        }
        String insertRecipeString = "INSERT INTO recipe (recipeid, version, name, madeby, " +
                "ingredientlistid, in_use, minbatchsize) VALUES(?,?,?,?,?,?,?)";
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmtInsertRecipe = conn.prepareStatement(insertRecipeString);
            pstmtInsertRecipe.setInt(1, recipeDTO.getRecipeId());
            pstmtInsertRecipe.setInt(2, version);
            pstmtInsertRecipe.setString(3, recipeDTO.getName());
            pstmtInsertRecipe.setInt(4, recipeDTO.getMadeBy().getUserId());
            pstmtInsertRecipe.setInt(5, recipeDTO.getRecipeId());
            pstmtInsertRecipe.setBoolean(6, true);
            pstmtInsertRecipe.setInt(7, recipeDTO.getMinBatchSize());

            ingredientListDAO.createIngredientList(recipeDTO, version);
            pstmtInsertRecipe.executeUpdate();
            conn.commit();
            if (version == 1) {
                System.out.println("The recipe was successfully created.");
            } else {
                System.out.println("The recipe was successfully updated.");
            }
            updateMinAmounts();
            checkReorder(recipeDTO);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException("An error occurred in the database at RecipeDAO.");
        }
    }

    @Override
    public IRecipeDTO getActiveRecipe(int recipeId) throws DALException {
        IRecipeDTO recipeDTO = new RecipeDTO();
        String getRecipeString = "SELECT * FROM recipe WHERE recipeid = ? AND in_use = 1;";
        try {
            PreparedStatement pstmtGetRecipe = conn.prepareStatement(getRecipeString);
            pstmtGetRecipe.setInt(1, recipeId);
            ResultSet rs = pstmtGetRecipe.executeQuery();
            while (rs.next()) {
                recipeDTO.setRecipeId(recipeId);
                recipeDTO.setVersion(rs.getInt(2));
                recipeDTO.setName(rs.getString(3));
                recipeDTO.setMadeBy(userDAO.getUser(rs.getInt(4)));
                recipeDTO.setIngredientsList(ingredientListDAO.getIngredientList(recipeDTO));
                recipeDTO.setMinBatchSize(rs.getInt(8));
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
            throw new DALException("User not authorized to proceed!");
        }
        String updateRecipeString = "UPDATE recipe SET in_use = ?, last_used_date = ? WHERE recipeid = ? and version = ?;";
        try {
            conn.setAutoCommit(false);
            int oldVersionInt = recipeDTO.getVersion();
            int newVersionInt = oldVersionInt + 1;
            recipeDTO.setVersion(newVersionInt);
            Instant instant = Instant.now();
            Timestamp timestamp = Timestamp.from(instant);
            PreparedStatement pstmtUpdateRecipe = conn.prepareStatement(updateRecipeString);
            pstmtUpdateRecipe.setBoolean(1, false);
            pstmtUpdateRecipe.setTimestamp(2, timestamp);
            pstmtUpdateRecipe.setInt(3, recipeDTO.getRecipeId());
            pstmtUpdateRecipe.setInt(4, oldVersionInt);
            pstmtUpdateRecipe.executeUpdate();
            createRecipe(recipeDTO);
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException("An error occurred in the database at RecipeDAO.");
        }
    }

    @Override
    public void archiveRecipe(int recipeId, IUserDTO userDTO) throws DALException {
        if (!userDTO.getRoles().contains("farmaceut") || !userDTO.getIsActive()) {
            throw new DALException("User not authorized to proceed!");
        }
        try {
            String deleteRecipeString = "UPDATE recipe SET in_use = 0 WHERE recipeid = ? AND in_use = 1;";
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

    @Override
    public List<IRecipeDTO> getListOfOldRecipes() throws DALException {
        List<IRecipeDTO> oldRecipes = new ArrayList<>();
        String getOldRecipesString = "SELECT * FROM recipe WHERE in_use = 0;";
        try {
            PreparedStatement pstmtGetOldRecipe = conn.prepareStatement(getOldRecipesString);
            ResultSet rs = pstmtGetOldRecipe.executeQuery();
            while (rs.next()) {
                IRecipeDTO recipeDTO = new RecipeDTO();
                recipeDTO.setRecipeId(rs.getInt(1));
                recipeDTO.setVersion(rs.getInt(2));
                recipeDTO.setName(rs.getString(3));
                recipeDTO.setMadeBy(userDAO.getUser(rs.getInt(4)));
                recipeDTO.setIngredientsList(ingredientListDAO.getIngredientList(recipeDTO));
                recipeDTO.setExpired(rs.getTimestamp(7));
                recipeDTO.setMinBatchSize(rs.getInt(8));
                oldRecipes.add(recipeDTO);
            }
        } catch (SQLException e) {
            throw new DALException("An error occurred in the database at RecipeDAO.");
        }
        return oldRecipes;
    }

    private void updateMinAmounts() throws DALException {
        try {
            conn.setAutoCommit(false);
            //Dette query returnerer ingredientid, mindste mængde forekommende(ingrediens) og minimumamount
            String minAmountsString = "SELECT ingredientlist.ingredientid, min(amountmg*minbatchsize) AS amount, minamountinmg " +
                    "FROM ingredientlist JOIN recipe ON ingredientlist.ingredientlistid = recipe.ingredientlistid " +
                    "JOIN ingredient ON ingredient.ingredientid = ingredientlist.ingredientid WHERE in_use = 1 " +
                    "GROUP BY ingredientid ASC;";
            String updateIngString = "UPDATE ingredient " +
                    "SET minamountinmg = ? " +
                    "WHERE ingredientid = ?";
            PreparedStatement preparedStatementAmounts = conn.prepareStatement(minAmountsString);
            ResultSet resultSet = preparedStatementAmounts.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getDouble("amount") > resultSet.getDouble("minamountinmg")) {
                    PreparedStatement preparedStatementNewMin = conn.prepareStatement(updateIngString);
                    preparedStatementNewMin.setInt(1, resultSet.getInt("amount"));
                    preparedStatementNewMin.setInt(2, resultSet.getInt("ingredientid"));
                    preparedStatementNewMin.executeUpdate();
                }
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException("An error occurred in the database at RecipeDAO.");
        }
    }

    private void checkReorder(IRecipeDTO newRecipeDTO) throws DALException {
        List<ICommodityBatchDTO> commodities = commodityBatchDAO.getAllCommodityBatchList();
        List<IIngredientDTO> ingredientDTOS = ingredientListDAO.getIngredientList(newRecipeDTO);
        List<IIngredientDTO> ingToBeReordered = new ArrayList<>();
        for (ICommodityBatchDTO commod : commodities) {
            if (commod.getAmountInKg() * 1000000 < commod.getIngredientDTO().getMinAmountMG()) {
                ingToBeReordered.add(commod.getIngredientDTO());
            }
        }
        if (commodities.size() == 0) {
            ingToBeReordered = ingredientDTOS;
        }
        ingredientDAO.setReorder(ingToBeReordered);
    }
}

