package com.fournineseven.dietstock


import android.app.*
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

import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.*
import java.util.concurrent.TimeUnit

val CHANNEL_ID = "ForegroundChannel"
private const val TAG = "CheckKcal"

class Foreground : Service() {

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


    fun myBackgroundTask(notification: Notification) {

        GlobalScope.launch {
            while(threadState){
                delay(5000)
                updateCalories()


                val notification: Notification = NotificationCompat.Builder(baseContext, CHANNEL_ID)
                    .setContentTitle("칼로리체크")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentText(
                        "bk :${User.kcal}, pk : ${User.PKcal}" +
                                "H: ${User.highKcal} , " +
                                "L: ${User.lowKcal}"
                    )
                    .build()
                startForeground(1, notification)
            }
        }
    }


    fun updateCalories() {
        val end = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond()

        val start =
            LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault())
                .toEpochSecond()


        //하루가 바뀌었을 경우 오늘날짜 변경과 룸 데이터베이스에 데이터 인서트
        if(TimeCheck.appStartTime != start){
            val db = Room.databaseBuilder(
                    applicationContext,
                    KcalDatabase::class.java, "database-name"
            ).allowMainThreadQueries()
                    .build()
            val userDao = db.kcalDao()

            //어제부터 오늘 0시까지의 시간의 칼로리 읽어오기. 86400L은 하루 시간을 Long타입으로 나타낸것.
            readCalories(TimeCheck.appStartTime!!,TimeCheck.appStartTime!! + 86400L)

            var yesterdayData = userDao.getLastData()
            User.startKcal = 0f
            User.endKcal = yesterdayData.baseKcal + yesterdayData.physicalKcal



            var userData = UserKcalData(TimeCheck.appDate,User.kcal,User.PKcal,
                    User.startKcal,User.endKcal,User.highKcal,User.lowKcal)
            userDao.insert(userData)
            TimeCheck.appStartTime = start
            TimeCheck.appDate++

            User.highKcal = 0f
            User.lowKcal = 0f

        }else{
            //오늘0시부터 현재까지의 칼로리 읽기.
            readCalories(start,end)
            getHighLowKcal()
        }
    }

    fun readCalories(startTime:Long , endTime:Long){

        //칼로리 총량 읽기
        val readRequest = DataReadRequest.Builder()
                .aggregate(DataType.AGGREGATE_CALORIES_EXPENDED)
                .bucketByActivityType(1, TimeUnit.SECONDS)
                .setTimeRange(startTime, endTime, TimeUnit.SECONDS)
                .build()

        Fitness.getHistoryClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
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

    fun getHighLowKcal(){
        var totalKcal = User.kcal + User.PKcal

        if(totalKcal > User.highKcal){
            User.highKcal = totalKcal
        }
        if(totalKcal < User.lowKcal){
            User.lowKcal = totalKcal
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentText("Hello World")
            .build()


        myBackgroundTask(notification)
        //alarmRegister()
        startForeground(1, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        threadState = false
        isActivityForeground = false
    }


    fun alarmRegister(){
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, AlarmReceiver.REQUEST_ID, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        val repeatInterval: Long =  86400 // 하루시간
        val triggerTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault())
                .toEpochSecond() + 1619741870


        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
                1619742144, repeatInterval,
            pendingIntent)
    }
}