package com.fournineseven.dietstock.model.getFoodLogs;

import com.google.gson.annotations.SerializedName;

public class GetFoodLogsRequest {
    @SerializedName("user_no")
    private int user_no;

    public GetFoodLogsRequest(int user_no){
        this.user_no = user_no;
    }
}
