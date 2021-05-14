package com.fournineseven.dietstock.ui.feedback;

import com.google.gson.annotations.SerializedName;

public class getDailyFood {
    //@SerializedName("recommend_kcal")
    //private float recommend_kcal;
    @SerializedName("food_name")
    private String food_name;
    @SerializedName("kcal")
    private float kcal;
    @SerializedName("serving")
    private int serving;
    @SerializedName("updated_dt")
    private String updated_dt;
    @SerializedName("carbs")
    private float carbs;
    @SerializedName("protein")
    private float protein;

    /*public float getRecommend_kcal() {
        return recommend_kcal;
    }

    public void setRecommend_kcal(float recommend_kcal) {
        this.recommend_kcal = recommend_kcal;
    }*/

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public float getKcal() {
        return kcal;
    }

    public void setKcal(float kcal) {
        this.kcal = kcal;
    }

    public int getServing() {
        return serving;
    }

    public void setServing(int serving) {
        this.serving = serving;
    }

    public String getUpdated_dt() {
        return updated_dt;
    }

    public void setUpdated_dt(String updated_dt) {
        this.updated_dt = updated_dt;
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

    public float getFat() {
        return fat;
    }

    public void setFat(float fat) {
        this.fat = fat;
    }

    @SerializedName("fat")
    private float fat;

    @Override
    public String toString() {
        return "GetDailyFood{" +
                "food_name=" + food_name +
                ", kcal=" + kcal +
                ", serving='" + serving +
                ", updated_dt='" + updated_dt  +
                ", carbs='" + carbs  +
                ", protein='" + protein  +
                ", fat='" + fat  +
                '}';
    }


}
