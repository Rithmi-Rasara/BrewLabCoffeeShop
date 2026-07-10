package com.nibm.brewlab.Admin.Customers;


public class Customer {

    public String id;
    public String name;
    public String email;
    public String phone;


    // Firebase සඳහා empty constructor එක අනිවාර්යයි
    public Customer() {

    }


    public Customer(String id, String name, String email, String phone) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;

    }
}