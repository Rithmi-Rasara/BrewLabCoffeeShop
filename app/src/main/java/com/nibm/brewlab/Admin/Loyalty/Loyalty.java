package com.nibm.brewlab.Admin.Loyalty;

public class Loyalty {

    private String id;
    private String name;
    private String email;
    private int points;
    private String level;



    public Loyalty() {

    }


    public Loyalty(String id, String name, String email, int points, String level) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.points = points;
        this.level = level;
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


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }


    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }


    // Auto calculate membership level
    public static String calculateLevel(int points) {

        if(points >= 1000){
            return "Platinum";
        }
        else if(points >= 500){
            return "Gold";
        }
        else if(points >= 100){
            return "Silver";
        }
        else{
            return "Bronze";
        }
    }
}