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
import kotlin.concurrent.thread

val CHANNEL_ID = "ForegroundChannel"

class Foreground : Service() {

    var num = 0
    var threadState = true

    override fun onBind(intent: Intent): IBinder {
        return Binder()
    }

    fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    fun myTask(notification: Notification){
        thread(start=true){
            while(threadState){
                //이게 내 작업
                Thread.sleep(1000)
                //Log.d("MyTag","number : ${num} ")
                //num++

                    //이게 알림 띄우는거
                    val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Foreground Service")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentText("오늘 걸음 수 :  ${User.sensorStep + User.step}\n" +
                            "오늘 칼로리 소모 수 :${User.sensorKcal} +  ${User.kcal}")
                    .build()
                startForeground(1,notification)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentText("Hello World")
            .build()

        myTask(notification)
        startForeground(1,notification)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        threadState = false
    }

}