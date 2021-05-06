package com.fournineseven.dietstock.model.getKcalByMonth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KcalByMonthResult {
    @SerializedName("month")
    @Expose
    private int month;

    @SerializedName("low")
    @Expose
    private float low;

    @SerializedName("high")
    @Expose
    private float high;

    @SerializedName("start_kcal")
    @Expose
    private float start_kcal;

    @SerializedName("end_kcal")
    @Expose
    private float end_kcal;



    public KcalByMonthResult(){}

    public int getMonth() {
        return month;
    }

    public float getHigh() {
        return high;
    }

    public float getLow() {
        return low;
    }

    public float getEnd_kcal() {
        return end_kcal;
    }

    public float getStart_kcal() {
        return start_kcal;
    }

    @Override
    public String toString() {
        return "KcalByMonthResult{" +
                "month=" + month +
                ", low=" + low +
                ", high=" + high +
                ", start_kcal=" + start_kcal +
                ", end_kcal=" + end_kcal +
                '}';
    }
}
