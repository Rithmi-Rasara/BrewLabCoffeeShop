package com.nibm.brewlab.Admin.Customers;

<<<<<<< HEAD
public class Customer {

    public String name;
    public String email;
    public String phone;

    public Customer(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
=======

public class Customer {

    public String id;
    public String name;
    public String email;
    public String phone;
    public String role;
    public String status;

    public Customer() {

    }


    public Customer(String id,
                    String name,
                    String email,
                    String phone,
                    String role,
                    String status) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = status;

>>>>>>> origin/develop
    }
}