package com.fournineseven.dietstock.retrofitness

import com.fournineseven.dietstock.RequestUserInfoResult
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GetUserInfoResponse(
    @SerializedName("success")
    @Expose
    var success: Boolean,

    @SerializedName("result")
    @Expose
    val result: ArrayList<RequestUserInfoResult>? = null
)