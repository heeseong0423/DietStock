package com.fournineseven.dietstock.retrofitness

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UpdateAfterImageResponse(
    @SerializedName("success")
    @Expose
    var success: Boolean
)
