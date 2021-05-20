package com.fournineseven.dietstock.model.getDailyFood;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DailyFoodResult {
    @SerializedName("food_name")
    @Expose
    private String food_image;

    @SerializedName("food_name")
    @Expose
    private String food_name;

    @SerializedName("kcal")
    @Expose
    private float kcal;

    @SerializedName("serving")
    @Expose
    private float serving;

    @SerializedName("updated_dt")
    @Expose
    private String updated_dt;

    @SerializedName("carbs")
    @Expose
    private float carbs;

    @SerializedName("protein")
    @Expose
    private float protein;

    @SerializedName("fat")
    @Expose
    private float fat;


    public DailyFoodResult(){}

    public String getFood_image() {
        return food_image;
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

    public float getServing() {
        return serving;
    }

    public String getFood_name() {
        return food_name;
    }

    public String getUpdated_dt() {
        return updated_dt;
    }
}
