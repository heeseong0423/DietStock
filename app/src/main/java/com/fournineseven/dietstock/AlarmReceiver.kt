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
import com.fournineseven.dietstock.retrofitness.GetDailyKcalResponse
import com.fournineseven.dietstock.retrofitness.RetrofitBuilder
import com.fournineseven.dietstock.retrofitness.SaveKcalLogResponse
import com.fournineseven.dietstock.room.KcalDatabase
import com.fournineseven.dietstock.room.UserKcalData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "AlarmReceiver"
        const val REQUEST_ID = 1
    }
    //필요한 권한들 정의
    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_NUTRITION)
        .addDataType(DataType.TYPE_CALORIES_EXPENDED)
        .addDataType(DataType.AGGREGATE_NUTRITION_SUMMARY)
        .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED)
        .build()

    override fun onReceive(context: Context, intent: Intent) {
        val dt = Date()
        val full_sdf = SimpleDateFormat("yyyy-MM-dd")
        var today = full_sdf.format(dt)

        var sharedPreferences = context.getSharedPreferences(LoginState.SHARED_PREFS,Context.MODE_PRIVATE)
        var sharedToday = sharedPreferences.getString(LoginState.DATE_KEY,null)

        Log.d(TAG,"오늘 $today 그리고 쉐어드의 ${sharedToday}")
        Log.d(TAG, "Received intent : $intent")

        readKcal(context)


        //테스트
         RetrofitBuilder.api.getDailyKcal(GetDailyKcalRequest(user_no = 2, date = sharedToday.toString()))
             .enqueue(object : Callback<GetDailyKcalResponse>{
                 override fun onResponse(
                     call: Call<GetDailyKcalResponse>,
                     response: Response<GetDailyKcalResponse>
                 ) {
                     Log.d(TAG,"오늘의 칼로리 ${response.body()?.result?.get(0)?.kcalSum}")
                     User.UserIntakeKcal = response.body()?.result?.get(0)!!.kcalSum

                 }

                 override fun onFailure(call: Call<GetDailyKcalResponse>, t: Throwable) {
                     Log.d(TAG,"실패했습니다.")
                 }

             })

        Log.d(TAG,"그냥 칼로리 : ${User.kcal} 운동 칼로리 : ${User.PKcal} ${User.UserIntakeKcal}")


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
            var sharedHighKcal = sharedPreferences.getFloat(LoginState.HIGH_KEY,0f)
            var sharedLowKcal = sharedPreferences.getFloat(LoginState.LOW_KEY,0f)
            var sharedStartKcal = sharedPreferences.getFloat(LoginState.START_KEY,0.0f)
            editor.putFloat(LoginState.END_KEY,User.PKcal+User.kcal - User.UserIntakeKcal)
            var sharedEndKcal = sharedPreferences.getFloat(LoginState.END_KEY,0.0f)
            var userNumber = sharedPreferences.getString(LoginState.USER_NUMBER,"0")!!.toInt()



            /*var editor = sharedPreferences.edit()
            //editor.putString(LoginState.DATE_KEY,today) //바뀐 오늘 날짜 넣기
            //editor.putFloat(LoginState.START_KEY,User.kcal + User.PKcal) // 시작 칼로리 넣기 {하루 바뀌고 난 후의 종료 칼로리 넣으면 됨}
            editor.putFloat(LoginState.HIGH_KEY,User.PKcal + User.kcal) //high kcal 넣기 { 소모 칼로리만 넣으면 됨 }
            editor.putFloat(LoginState.LOW_KEY,User.UserIntakeKcal) //logKcal 넣기 {오늘 섭취한 음식들 총합 넣으면 됨}
            editor.putFloat(LoginState.END_KEY,(User.PKcal+User.kcal) - User.UserIntakeKcal) // 종료 칼로리 넣기 {오늘 소모 - 섭취 칼로리 넣기}
            editor.apply()
            var sharedHighKcal = sharedPreferences.getFloat(LoginState.HIGH_KEY,0f)                 // -> 0이 들어감
            var sharedLowKcal = sharedPreferences.getFloat(LoginState.LOW_KEY,0f)                    // -> 0이 들어감
            var sharedEndKcal = sharedPreferences.getFloat(LoginState.END_KEY,0f)                  //-> 0이 들어감
            var sharedStartKcal = sharedPreferences.getFloat(LoginState.START_KEY,0.0f)            //-> 0이 들어감
            var userNumber = sharedPreferences.getString(LoginState.USER_NUMBER,"0")!!.toInt()
*/
            val db = Room.databaseBuilder(
                context,
                KcalDatabase::class.java, "database-name1"
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
                        Log.d(TAG,"알람 성공 HELLO WORLD HTTPS")
                        editor.putFloat(LoginState.START_KEY,sharedEndKcal)
                        editor.putString(LoginState.DATE_KEY,today)
                        editor.putLong(LoginState.START_TIME_KEY,
                            LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault())
                            .toEpochSecond())
                        editor.putFloat(LoginState.INTAKE_KEY,0.0f)
                        editor.apply()
                    }

                    override fun onFailure(call: Call<SaveKcalLogResponse>, t: Throwable) {
                        Log.d(TAG,"알람 실패 HELLO WORLD HTTPS")
                    }
                })
        }
    }

    fun readKcal(context: Context){
        var sharedPreferences = context.getSharedPreferences(LoginState.SHARED_PREFS,Context.MODE_PRIVATE)
        var start = sharedPreferences.getLong(LoginState.START_TIME_KEY,0L)
        var end = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond()
        //칼로리 총량 읽기
        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.AGGREGATE_CALORIES_EXPENDED)
            .bucketByActivityType(1, TimeUnit.SECONDS)
            .setTimeRange(start, end, TimeUnit.SECONDS)
            .build()

        Fitness.getHistoryClient(context, GoogleSignIn.getAccountForExtension(context, fitnessOptions))
            .readData(readRequest)
            .addOnSuccessListener { response ->

                for ((i, dataSet) in response.buckets.withIndex()) {
                    if (i == 0) {
                        for (i in dataSet.dataSets[0].dataPoints[0].dataType.fields) {
                            User.kcal =
                                dataSet.dataSets[0].dataPoints[0].getValue(i).toString().toFloat()
                        }
                    } else {
                        for (i in dataSet.dataSets[0].dataPoints[0].dataType.fields) {
                            User.PKcal =
                                dataSet.dataSets[0].dataPoints[0].getValue(i).toString().toFloat()
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "There was an error reading data from Google Fit", e)
            }

    }
}