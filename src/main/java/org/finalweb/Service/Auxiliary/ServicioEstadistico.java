package org.finalweb.Service.Auxiliary;

import org.finalweb.Entity.History;
import org.finalweb.Entity.Product;
import org.finalweb.Entity.Receipt;
import org.finalweb.Entity.User;
import org.finalweb.Repository.HistorialRepositorio;
import org.finalweb.Repository.ArticuloRepositorio;
import org.finalweb.Repository.FacturaRepositorio;
import org.finalweb.Repository.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class ServicioEstadistico {

    // Repositories
    @Autowired
    private HistorialRepositorio historialRepositorio;
    @Autowired
    private ArticuloRepositorio articuloRepositorio;
    @Autowired
    private FacturaRepositorio facturaRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    // Functions
    // Product related Statistics
    public ArrayList<String> productViewStatistics(){

        Map<Integer, Integer> statistic = fetchProductLegend();
        ArrayList<String> buffer = new ArrayList<>();

        try {
            for (History history: historialRepositorio.findAll())
                for (Product product: history.getBrowsingHistory())
                    statistic.replace(product.getProductId(), statistic.get(product.getProductId()) + 1);

            for (Integer i:
                 statistic.keySet())
                buffer.add("'" + i.toString() + "--" + articuloRepositorio.findByProductId(i).getProductName() + "', " + statistic.get(i).toString() + ", 'color: #b87333'");

            return buffer;
        } catch (Exception exp) {
            System.out.println();
        }

        return null;
    }

    public ArrayList<String> productPurchaseStatistics(){

        Map<Integer, Integer> statistic = fetchProductLegend();
        ArrayList<String> buffer = new ArrayList<>();

        try {
            for (Receipt receipt: facturaRepositorio.findAll())
                for (Integer p: receipt.getProductList())
                    statistic.replace(p, statistic.get(p) + 1);

            for (Integer i:
                 statistic.keySet())
                buffer.add("'" + i.toString() + "--" + articuloRepositorio.findByProductId(i).getProductName() + "', " + statistic.get(i).toString() + "', 'color: gold'");

            return buffer;
        } catch (Exception exp) {
            //
        }

        return null;
    }

    public ArrayList<String> productSupplierStatistics(){

        try {
            Map<String, Integer> statistic = fetchSupplierLegend();
            ArrayList<String> buffer = new ArrayList<>();

            for (Product product: articuloRepositorio.findAll())
                statistic.replace(product.getSupplier(), statistic.get(product.getSupplier()) + 1);

            for (String supplier:
                 statistic.keySet())
                buffer.add("'" + supplier + "', " + statistic.get(supplier).toString());

            return buffer;

        } catch (Exception exp) {
            //
        }

        return null;
    }

    // Transaction Related Functions
    public ArrayList<String> userAveragePurchaseByDollar(){

        try {
            Map<String, Float> statistics = fetchUserLegend();
            ArrayList<String> buffer = new ArrayList<>();

            for (String email: statistics.keySet()) {
                int count = 0;
                for (Receipt receipt : facturaRepositorio.findByUser(email)) {
                    statistics.replace(email, statistics.get(email) + receipt.getTotal());
                    count++;
                }

                statistics.replace(email, statistics.get(email)/count);
            }

            for (String email: statistics.keySet())
                buffer.add("'" + email + "', " + Float.toString(statistics.get(email)) + ", 'color: silver'");

            return buffer;
        } catch (Exception exp) {
            //
        }

        return null;
    }

    public ArrayList<String> userAverageNumberOfItemPurchase(){

        try {
            Map<String, Float> statistics = fetchUserLegend();
            ArrayList<String> buffer = new ArrayList<>();

            for (String email: statistics.keySet()) {
                int count = 0;
                for (Receipt receipt : facturaRepositorio.findByUser(email)) {
                    statistics.replace(email, statistics.get(email) + receipt.getProductList().size());
                    count++;
                }

                statistics.replace(email, statistics.get(email)/count);
            }

            for (String email: statistics.keySet())
                buffer.add("'" + email + "', " + Float.toString(statistics.get(email)) + ", 'color: silver'");

            return buffer;
        } catch (Exception exp) {
            //
        }

        return null;
    }


    // Auxiliary Functions
    private Map<Integer, Integer> fetchProductLegend(){
        Map<Integer, Integer> legend = new HashMap<>();

        for (Product p:
             articuloRepositorio.findAll()) {
            legend.put(p.getProductId(), 0);
        }

        return legend;
    }

    private Map<String, Integer> fetchSupplierLegend(){
        Map<String, Integer> legend = new HashMap<>();

        for (Product p: articuloRepositorio.findAll())
            legend.putIfAbsent(p.getSupplier(), 0);

        return legend;
    }

    private Map<String, Float> fetchUserLegend(){
        Map<String, Float> legend = new HashMap<>();

        for (User user: usuarioRepositorio.findAll())
            legend.putIfAbsent(user.getEmail(), 0.00f);

        return legend;
    }

}
