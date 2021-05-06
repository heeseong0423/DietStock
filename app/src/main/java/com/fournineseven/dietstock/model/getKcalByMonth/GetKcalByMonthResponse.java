package com.fournineseven.dietstock.model.getKcalByMonth;

import com.fournineseven.dietstock.model.getFoodLogs.FoodLogResult;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetKcalByMonthResponse {
    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("results")
    @Expose
    private ArrayList<KcalByMonthResult> results;

    public GetKcalByMonthResponse(){}

    public boolean isSuccess() {
        return success;
    }

    public ArrayList<KcalByMonthResult> getResults() {
        return results;
    }
}
