package org.feup.ddmm.acmesupermarket;

public class Product {
    private String uuid;
    private float price;
    private String name;

    public Product(String uuid, float price, String name) {
        this.uuid = uuid;
        this.price = price;
        this.name = name;
    }
}