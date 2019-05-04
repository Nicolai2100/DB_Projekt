package dal;

import dal.dto.*;

import java.util.List;

public interface IProductBatchDAO {
    void createProductbatch(IProductBatchDTO productbatch) throws DALException;

    ProductBatchDTO getProductbatch(int productBatchID) throws DALException;

    void updateProductBatch(IProductBatchDTO productbatch, IUserDTO user) throws DALException;

    void initiateProduction(IProductBatchDTO productbatch, IUserDTO user) throws DALException;

    void produceProductBatch(IProductBatchDTO productbatch, IUserDTO user) throws DALException;

    void createRelations(List<ICommodityBatchDTO> commodityBatchList, int productbatchId) throws DALException;

    List<IProductBatchDTO> getProductsOrdered() throws DALException;

    List<IProductBatchDTO> getProductsUnderProduction() throws DALException;

    List<IProductBatchDTO> getProductsCompleted() throws DALException;

}
