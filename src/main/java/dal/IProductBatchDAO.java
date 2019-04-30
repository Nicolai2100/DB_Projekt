package dal;

import dal.dto.*;

import java.util.List;

public interface IProductBatchDAO {
    void createProductbatch(ProductBatchDTO productbatch) throws DALException;

    ProductBatchDTO getProductbatch(int productBatch) throws DALException;

    void updateProductBatch(ProductBatchDTO productbatch, IUserDTO user) throws DALException;

    void initiateProduction(ProductBatchDTO productbatch, IUserDTO user) throws DALException;

    void produceProductBatch(ProductBatchDTO productbatch, IUserDTO user) throws DALException;

    void createRelations(List<ICommodityBatchDTO> commodityBatchList, int productbatchId) throws DALException;
}
