package com.example.myapplication.database.model;

public class Sales {
    private int productId;
    private double quantitySold;
    private String date;

    public Sales() {}

    public Sales(int productId, double quantitySold, String date) {
        this.productId = productId;
        this.quantitySold = quantitySold;
        this.date = date;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public double getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(double quantitySold) {
        this.quantitySold = quantitySold;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
