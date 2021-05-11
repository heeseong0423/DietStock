package com.fournineseven.dietstock.model.getKcalByWeek;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetKcalByWeekResponse {
    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("results")
    @Expose
    private ArrayList<KcalByWeekResult> results;

    public GetKcalByWeekResponse(){}

    public boolean isSuccess() {
        return success;
    }

    public ArrayList<KcalByWeekResult> getResults() {
        return results;
    }
}
