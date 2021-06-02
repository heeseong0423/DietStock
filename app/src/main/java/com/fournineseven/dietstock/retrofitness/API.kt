package com.fournineseven.dietstock.retrofitness

import com.fournineseven.dietstock.GetUserInfoRequest
import com.fournineseven.dietstock.UpdateGoalRequest
import com.fournineseven.dietstock.UpdateHeightRequest
import com.fournineseven.dietstock.UpdateWeightRequest
import retrofit2.Call
import retrofit2.http.*

interface API {
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
}