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

    //todo
    // Vi kunne godt tænke os at det er muligt at udsøge produktbatches,
    // der er hhv. bestilt, under produktion og færdiggjort.

    public void createProduct(ProductDTO product) {
        //kontroller om han er aktiv i systemet
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


    public void createCommodityBatch(ICommodityBatchDTO commodityBatch) {
        IUserDTO userDTO = commodityBatch.getOrderedBy();
        if (!userDTO.getRoles().contains("productleader")) {
            System.out.println("User not authorized to proceed!");
            return;
        }
        try {
            PreparedStatement pstmtInsertCommodityBatch = conn.prepareStatement(
                    "INSERT INTO commoditybatch " +
                            "VALUES(?,?,?,?,?,?)");
            pstmtInsertCommodityBatch.setInt(1, commodityBatch.getBatchId());
            pstmtInsertCommodityBatch.setInt(2, commodityBatch.getIngredientDTO().getIngredientId());
            pstmtInsertCommodityBatch.setInt(3, commodityBatch.getOrderedBy().getUserId());
            pstmtInsertCommodityBatch.setDouble(4, commodityBatch.getAmountInKg());
            pstmtInsertCommodityBatch.setString(5, commodityBatch.getOrderDate());
            pstmtInsertCommodityBatch.setBoolean(6, false);
            pstmtInsertCommodityBatch.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ICommodityBatchDTO getCommodityBatch(int commodityBatchId) {
        ICommodityBatchDTO commodityBatch = new CommodityBatchDTO();
        commodityBatch.setBatchId(commodityBatchId);
        IIngredientDTO ingredientDTO = new IngredientDTO();
        IUserDTO userDTO = new UserDTO();
        try {
            PreparedStatement pstmtGetCommodityBatch = conn.prepareStatement(
                    "SELECT * FROM commoditybatch " +
                            "JOIN ingredient ON ingredient.ingredientid " +
                            "JOIN user " +
                            "ON orderedby = userid " +
                            "WHERE commoditybatchid = ?;");

            pstmtGetCommodityBatch.setInt(1, commodityBatchId);

            ResultSet rs = pstmtGetCommodityBatch.executeQuery();
            while (rs.next()) {

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

    public void updateRecipe(IRecipeDTO recipeDTO) {
        IUserDTO userDTO = recipeDTO.getMadeBy();
        if (!userDTO.getRoles().contains("farmaceut") || !userDTO.getIsActive()) {
            System.out.println("User not authorized to proceed!");
            return;
        }
        try {
            conn.setAutoCommit(false);

            PreparedStatement pstmtGetEdition = conn.prepareStatement(
                    "SELECT edition " +
                            "FROM recipe " +
                            "WHERE recipeid = ?;");
            pstmtGetEdition.setInt(1, recipeDTO.getRecipeId());
            ResultSet rs = pstmtGetEdition.executeQuery();
            int edition = 1;
            if (rs.next()) {
                edition += rs.getInt(1);
            }
            PreparedStatement pstmtUpdateRecipe = conn.prepareStatement(
                    "UPDATE recipe " +
                            "SET " +
                            "edition = ?, " +
                            "name = ?, " +
                            "madeby = ?;");

            pstmtUpdateRecipe.setInt(1, edition);
            pstmtUpdateRecipe.setString(2, recipeDTO.getName());
            pstmtUpdateRecipe.setInt(3, recipeDTO.getMadeBy().getUserId());

            pstmtUpdateRecipe.executeUpdate();
            //Hver liste af ingredienser bliver oprettet med opskriftens id som id... !?
            updateIngredientList(recipeDTO, edition);
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
            int returnEdition = haveOldRecipe(recipeDTO.getRecipeId());
            if (returnEdition != 0) {
                edition += returnEdition;
            }

            conn.setAutoCommit(false);
            PreparedStatement pstmtInsertRecipe = conn.prepareStatement(
                    "INSERT INTO recipe " +
                            "VALUES(?,?,?,?,?)");
            pstmtInsertRecipe.setInt(1, recipeDTO.getRecipeId());
            pstmtInsertRecipe.setInt(2, edition);
            pstmtInsertRecipe.setString(3, recipeDTO.getName());
            pstmtInsertRecipe.setInt(4, recipeDTO.getMadeBy().getUserId());
            //Hver liste af ingredienser bliver oprettet med opskriftens id som id... !?
            pstmtInsertRecipe.setInt(5, recipeDTO.getRecipeId());

            isIngredientListCreated(recipeDTO, edition);
            pstmtInsertRecipe.executeUpdate();
            conn.commit();
            System.out.println("The recipe was successfully created.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int haveOldRecipe(int recipeId) {
        int edition = 0;
        try {
            PreparedStatement pstmtHaveOldRecipe = conn.prepareStatement(
                    "SELECT MAX(edition) " +
                            "FROM oldrecipe " +
                            "WHERE recipeid = ?;");
            pstmtHaveOldRecipe.setInt(1, recipeId);
            ResultSet rs = pstmtHaveOldRecipe.executeQuery();
            if (rs.next()) {
                edition = rs.getInt(1);
                System.out.println(edition);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return edition;
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
                recipeDTO.setName(rs.getString(3));
                recipeDTO.setMadeBy(userDAO.getUser(rs.getInt(4)));
                recipeDTO.setIngredientsList(getIngredientList(recipeDTO));
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
            PreparedStatement pstmtDeleteRecipe = conn.prepareStatement(
                    "DELETE FROM recipe " +
                            "WHERE recipeid = ?;");
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

    /**
     * Metoden undersøger om der allerede er en ingrediensliste, hvis ikke bliver der skabt en og hvis der er
     * gør der ikke.
     *
     * @param recipeDTO
     */
    public void isIngredientListCreated(IRecipeDTO recipeDTO, int edition) {
        try {
            PreparedStatement pstmtGetIngredientList = conn.prepareStatement(
                    "SELECT COUNT(*) FROM ingredientlist " +
                            "WHERE ingredientlistid = ? " +
                            "AND edition = ?;");
            pstmtGetIngredientList.setInt(1, recipeDTO.getRecipeId());
            pstmtGetIngredientList.setInt(2, edition);

            ResultSet rs = pstmtGetIngredientList.executeQuery();
            int result = 0;
            if (rs.next()) {
                result = rs.getInt(1);
            }
            if (result > 0) {
                return;
            } else {
                createIngredientList(recipeDTO, edition);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateIngredientList(IRecipeDTO recipeDTO, int edition) {
        try {
            conn.setAutoCommit(false);
            PreparedStatement pstmtInsertIngredientList = conn.prepareStatement(
                    "INSERT INTO ingredientlist(ingredientlistid, edition, ingredientid, amountmg) " +
                            "VALUES(?,?,?,?)");
            pstmtInsertIngredientList.setInt(1, recipeDTO.getRecipeId());
            pstmtInsertIngredientList.setInt(2, edition);

            for (IIngredientDTO ingredient : recipeDTO.getIngredientsList()) {
                pstmtInsertIngredientList.setInt(3, ingredient.getIngredientId());
                pstmtInsertIngredientList.setDouble(4, ingredient.getAmount());
                pstmtInsertIngredientList.executeUpdate();
            }
            conn.commit();
            System.out.println("The ingredientlist was successfully updated.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createIngredientList(IRecipeDTO recipeDTO, int edition) {
        try {
            for (IIngredientDTO ingredient: recipeDTO.getIngredientsList()) {
                isIngredientThere(ingredient);
            }
            conn.setAutoCommit(false);
            PreparedStatement pstmtInsertIngredientList = conn.prepareStatement(
                    "INSERT INTO ingredientlist(ingredientlistid, edition, ingredientid, amountmg) " +
                            "VALUES(?,?,?,?)");
            pstmtInsertIngredientList.setInt(1, recipeDTO.getRecipeId());

            for (IIngredientDTO ingredient : recipeDTO.getIngredientsList()) {
                pstmtInsertIngredientList.setInt(2, edition);
                pstmtInsertIngredientList.setInt(3, ingredient.getIngredientId());
                pstmtInsertIngredientList.setDouble(4, ingredient.getAmount());
                pstmtInsertIngredientList.executeUpdate();
            }
            conn.commit();
            System.out.println("The ingredientlist was successfully created.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opretter et ingrediens i DB, hvis der ikke findes et
     * med dets id
     * @param ingredient
     */
    private void isIngredientThere(IIngredientDTO ingredient) {
        try {
            PreparedStatement pstmtIsIngThere = conn.prepareStatement(
                    "SELECT COUNT(*) " +
                            "FROM ingredient " +
                            "WHERE ingredientid = ?;");
            pstmtIsIngThere.setInt(1, ingredient.getIngredientId());
           ResultSet rs = pstmtIsIngThere.executeQuery();
           if (rs.next()){
               int result = rs.getInt(1);
               if (result < 1){
                   createIngredient(ingredient);
               }
           }
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
                IIngredientDTO ingredientDTO = getIngredient(rs.getInt(3));
                ingredientDTO.setAmount(rs.getDouble(4));
                ingredientList.add(ingredientDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingredientList;

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

    public void createTriggerReorder() {
        try {
            PreparedStatement pstmtCreateTriggerReorder = conn.prepareStatement(
                    "CREATE TRIGGER set_reorder " +
                            "AFTER INSERT ON commoditybatch " +
                            "FOR EACH ROW " +
                            "BEGIN " +
                            "UPDATE ingredient " +
                            "SET ingredient.reorder = 0 " +
                            "WHERE ingredient.ingredientid = new.ingredientid; " +
                            "END;");

            pstmtCreateTriggerReorder.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void createTriggerOldRecipe() {
        try {
            PreparedStatement pstmtCreateTriggerSaveDeletedRecipe = conn.prepareStatement(
                    "CREATE TRIGGER save_recipe_delete " +
                            "BEFORE DELETE ON recipe " +
                            "FOR EACH ROW " +
                            "BEGIN " +
                            "INSERT INTO oldrecipe " +
                            "VALUES " +
                            "(old.recipeid,  " +
                            "old.edition, " +
                            "old.name, " +
                            "old.madeby, " +
                            "old.ingredientlistid, " +
                            "NOW()); " +
                            "END;");

            PreparedStatement pstmtCreateTriggerSaveUpdatedRecipe = conn.prepareStatement(
                    "CREATE TRIGGER save_recipe_update " +
                            "BEFORE UPDATE ON recipe " +
                            "FOR EACH ROW " +
                            "BEGIN " +
                            "INSERT INTO oldrecipe " +
                            "VALUES " +
                            "(old.recipeid,  " +
                            "old.edition, " +
                            "old.name, " +
                            "old.madeby, " +
                            "old.ingredientlistid, " +
                            "NOW()); " +
                            "END;");

            pstmtCreateTriggerSaveUpdatedRecipe.execute();
            pstmtCreateTriggerSaveDeletedRecipe.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dropTriggers() {
        try {

            PreparedStatement pstmtDropTriggerReorder = conn.prepareStatement(
                    "DROP TRIGGER IF EXISTS set_reorder;");

            PreparedStatement pstmtDropSaveRecipeDeleteTrigger = conn.prepareStatement(
                    "DROP TRIGGER IF EXISTS save_recipe_delete;");

            PreparedStatement pstmtDropSaveRecipeUpdateTrigger = conn.prepareStatement(
                    "DROP TRIGGER IF EXISTS save_recipe_update"
            );
            pstmtDropSaveRecipeDeleteTrigger.execute();
            pstmtDropSaveRecipeUpdateTrigger.execute();
            pstmtDropTriggerReorder.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}