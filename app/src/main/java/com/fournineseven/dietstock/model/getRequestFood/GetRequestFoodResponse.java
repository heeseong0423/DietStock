package com.fournineseven.dietstock.model.getRequestFood;

import com.fournineseven.dietstock.model.getDailyFood.DailyFoodResult;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetRequestFoodResponse {
    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("result")
    @Expose
    private ArrayList<RequestFoodResult> result;

    public GetRequestFoodResponse(){}

    public boolean isSuccess() {
        return success;
    }

    public ArrayList<RequestFoodResult> getResult() {
        return result;
    }
}
