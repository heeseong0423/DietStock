package com.fournineseven.dietstock.model.getFoodLogs;

import com.fournineseven.dietstock.model.getRolemodel.RolemodelResult;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetFoodLogsResponse {
    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("result")
    @Expose
    private ArrayList<FoodLogResult> result;

    public GetFoodLogsResponse(){}

    public boolean isSuccess() {
        return success;
    }

    public ArrayList<FoodLogResult> getResult() {
        return result;
    }
}
