package com.fournineseven.dietstock.model;

import com.fournineseven.dietstock.model.getRanking.RankingResult;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DefaultResponse {
    @SerializedName("success")
    @Expose
    private boolean success;

    public DefaultResponse(){}

    public boolean isSuccess() {
        return success;
    }
}
