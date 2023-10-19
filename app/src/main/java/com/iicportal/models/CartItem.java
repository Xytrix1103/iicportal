package com.iicportal.models;

public class CartItem {

    private String name;
    private String description;
    Double price;
    private String image;

    private int quantity;

    private String category;

    private boolean selected;

    public CartItem() {
    }

    public CartItem(FoodItem foodItem) {
        this.name = foodItem.getName();
        this.description = foodItem.getDescription();
        this.price = foodItem.getPrice();
        this.image = foodItem.getImage();
        this.category = foodItem.getCategory();
        this.quantity = 1;
        this.selected = false;
    }

    public CartItem(String name, String description, Double price, String image, String category, int quantity, boolean selected) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.category = category;
        this.quantity = quantity;
        this.selected = selected;
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

    public int getQuantity() {
        return quantity;
    }

    public boolean getSelected() {
        return selected;
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

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void resetQuantity() {
        this.quantity = 1;
    }
}
