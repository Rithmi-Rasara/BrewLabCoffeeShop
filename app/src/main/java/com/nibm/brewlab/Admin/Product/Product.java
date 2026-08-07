package com.nibm.brewlab.Admin.Product;

public class Product {

    private String id;
    private String name;
    private String price;
    private String category;
    private String desc;
    private String imageUri;


    public Product() {
    }


    public Product(String name, String price, String category, String desc, String imageUri) {

        this.name = name;
        this.price = price;
        this.category = category;
        this.desc = desc;
        this.imageUri = imageUri;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getDesc() {
        return desc;
    }

    public String getImageUri() {
        return imageUri;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}