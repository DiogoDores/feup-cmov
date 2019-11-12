package org.feup.ddmm.acmesupermarket;

import androidx.annotation.NonNull;

public class Product {
    private String uuid;
    private float price;
    private float totalPrice;
    private String name;
    private int quantity;

    public Product(String uuid, float price, String name) {
        this.uuid = uuid;
        this.price = price;
        this.totalPrice = price;
        this.name = name;
        this.quantity = 1;
    }

    public String getUuid() {
        return uuid;
    }

    public float getPrice() {
        return price;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public String getName() { return name; }

    public int getQuantity() { return quantity; }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }

    public void incrementQuantity() {
        this.quantity += 1;
        this.totalPrice = this.price * this.quantity;
    }
}
