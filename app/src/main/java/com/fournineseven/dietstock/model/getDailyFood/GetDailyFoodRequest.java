package com.fournineseven.dietstock.model.getDailyFood;

import com.google.gson.annotations.SerializedName;

public class GetDailyFoodRequest {
    @SerializedName("user_no")
    private int user_no;

    @SerializedName("updated_dt")
    private String updated_dt;

    public GetDailyFoodRequest(int user_no, String updated_dt){
        this.user_no = user_no;
        this.updated_dt = updated_dt;
    }
}
