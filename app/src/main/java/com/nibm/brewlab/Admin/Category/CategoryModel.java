package com.nibm.brewlab.Admin.Category;

public class CategoryModel {

<<<<<<< HEAD
    private String name;
    private int image;

    public CategoryModel(String name, int image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(int image) {
        this.image = image;
=======
    private String id;
    private String name;

    public CategoryModel() {

    }

    public CategoryModel(String id, String name) {

        this.id = id;
        this.name = name;

    }

    public String getId() {

        return id;

    }

    public String getName() {

        return name;

    }

    public void setId(String id) {

        this.id = id;

    }

    public void setName(String name) {

        this.name = name;

>>>>>>> origin/develop
    }
}