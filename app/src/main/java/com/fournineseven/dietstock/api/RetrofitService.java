package com.fournineseven.dietstock.api;

import com.fournineseven.dietstock.model.DefaultResponse;
import com.fournineseven.dietstock.model.getDailyFood.GetDailyFoodRequest;
import com.fournineseven.dietstock.model.getDailyFood.GetDailyFoodResponse;
import com.fournineseven.dietstock.model.getFoodLogs.GetFoodLogsRequest;
import com.fournineseven.dietstock.model.getFoodLogs.GetFoodLogsResponse;

import com.fournineseven.dietstock.model.getKcalByWeek.GetKcalByWeekRequest;
import com.fournineseven.dietstock.model.getKcalByWeek.GetKcalByWeekResponse;
import com.fournineseven.dietstock.model.getRanking.GetRankingResponse;
import com.fournineseven.dietstock.model.getRequestFood.GetRequestFoodRequest;
import com.fournineseven.dietstock.model.getRequestFood.GetRequestFoodResponse;
import com.fournineseven.dietstock.model.getRolemodel.GetRolemodelResponse;
import com.fournineseven.dietstock.model.login.LoginModel;
import com.fournineseven.dietstock.model.login.LoginResponse;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;

import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import retrofit2.http.Path;

public interface RetrofitService {
    @GET("api/ranking/getRanking/{day}/all")
    Call<GetRankingResponse> getRanking(@Path("day") String day);

    @GET("api/rolemodel/getRolemodel")
    Call<GetRolemodelResponse> getRolemodel();


    @Multipart
    @POST("api/member/register")
    Call<DefaultResponse> saveUser(@Part("user_id") RequestBody param,
                                   @Part("password") RequestBody password,
                                   @Part("name") RequestBody name,
                                   @Part("height") RequestBody height,
                                   @Part("goal") RequestBody goal,

                                   @Part("weight") RequestBody weight,
                                   @Part("age") RequestBody age,
                                   @Part("sex") RequestBody sex,
                                   @Part("activity") RequestBody activity,
                                   @Part MultipartBody.Part beforeImage
    );
    @POST("api/member/login")
    Call<LoginResponse> login(@Body LoginModel param);


    @POST("api/member/getFoodLogs")
    Call<GetFoodLogsResponse> getFoodLogs(@Body GetFoodLogsRequest param);

    @POST("api/member/getKcalByWeek")
    Call<GetKcalByWeekResponse> getKcalByWeek(@Body GetKcalByWeekRequest param);

    @POST("api/food/getDailyFood")
    Call<GetDailyFoodResponse> getDailyFood(@Body GetDailyFoodRequest param);

    @POST("api/member/getRequestFoods")
    Call<GetRequestFoodResponse> getRequestFood(@Body GetRequestFoodRequest param);

}
