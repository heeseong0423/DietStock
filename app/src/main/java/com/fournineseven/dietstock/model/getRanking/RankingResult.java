package com.fournineseven.dietstock.model.getRanking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RankingResult {


    @SerializedName("user_no")
    @Expose
    private int user_no;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("kcal")
    @Expose
    private Float kcal;

    public RankingResult(int user_no, String name, Float kcal){
        this.user_no = user_no;
        this.name = name;
        this.kcal = kcal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getKcal() {
        return kcal;
    }

    public void setKcal(Float kcal) {
        this.kcal = kcal;
    }

    public int getUser_no() {
        return user_no;
    }

    public void setUser_no(int user_no) {
        this.user_no = user_no;
    }

    @Override
    public String toString() {
        return "RankingResult{" +
                "user_no=" + user_no +
                ", name='" + name + '\'' +
                ", kcal=" + kcal +
                '}';
    }
}
