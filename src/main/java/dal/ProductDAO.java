package dal;

import dal.dto.*;

import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ProductDAO {
    private Connection conn;
    private UserDAOImpl userDAO;

    public ProductDAO() {
        userDAO = new UserDAOImpl();
        try {
            conn = createConnection();
        } catch (IUserDAO.DALException e) {
            e.printStackTrace();
        }
    }

    public Connection getConn() {
        return conn;
    }

    public Connection createConnection() throws IUserDAO.DALException {
        String dataBase = "jdbc:mysql://ec2-52-30-211-3.eu-west-1.compute.amazonaws.com/jekala";
        String user = "jekala";
        String password = "d0czCtqcu5015NhwwP5zl";
        try {
            return DriverManager.getConnection(dataBase, user, password);
        } catch (SQLException e) {
            throw new IUserDAO.DALException(e.getMessage());
        }
    }

    public void createProduct(ProductDTO product) {
        if (!product.getMadeBy().getRoles().contains("productionleader")) {
            System.out.println("User not authorized to proceed!");
            return;
        }
        try {
            conn.setAutoCommit(false);
            PreparedStatement insertProduct = conn.prepareStatement(
                    "INSERT INTO product " +
                            "VALUES(?,?,?,?)");

            insertProduct.setInt(1, product.getProductId());
            insertProduct.setString(2, product.getName());
            insertProduct.setInt(3, product.getMadeBy().getUserId());
            insertProduct.setInt(4, product.getRecipe());
            insertProduct.executeUpdate();
            conn.commit();
            System.out.println("The product was successfully created.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void createRecipe(RecipeDTO recipeDTO) {
        try {
            IUserDTO userDTO = userDAO.getUser(recipeDTO.getMadeBy());
            if (!userDTO.getRoles().contains("farmaceut")) {
                System.out.println("User not authorized to proceed!");
                return;
            }

        } catch (IUserDAO.DALException e) {
            e.printStackTrace();
        }

        try {
            conn.setAutoCommit(false);
            PreparedStatement insertProduct = conn.prepareStatement(
                    "INSERT INTO recipe " +
                            "VALUES(?,?,?,?)");

            insertProduct.setInt(1, recipeDTO.getRecipeId());
            insertProduct.setString(2, recipeDTO.getName());
            insertProduct.setInt(3, recipeDTO.getMadeBy());
            //Hver liste af ingredienser bliver oprettet med opskriftens id som id... !?
            insertProduct.setInt(4, recipeDTO.getRecipeId());

            createIngredientList(recipeDTO);
            insertProduct.executeUpdate();
            conn.commit();
            System.out.println("The recipe was successfully created.");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    //Liste af recipes...

    public void createIngredient(IngredientDTO ingredientDTO) {
        try {
            conn.setAutoCommit(false);
            PreparedStatement insertIngredient = conn.prepareStatement(
                    "INSERT INTO ingredient " +
                            "VALUES(?,?,?)");

            insertIngredient.setInt(1, ingredientDTO.getIngredientId());
            insertIngredient.setString(2, ingredientDTO.getName());
            insertIngredient.setString(3,ingredientDTO.getType());
            insertIngredient.executeUpdate();
            conn.commit();
            System.out.println("The ingredient was successfully created.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public IIngredientDTO getIngredient(int ingredientId) {
        IIngredientDTO ingredientDTO = new IngredientDTO();
        try {
            PreparedStatement getIngredient = conn.prepareStatement(
                    "SELECT FROM ingredient " +
                            "WHERE ingredientid = ?;");
            getIngredient.setInt(1, ingredientId);
            ResultSet rs = getIngredient.executeQuery();

            while (rs.next()) {
                ingredientDTO.setIngredientId(rs.getInt(1));
                ingredientDTO.setType(rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredientDTO;
    }

    public void createIngredientList(RecipeDTO recipeDTO) {
        HashMap<String, IngredientDTO> ingredients = new HashMap<>();
        ingredients = recipeDTO.getIngredients();
        try {
            conn.setAutoCommit(false);
            PreparedStatement insertIngredientList = conn.prepareStatement(
                    "INSERT INTO ingredientlist " +
                            "VALUES(?,?,?)");

            insertIngredientList.setInt(1, recipeDTO.getRecipeId());

            Iterator it = ingredients.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                IngredientDTO ingredientDTO = (IngredientDTO) pair.getValue();
                insertIngredientList.setInt(2, ingredientDTO.getIngredientId());
                insertIngredientList.setDouble(3, ingredientDTO.getAmountInMG());
                insertIngredientList.executeUpdate();
                it.remove(); // avoids a ConcurrentModificationException
            }
            conn.commit();
            System.out.println("The ingredient list was successfully created.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Metoden henter et specifikt ingrediensid  fra ingredient-tabellen og returnerer den.
     */
    public int getIngredientId(String type) throws IUserDAO.DALException {
        int returnInt = 0;
        try {
            PreparedStatement pStmtSelectRoleId = conn.prepareStatement(
                    "SELECT  ingredientid  " +
                            "FROM ingredient " +
                            "WHERE type = ?");
            pStmtSelectRoleId.setString(1, type);

            ResultSet rs = pStmtSelectRoleId.executeQuery();
            if (rs.next()) {
                returnInt = rs.getInt("ingredientid");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if (returnInt == 0) {
            // Hvis den returnerer 0 betyder det at ingrediensen ikke findes, og derfor skal den oprettes. - laves senere

/*          todo laves senere
            returnInt = createNewRole(conn, role);
*/
        }
        return returnInt;
    }

}