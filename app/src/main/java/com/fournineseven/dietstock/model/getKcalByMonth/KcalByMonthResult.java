package com.fournineseven.dietstock.model.getKcalByMonth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KcalByMonthResult {
    @SerializedName("kcal")
    @Expose
    private Float kcal;

    @SerializedName("month")
    @Expose
    private int month;


    public KcalByMonthResult(){}

    public int getMonth() {
        return month;
    }

    public Float getKcal() {
        return kcal;
    }
}
