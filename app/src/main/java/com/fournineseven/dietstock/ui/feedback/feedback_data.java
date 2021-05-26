package com.fournineseven.dietstock.ui.feedback;

public class feedback_data {

    private String food_image;
    private float kcal;
    private float carbs;
    private float protein;
    private float fat;
    private String time;
    private float serving;
    private String foodname;

    public feedback_data(String food_image,float serving,float kcal, float carbs, float protein, float fat, String time, String foodname) {
        this.food_image = food_image;
        this.kcal = kcal;
        this.serving = serving;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.time = time;
        this.foodname = foodname;
    }

    public String getFood_image() {
        return food_image;
    }

    public float getServing() {
        return serving;
    }

    public void setFood_image(String food_image) {
        this.food_image = food_image;
    }

    public float getKcal() {
        return kcal;
    }

    public void setKcal(float kcal) {
        this.kcal = kcal;
    }

    public float getCarbs() {
        return carbs;
    }

    public void setCarbs(float carbs) {
        this.carbs = carbs;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public void setServing(float serving) {
        this.serving = serving;
    }

    public float getFat() {
        return fat;
    }

    public void setFat(float fat) {
        this.fat = fat;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFoodname() {
        return foodname;
    }

    public void setFoodname(String foodname) {
        this.foodname = foodname;
    }
}
