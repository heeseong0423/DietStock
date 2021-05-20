package com.fournineseven.dietstock.model.FoodCamera

import com.google.gson.annotations.SerializedName

data class DefaultResponseKo(
    @SerializedName("success") var success: Boolean
)

data class GetFoodResponse(
    @SerializedName("success") var success: Boolean,
    @SerializedName("result") var result: ArrayList<ResultFood>
)

data class ResultFood(
    @SerializedName("food_no") var foodNo: String,
    @SerializedName("kcal") var kcal: Float,
    @SerializedName("carbs") var carbs: Float,
    @SerializedName("protein") var protein: Float,
    @SerializedName("fat") var fat: Float,
    @SerializedName("sugar") var sugar: Float,
    @SerializedName("natrium") var natrium: Float,
    @SerializedName("cholesterol") var cholesterol: Float
)