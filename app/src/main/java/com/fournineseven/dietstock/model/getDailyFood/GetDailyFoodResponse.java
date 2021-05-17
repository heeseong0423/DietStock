package com.fournineseven.dietstock.model.getDailyFood;

import com.fournineseven.dietstock.model.getFoodLogs.FoodLogResult;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetDailyFoodResponse {
    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("result")
    @Expose
    private ArrayList<DailyFoodResult> result;

    public GetDailyFoodResponse(){}

    public boolean isSuccess() {
        return success;
    }

    public ArrayList<DailyFoodResult> getResult() {
        return result;
    }
}
