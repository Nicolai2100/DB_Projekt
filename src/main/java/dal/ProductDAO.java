package dal;

import dal.dto.*;

import java.sql.*;
import java.util.*;

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

    /*Systemet skal således understøtte
  Oprettelse og administration af opskrifter med indholdsstoffer (Farmaceut)
  Oprettelse og administration af råvarebatches (Produktionsleder)
  Oprettelse og igangsætning af produktbatches (Produktionsleder)
  Produktion af produktbatches (Laborant)
  Lagerstatus af råvarer og råvarebatches (Produktionsleder)
  */
    public void createProduct(ProductDTO product) {
        if (!product.getMadeBy().getRoles().contains("productionleader")) {
            System.out.println("User not authorized to proceed!");
            return;
        }
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmtInsertProduct = conn.prepareStatement(
                    "INSERT INTO product " +
                            "VALUES(?,?,?,?)");
            pstmtInsertProduct.setInt(1, product.getProductId());
            pstmtInsertProduct.setString(2, product.getName());
            pstmtInsertProduct.setInt(3, product.getMadeBy().getUserId());
            pstmtInsertProduct.setInt(4, product.getRecipe());
            pstmtInsertProduct.executeUpdate();
            conn.commit();
            System.out.println("The product was successfully created.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*Systemet skal således understøtte
Oprettelse og administration af opskrifter med indholdsstoffer (Farmaceut)
Oprettelse og administration af råvarebatches (Produktionsleder)
Oprettelse og igangsætning af produktbatches (Produktionsleder)
Produktion af produktbatches (Laborant)
Lagerstatus af råvarer og råvarebatches (Produktionsleder)
*/

    public void createCommodityStock(){

        try{
            PreparedStatement pstmtInsertCommodityStock = conn.prepareStatement(
                    "INSERT INTO commoditystock "
            );
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getCommodityStock(){

    }

    public void createCommodityBatch(ICommodityBatchDTO commodityBatch) {
        IUserDTO userDTO = commodityBatch.getOrderedBy();
        if (!userDTO.getRoles().contains("productleader")) {
            System.out.println("User not authorized to proceed!");
            return;
        }
        try {
            PreparedStatement pstmtInsertProductBatch = conn.prepareStatement(
                    "INSERT INTO commoditybatch " +
                            "VALUES(?,?,?,?,?)");
            pstmtInsertProductBatch.setInt(1,commodityBatch.getBatchId());
            pstmtInsertProductBatch.setInt(2,commodityBatch.getIngredientDTO().getIngredientId());
            pstmtInsertProductBatch.setInt(3,commodityBatch.getOrderedBy().getUserId());
            pstmtInsertProductBatch.setDouble(4, commodityBatch.getAmountInKg());
            pstmtInsertProductBatch.setString(5, commodityBatch.getOrderDate());
            pstmtInsertProductBatch.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ICommodityBatchDTO getCommodityBatch(int commodityBatchId) {
        ICommodityBatchDTO commodityBatch = new CommodityBatchDTO();
        commodityBatch.setBatchId(commodityBatchId);
        IIngredientDTO ingredientDTO = new IngredientDTO();
        IUserDTO userDTO = new UserDTO();
        try{
            PreparedStatement pstmtGetCommodityBatch = conn.prepareStatement(
              "SELECT * FROM commoditybatch " +
                      "JOIN ingredient ON ingredient.ingredientid " +
                      "JOIN user " +
                      "ON orderedby = userid " +
                      "WHERE commoditybatchid = ?;");

            pstmtGetCommodityBatch.setInt(1,commodityBatchId);

            ResultSet rs = pstmtGetCommodityBatch.executeQuery();
            while (rs.next()){

                ingredientDTO.setIngredientId(rs.getInt(6));
                ingredientDTO.setName(rs.getString(7));
                ingredientDTO.setType(rs.getString(8));

                userDTO.setUserId(rs.getInt(9));
                userDTO.setUserName(rs.getString(10));
                userDTO.setIni(rs.getString(11));

                userDTO.setRoles(userDAO.getUserRoleList(userDTO.getUserId()));

                commodityBatch.setIngredientDTO(ingredientDTO);
                commodityBatch.setOrderedBy(userDTO);
                commodityBatch.setAmountInKg(rs.getDouble(4));
                commodityBatch.setOrderDate(rs.getString(5));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commodityBatch;
    }

    public void createRecipe(IRecipeDTO recipeDTO) {
        //Først undersøges det om brugeren, der står på til at have oprettet opskriften har
        //den rette rolle til at kunne gøre det.
        IUserDTO userDTO = recipeDTO.getMadeBy();
        if (!userDTO.getRoles().contains("farmaceut")) {
            System.out.println("User not authorized to proceed!");
            return;
        }
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmtInsertProduct = conn.prepareStatement(
                    "INSERT INTO recipe " +
                            "VALUES(?,?,?,?)");

            pstmtInsertProduct.setInt(1, recipeDTO.getRecipeId());
            pstmtInsertProduct.setString(2, recipeDTO.getName());
            pstmtInsertProduct.setInt(3, recipeDTO.getMadeBy().getUserId());
            //Hver liste af ingredienser bliver oprettet med opskriftens id som id... !?
            pstmtInsertProduct.setInt(4, recipeDTO.getRecipeId());

            peakIngredientList(recipeDTO);
            pstmtInsertProduct.executeUpdate();
            conn.commit();
            System.out.println("The recipe was successfully created.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public IRecipeDTO getRecipe(int recipeId) {
        IRecipeDTO recipeDTO = new RecipeDTO();
        try {
            PreparedStatement pstmtGetRecipe = conn.prepareStatement(
                    "SELECT * FROM recipe " +
                            "WHERE recipeid = ?;");
            pstmtGetRecipe.setInt(1, recipeId);
            ResultSet rs = pstmtGetRecipe.executeQuery();
            while (rs.next()) {
                recipeDTO.setRecipeId(recipeId);
                recipeDTO.setName(rs.getString(2));
                recipeDTO.setMadeBy(userDAO.getUser(rs.getInt(3)));
                recipeDTO.setIngredientsList(getIngredientList(recipeDTO));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IUserDAO.DALException e) {
            e.printStackTrace();
        }
        return recipeDTO;
    }

    /**
     * Metoden undersøger om der allerede er en ingrediensliste, hvis ikke bliver der skabt en og hvis der er
     * gør der ikke.
     *
     * @param recipeDTO
     */
    public void peakIngredientList(IRecipeDTO recipeDTO) {
        try {
            PreparedStatement pstmtGetIngredientList = conn.prepareStatement(
                    "SELECT COUNT(*) FROM ingredientlist " +
                            "WHERE ingredientlistid = ?;");
            pstmtGetIngredientList.setInt(1, recipeDTO.getRecipeId());
            ResultSet rs = pstmtGetIngredientList.executeQuery();
            int result = 0;
            if (rs.next()) {
                result = rs.getInt(1);
            }
            if (result > 0) {
                return;
            } else {
                createIngredientList(recipeDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createIngredientList(IRecipeDTO recipeDTO) {
    /*    HashMap<String, IngredientDTO> ingredients = new HashMap<>();
        ingredients = recipeDTO.getIngredients();*/
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmtInsertIngredientList = conn.prepareStatement(
                    "INSERT INTO ingredientlist " +
                            "VALUES(?,?,?)");

            pstmtInsertIngredientList.setInt(1, recipeDTO.getRecipeId());

            for (IIngredientDTO ingredient : recipeDTO.getIngredientsList()) {
                pstmtInsertIngredientList.setInt(2, ingredient.getIngredientId());
                pstmtInsertIngredientList.setDouble(3, ingredient.getAmount());
                pstmtInsertIngredientList.executeUpdate();
            }

           /* Iterator it = ingredients.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                IngredientDTO ingredientDTO = (IngredientDTO) pair.getValue();
                stmtInsertIngredientList.setInt(2, ingredientDTO.getIngredientId());
                stmtInsertIngredientList.setDouble(3, ingredientDTO.getAmountInMG());
                stmtInsertIngredientList.executeUpdate();
                it.remove(); // avoids a ConcurrentModificationException
                }*/

            conn.commit();
            System.out.println("The ingredientlist was successfully created.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                IIngredientDTO ingredientDTO = getIngredient(rs.getInt(1));
                ingredientDTO.setAmount(rs.getDouble(2));
                ingredientList.add(ingredientDTO);




            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredientList;

    }

    public void createIngredient(IngredientDTO ingredientDTO) {
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmtInsertIngredient = conn.prepareStatement(
                    "INSERT INTO ingredient " +
                            "VALUES(?,?,?)");

            pstmtInsertIngredient.setInt(1, ingredientDTO.getIngredientId());
            pstmtInsertIngredient.setString(2, ingredientDTO.getName());
            pstmtInsertIngredient.setString(3, ingredientDTO.getType());
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