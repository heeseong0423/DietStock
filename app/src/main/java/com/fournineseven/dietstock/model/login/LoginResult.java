package com.fournineseven.dietstock.model.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResult {

    @SerializedName("user_no")
    @Expose
    private int user_no;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("height")
    @Expose
    private Float height;

    @SerializedName("goal")
    @Expose
    private Float goal;

    public LoginResult(){}

    public int getUser_no() {
        return user_no;
    }

    public String getName() {
        return name;
    }

    public Float getGoal() {
        return goal;
    }

    public Float getHeight() {
        return height;
    }
}
