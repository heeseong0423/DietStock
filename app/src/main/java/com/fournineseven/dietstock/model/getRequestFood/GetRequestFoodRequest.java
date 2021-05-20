package com.fournineseven.dietstock.model.getRequestFood;

import com.google.gson.annotations.SerializedName;

public class GetRequestFoodRequest {

    @SerializedName("avoid_food")
    private String avoid_food;


    @SerializedName("nutriention")
    private String nutriention;

    @SerializedName("gram")
    private float gram;

    public GetRequestFoodRequest(String avoid_food, String nutriention, float gram) {
        this.avoid_food = avoid_food;
        this.nutriention = nutriention;
        this.gram = gram;
    }
}
