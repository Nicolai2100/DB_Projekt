package dal;

import dal.dto.ICommodityBatchDTO;
import dal.dto.IIngredientDTO;

import java.util.List;

public interface ICommodityBatchDAO {

    void createCommodityBatch(ICommodityBatchDTO commodityBatch) throws DALException;

    ICommodityBatchDTO getCommodityBatch(int commodityBatchId) throws DALException;

    void updateCommodityBatch(ICommodityBatchDTO commodityBatch) throws DALException;

    double getTotalCommodityAmountInKG(IIngredientDTO ingredient) throws DALException;

    List<ICommodityBatchDTO> getCommodityBatchList(IIngredientDTO ingredient) throws DALException;

    void checkForResidue() throws DALException;

    List<ICommodityBatchDTO> getAllCommodityBatchList() throws DALException;

}
