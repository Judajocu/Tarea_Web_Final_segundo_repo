package org.finalweb.Service.CRUD;

import org.finalweb.Entity.History;
import org.finalweb.Entity.Product;
import org.finalweb.Entity.Receipt;
import org.finalweb.Entity.User;
import org.finalweb.Repository.HistorialRepositorio;
import org.finalweb.Repository.ArticuloRepositorio;
import org.finalweb.Repository.FacturaRepositorio;
import org.finalweb.Repository.UsuarioRepositorio;
import org.finalweb.Service.Auxiliary.ServicioEncryption;
import org.finalweb.Tools.Enums.Permiso;
import freemarker.template.utility.NullArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import java.util.ArrayList;

@Service
public class ServicioCreacion
{
    // Repositories
    @Autowired
    private HistorialRepositorio historialRepositorio;
    @Autowired
    private ArticuloRepositorio articuloRepositorio;
    @Autowired
    private FacturaRepositorio facturaRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private ServicioEncryption servicioEncryption;

    // Product Creation
    public Product registerNewProduct(String productName, String supplier, String productDescription, Float productPrice, Integer productInStock) throws Exception{

        if (productPrice <= 0.00f)
            throw new IllegalArgumentException("All price must be positive decimal numbers");

        if (productInStock < 0)
            throw new IllegalArgumentException("There must be at least one unit registered");

        try {
            return articuloRepositorio.save(new Product(productName, supplier, productDescription, productPrice, productInStock));
        } catch (PersistenceException exp){
            throw new PersistenceException("Persistence Error --> " + exp.getMessage());
        } catch (NullArgumentException exp){
            throw new NullArgumentException("Null Argument Error --> " + exp.getMessage());
        } catch (Exception exp){
            throw new Exception("General Error --> " + exp.getMessage());
        }
    }

    public Product registerNewProduct(Product p) throws Exception{

        if (p.getProductPrice() <= 0.00f)
            throw new IllegalArgumentException("All price must be positive decimal numbers");

        if (p.getProductInStock() < 0)
            throw new IllegalArgumentException("There must be at least one unit registered");

        try {
            return articuloRepositorio.save(p);
        } catch (PersistenceException exp){
            throw new PersistenceException("Persistence Error --> " + exp.getMessage());
        } catch (NullArgumentException exp){
            throw new NullArgumentException("Null Argument Error --> " + exp.getMessage());
        } catch (Exception exp){
            throw new Exception("General Error --> " + exp.getMessage());
        }
    }

    // Receipt Creation
    public Receipt registerTransaction(String email, ArrayList<Integer> productList, ArrayList<Integer> amount, Float total) throws Exception {

        if (!isEmailAddressTaken(email))
            throw new IllegalArgumentException("This user account does not exist");

        if (productList.isEmpty())
            throw new IllegalArgumentException("There needs to be purchased items to realize a transaction");

        if (total < 0.00f)
            throw new IllegalArgumentException("Nothing is free in life");

        if (productList.size() != amount.size())
            throw new IllegalStateException("An error occurred while registering items; productList size is no equal to amount size");

        try {
            return facturaRepositorio.save(new Receipt(usuarioRepositorio.findByEmail(email), productList, amount, total));
        } catch (PersistenceException exp){
            throw new PersistenceException("Persistence Error --> " + exp.getMessage());
        } catch (NullArgumentException exp){
            throw new NullArgumentException("Null Argument Error --> " + exp.getMessage());
        } catch (Exception exp){
            throw new Exception("General Error --> " + exp.getMessage());
        }
    }

    // User and History Creation
    public User registerNewUser(String email, String firstName, String lastName, String shippingAddress, String country, String city, String password, Permiso permiso) throws Exception{

        if (isEmailAddressTaken(email))
            throw new IllegalArgumentException("This user Account already exist");

        try {
            User user = usuarioRepositorio.save(new User(email, firstName, lastName,  shippingAddress, country, city, servicioEncryption.encryptPassword(password), permiso));
            historialRepositorio.save(new History(user)); // Creating the users history
            return user;
        } catch (PersistenceException exp){
            throw new PersistenceException("Persistence Error --> " + exp.getMessage());
        } catch (NullArgumentException exp){
            throw new NullArgumentException("Null Argument Error --> " + exp.getMessage());
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
