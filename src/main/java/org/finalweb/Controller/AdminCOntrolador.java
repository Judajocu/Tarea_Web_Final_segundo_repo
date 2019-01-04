package org.finalweb.Controller;

import org.finalweb.Entity.Product;
import org.finalweb.Entity.Receipt;
import org.finalweb.Entity.User;
import org.finalweb.Service.Auxiliary.ServicioEstadistico;
import org.finalweb.Service.CRUD.ServicioCreacion;
import org.finalweb.Service.CRUD.ServicioEliminar;
import org.finalweb.Service.CRUD.ServicioLector;
import org.finalweb.Service.CRUD.ServicioActualizacion;
import org.finalweb.Tools.Enums.EstadoCuenta;
import org.finalweb.Tools.Enums.EstadoOrden;
import org.finalweb.Tools.Enums.Permiso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.websocket.server.PathParam;

@Controller
public class AdminCOntrolador implements ErrorController {

    // Services
    @Autowired
    private ServicioCreacion CDS;
    @Autowired
    private ServicioLector RDS;
    @Autowired
    private ServicioActualizacion UDS;
    @Autowired
    private ServicioEliminar DDS;
    @Autowired
    private ServicioEstadistico SS;
    private static final String ERR_PATH = "/error";

    @RequestMapping(value = ERR_PATH)
    public String error() {
        return "Backend/layouts/error";
    }

    @GetMapping("/admin")
    public ModelAndView index(Model model){

        if (!RDS.isUserLoggedIn())
            return new ModelAndView("redirect:/login");

        if (RDS.getCurrentLoggedUser().getRole() != Permiso.ADMIN)
            return new ModelAndView("redirect:/login");

        model.addAttribute("userRole", RDS.getCurrentLoggedUser().getRole());
        model.addAttribute("user", RDS.getSessionAttr("user"));

        if (RDS.getCurrentLoggedUser().getRole() != Permiso.ADMIN)
            model.addAttribute("isAdmin", false);
        else
            model.addAttribute("isAdmin", true);

        return new ModelAndView("/Backend/homepage/index");
    }

    @GetMapping("/admin/inventory")
    public ModelAndView viewInventory(Model model){

        if(!RDS.isUserLoggedIn())
            return new ModelAndView("redirect:/login");

        if (RDS.getCurrentLoggedUser().getRole() != Permiso.ADMIN)
            return new ModelAndView("redirect:/login");

        model.addAttribute("selection", RDS.findAllRegisteredProducts());

        return new ModelAndView("/Backend/inventory/inventory");
    }

    @GetMapping("/admin/inventory/edit/{id}")
    public ModelAndView editProduct(Model model,@PathParam("id") String productId ){

        if(!RDS.isUserLoggedIn())
            return new ModelAndView("redirect:/login");

        if (RDS.getCurrentLoggedUser().getRole() != Permiso.ADMIN)
            return new ModelAndView("redirect:/login");

        model.addAttribute("userID", productId);

        return new ModelAndView("/Backend/inventory/edit_product");
    }

    @GetMapping("/admin/users")
    public ModelAndView viewUsers(Model model){

        if (!RDS.isUserLoggedIn())
            return new ModelAndView("redirect:/login");

        if (RDS.getCurrentLoggedUser().getRole() != Permiso.ADMIN)
            return new ModelAndView("redirect:/login");

        model.addAttribute("userList", RDS.findAllRegisteredAccounts());

        return new ModelAndView("Backend/users/allUsers");
    }

    @GetMapping("/admin/transactions")
    public ModelAndView viewTransactions(Model model){

        if(!RDS.isUserLoggedIn())
            return new ModelAndView("redirect:/login");

        if (RDS.getCurrentLoggedUser().getRole() != Permiso.ADMIN)
            return new ModelAndView("redirect:/login");

        model.addAttribute("transactions", RDS.findAllRegisteredTransactions());

        return new ModelAndView("/Backend/transactions/transactions");
    }

    @GetMapping("/admin/statistics")
    public ModelAndView viewStatistics(Model model){

        if(!RDS.isUserLoggedIn())
            return new ModelAndView("redirect:/login");

        if (RDS.getCurrentLoggedUser().getRole() != Permiso.ADMIN)
            return new ModelAndView("redirect:/login");

        model.addAttribute("productsView", SS.productViewStatistics());
        model.addAttribute("purchaseStatistics", SS.productPurchaseStatistics());
        model.addAttribute("supplierStatistics", SS.productSupplierStatistics());
        model.addAttribute("averagePurchase", SS.userAveragePurchaseByDollar());
        model.addAttribute("averageItems", SS.userAverageNumberOfItemPurchase());

        return new ModelAndView("/Backend/statistics/statistics");
    }

    // Posts
    @PostMapping("/register")
    public String register(@RequestParam("email") String email, @RequestParam("first") String firstName, @RequestParam("last") String lastName, @RequestParam("address") String shippingAddress,@RequestParam("state") String city, @RequestParam("country") String country,@RequestParam("password") String password, @RequestParam("role") String role){

        if(!RDS.isUserLoggedIn())
            return "redirect:/login";

        if (RDS.getCurrentLoggedUser().getRole() != Permiso.ADMIN)
            return "redirect:/login";

        Permiso per;
        if (role.equals("ADMIN"))
        {
            per = Permiso.ADMIN;
        }
        else
        {
            per = Permiso.CONSUMER;
        }

        try {
            CDS.registerNewUser(email.toLowerCase(), firstName.toLowerCase(), lastName.toLowerCase(), shippingAddress,country,city, password,per );

            // TODO: Send confirmation email

            return "redirect:/admin/users";
        } catch (Exception exp){
            //
        }

        return "redirect:/register_page"; // TODO: Add error message
    }

    @PostMapping("/payment")
    public String payment (@RequestParam("stripeToken") String amm,@RequestParam("stripeEmail") String curr)
    {
        System.out.println("FUCK THE MONETARY SYSTEM");
        System.out.println(amm);
        System.out.println(curr);
        return "redirect:/admin/";
    }

    @PostMapping("/add_new_product")
    public String registerNewProduct(@RequestParam("name") String productName, @RequestParam("supplier") String supplier, @RequestParam("description") String productDescription, @RequestParam("price") Float productPrice, @RequestParam("quantity") Integer productInStock,@RequestParam("file") MultipartFile picture){

        if(!RDS.isUserLoggedIn())
            return "redirect:/login";

        if (RDS.getCurrentLoggedUser().getRole() != Permiso.ADMIN)
            return "redirect:/login"; // User must be an admin

        try {
            Product p = new Product(productName,supplier,productDescription,productPrice,productInStock);
            p.setPhoto(processImageFile(picture.getBytes()));
            CDS.registerNewProduct(p);

            return "redirect:/admin/inventory";
        } catch (Exception exp){
            //
        }

        return "redirect:/admin/inventory"; // TODO: Add error handling
    }

    @PostMapping("/edit/product/{productId}")
    public String editProductInformation(@PathParam("productId") Integer productId, @RequestParam("name") String productName, @RequestParam("supplier") String supplier, @RequestParam("description") String productDescription, @RequestParam("price") Float productPrice){

        if(!RDS.isUserLoggedIn())
            return "redirect:/login";

        if (RDS.getCurrentLoggedUser().getRole() != Permiso.ADMIN)
            return "redirect:/login"; // User must be an admin

        try {
            Product product = RDS.findRegisteredProduct(productId);

            if (!productName.equals(""))
                product.setProductName(productName);

            if (!supplier.equals(""))
                product.setSupplier(supplier);

            if (!productDescription.equals(""))
                product.setProductDescription(productDescription);

            if (!productPrice.equals(product.getProductPrice()))
                product.setProductPrice(productPrice);

            UDS.updateRegisteredProduct(product);

            return "redirect:/admin/inventory";
        } catch (Exception exp){
            //
        }

        return "redirect:/admin/inventory"; // TODO: Add error handling
    }

    @PostMapping("/admin/delete_product")
    public String deleteProduct(@RequestParam("ID") Integer productId){

        if(!RDS.isUserLoggedIn())
            return "redirect:/login";

        if (RDS.getCurrentLoggedUser().getRole() != Permiso.ADMIN)
            return "redirect:/login";

        try {
            DDS.deleteRegisteredProduct(productId);
            return "redirect:/admin/inventory";
        } catch (Exception exp){
            //
        }

        return "redirect:/admin/inventory"; // TODO: Add error handling
    }

    @PostMapping("/restock/{productId}")
    public String restockProduct(@PathParam("productId") Integer productId, @RequestParam("addition") Integer addition){

        if(!RDS.isUserLoggedIn())
            return "redirect:/login";

        if (RDS.getCurrentLoggedUser().getRole() != Permiso.ADMIN)
            return "redirect:/login"; // User must be an admin

        try {
            Product product = RDS.findRegisteredProduct(productId);
            product.setProductInStock(product.getProductInStock() + addition); // Addition can be positive or negative
            UDS.updateRegisteredProduct(product);

            return "redirect:/admin/inventory";
        } catch (Exception exp){
            //
        }

        return "redirect:/admin/inventory"; // TODO: Add error handling
    }

    @PostMapping("/ship/{fiscalCode}")
    public String markOrderAsShipped(@PathParam("fiscalCode") String fiscalCode){

        if(!RDS.isUserLoggedIn())
            return "redirect:/login";

        if (RDS.getCurrentLoggedUser().getRole() != Permiso.ADMIN)
            return "redirect:/login"; // User must be an admin

        try {
            Receipt receipt = RDS.findRegisteredTransaction(fiscalCode);
            receipt.setStatus(EstadoOrden.DELIVERED);
            UDS.updateRegisteredUserTransaction(receipt);

            // TODO: send email to client
            // TODO: Apply payment of transaction

            return "redirect:/admin/transactions";
        } catch (Exception exp){
            //
        }

        return "redirect:/admin/transactions"; // TODO: Add error handling
    }

    @PostMapping("/upload/product_picture/{productId}")
    public String uploadProductPicture(@PathParam("productId") Integer productId, @RequestParam("file") MultipartFile picture){

        if(!RDS.isUserLoggedIn())
            return "redirect:/login";

        if (RDS.getCurrentLoggedUser().getRole() != Permiso.ADMIN)
            return "redirect:/login"; // User must be an admin

        try {
            Product product = RDS.findRegisteredProduct(productId);
            product.setPhoto(processImageFile(picture.getBytes()));
            UDS.updateRegisteredProduct(product);

            return "redirect:/admin/inventory";
        } catch (Exception exp){
            //
        }

        return "redirect:/admin/inventory"; // TODO: Add error handling
    }

                                                                                                    // TODO: emailUser

    @PostMapping("/suspend_user")
    public String suspendUserAccount(@RequestParam("email") String email){

        if(!RDS.isUserLoggedIn())
            return "redirect:/login";

        if (RDS.getCurrentLoggedUser().getRole() != Permiso.ADMIN)
            return "redirect:/login"; // User must be an admin

        try {
            User user = RDS.findRegisteredUserAccount(email);
            user.setStatus(EstadoCuenta.SUSPENDED);
            UDS.updateRegisteredUserAccount(user);

            return "redirect:/admin/users";
        } catch (Exception exp){
            //
        }

        return "redirect:/admin/users"; // TODO: Add error handling
    }

    @PostMapping("/make_admin")
    public String makeUserAdmin(@RequestParam("email") String email){

        if(!RDS.isUserLoggedIn())
            return "redirect:/login";

        if (RDS.getCurrentLoggedUser().getRole() != Permiso.ADMIN)
            return "redirect:/login"; // User must be an admin

        try {
            User user = RDS.findRegisteredUserAccount(email);

            if (user.getRole() != Permiso.CONSUMER)
                return "redirect:/admin/users"; // TODO: only individuals can be made admin

            user.setRole(Permiso.ADMIN);
            UDS.updateRegisteredUserAccount(user);

            return "redirect:/admin/users";
        } catch (Exception exp){
            //
        }

        return "redirect:/admin/users"; // TODO: Add error handling
    }

    @PostMapping("/remove_admin_rights")
    public String removeAdminRights(@RequestParam("email") String email){

        if(!RDS.isUserLoggedIn())
            return "redirect:/login";

        if (RDS.getCurrentLoggedUser().getRole() != Permiso.ADMIN)
            return "redirect:/login"; // User must be an admin

        try {
            User user = RDS.findRegisteredUserAccount(email);
            user.setRole(Permiso.CONSUMER);
            UDS.updateRegisteredUserAccount(user);

            return "redirect:/admin/users";
        } catch (Exception exp){
            //
        }

        return "redirect:/admin/users"; // TODO: Add error handling
    }

    // Auxiliary Functions
    private Byte[] processImageFile(byte[] buffer) {
        Byte[] bytes = new Byte[buffer.length];
        int i = 0;

        for (byte b :
                buffer)
            bytes[i++] = b; // Autoboxing

        return bytes;
    }

    @Override
    public String getErrorPath() {
        return null;
    }
}
