package com.fournineseven.dietstock.model.getKcalByWeek;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KcalByWeekResult {
    @SerializedName("week")
    @Expose
    private int week;

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



    public KcalByWeekResult(){}

    public int getWeek() {
        return week;
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
        return "KcalByWeekResult{" +
                "week=" + week +
                ", low=" + low +
                ", high=" + high +
                ", start_kcal=" + start_kcal +
                ", end_kcal=" + end_kcal +
                '}';
    }
}
