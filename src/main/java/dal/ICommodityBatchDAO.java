package dal;

import dal.dto.ICommodityBatchDTO;

public interface ICommodityBatchDAO {

    void createCommodityBatch(ICommodityBatchDTO commodityBatch) throws DALException;

    ICommodityBatchDTO getCommodityBatch(int commodityBatchId) throws DALException;

    void updateCommodityBatch(ICommodityBatchDTO commodityBatch) throws DALException;

/* todo slet
    void deleteCommodityBatch(int commodityBatchId) throws DALException;
*/
}
