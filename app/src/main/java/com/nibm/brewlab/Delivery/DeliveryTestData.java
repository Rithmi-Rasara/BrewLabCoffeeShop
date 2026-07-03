package com.nibm.brewlab.Delivery;

import com.nibm.brewlab.model.Order;

import java.util.ArrayList;
import java.util.List;

public class DeliveryTestData {

    private static final List<Order> ORDERS = new ArrayList<>();
    private static boolean seeded = false;

    public static List<Order> getOrders() {
        if (!seeded) {
            seed();
            seeded = true;
        }
        return ORDERS;
    }

    public static Order findById(String orderId) {
        if (orderId == null) return null;
        for (Order order : getOrders()) {
            if (orderId.equals(order.getOrderId())) {
                return order;
            }
        }
        return null;
    }

    public static void updateStatus(String orderId, String newStatus) {
        Order order = findById(orderId);
        if (order != null) {
            order.setStatus(newStatus);
        }
    }

    private static void seed() {

        long now = System.currentTimeMillis();

        ORDERS.add(new Order(
                "1001", "cust001", "SSB Xavier", "0771234567",
                "No 265, Colombo Road , Galle ", 7.2083, 79.8358,
                2500.0, "assigned", "me", now));

        ORDERS.add(new Order(
                "1006", "cust006", "Imasha Kavindi", "0771285634",
                "No 45, Mathara Road, Glle", 7.2086, 79.8386,
                1500.0, "assigned", "me", now));


        ORDERS.add(new Order(
                "1002", "cust002", "sathsara Silva", "0719876543",
                "No 12, Colombo Road, Hikkaduwa ", 7.2100, 79.8400,
                1450.0, "assigned", "me", now));

        ORDERS.add(new Order(
                "1003", "cust003", "Amali Fernando", "0752223344",
                "No 8, Lake Road, Galle ", 7.2050, 79.8300,
                980.0, "accepted", "me", now));


        ORDERS.add(new Order(
                "1004", "cust004", "Tharusha Presad ", "0776541230",
                "No 20,dambuluoya  , dambulla ", 7.1750, 79.8850,
                1800.0, "delivered", "me", now));

        ORDERS.add(new Order(
                "1005", "cust005", "jayamini ", "0723334455",
                "No 33, Beach Road, Negombo", 7.2200, 79.8500,
                3200.0, "delivered", "me", now));
    }
}