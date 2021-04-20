package com.fournineseven.dietstock.model.getRolemodel;

import com.fournineseven.dietstock.model.getRanking.RankingResult;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetRolemodelResponse {
    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("result")
    @Expose
    private ArrayList<RolemodelResult> result;

    public GetRolemodelResponse(){}

    public boolean isSuccess() {
        return success;
    }

    public ArrayList<RolemodelResult> getResult() {
        return result;
    }
}
