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
