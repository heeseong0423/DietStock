package com.fournineseven.dietstock.model.getKcalByWeek;

import com.google.gson.annotations.SerializedName;

public class GetKcalByWeekRequest {
    @SerializedName("user_no")
    private int user_no;

    public GetKcalByWeekRequest(int user_no){
        this.user_no = user_no;
    }
}
