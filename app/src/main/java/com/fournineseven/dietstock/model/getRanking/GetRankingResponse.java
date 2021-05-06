package com.fournineseven.dietstock.model.getRanking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetRankingResponse {
    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("result")
    @Expose
    private ArrayList<RankingResult> result;

    public GetRankingResponse(){}

    public boolean isSuccess() {
        return success;
    }

    public ArrayList<RankingResult> getResult() {
        return result;
    }
}
