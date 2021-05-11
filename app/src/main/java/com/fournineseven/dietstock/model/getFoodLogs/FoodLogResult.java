package com.fournineseven.dietstock.model.getFoodLogs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FoodLogResult {
    @SerializedName("user_no")
    @Expose
    private int user_no;

    @SerializedName("cnt")
    @Expose
    private int cnt;

    @SerializedName("name")
    @Expose
    private String name;

    public FoodLogResult(){}

    public String getName() {
        return name;
    }

    public int getCnt() {
        return cnt;
    }
}
