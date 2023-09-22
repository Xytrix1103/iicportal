package com.iicportal.models;

import java.util.List;

// This class is used to store the category of food items and the food items in that category from the Firebase database.
// The structure of the Firebase database is as follows:
// ecanteen
//   fooditems
//     Sandwich
//       ABC
//         name: "ABC"
//         description: "ABC"
//         price: 100
//         image: "https://firebasestorage.googleapis.com/v0/b/iicportal-1f0f1.appspot.com/o/ABC.jpg?alt=media&token=8b7b5b5e-7b9e-4b9e-8b0a-8b0b5b5b5b5b"
//       DEF
//         name: "DEF"
//         description: "DEF"
//         price: 200
//         image: "https://firebasestorage.googleapis.com/v0/b/iicportal-1f0f1.appspot.com/o/DEF.jpg?alt=media&token=8b7b5b5e-7b9e-4b9e-8b0a-8b0b5b5b5b5b"
// the above structure is repeated for all the categories of food items.
// the user should be able to get the category as a string and the food items in that category as a list of FoodItem objects.
public class Category {
    private String category;
    private List<FoodItem> foodItems;

    public Category() {
    }

    public Category(String category, List<FoodItem> foodItems) {
        this.category = category;
        this.foodItems = foodItems;
    }

    public String getCategory() {
        return category;
    }

    public List<FoodItem> getFoodItems() {
        return foodItems;
    }
}