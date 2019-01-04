package org.finalweb.Service.CRUD;

import org.finalweb.Entity.History;
import org.finalweb.Entity.Product;
import org.finalweb.Repository.HistorialRepositorio;
import org.finalweb.Repository.ArticuloRepositorio;
import org.finalweb.Repository.FacturaRepositorio;
import org.finalweb.Tools.Enums.EstadoOrden;
import freemarker.template.utility.NullArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import java.util.Set;

@Service
public class ServicioEliminar {

    // Repositories
    @Autowired
    private HistorialRepositorio historialRepositorio;
    @Autowired
    private ArticuloRepositorio articuloRepositorio;
    @Autowired
    private FacturaRepositorio facturaRepositorio;

    // Deletes
    public void deleteRegisteredProduct(Integer productId) throws Exception{

        if (!doesProductIdExist(productId))
            throw new IllegalArgumentException("This product does not exists");

        try {
            Product product = articuloRepositorio.findByProductId(productId);

            // Applying Cascade
            for (History history:
                 historialRepositorio.findAll()) {
                // Fetching sets
                Set<Product> browsingHistory = history.getBrowsingHistory();
                Set<Product> shoppingCart = history.getShoppingCart();

                // Removing the item
                browsingHistory.remove(product);
                shoppingCart.remove(product);

                // Update History
                history.setBrowsingHistory(browsingHistory);
                history.setShoppingCart(shoppingCart);
                historialRepositorio.save(history);
            }

            // Deleting Product
            articuloRepositorio.delete(product);
        } catch (PersistenceException exp){
            throw new PersistenceException("Persistence Error --> " + exp.getMessage());
        } catch (NullArgumentException exp){
            throw new NullArgumentException("Null Argument Error --> " + exp.getMessage());
        } catch (Exception exp){
            throw new Exception("General Error --> " + exp.getMessage());
        }
    }

    public void deleteRegisteredPendingTransaction(String fiscalCode) throws Exception{

        if (facturaRepositorio.findByFiscalCode(fiscalCode).getStatus() == EstadoOrden.PENDING)
            throw new IllegalArgumentException("This is an illegal action! You cannot delete a pending transaction");

        try {
            facturaRepositorio.delete(fiscalCode);
        } catch (PersistenceException exp){
            throw new PersistenceException("Persistence Error --> " + exp.getMessage());
        } catch (NullArgumentException exp){
            throw new NullArgumentException("Null Argument Error --> " + exp.getMessage());
        } catch (Exception exp){
            throw new Exception("General Error --> " + exp.getMessage());
        }
    }

    // Auxiliary Functions
    private boolean doesProductIdExist(Integer productId){
        Product product = articuloRepositorio.findByProductId(productId);
        return (product != null);
    }
}
