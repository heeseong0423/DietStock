package com.fournineseven.dietstock.model.getKcalByMonth;

import com.fournineseven.dietstock.model.getFoodLogs.FoodLogResult;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetKcalByMonthResponse {
    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("result")
    @Expose
    private ArrayList<KcalByMonthResult> result;

    public GetKcalByMonthResponse(){}

    public boolean isSuccess() {
        return success;
    }

    public ArrayList<KcalByMonthResult> getResult() {
        return result;
    }
}
