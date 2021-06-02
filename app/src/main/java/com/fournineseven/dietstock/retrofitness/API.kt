package com.fournineseven.dietstock.retrofitness

import com.fournineseven.dietstock.GetUserInfoRequest
import retrofit2.Call
import retrofit2.http.*

interface API {
    @POST("api/member/getUserInfo")
    fun getUserInfo(@Body param: GetUserInfoRequest)
    : Call<GetUserInfoResponse>
}