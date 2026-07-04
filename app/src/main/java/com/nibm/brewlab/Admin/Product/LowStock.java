package com.nibm.brewlab.Admin.Product;

public class LowStock {

    private String productName;
    private String stockQty;

    // Required empty constructor for Firebase
    public LowStock() {
    }

    public LowStock(String productName, String stockQty) {
        this.productName = productName;
        this.stockQty = stockQty;
    }

    // Getters
    public String getProductName() {
        return productName;
    }

    public String getStockQty() {
        return stockQty;
    }

    // Setters (IMPORTANT for Firebase + updates)
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setStockQty(String stockQty) {
        this.stockQty = stockQty;
    }

    // Optional helper (VERY useful in adapter)
    public int getStockQtyInt() {
        try {
            return Integer.parseInt(stockQty);
        } catch (Exception e) {
            return 0;
        }
    }
}