package com.nibm.brewlab.Admin.Product;

public class Product {

    private String id;
    private String name;
    private String price;
    private String category;
    private String desc;
    private String imageUri;
    private String stock;

    public Product() {}

    public Product(String name, String price, String category,
                   String desc, String imageUri, String stock) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.desc = desc;
        this.imageUri = imageUri;
        this.stock = stock;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }

    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }

    public String getStock() { return stock; }
    public void setStock(String stock) { this.stock = stock; }
}