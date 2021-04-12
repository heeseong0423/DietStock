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
import com.fournineseven.dietstock.databinding.ActivitySplashBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.request.DataReadRequest
import java.time.*
import java.util.concurrent.TimeUnit

private const val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1
private const val PERMISSION_REQUEST_ACTIVITY = 2
private const val TAG = "DietStockTag"
class SplashActivity : AppCompatActivity() {

    //필요한 권한들 정의
    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
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

        checkPermission()

        binding.button.setOnClickListener {
            var intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }


    //권한 체크
    private fun checkPermission(){

        //안드로이드 Q이상일 경우.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d(TAG, "안드로이드 버전이 Q이상이다.")

            //권한이 허가되어있지 않을 경우
            if (checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle("Activity recognition permission")
                builder.setMessage("동의하시겠습니까?")
                builder.setPositiveButton(R.string.ok, null)
                builder.setOnDismissListener(DialogInterface.OnDismissListener {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.ACTIVITY_RECOGNITION
                        ), PERMISSION_REQUEST_ACTIVITY
                    )
                })
                builder.show()
            }
            //권한이 이미 승인 되어있을 경우 구글허가 확인
            else{
                Log.d(TAG,"권한이 이미 승인되어있습니다.")
                googleSignInCheckPermission()//구글핏 API 권한체크
            }
        }
    }


    //구글핏 API 권한 체크 , 이미 되어있다면 걸음수,칼로리 읽어옴.readStepCounter()
    private fun googleSignInCheckPermission(){
        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) { //권한 없을 경우 권한요청
            GoogleSignIn.requestPermissions( //호출 후 결과 요청을 onActivityResult에서 알려줌.
                this, // your activity
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE, // e.g. 1
                account,
                fitnessOptions
            )

        } else {//이미 권한들 체크했을 경우 코드 실행
            readStepCount() //걸음수 읽어옴.
            readCalorie() // 칼로리 읽어옴
        }
    }


    //구글핏 권한체크 후 호출 결과, 승인하면 걸음수 읽어오고 거절하면 종료
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> when (requestCode) {
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE -> {
                    subscribe()
                    readStepCount()
                }
                PERMISSION_REQUEST_ACTIVITY->{
                    Log.d(TAG,"활동 권한 승인")
                }
                else -> {
                    // Result wasn't from Google Fit
                    Log.d(TAG, "Result wasn't from Google fit")
                }
            }
            else -> {
                // Permission not granted
                Log.d(TAG, "Permission not granted")
                Toast.makeText(this, "권한 허가 안함?", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSION_REQUEST_ACTIVITY -> {
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG,"구글 활동 허락했음.")
                    googleSignInCheckPermission()
                }else{
                    Log.d(TAG, "거절했네")
                    finish()
                }
            }
        }
        Log.d(TAG,"REQUEST 권한 설정 결과")
    }

    //구독
    private fun subscribe(){

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

    private fun readStepCount(){
        Fitness.getHistoryClient(this, account)
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener { result ->
                val totalSteps =
                    result.dataPoints.firstOrNull()?.getValue(Field.FIELD_STEPS)?.asInt() ?: 0
                // Use response data here
                Log.i(TAG, "OnSuccess()")
                Log.d(TAG, "걷기 수 : $totalSteps")
                User.step = totalSteps
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "There was a problem getting steps.", e)
            }
    }

    private fun readCalorie(){
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
                    for (dataSet in response.buckets.flatMap { it.dataSets }) {
                        dumpDataSet(dataSet)
                    }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG,"There was an error reading data from Google Fit", e)
                }

    }

    fun dumpDataSet(dataSet: DataSet) {
        Log.i(TAG, "Data returned for Data type: ${dataSet.dataType.name}")
        for (dp in dataSet.dataPoints) {
            Log.i(TAG,"Data point: ${dp.toString()}")
            Log.i(TAG,"\tType: ${dp.dataType.name}")
            Log.i(TAG,"\tStart: ${dp.getStartTimeString()}")
            Log.i(TAG,"\tEnd: ${dp.getEndTimeString()}")
            for (field in dp.dataType.fields) {
                Log.i(TAG,"\tField: ${field.name.toString()} Value: ${dp.getValue(field)}")
                var a = (dp.getValue(field).toString().toFloat()).toInt()
                User.kcal += a
            }
        }
    }

    fun DataPoint.getStartTimeString() = Instant.ofEpochSecond(this.getStartTime(TimeUnit.SECONDS))
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime().toString()

    fun DataPoint.getEndTimeString() = Instant.ofEpochSecond(this.getEndTime(TimeUnit.SECONDS))
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime().toString()

}