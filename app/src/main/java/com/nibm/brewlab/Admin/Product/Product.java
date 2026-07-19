package com.nibm.brewlab.Admin.Product;

public class Product {

    private String id;
    private String name;
    private String price;
    private String category;
    private String desc;
    private String imageUri;

<<<<<<< HEAD
    public Product() {
    }

    public Product(String name, String price, String category, String desc, String imageUri) {
=======

    public Product() {
    }


    public Product(String name,
                   String price,
                   String category,
                   String desc,
                   String imageUri) {

>>>>>>> origin/develop
        this.name = name;
        this.price = price;
        this.category = category;
        this.desc = desc;
        this.imageUri = imageUri;
    }

<<<<<<< HEAD
    public Product(String id, String name, String price, String category, String desc, String imageUri) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.desc = desc;
        this.imageUri = imageUri;
    }
=======
>>>>>>> origin/develop

    public String getId() {
        return id;
    }

<<<<<<< HEAD
    public void setId(String id) {
        this.id = id;
    }

=======
>>>>>>> origin/develop
    public String getName() {
        return name;
    }

<<<<<<< HEAD
    public void setName(String name) {
        this.name = name;
    }

=======
>>>>>>> origin/develop
    public String getPrice() {
        return price;
    }

<<<<<<< HEAD
    public void setPrice(String price) {
        this.price = price;
    }

=======
>>>>>>> origin/develop
    public String getCategory() {
        return category;
    }

<<<<<<< HEAD
    public void setCategory(String category) {
        this.category = category;
    }

=======
>>>>>>> origin/develop
    public String getDesc() {
        return desc;
    }

<<<<<<< HEAD
    public void setDesc(String desc) {
        this.desc = desc;
    }

=======
>>>>>>> origin/develop
    public String getImageUri() {
        return imageUri;
    }

<<<<<<< HEAD
    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", category='" + category + '\'' +
                ", desc='" + desc + '\'' +
                ", imageUri='" + imageUri + '\'' +
                '}';
    }
=======

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
>>>>>>> origin/develop
}