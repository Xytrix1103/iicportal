package com.iicportal.models;

public class FoodItem {
    private String name;
    private String description;
    Double price;
    private String image;

    public FoodItem() {
    }

    public FoodItem(String name, String description, Double price, String image) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() { return price; }

    public String getImage() {
        return image;
    }
}
