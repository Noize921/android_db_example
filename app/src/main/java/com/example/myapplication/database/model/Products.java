package com.example.myapplication.database.model;

public class Products {
    private int id;
    private String name;
    private String unitOfMeasure;
    private double price;
    private int categoryId;

    public Products() {}
    public Products(int id, String name, String measurementUnit, double price, int categoryId) {
        this.id = id;
        this.name = name;
        this.unitOfMeasure = measurementUnit;
        this.price = price;
        this.categoryId = categoryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String measurementUnit) {
        this.unitOfMeasure = measurementUnit;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
