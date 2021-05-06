package com.fournineseven.dietstock.model.getRolemodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RolemodelResult {
    @SerializedName("user_no")
    @Expose
    private int user_no;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("weight_gap")
    @Expose
    private float weight_gap;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("beforeimage")
    @Expose
    private String beforeimage;

    @SerializedName("afterimage")
    @Expose
    private String afterimage;

    public RolemodelResult(){};

    public int getUser_no() {
        return user_no;
    }

    public String getName() {
        return name;
    }

    public float getWeight_gap() {
        return weight_gap;
    }

    public String getAfterimage() {
        return afterimage;
    }

    public String getBeforeimage() {
        return beforeimage;
    }

    public String getDate() {
        return date;
    }
}
