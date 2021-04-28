package com.fournineseven.dietstock.api;

import com.fournineseven.dietstock.model.DefaultResponse;
import com.fournineseven.dietstock.model.getFoodLogs.GetFoodLogsRequest;
import com.fournineseven.dietstock.model.getFoodLogs.GetFoodLogsResponse;
import com.fournineseven.dietstock.model.getKcalByMonth.GetKcalByMonthRequest;
import com.fournineseven.dietstock.model.getKcalByMonth.GetKcalByMonthResponse;
import com.fournineseven.dietstock.model.getRanking.GetRankingResponse;
import com.fournineseven.dietstock.model.getRolemodel.GetRolemodelResponse;
import com.fournineseven.dietstock.model.login.LoginModel;
import com.fournineseven.dietstock.model.login.LoginResponse;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
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
                                   @Part MultipartBody.Part beforeImage
    );
    @POST("api/member/login")
    Call<LoginResponse> login(@Body LoginModel param);

    @POST("api/member/getFoodLogs")
    Call<GetFoodLogsResponse> getFoodLogs(@Body GetFoodLogsRequest param);

    @POST("api/member/getKcalByMonth")
    Call<GetKcalByMonthResponse> getKcalByMonth(@Body GetKcalByMonthRequest param);
}
