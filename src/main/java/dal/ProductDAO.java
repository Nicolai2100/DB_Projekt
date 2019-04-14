package dal;

import dal.dto.*;

import java.sql.*;

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


    public void createRecipe(RecipeDTO recipeDTO, int userID) {
        try {
            IUserDTO userDTO = userDAO.getUser(userID);
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
            insertProduct.setInt(3, userID);
            //Mangler listen af ingredienser
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
                            "VALUES(?,?)");

            insertIngredient.setInt(1, ingredientDTO.getIngredientId());
            insertIngredient.setString(2, ingredientDTO.getType());
            insertIngredient.executeUpdate();
            conn.commit();
            System.out.println("The ingredient was successfully created.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public IIngredientDTO getIngredient(int ingredientId){
        IIngredientDTO ingredientDTO = new IngredientDTO();
        try {
            PreparedStatement getIngredient = conn.prepareStatement(
                    "SELECT FROM ingredient " +
                            "WHERE ingredientid = ?;");
            getIngredient.setInt(1, ingredientId);
            ResultSet rs = getIngredient.executeQuery();

            while (rs.next()){
                ingredientDTO.setIngredientId(rs.getInt(1));
                ingredientDTO.setType(rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredientDTO;
    }

    public void createIngredientList(RecipeDTO recipeDTO){
        try {
            conn.setAutoCommit(false);
            PreparedStatement insertIngredient = conn.prepareStatement(
                    "INSERT INTO ingredientlist " +
                            "VALUES(?,?)");

            insertIngredient.setInt(1, recipeDTO.getRecipeId());

            for (int i = 0; i < recipeDTO.getIngredients().size(); i++) {
                String indgredient = recipeDTO.getIngredients().get(i);
                int ingredientid = getIngredientId(indgredient);
                insertIngredient.setInt(2, ingredientid);
                insertIngredient.executeUpdate();
            }
            conn.commit();
            System.out.println("The ingredientlist was successfully created.");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IUserDAO.DALException e) {
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