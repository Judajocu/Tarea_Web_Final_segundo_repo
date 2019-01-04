package org.finalweb.Entity;

import org.finalweb.Tools.Enums.EstadoOrden;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

@Entity
@Table(name = "receipts")
public class Receipt implements Serializable {

    // Atributes
    @Id
    private String fiscalCode;
    @ManyToOne
    private User user;
    private Timestamp transactionDate;
    private ArrayList<Integer> amount;
    private ArrayList<Integer> productList;
    private Float total;
    private EstadoOrden status;

    // Constructors
    public Receipt(){

    }

    public Receipt(User user, ArrayList<Integer> productList, ArrayList<Integer> amount, Float total){
        this.setFiscalCode(UUID.randomUUID().toString().split("-")[0].toUpperCase());
        this.setUser(user);
        this.setTransactionDate(new Timestamp(Calendar.getInstance().getTime().getTime())); // Today's Date and Current Time
        this.setProductList(productList);
        this.setAmount(amount);
        this.setTotal(total);
        this.setStatus(EstadoOrden.PENDING);
    }

    public String getFiscalCode() {
        return fiscalCode;
    }

    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<Integer> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<Integer> productList) {
        this.productList = productList;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public EstadoOrden getStatus() {
        return status;
    }

    public void setStatus(EstadoOrden status) {
        this.status = status;
    }

    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }

    public ArrayList<Integer> getAmount() {
        return amount;
    }

    public void setAmount(ArrayList<Integer> amount) {
        this.amount = amount;
    }
}
