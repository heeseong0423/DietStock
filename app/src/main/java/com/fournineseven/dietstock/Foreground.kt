package com.fournineseven.dietstock

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.room.Room
import com.fournineseven.dietstock.room.KcalDatabase
import com.fournineseven.dietstock.room.UserKcalData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import java.time.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

val CHANNEL_ID = "ForegroundChannel"
private const val TAG = "CheckKcal"

class Foreground : Service() {
    var num = 0
    var list = mutableListOf<UserKcalData>()
    var threadState = true

    //필요한 권한들 정의
    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_CALORIES_EXPENDED)
        .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED)
        .build()

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    fun myTask(notification: Notification) {

        val db = Room.databaseBuilder(
            applicationContext,
            KcalDatabase::class.java, "database-name"
        ).allowMainThreadQueries()
            .build()
        val userDao = db.kcalDao()

        thread(start = true) {
            while (threadState) {
                //이게 내 작업
                Thread.sleep(1000)
                //Log.d("MyTag","number : ${num} ")
                //num++

                updateCalories()
                var kcal = User.kcal
                var pkcal = User.PKcal
                var userkcal = UserKcalData(num,kcal, pkcal, 0, 0)
                num++

                userDao.insert(userkcal)
                //이게 알림 띄우는거
                val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Foreground Service")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentText(
                        "오늘 걸음 수 :  ${User.sensorStep + User.step}\n" +
                                "dk :${User.kcal}, pk : ${User.PKcal}"
                    )
                    .build()

                Log.d("룸테스트", "룸 값 : ${userDao.getAll()}")
                startForeground(1, notification)
            }
        }
    }


    fun updateCalories() {
        val end = LocalDateTime.now().atZone(ZoneId.systemDefault())
        val start =
            LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault())
                .toEpochSecond()

        //칼로리 총량 읽기
        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.AGGREGATE_CALORIES_EXPENDED)
            .bucketByActivityType(1, TimeUnit.SECONDS)
            .setTimeRange(start, end.toEpochSecond(), TimeUnit.SECONDS)
            .build()

        Fitness.getHistoryClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
            .readData(readRequest)
            .addOnSuccessListener { response ->
                /*// The aggregate query puts datasets into buckets, so flatten into a single list of datasets
                for (dataSet in response.buckets.flatMap { it.dataSets }) {
                    Log.d(TAG,"${response.buckets}")
                    dumpDataSet(dataSet)
                }
                for ((i, dataSet) in response.buckets.withIndex()) {
                    if (i == 0) {
                        Log.d(TAG, "This is 기초 ㅇㅇ")
                        for (i in dataSet.dataSets[0].dataPoints[0].dataType.fields) {
                            User.kcal =
                                dataSet.dataSets[0].dataPoints[0].getValue(i).toString().toFloat()
                        }
                        //dumpDataSet(dataSet.dataSets[0])
                    } else {
                        Log.d(TAG, "This is 기초 ㄴㄴ")
                        for (i in dataSet.dataSets[0].dataPoints[0].dataType.fields) {
                            User.PKcal =
                                dataSet.dataSets[0].dataPoints[0].getValue(i).toString().toFloat()
                        }
                        // dumpDataSet(dataSet.dataSets[0])
                    }
                }*/
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "There was an error reading data from Google Fit", e)
            }
    }

    fun dumpDataSet(dataSet: DataSet) {
        Log.i(TAG, "Data returned for Data type: ${dataSet.dataType.name}")
        for (dp in dataSet.dataPoints) {
            Log.i(TAG, "Data point: $dp")
            Log.i(TAG, "\tType: ${dp.dataType.name}")
            Log.i(TAG, "\tStart: ${dp.getStartTimeString()}")
            Log.i(TAG, "\tEnd: ${dp.getEndTimeString()}")
            for (field in dp.dataType.fields) {
                Log.i(TAG, "\tField: ${field.name} Value: ${dp.getValue(field)}")
                var kcal = (dp.getValue(field).toString().toFloat())
            }
        }
    }

    fun DataPoint.getStartTimeString() = Instant.ofEpochSecond(this.getStartTime(TimeUnit.SECONDS))
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime().toString()

    fun DataPoint.getEndTimeString() = Instant.ofEpochSecond(this.getEndTime(TimeUnit.SECONDS))
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime().toString()


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentText("Hello World")
            .build()

        myTask(notification)
        startForeground(1, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        threadState = false
        isActivityForeground = false
    }

}