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
import org.finalweb.Tools.Enums.EstadoCuenta;
import org.finalweb.Tools.Enums.EstadoOrden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class ServicioLector {

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
    private HttpSession session;
    @Autowired
    private ServicioEncryption EncriptService;


    public Object getSessionAttr(String name)
    {
        return session.getAttribute(name);
    }

    public void setSessionAttr(String name,Object obj)
    {
        session.setAttribute(name,obj);
    }

    // Single Search
    public History findRegisteredUserHistory(String email) { return historialRepositorio.findByUser(email); }

    public Product findRegisteredProduct(Integer productId) { return articuloRepositorio.findByProductId(productId); }

    public Receipt findRegisteredTransaction(String fiscalCode) { return facturaRepositorio.findByFiscalCode(fiscalCode); }

    public User findRegisteredUserAccount(String email) { return usuarioRepositorio.findByEmail(email); } // Used for profiles

    public boolean findRegisteredUserAccount(String email, String password) {
        User user = usuarioRepositorio.findUserAccountWithUsernameAndPassword(email, EncriptService.encryptPassword(password));
        return (user != null);
    }

    // Complete Search
    public List<Product> findAllRegisteredProducts() { return articuloRepositorio.findAll(); }

    public List<Receipt> findAllRegisteredTransactions() { return facturaRepositorio.findAll(); }

    public List<User> findAllRegisteredAccounts() { return usuarioRepositorio.findAll(); }

    // Specific Search
    public List<Product> findRegisteredProductsWithName(String name) { return articuloRepositorio.findByName(name); }

    public List<Product> findRegisteredProductsFromSupplier(String supplier) { return articuloRepositorio.findBySupplier(supplier); }

    public List<Product> findRegisteredProductsByPriceRange(Float minPrice, Float maxPrice){

        if (minPrice < 0.00f || maxPrice < 0.00f)
            throw new IllegalArgumentException("Price range must be in the positive");

        if (minPrice < maxPrice)
            return articuloRepositorio.findByPriceRange(minPrice, maxPrice);
        else
            return articuloRepositorio.findByPriceRange(maxPrice, minPrice);
    }

    public List<Receipt> findRegisteredUserTransactions(String email) {

        if (!isEmailAddressTaken(email))
            throw new IllegalArgumentException("This user account does not exist");

        return facturaRepositorio.findByUser(email);
    }

    public List<Receipt> findRegisteredTransactionByStatus(EstadoOrden status) { return facturaRepositorio.findByOrderStatus(status); }

    public List<User> findRegisteredAccountsByStatus(EstadoCuenta status) { return usuarioRepositorio.findByAccountStatus(status); }
    // TODO: Add specific searches as the need comes

    // Auxiliary Functions
    private boolean isEmailAddressTaken(String email){
        User user = usuarioRepositorio.findByEmail(email);
        return (user != null);
    }

    public boolean isUserLoggedIn() {
        return null != session.getAttribute("user");
    }

    public void logOut()
    {
        session.invalidate();
    }

    public User getCurrentLoggedUser()
    {
        return (User)session.getAttribute("user");
    }

    // User Queries
    public User findUserInformation(String email) { return usuarioRepositorio.findByEmail(email); }



}
