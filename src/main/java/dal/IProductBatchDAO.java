package dal;

import dal.dto.*;

import java.util.List;

public interface IProductBatchDAO {
    void createProductbatch(ProductbatchDTO productbatch) throws DALException;

    ProductbatchDTO getProductbatch(int productBatch) throws DALException;

    void updateProductBatch(ProductbatchDTO productbatch, UserDTO user) throws DALException;

    void initiateProduction(ProductbatchDTO productbatch, UserDTO user) throws DALException;

    void produceProductBatch(ProductbatchDTO productbatch, UserDTO user) throws DALException;

    void createRelations(List<ICommodityBatchDTO> commodityBatchList, int productbatchId) throws DALException;
}
