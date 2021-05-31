package com.fournineseven.dietstock

import com.fournineseven.dietstock.model.getFoodLogs.GetFoodLogsResponse
import com.fournineseven.dietstock.model.getRanking.GetRankingResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface API {
    @POST("api/member/getUserInfo")
    fun getUserInfo(@Body param: GetUserInfoRequest )
    : Call<GetUserInfoResponse>
}