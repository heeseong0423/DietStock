package com.fournineseven.dietstock.model.getRequestFood;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestFoodResult {
    @SerializedName("food_name")
    @Expose
    private String food_name;

    @SerializedName("kcal")
    @Expose
    private float kcal;

    @SerializedName("carbs")
    @Expose
    private float carbs;

    @SerializedName("protein")
    @Expose
    private float protein;

    @SerializedName("fat")
    @Expose
    private float fat;

    @SerializedName("food_image")
    @Expose
    private String food_image;


    public RequestFoodResult(){}

    public String getFood_name() {
        return food_name;
    }

    public float getCarbs() {
        return carbs;
    }

    public float getFat() {
        return fat;
    }

    public float getKcal() {
        return kcal;
    }

    public float getProtein() {
        return protein;
    }

    public String food_image() {
        return food_image;
    }
}
