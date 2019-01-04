package org.finalweb.Service.CRUD;

import org.finalweb.Entity.History;
import org.finalweb.Entity.Product;
import org.finalweb.Entity.Receipt;
import org.finalweb.Entity.User;
import org.finalweb.Repository.HistorialRepositorio;
import org.finalweb.Repository.ArticuloRepositorio;
import org.finalweb.Repository.FacturaRepositorio;
import org.finalweb.Repository.UsuarioRepositorio;
import freemarker.template.utility.NullArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;

@Service
public class ServicioActualizacion {

    // Repositories
    @Autowired
    private HistorialRepositorio historialRepositorio;
    @Autowired
    private ArticuloRepositorio articuloRepositorio;
    @Autowired
    private FacturaRepositorio facturaRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    // History Updates
    public void updateRegisteredUserHistory(History history) throws Exception{

        if (history == null)
            throw new NullArgumentException("This history is void");

        try {
            historialRepositorio.save(history);
        } catch (PersistenceException exp){
            throw new PersistenceException("Persistence Error --> " + exp.getMessage());
        } catch (Exception exp){
            throw new Exception("General Error --> " + exp.getMessage());
        }
    }

    // Product Updates
    public void updateRegisteredProduct(Product product) throws Exception {

        if (product == null)
            throw new NullArgumentException("This product is void");

        try {
            articuloRepositorio.save(product);
        } catch (PersistenceException exp){
            throw new PersistenceException("Persistence Error --> " + exp.getMessage());
        } catch (Exception exp){
            throw new Exception("General Error --> " + exp.getMessage());
        }
    }

    // Receipt Updates
    public void updateRegisteredUserTransaction(Receipt receipt) throws Exception{

        if (receipt == null)
            throw new NullArgumentException("This transaction is void");

        try {
            facturaRepositorio.save(receipt);
        } catch (PersistenceException exp){
            throw new PersistenceException("Persistence Error --> " + exp.getMessage());
        } catch (Exception exp){
            throw new Exception("General Error --> " + exp.getMessage());
        }
    }

    // User and History Updates
    public void updateRegisteredUserAccount(User user) throws Exception {

        if (user == null)
            throw new NullArgumentException("This user has a null value");

        if (!isEmailAddressTaken(user.getEmail()))
            throw new IllegalArgumentException("This user account does not exist");

        try {
            // Updating user
            usuarioRepositorio.save(user);

            // Updating History
            History history = historialRepositorio.findByUser(user.getEmail());

            history.setUser(user);

            historialRepositorio.save(history);
        } catch (PersistenceException exp){
            throw new PersistenceException("Persistence Error --> " + exp.getMessage());
        } catch (Exception exp){
            throw new Exception("General Error --> " + exp.getMessage());
        }
    }

    // Auxiliary Functions
    private boolean isEmailAddressTaken(String email){
        User user = usuarioRepositorio.findByEmail(email);
        return (user != null);
    }
}
