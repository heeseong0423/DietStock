package com.fournineseven.dietstock.model.login;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LoginResponse {
    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("result")
    @Expose
    private LoginResult result;

    public LoginResponse(){}

    public boolean isSuccess() {
        return success;
    }

    public LoginResult getResult() {
        return result;
    }
}
