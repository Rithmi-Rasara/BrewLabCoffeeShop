package com.nibm.brewlab.Admin.Product;

public class LowStock {

    private String productName;
    private String stockQty;

    public LowStock() {
    }

    public LowStock(String productName, String stockQty) {
        this.productName = productName;
        this.stockQty = stockQty;
    }

    public String getProductName() {
        return productName;
    }

    public String getStockQty() {
        return stockQty;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setStockQty(String stockQty) {
        this.stockQty = stockQty;
    }

    public int getStockQtyInt() {
        try {
            return Integer.parseInt(stockQty);
        } catch (Exception e) {
            return 0;
        }
    }
}