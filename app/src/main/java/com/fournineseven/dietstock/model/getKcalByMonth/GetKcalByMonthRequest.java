package com.fournineseven.dietstock.model.getKcalByMonth;

import com.google.gson.annotations.SerializedName;

public class GetKcalByMonthRequest {
    @SerializedName("user_no")
    private int user_no;

    public GetKcalByMonthRequest(int user_no){
        this.user_no = user_no;
    }
}
