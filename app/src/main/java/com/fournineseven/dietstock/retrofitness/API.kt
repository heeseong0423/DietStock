package com.fournineseven.dietstock.retrofitness

import com.fournineseven.dietstock.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface API {
    @GET("api/kcal/getUserKcalLog")
    fun getUserKcalLogResponse(@Query("user_no") user_no: Int)
    : Call<GetUserKcalLogResponse>

    @POST("api/member/getUserInfo")
    fun getUserInfo(@Body param: GetUserInfoRequest)
    : Call<GetUserInfoResponse>

    @POST("api/member/updateWeight")
    fun updateWeight(@Body param: UpdateWeightRequest)
    : Call<UpdateWeightResponse>

    @POST("api/member/updateHeight")
    fun updateHeight(@Body param: UpdateHeightRequest)
    : Call<UpdateHeightResponse>

    @POST("api/member/updateGoal")
    fun updateGoal(@Body param: UpdateGoalRequest)
    : Call<UpdateGoalResponse>

    @Multipart
    @POST("api/member/setBeforeImage")
    fun updateBeforeImage(
        @Part("user_no") param: RequestBody,
        @Part before_image: MultipartBody.Part)
    : Call<UpdateBeforeImageResponse>

    @Multipart
    @POST("api/member/setAfterImage")
    fun updateAfterImage(@Part("user_no") param: RequestBody,
                         @Part after_image: MultipartBody.Part) : Call<UpdateAfterImageResponse>

    @POST("api/kcal/saveKcalLog")
    fun saveKcalLog(@Body param: SaveKcalLogRequest)
    : Call<SaveKcalLogResponse>

    @POST("api/kcal/getDailyKcal")
    fun getDailyKcal(@Body param: GetDailyKcalRequest)
    : Call<GetDailyKcalResponse>
}