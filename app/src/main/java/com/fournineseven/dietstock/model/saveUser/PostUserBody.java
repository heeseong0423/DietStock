package com.fournineseven.dietstock.model.saveUser;

import com.google.gson.annotations.SerializedName;

public class PostUserBody {
    @SerializedName("user_id")
    private String user_id;

    @SerializedName("password")
    private String password;

    @SerializedName("name")
    private String name;

    @SerializedName("height")
    private Float height;

    @SerializedName("goal")
    private Float goal;

    @SerializedName("weight")
    private Float weight;
    @SerializedName("age")
    private Float age;
    @SerializedName("sex")
    private Float sex;


    public PostUserBody(String user_id, String password, String name, Float height, Float goal){
        this.goal = goal;
        this.height = height;
        this.name = name;
        this.user_id=user_id;
        this.password=password;
    }
}
