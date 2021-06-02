package com.fournineseven.dietstock.retrofitness

import com.fournineseven.dietstock.RequestUserInfoResult
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UpdateWeightResponse(
    @SerializedName("success")
    @Expose
    var success: Boolean
)
