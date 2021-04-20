package com.fournineseven.dietstock.api;

import com.fournineseven.dietstock.model.getRanking.GetRankingResponse;
import com.fournineseven.dietstock.model.getRolemodel.GetRolemodelResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitService {
    @GET("api/ranking/getRanking/{day}/all")
    Call<GetRankingResponse> getRanking(@Path("day") String day);

    @GET("api/rolemodel/getRolemodel")
    Call<GetRolemodelResponse> getRolemodel();
}
