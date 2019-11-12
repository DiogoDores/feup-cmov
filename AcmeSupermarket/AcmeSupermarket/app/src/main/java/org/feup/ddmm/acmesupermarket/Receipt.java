package org.feup.ddmm.acmesupermarket;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;

public class Receipt {

    private int id;
    private Date purchaseDate;
    private ArrayList<Product> products;
    private float totalPrice;
    private boolean voucherUsed;

    public Receipt(Date purchaseDate, ArrayList<Product> products, float totalPrice, boolean voucherUsed) {
        this.id += 1;
        this.purchaseDate = purchaseDate;
        this.products = products;
        this.totalPrice = totalPrice;
        this.voucherUsed = voucherUsed;
    }


    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public boolean isVoucherUsed() {
        return voucherUsed;
    }
}
