package com.fournineseven.dietstock

import com.fournineseven.dietstock.model.getRequestFood.RequestFoodResult
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetUserInfoRequest(
    @field:SerializedName("user_no") private val user_no: Int
)

class UpdateWeightRequest(
    @field:SerializedName("user_no") private val user_no: Int,
    @field:SerializedName("weight") private val weight: Float
)

class UpdateHeightRequest(
    @field:SerializedName("user_no") private val user_no: Int,
    @field:SerializedName("height") private val height: Float
)

class GetDailyKcalRequest(
    @field:SerializedName("user_no") private val user_no: Int,
    @field:SerializedName("date") private val date: String
)

class UpdateGoalRequest(
    @field:SerializedName("user_no") private val user_no: Int,
    @field:SerializedName("goal") private val goal: Float
)

class SaveKcalLogRequest(
    @field:SerializedName("user_no") private val user_no: Int,
    @field:SerializedName("low") private val low: Float,
    @field:SerializedName("high") private val high: Float,
    @field:SerializedName("start_kcal") private val start_kcal: Float,
    @field:SerializedName("end_kcal") private val end_kcal: Float,
    @field:SerializedName("date") private val date: String,
)

class RequestKcalInfoResult{
    @SerializedName("sumkcal")
    @Expose
    val kcalSum: Float = 0.0f
}

class GetDailyKcalResponseResult{
    @SerializedName("name")
    @Expose
    val sumKcal: Float = 0.0f

}

/*
* user_no
* low
* high
* start_kcal
* end_kcal
* updated_dt
* */

/*class GetUserKcalLogRequest{
    @SerializedName("user_no")
    @Expose
    val user_no: Int = 0

    @SerializedName("low")
    @Expose
    val low: Float = 0.0f

    @SerializedName("high")
    @Expose
    val high:Float = 0.0f

    @SerializedName("start_kcal")
    @Expose
    val start_kcal:Float = 0.0f

    @SerializedName("end_kcal")
    @Expose
    val end_kcal:Float = 0.0f

    @SerializedName("updated_dt")
    @Expose
    val updated_dt:Float = 0.0f
}*/

class RequestUserInfoResult {
    @SerializedName("name")
    @Expose
    val user_name: String? = null

    @SerializedName("height")
    @Expose
    val height = 0f

    @SerializedName("goal")
    @Expose
    val goal = 0f

    @SerializedName("beforeimage")
    @Expose
    val beforeImage: String? = null


    @SerializedName("activity")
    @Expose
    val activity: Int = 0

    @SerializedName("age")
    @Expose
    val age: Int = 0

    @SerializedName("bmi")
    @Expose
    val bmi = 0f

    @SerializedName("weight")
    @Expose
    val weight = 0f

    @SerializedName("beforeweight")
    @Expose
    val before_weight = 0f

    @SerializedName("sex")
    @Expose
    val sex: Int = 0
}
