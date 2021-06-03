package com.fournineseven.dietstock

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.net.Uri
import android.widget.ImageView
import java.io.File

@SuppressLint("StaticFieldLeak")
object LoginState {
    const val USER_NUMBER = "user_number"

    // creating constant keys for shared preferences.
    const val SHARED_PREFS = "shared_prefs"

    // key for storing email.
    const val EMAIL_KEY = "email_key"

    // key for storing password.
    const val PASSWORD_KEY = "password_key"
    const val NAME_KEY = "name_key"
    const val BEFORE_IMAGE_KEY = "before_image"
    const val AFTER_IMAGE_KEY = "after_image"
    const val GOAL_KEY = "goal_key"
    const val WEIGHT_KEY = "weight_key"
    const val HEIGHT_KEY = "height_key"
    const val AGE_KEY = "age_key"
    const val GENDER_KEY = "gender_key"
    const val ACTIVITY_KEY = "activity_key"



    // variable for shared preferences.
    //var sharedpreferences: SharedPreferences? = null
    var email: String?=null
    var password:String?=null
    var beforeImage:String?=null
    var afterImage:String?=null
    var goal:Float=0.0f
    var weight:Float=0.0f
    var height:Float=0.0f
    var age:Int = 0


    //ex: {"user_no" : 33, "low" : -12.2, "high":831.2, "start_kcal": 0.0 ,"end_kcal": 323.5, "date":"2021-05-01"}
    const val LOW_KEY = "low_key" //섭취한 칼로리만 표시
    const val HIGH_KEY = "high_key" //소모한 칼로리만 표시
    const val START_KEY = "start_key" // 어제의 시작 또는 0
    const val END_KEY = "end_key" //오늘의 소모한칼로리 - 섭취한 칼로리
    const val DATE_KEY = "date_key" // 오늘의 날짜
    const val START_TIME_KEY = "start_time_key"
    const val INTAKE_KEY = "intake_key"
}
