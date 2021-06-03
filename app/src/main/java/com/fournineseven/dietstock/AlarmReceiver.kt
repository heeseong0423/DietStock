package com.fournineseven.dietstock

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.room.Room
import com.fournineseven.dietstock.retrofitness.RetrofitBuilder
import com.fournineseven.dietstock.retrofitness.SaveKcalLogResponse
import com.fournineseven.dietstock.room.KcalDatabase
import com.fournineseven.dietstock.room.UserKcalData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "AlarmReceiver"
        const val REQUEST_ID = 1
    }
    override fun onReceive(context: Context, intent: Intent) {
        val dt = Date()
        val full_sdf = SimpleDateFormat("yyyy-MM-dd")
        var today = full_sdf.format(dt)

        var sharedPreferences = context.getSharedPreferences(LoginState.SHARED_PREFS,Context.MODE_PRIVATE)
        var sharedToday = sharedPreferences.getString(LoginState.DATE_KEY,null)

        Log.d(TAG,"오늘 $today 그리고 쉐어드의 ${sharedToday}")
        Log.d(TAG, "Received intent : $intent")

        //테스트
       /* RetrofitBuilder.api.saveKcalLog(SaveKcalLogRequest(user_no = 22, low = -500.0f,
            high = 1231.8f, end_kcal = 9000.0f,start_kcal = 0.0f , date = sharedToday.toString()))
            .enqueue(object : Callback<SaveKcalLogResponse>{
                override fun onResponse(
                    call: Call<SaveKcalLogResponse>,
                    response: Response<SaveKcalLogResponse>
                ) {
                    Log.d(TAG,"알람 성공")
                    Log.e("error", response.toString())

                    *//*editor.putFloat(LoginState.START_KEY,sharedEndKcal)
                    editor.putString(LoginState.DATE_KEY,today)
                    editor.putLong(LoginState.START_TIME_KEY,
                        LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault())
                            .toEpochSecond())
                    editor.apply()*//*
                }

                override fun onFailure(call: Call<SaveKcalLogResponse>, t: Throwable) {
                    Log.d(TAG,"알람 실패")
                }
            })
*/

        if(sharedToday == today){
            Log.d(TAG,"오늘인데요.")
        }else{
            Log.d(TAG,"하루가 바뀜")

            var editor = sharedPreferences.edit()
            //editor.putString(LoginState.DATE_KEY,today) //바뀐 오늘 날짜 넣기
            //editor.putFloat(LoginState.START_KEY,User.kcal + User.PKcal) // 시작 칼로리 넣기 {하루 바뀌고 난 후의 종료 칼로리 넣으면 됨}
            editor.putFloat(LoginState.HIGH_KEY,User.PKcal + User.kcal) //high kcal 넣기 { 소모 칼로리만 넣으면 됨 }
            editor.putFloat(LoginState.LOW_KEY,0.0f) //logKcal 넣기 {오늘 섭취한 음식들 총합 넣으면 됨}
            editor.putFloat(LoginState.END_KEY,(User.PKcal+User.kcal) - 0.0f) // 종료 칼로리 넣기 {오늘 소모 - 섭취 칼로리 넣기}
            editor.apply()
            var sharedHighKcal = sharedPreferences.getFloat(LoginState.HIGH_KEY,0f)
            var sharedLowKcal = sharedPreferences.getFloat(LoginState.LOW_KEY,0f)
            var sharedEndKcal = sharedPreferences.getFloat(LoginState.END_KEY,0f)
            var sharedStartKcal = sharedPreferences.getFloat(LoginState.START_KEY,0.0f)
            var userNumber = sharedPreferences.getString(LoginState.USER_NUMBER,"0")!!.toInt()

            val db = Room.databaseBuilder(
                context,
                KcalDatabase::class.java, "database-name"
            ).allowMainThreadQueries()
                .build()
            val userDao = db.kcalDao()
            userDao.insert(UserKcalData(0,User.kcal,User.PKcal,sharedStartKcal,sharedEndKcal,sharedHighKcal,sharedLowKcal))

            RetrofitBuilder.api.saveKcalLog(SaveKcalLogRequest(user_no = userNumber, low = sharedLowKcal,
            high = sharedHighKcal, end_kcal = sharedEndKcal,start_kcal = sharedStartKcal , date = sharedToday.toString()))
                .enqueue(object : Callback<SaveKcalLogResponse>{
                    override fun onResponse(
                        call: Call<SaveKcalLogResponse>,
                        response: Response<SaveKcalLogResponse>
                    ) {
                        Log.e("error", response.toString())
                        Log.d(TAG,"알람 성공")
                        editor.putFloat(LoginState.START_KEY,sharedEndKcal)
                        editor.putString(LoginState.DATE_KEY,today)
                        editor.putLong(LoginState.START_TIME_KEY,
                            LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault())
                            .toEpochSecond())
                        editor.apply()
                    }

                    override fun onFailure(call: Call<SaveKcalLogResponse>, t: Throwable) {
                        Log.d(TAG,"알람 실패")
                    }
                })
        }

    }
}