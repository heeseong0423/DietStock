package com.fournineseven.dietstock.ui.home


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import androidx.room.Room
import com.fournineseven.dietstock.CSStock
import com.fournineseven.dietstock.LoginState
import com.fournineseven.dietstock.R
import com.fournineseven.dietstock.User
import com.fournineseven.dietstock.room.KcalDatabase
import com.fournineseven.dietstock.room.UserKcalData
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

private const val TAG = "HomeFragmentTag"
private const val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var rootView: View
    private lateinit var textView:TextView
    private lateinit var consumeTextVIew: TextView
    private var CoroutineState = false

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

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)


        val root = inflater.inflate(R.layout.fragment_home, container, false)
        textView = root.findViewById(R.id.text_home)
        consumeTextVIew = root.findViewById(R.id.textConsume)
        val dietChart:CandleStickChart = root.findViewById(R.id.dietStockChart)
        val button: Button = root.findViewById(R.id.google_button)
        val account = GoogleSignIn.getAccountForExtension(root.context, fitnessOptions)

        rootView = root

        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        //구글피트가 연동되어있으면 차트 보여주고,
        //안되어있으면 버튼 보여준다.
        checkGooglefitPermission(root)

        //버튼 클릭 시 구글연동 권한 창 띄움.
        button.setOnClickListener {
            GoogleSignIn.requestPermissions( //호출 후 결과 요청을 onActivityResult에서 알려줌.
                this, // your activity
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE, // e.g. 1
                account,
                fitnessOptions
            )
        }


        return root
    }


    override fun onPause() {
        super.onPause()
        Log.d(TAG, "이것은 onPause()")
        CoroutineState = false
    }

    override fun onResume() {
        super.onResume()
        val dt = Date()
        val full_sdf = SimpleDateFormat("yyyy-MM-dd")
        val dietChart: CandleStickChart = rootView.findViewById(R.id.dietStockChart)
        //오늘 날짜 Long값으로 받기
        var sharedpreferences = this.activity?.getSharedPreferences(LoginState.SHARED_PREFS,
            Context.MODE_PRIVATE)
        var startTime = sharedpreferences?.getLong(LoginState.START_TIME_KEY,0)
        var consumeKcal = sharedpreferences?.getFloat(LoginState.LOW_KEY,0.0f)
        if(startTime!! <2){
            var editor = sharedpreferences?.edit()
            startTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault())
                .toEpochSecond()
            editor?.putLong(LoginState.START_TIME_KEY,startTime)
            editor?.apply()
        }


        Log.d(TAG, "이것은 onResume()")
        if(CoroutineState){
            Log.d(TAG, "This is already true.")
            return
        }
        CoroutineState = true
        CoroutineScope(Dispatchers.Main).launch {
            while (CoroutineState) {
                Log.d(TAG, "현재 칼로리 : ${User.kcal} , 피지컬: ${User.PKcal}" +
                        "현재 ${User.highKcal}, ${User.lowKcal} and ${full_sdf.format(dt)}")
                updateCalories(startTime!!)
                textView.text = "칼로리 소모량 : ${User.kcal + User.PKcal}Kcal"
                consumeTextVIew.text = "칼로리 소모량 : ${User.UserIntakeKcal} Kcal"
                drawCandlestickChart(dietChart,rootView)
                delay(3000)
            }
        }
    }

    fun readCalories(startTime: Long, endTime: Long) {

        //칼로리 총량 읽기
        val readRequest = DataReadRequest.Builder()
            .aggregate(DataType.AGGREGATE_CALORIES_EXPENDED)
            .bucketByActivityType(1, TimeUnit.SECONDS)
            .setTimeRange(startTime, endTime, TimeUnit.SECONDS)
            .build()

        Fitness.getHistoryClient(rootView.context, GoogleSignIn.getAccountForExtension(rootView.context, fitnessOptions))
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

    fun updateCalories(startTime: Long) {
        var end = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond()
        var start =
            LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault())
                .toEpochSecond()

        readCalories(startTime, end)
//        getHighLowKcal()
        var current : CandleEntry
    }

    //구글 연동에 따라 버튼 또는 차트 보여준다.
    fun checkGooglefitPermission(root: View) {
        val dietChart: CandleStickChart = root.findViewById(R.id.dietStockChart)
        val button: Button = root.findViewById(R.id.google_button)
        val account = GoogleSignIn.getAccountForExtension(root.context, fitnessOptions)

        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            Log.d(TAG, "구글 계정 연동 안되어있다.")
            dietChart.visibility = View.GONE
            button.visibility = View.VISIBLE
        } else {
            Log.d(TAG, "구글계정 연동되어있음.")
            dietChart.visibility = View.VISIBLE
            button.visibility = View.GONE
            drawCandlestickChart(dietChart,root)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "호출되려")
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE -> {
                    subscribe()
                    checkGooglefitPermission(rootView)
                    Log.d(TAG, "This is Activity result")
                }
            }
        } else {
            Toast.makeText(context, "연동하세요", Toast.LENGTH_SHORT).show()
        }
    }

    private fun subscribe() {
        //누적걷기 구독
        Fitness.getRecordingClient(
            rootView.context,
            GoogleSignIn.getAccountForExtension(rootView.context, fitnessOptions)
        )
            .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
            .addOnSuccessListener {
                Log.d(TAG, "누적걷기수 구독 성공")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "누적걷기수 구독 오류 발생", e)
            }

        //걷기 구독
        Fitness.getRecordingClient(
            rootView.context,
            GoogleSignIn.getAccountForExtension(rootView.context, fitnessOptions)
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
            rootView.context,
            GoogleSignIn.getAccountForExtension(rootView.context, fitnessOptions)
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
            rootView.context,
            GoogleSignIn.getAccountForExtension(rootView.context, fitnessOptions)
        )
            .subscribe(DataType.TYPE_CALORIES_EXPENDED)
            .addOnSuccessListener {
                Log.i(TAG, "칼로리 구독 성공")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "칼로리 구독 오류 발생", e)
            }
    }

    fun drawCandlestickChart(dietChart: CandleStickChart,root: View) {
        val entries = ArrayList<CandleEntry>()
        val db = Room.databaseBuilder(
            root.context,
            KcalDatabase::class.java, "database-name1"
        ).allowMainThreadQueries()
            .build()
        val userDao = db.kcalDao()

        /*for (csStock in DataUtil.getCSStockData()) {
            entries.add(
                CandleEntry(
                    csStock.createdAt.toFloat(),
                    csStock.shadowHigh,
                    csStock.shadowLow,
                    csStock.open,
                    csStock.close
                )
            )
        }*/

        //userDao.insert(UserKcalData(0,13.2f,141.0f,0.0f,434.3f,2323.0f,-23.3f))
        Log.d(TAG,"${userDao.getLastData()}")

        for(csStock in userDao.getAll()){
            entries.add(
                CandleEntry(
                    csStock.no.toFloat(),
                    csStock.highKcal,
                    csStock.lowKcal,
                    csStock.startTime,
                    csStock.endTime
                )
            )
        }


        entries.add(
            CandleEntry(
                userDao.getLastData().no+1.toFloat(),
                User.PKcal + User.kcal,
                User.UserIntakeKcal,
                0.0f,
                User.PKcal + User.kcal - User.UserIntakeKcal
            )
        )

        val dataSet = CandleDataSet(entries, "").apply {
            //심지 부분
            shadowColor = Color.LTGRAY
            shadowWidth = 1F

            //음봉
            decreasingColor = Color.BLUE
            decreasingPaintStyle = Paint.Style.FILL_AND_STROKE

            //양봉
            increasingColor = Color.RED
            increasingPaintStyle = Paint.Style.FILL_AND_STROKE

            neutralColor = Color.DKGRAY
            setDrawValues(true)
            //터치시 노란선 제거
            highLightColor = Color.TRANSPARENT

            valueTextColor = Color.BLACK
            valueTextSize = 12.0f
        }

        dietChart.axisLeft.run {
            setDrawAxisLine(true)
            setDrawGridLines(false)
            textColor = Color.TRANSPARENT
        }
        dietChart.axisRight.run {
            setDrawAxisLine(true)
            setDrawGridLines(false)
        }

        dietChart.xAxis.run {
            textColor = Color.TRANSPARENT
            setDrawAxisLine(false)
            setDrawGridLines(false)
            setAvoidFirstLastClipping(true)
        }
        dietChart.legend.run {
            //isEnabled = false
            isEnabled = false

        }


        dietChart.apply {
            this.data = CandleData(dataSet)
            description.isEnabled = false
            isHighlightPerDragEnabled = true
            requestDisallowInterceptTouchEvent(true)
            setVisibleXRangeMaximum(14f)
            setVisibleXRangeMinimum(0f)
            moveViewToX(dietChart.xAxis.axisMaximum)
            invalidate()
        }
    }
}


object DataUtil {
    fun getCSStockData(): List<CSStock> {
        return listOf(
            CSStock(
                createdAt = 0,
                open = 0f,
                close = 1778.4f,
                shadowHigh = 2004.8f,
                shadowLow = -305.0F
            ),
            CSStock(
                createdAt = 1,
                open = 1778.4f,
                close = 1435.8f,
                shadowHigh = 1574.9f,
                shadowLow = 350.4f
            ),
            CSStock(
                createdAt = 2,
                open = 1435.8f,
                close = 221.9F,
                shadowHigh = 868.4f,
                shadowLow = -648f
            ),
            CSStock(
                createdAt = 3,
                open = 211.9f,
                close = 864.2F,
                shadowHigh = 1500.7F,
                shadowLow = 222.1F
            ),
            CSStock(
                createdAt = 4,
                open = 864.2F,
                close = 1876.4F,
                shadowHigh = 2452.7F,
                shadowLow = 0F
            ),
            CSStock(
                createdAt = 5,
                open = 1876.4F,
                close = 137.2F,
                shadowHigh = 2225.0F,
                shadowLow = 0F
            ),
            CSStock(
                createdAt = 6,
                open = 137.2F,
                close = -332.7F,
                shadowHigh = 831.5F,
                shadowLow = -435.7F
            ),
            CSStock(
                createdAt = 7,
                open = -332.7F,
                close = 1522.2F,
                shadowHigh = 1822.5F,
                shadowLow = 357.8F
            ),
            CSStock(
                createdAt = 8,
                open = 1522.2F,
                close = 2830.7F,
                shadowHigh = 2985.4F,
                shadowLow = 0F
            ),
            CSStock(
                createdAt = 9,
                open = 2830.7F,
                close = -851.5F,
                shadowHigh = 2830.7F,
                shadowLow = -907.4F
            ),
            CSStock(
                createdAt = 10,
                open = -851.5F,
                close = -448.4F,
                shadowHigh = -157.1F,
                shadowLow = -851.5F
            ),
            CSStock(
                createdAt = 11,
                open = -448.4F,
                close = 101.7F,
                shadowHigh = 120.7F,
                shadowLow = -448.4F
            ),
            CSStock(
                createdAt = 12,
                open = 101.7f,
                close = 12.5F,
                shadowHigh = 120.7F,
                shadowLow = -5.1F
            ),
            CSStock(
                createdAt = 13,
                open = 12.5F,
                close = -707F,
                shadowHigh = 606.4F,
                shadowLow = -800F
            )
        )
    }
}
