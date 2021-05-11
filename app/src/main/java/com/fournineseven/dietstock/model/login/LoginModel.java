package com.fournineseven.dietstock.model.login;

import com.google.gson.annotations.SerializedName;

public class LoginModel {
    @SerializedName("user_id")
    private String user_id;

    @SerializedName("password")
    private String password;
    public LoginModel( String user_id, String password){
        this.user_id = user_id;
        this.password = password;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
