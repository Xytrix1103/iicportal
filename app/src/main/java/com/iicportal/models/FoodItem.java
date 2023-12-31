package com.iicportal.models;

public class FoodItem {
    private String name;
    private String description;
    Double price;
    private String image;

    private String category;

    public FoodItem() {
    }

    public FoodItem(String name, String description, Double price, String image, String category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.category = category;
    }

    public FoodItem(String name, String description, Double price, String image, String category, int quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public String getCategory() {
        return category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    @Override
    public String toString() {
        return "FoodItem{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", image='" + image + '\'' +
                ", category='" + category + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FoodItem) {
            FoodItem foodItem = (FoodItem) obj;
            return foodItem.getName().equals(this.getName()) && foodItem.getDescription().equals(this.getDescription()) && foodItem.getPrice().equals(this.getPrice()) && foodItem.getImage().equals(this.getImage()) && foodItem.getCategory().equals(this.getCategory());
        }
        return false;
    }
}