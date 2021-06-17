package com.fournineseven.dietstock

import android.Manifest
import android.R
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog


import androidx.core.content.ContextCompat
import androidx.room.Room
import com.fournineseven.dietstock.databinding.ActivitySplashBinding
import com.fournineseven.dietstock.room.KcalDatabase
import com.fournineseven.dietstock.room.UserKcalData

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field

import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.DataSourcesRequest
import com.google.android.gms.fitness.request.OnDataPointListener
import com.google.android.gms.fitness.request.SensorRequest
import java.time.*
import java.util.concurrent.TimeUnit

private const val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1
private const val PERMISSION_REQUEST_ACTIVITY = 2
private const val TAG = "DietStockTag"

var isActivityForeground = false

class SplashActivity : AppCompatActivity() {

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

    //구글핏 접근을 위한 API Account 인스턴스 생성
    private val account by lazy {
        GoogleSignIn.getAccountForExtension(this, fitnessOptions)
    }

    //화면 정의
    private val binding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        /*val db = Room.databaseBuilder(
                this,
                KcalDatabase::class.java, "database-name"
        ).allowMainThreadQueries()
                .build()
        val userDao = db.kcalDao()

        var userKcalData = UserKcalData(0, User.kcal,User.PKcal,User.startKcal,User.endKcal,User.highKcal,User.lowKcal)
        userDao.insert(userKcalData)
*/
        //오늘 날짜 얻기
        TimeCheck.appStartTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault())
                .toEpochSecond()

        checkPermission()
        if (!isActivityForeground) {
            val intent = Intent(this, Foreground::class.java)
            ContextCompat.startForegroundService(this, intent)
            isActivityForeground = true
        } else {
            Log.d(TAG,"포어그라운드가 현재 실행중입니다")
        }

        binding.button.setOnClickListener {
            var intent = Intent(this,SignActivity::class.java)
            val intent1 = Intent(this, Foreground::class.java)
            startActivity(intent)
        }


    }
    //권한 체크
    private fun checkPermission() {

        //안드로이드 Q이상일 경우.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            //권한이 허가되어있지 않을 경우
            if (checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle("Activity recognition permission")
                builder.setMessage("동의하시겠습니까?")
                builder.setPositiveButton(R.string.ok, null)
                builder.setOnDismissListener(DialogInterface.OnDismissListener {
                    requestPermissions(
                            arrayOf(
                                    Manifest.permission.ACTIVITY_RECOGNITION,
                                    Manifest.permission.CAMERA,

                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,

                            ), PERMISSION_REQUEST_ACTIVITY
                    )
                })
                builder.show()
            }
            //권한이 이미 승인 되어있을 경우 구글허가 확인
            else {
                googleSignInCheckPermission()//구글핏 API 권한체크
            }
        }
    }


    //구글핏 API 권한 체크
    private fun googleSignInCheckPermission() {
        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) { //권한 없을 경우 권한요청
            GoogleSignIn.requestPermissions( //호출 후 결과 요청을 onActivityResult에서 알려줌.
                    this, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE, // e.g. 1
                    account,
                    fitnessOptions
            )

        } else {//이미 권한들 체크했을 경우 코드 실행
            readStepCount() //걸음수 읽어옴.
            //readCalorie()
        }
    }


    //구글핏 권한체크 후 호출 결과
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> when (requestCode) {
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE -> {
                    subscribe()
                    readStepCount()
                    //readCalorie()
                }
                PERMISSION_REQUEST_ACTIVITY -> {
                }
                else -> {
                    // Result wasn't from Google Fit
                    Log.d(TAG, "Result wasn't from Google fit")
                }
            }
            else -> {
                // Permission not granted
                Toast.makeText(this, "권한 허가 안함?", Toast.LENGTH_LONG).show()

                //finish()
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_ACTIVITY -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //googleSignInCheckPermission()

                } else {
                    finish()
                }
            }
        }
    }

    //구독
    private fun subscribe() {

        //누적걷기 구독
        Fitness.getRecordingClient(
                this,
                GoogleSignIn.getAccountForExtension(this, fitnessOptions)
        )
                .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addOnSuccessListener {
                    Log.i(TAG, "누적걷기수 구독 성공")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "누적걷기수 구독 오류 발생", e)
                }

        //걷기 구독
        Fitness.getRecordingClient(
                this,
                GoogleSignIn.getAccountForExtension(this, fitnessOptions)
        )
                .subscribe(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener {
                    Log.i(TAG, "걷기수 구독 성공")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "걷기수 구독 오류 발생 ", e)
                }

        //칼로리 총량 구독
        Fitness.getRecordingClient(
                this,
                GoogleSignIn.getAccountForExtension(this, fitnessOptions)
        )
                .subscribe(DataType.AGGREGATE_CALORIES_EXPENDED)
                .addOnSuccessListener {
                    Log.i(TAG, "칼로리 총량 구독 성공")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "칼로리 총량 구독 오류 발생", e)
                }

        //칼로리 구독
        Fitness.getRecordingClient(
                this,
                GoogleSignIn.getAccountForExtension(this, fitnessOptions)
        )
                .subscribe(DataType.TYPE_CALORIES_EXPENDED)
                .addOnSuccessListener {
                    Log.i(TAG, "칼로리 구독 성공")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "칼로리 구독 오류 발생", e)
                }
    }

    private fun readCalorie() {
        val end = LocalDateTime.now().atZone(ZoneId.systemDefault())
        val start = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault()).toEpochSecond()

        //칼로리 총량 읽기
        val readRequest = DataReadRequest.Builder()
                .aggregate(DataType.AGGREGATE_CALORIES_EXPENDED)
                .bucketByActivityType(1, TimeUnit.SECONDS)
                .setTimeRange(start, end.toEpochSecond(), TimeUnit.SECONDS)
                .build()

        Fitness.getHistoryClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
                .readData(readRequest)
                .addOnSuccessListener { response ->
                    // The aggregate query puts datasets into buckets, so flatten into a single list of datasets

//                    for (dataSet in response.buckets.flatMap { it.dataSets }) {
//                        dumpDataSet(dataSet)
//                    }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "There was an error reading data from Google Fit", e)
                }
    }

    fun dumpDataSet(dataSet: DataSet) {
        Log.i(TAG, "Data returned for Data type: ${dataSet.dataType.name}")
        for (dp in dataSet.dataPoints) {
            Log.i(TAG, "Data point: ${dp.toString()}")
            Log.i(TAG, "\tType: ${dp.dataType.name}")
            Log.i(TAG, "\tStart: ${dp.getStartTimeString()}")
            Log.i(TAG, "\tEnd: ${dp.getEndTimeString()}")
            for (field in dp.dataType.fields) {
                Log.i(TAG, "\tField: ${field.name.toString()} Value: ${dp.getValue(field)}")
                var a = (dp.getValue(field).toString().toFloat())
                User.kcal = a
            }
        }
    }

    fun DataPoint.getStartTimeString() = Instant.ofEpochSecond(this.getStartTime(TimeUnit.SECONDS))
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime().toString()

    fun DataPoint.getEndTimeString() = Instant.ofEpochSecond(this.getEndTime(TimeUnit.SECONDS))
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime().toString()

    private fun readStepCount() {
        Fitness.getHistoryClient(this, account)
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener { result ->
                    val totalSteps =
                            result.dataPoints.firstOrNull()?.getValue(Field.FIELD_STEPS)?.asInt()
                                    ?: 0
                    // Use response data here
                    User.step = totalSteps
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "There was a problem getting steps.", e)
                }


        Fitness.getSensorsClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
                .findDataSources(
                        DataSourcesRequest.Builder()
                                .setDataTypes(DataType.TYPE_STEP_COUNT_DELTA, DataType.TYPE_CALORIES_EXPENDED)
                                .setDataSourceTypes(DataSource.TYPE_RAW)
                                .build())
                .addOnSuccessListener { dataSources ->
                    dataSources.forEach {
                        Log.i(TAG, "Data source found: ${it.streamIdentifier}")
                        Log.i(TAG, "Data Source type: ${it.dataType.name}")

                        if (it.dataType == DataType.TYPE_STEP_COUNT_DELTA) {
                            Log.i(TAG, "Data source for STEP_COUNT_DELTA found!")
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Find data sources request failed", e)
                }

        val listener = OnDataPointListener { dataPoint ->
            for (field in dataPoint.dataType.fields) {
                val value = dataPoint.getValue(field)
                Log.i(TAG, "Detected DataPoint field: ${field.name}")
                Log.i(TAG, "Detected DataPoint value: $value")
                if ("${field.name}" == "steps") {
                    User.sensorStep += value.toString().toInt()
                } else {
                    User.sensorKcal += value.toString().toFloat()
                }
            }
        }

        Fitness.getSensorsClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
                .add(
                        SensorRequest.Builder()
                                //.setDataSource() // Optional but recommended for custom
                                // data sets.
                                .setDataType(DataType.TYPE_STEP_COUNT_DELTA) // Can't be omitted.
                                .setSamplingRate(10, TimeUnit.SECONDS)
                                .build(),
                        listener
                )
                .addOnSuccessListener {
                    Log.i(TAG, "Listener registered!")
                }
                .addOnFailureListener {
                    Log.e(TAG, "Listener not registered.")
                }

        Fitness.getSensorsClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
                .add(
                        SensorRequest.Builder()
                                //.setDataSource() // Optional but recommended for custom
                                // data sets.
                                .setDataType(DataType.TYPE_CALORIES_EXPENDED) // Can't be omitted.
                                .setSamplingRate(10, TimeUnit.SECONDS)
                                .build(),
                        listener
                )
                .addOnSuccessListener {
                    Log.i(TAG, "칼로리 리스너 등록 성공")
                }
                .addOnFailureListener {
                    Log.e(TAG, "칼로리 리스너 등록 실패")
                }
    }
}