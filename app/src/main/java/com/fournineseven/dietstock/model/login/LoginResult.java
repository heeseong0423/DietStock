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

    @SerializedName("weight")
    @Expose
    private Float weight;

    @SerializedName("age")
    @Expose
    private int age;

    @SerializedName("sex")
    @Expose
    private int sex;

    @SerializedName("activity")
    @Expose
    private int activity;

    @SerializedName("beforeimage")
    @Expose
    private String beforeimage;

    @SerializedName("afterimage")
    @Expose
    private String afterimage;


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

    public int getActivity() {
        return activity;
    }

    public Float getWeight() {
        return weight;
    }

    public int getAge() {
        return age;
    }

    public int getSex() {
        return sex;
    }

    public String getAfterimage() {
        return afterimage;
    }

    public String getBeforeimage() {
        return beforeimage;
    }
}
