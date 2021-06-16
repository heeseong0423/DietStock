package com.fournineseven.dietstock.retrofitness

import com.fournineseven.dietstock.model.getRanking.RankingResult
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

data class GetUserKcalLogResponse(
    @SerializedName("success")
    @Expose
    var success: Boolean,

    @SerializedName("result")
    @Expose
    val result: ArrayList<UserKcalLogResult>
)


data class UserKcalLogResult(
    @SerializedName("user_no")
    @Expose
    val user_no: Int = 0,

    @SerializedName("low")
    @Expose
    val low: Float = 0.0f,

    @SerializedName("high")
    @Expose
    val high:Float = 0.0f,

    @SerializedName("start_kcal")
    @Expose
    val start_kcal:Float = 0.0f,

    @SerializedName("end_kcal")
    @Expose
    val end_kcal:Float = 0.0f,

    @SerializedName("updated_dt")
    @Expose
    val updated_dt:String,
)