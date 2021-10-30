package com.app.wreduce.database;

import com.app.wreduce.Main;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import java.util.Date;

public class Product extends RealmObject {
    @PrimaryKey
    private String barcodeId;

    private String productName;

    @Required
    private String status = ProductStatus.Open.name();

    private String location;

    private int quantity;

    private Date expDate;

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public Date getExpDate() {
        return expDate;
    }

    public String getBarcodeId() {
        return barcodeId;
    }

    public Product (String barcodeId, String productName, String location, int quantity, Date expDate) {
        this.barcodeId = barcodeId;
        this.productName = productName;
        this.location = location;
        this.quantity = quantity;
        this.expDate = expDate;
    }

    public Product(String barcodeId) {
        this.barcodeId = barcodeId;
    }

    public Product() {}

    public void delete() {
        Main.dataBase.executeTransaction(
                transactionRealm -> {
                    Product product =
                            Main.dataBase
                                    .where(Product.class)
                                    .equalTo("barcodeId", barcodeId)
                                    .findFirst();
                    assert product != null;
                    product.deleteFromRealm();
                });
    }
}
