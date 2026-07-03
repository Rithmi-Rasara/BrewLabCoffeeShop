package com.nibm.brewlab.Admin.Product;

public class Product {

    private String id;
    private String name;
    private String price;
    private String category;
    private String desc;
    private String imageUri;

    public Product() {
        // Required for Firebase
    }

    public Product(String name, String price, String category,
                   String desc, String imageUri) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.desc = desc;
        this.imageUri = imageUri;
    }

    public Product(String id, String name, String price,
                   String category, String desc, String imageUri) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.desc = desc;
        this.imageUri = imageUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}