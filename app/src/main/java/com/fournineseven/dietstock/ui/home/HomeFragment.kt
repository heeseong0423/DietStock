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
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.fournineseven.dietstock.*
import com.fournineseven.dietstock.api.RetrofitService
import com.fournineseven.dietstock.model.getFoodLogs.FoodLogResult
import com.fournineseven.dietstock.model.getFoodLogs.GetFoodLogsRequest
import com.fournineseven.dietstock.model.getFoodLogs.GetFoodLogsResponse
import com.fournineseven.dietstock.model.getRanking.GetRankingResponse
import com.fournineseven.dietstock.retrofitness.GetUserKcalLogResponse
import com.fournineseven.dietstock.retrofitness.RetrofitBuilder
import com.fournineseven.dietstock.retrofitness.UserKcalLogResult
import com.fournineseven.dietstock.room.KcalDatabase
import com.fournineseven.dietstock.room.UserKcalData
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.data.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
import kotlin.collections.ArrayList

private const val TAG = "HomeFragmentTag"
private const val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var rootView: View
    private lateinit var textView:TextView
    private lateinit var consumeTextVIew: TextView
    private lateinit var baseKcalTextView: TextView
    private lateinit var activityKcalTextView: TextView
    private lateinit var myLayout :LinearLayout
    private lateinit var deleteDao :LinearLayout
    private lateinit var insertDao :LinearLayout
    var userKcalLogList = ArrayList<UserKcalLogResult>()

    private lateinit var tansuhwamulTextView :TextView
    private lateinit var fatTextView :TextView
    private lateinit var natriumTextView:TextView
    private lateinit var danbaekjilTextView:TextView
    private lateinit var kcalStockTextView: TextView

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
        baseKcalTextView = root.findViewById(R.id.text_base)
        activityKcalTextView = root.findViewById(R.id.text_physics)
        consumeTextVIew = root.findViewById(R.id.textConsume)
        tansuhwamulTextView = root.findViewById(R.id.tv_tansuhwamul)
        fatTextView = root.findViewById(R.id.tv_zibang)
        natriumTextView = root.findViewById(R.id.tv_natrium)
        danbaekjilTextView = root.findViewById(R.id.tv_danbaekjil)
        kcalStockTextView = root.findViewById(R.id.kcal_stock)
        myLayout = root.findViewById(R.id.my_food_type)
        deleteDao = root.findViewById(R.id.delete_dao)
        insertDao = root.findViewById(R.id.insert_dao)

        myLayout.setOnClickListener{
            var userFoodDialog = UserFoodDialog()
            userFoodDialog.show(parentFragmentManager,"userFoodDialog")
        }

        deleteDao.setOnClickListener {
            val db = Room.databaseBuilder(
                root.context,
                KcalDatabase::class.java, "database-name1"
            ).allowMainThreadQueries()
                .build()
            val userDao = db.kcalDao()
            userDao.deleteAllUsers()
            userDao.insert(UserKcalData(0,0f,0f,0f,0f,0f,0f))
        }

        insertDao.setOnClickListener {
            val db = Room.databaseBuilder(
                root.context,
                KcalDatabase::class.java, "database-name1"
            ).allowMainThreadQueries()
                .build()
            val userDao = db.kcalDao()
            userDao.deleteAllUsers()
            for (i in DataUtil.getCSStockData()) {

                userDao.insert(UserKcalData(0,i.open,0f,i.open,i.close,i.shadowHigh,i.shadowLow))
            }

        }

        val dietChart:CandleStickChart = root.findViewById(R.id.dietStockChart)
        val button: Button = root.findViewById(R.id.google_button)
        val account = GoogleSignIn.getAccountForExtension(root.context, fitnessOptions)

        rootView = root

        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        baseKcalTextView.text = "기초 소모량 : ${User.kcal} Kcal"
        activityKcalTextView.text = "활동 소모량 : ${User.PKcal} Kcal"
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

        var userNumber = sharedpreferences?.getString(LoginState.USER_NUMBER,"0")!!.toInt()

        var startTime = sharedpreferences?.getLong(LoginState.START_TIME_KEY,0)
        var consumeKcal = sharedpreferences?.getFloat(LoginState.INTAKE_KEY,0.0f)
        var todayZibang = sharedpreferences?.getFloat(LoginState.ZIBANG_KEY,0.0f)
        var todayTansuhwamul = sharedpreferences?.getFloat(LoginState.TANSUHWAMUL_KEY,0.0f)
        var todayDanbaekjil = sharedpreferences?.getFloat(LoginState.DANBAEKJIL_KEY,0.0f)
        var todayNatrium = sharedpreferences?.getFloat(LoginState.NATRIUM_KEY,0.0f)

        if(startTime!! <2){
            var editor = sharedpreferences?.edit()
            startTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault())
                .toEpochSecond()
            editor?.putLong(LoginState.START_TIME_KEY,startTime)
            editor?.apply()
        }


        val getUserKcalLogsService = App.retrofit.create(
            RetrofitService::class.java
        )



        val callGetUserKcalLog = getUserKcalLogsService

        RetrofitBuilder.api.getUserKcalLogResponse(userNumber)
            .enqueue(object : Callback<GetUserKcalLogResponse>{
                override fun onResponse(
                    call: Call<GetUserKcalLogResponse>,
                    response: Response<GetUserKcalLogResponse>
                ) {
                    userKcalLogList = response.body()?.result!!
                    drawCandlestickChart(dietChart,rootView)
                }

                override fun onFailure(call: Call<GetUserKcalLogResponse>, t: Throwable) {

                }
            })

        val getFoodLogsService = App.retrofit.create(
            RetrofitService::class.java
        )

        var foodLogResult = ArrayList<FoodLogResult>()


        val callGetFoodLogs = getFoodLogsService.getFoodLogs(GetFoodLogsRequest(userNumber))
        callGetFoodLogs.enqueue(object : Callback<GetFoodLogsResponse> {
            override fun onResponse(
                call: Call<GetFoodLogsResponse>,
                response: Response<GetFoodLogsResponse>
            ) {
                val getFoodLogsResponse = response.body()
                if (getFoodLogsResponse!!.isSuccess) {
                    foodLogResult = getFoodLogsResponse!!.result
                    // NOTE: The order of the entries when being added to the entries array determines their position around the center of
                    // the chart.
                    for (i in foodLogResult.indices) {
                        when(i){
                            0->{
                                var first:TextView = rootView.findViewById(R.id.first)
                                first.text = "1위 ${foodLogResult.get(i).name}"
                            }
                            1->{
                                var second:TextView = rootView.findViewById(R.id.second)
                                second.text = "2위 ${foodLogResult.get(i).name}"
                            }
                            2->{
                                var third :TextView = rootView.findViewById(R.id.third)
                                third.text = "3위 ${foodLogResult.get(i).name}"
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GetFoodLogsResponse>, t: Throwable) {

            }
        })



        if(CoroutineState){
            return
        }

        var consume = String.format("%.0f",consumeKcal)
        var natrium = String.format("%.1f",todayNatrium)
        var tansu = String.format("%.1f",todayTansuhwamul)
        var fat = String.format("%.1f",todayZibang)
        var danbaek = String.format("%.1f",todayDanbaekjil)


        consumeTextVIew.text = "칼로리 섭취량 : ${consume} Kcal"
        natriumTextView.text = "${natrium} g"
        tansuhwamulTextView.text = "${tansu} g"
        fatTextView.text = "${fat} g"
        danbaekjilTextView.text = "${danbaek} g"


        CoroutineState = true
        CoroutineScope(Dispatchers.Main).launch {
            while (CoroutineState) {
                updateCalories(startTime!!)
                var sum = String.format("%.1f",User.PKcal + User.kcal)
                var base = String.format("%.1f",User.kcal)
                var physics = String.format("%.1f",User.PKcal)
                var kcalStockValue = String.format("%.1f",User.PKcal + User.kcal - consumeKcal)


                kcalStockTextView.text = "칼로리 상태 : ${kcalStockValue} Kcal"
                textView.text = "총 소모량 : ${sum} Kcal"
                baseKcalTextView.text = "기초 소모량 : ${base} Kcal"
                activityKcalTextView.text = "활동 소모량 : ${physics} Kcal"
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
            dietChart.visibility = View.GONE
            button.visibility = View.VISIBLE
        } else {
            dietChart.visibility = View.VISIBLE
            button.visibility = View.GONE
            drawCandlestickChart(dietChart,root)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE -> {
                    subscribe()
                    checkGooglefitPermission(rootView)
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


        //userDao.insert(UserKcalData(0,13.2f,141.0f,0.0f,434.3f,2323.0f,-23.3f))
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
        //Log.d(TAG,"${userDao.getLastData()}")

        /*try{
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
        }catch (e :Error){
            e.stackTrace
            userDao.insert(UserKcalData(0,0f,0f,0.0f,0f,0f,0f))
        }*/

        var sharedpreferences = context?.getSharedPreferences(LoginState.SHARED_PREFS, Context.MODE_PRIVATE)
        var high = sharedpreferences?.getFloat(LoginState.HIGH_KEY,0.0f)
        var low = sharedpreferences?.getFloat(LoginState.LOW_KEY,0.0f)
        var end = sharedpreferences?.getFloat(LoginState.END_KEY,0.0f)
        var start = sharedpreferences?.getFloat(LoginState.START_KEY, 0.0f)
        var intake = sharedpreferences?.getFloat(LoginState.INTAKE_KEY,0.0f)





        if(userDao.getLastData() == null){
            userDao.insert(UserKcalData(0,0f,0f,0.0f,0f,0f,0f))
        }

        entries.add(
            CandleEntry(
                    0.0f,
                0.0f,
                0.0f,
                0.0f,
                0.0f
            )
        )

        var num = 0
        try{
            for(csStock in userKcalLogList){
                entries.add(
                    CandleEntry(
                        num+1.toFloat(),
                        csStock.high,
                        csStock.low,
                        csStock.start_kcal,
                        csStock.end_kcal
                    )
                )
                num++
            }
        }catch (e :Error){
            e.stackTrace
        }

        var size = userKcalLogList.size


        if(size>0){
            entries.add(
                CandleEntry(
                    num+1.toFloat(),
                    high!!,
                    low!!,
                    userKcalLogList[size-1].end_kcal,
                    User.PKcal + User.kcal - intake!!
                )
            )
        }else if(size == 0){
            entries.add(
                CandleEntry(
                    num+1.toFloat(),
                    high!!,
                    low!!,
                    0.0f,
                    User.PKcal + User.kcal - intake!!
                )
            )
        }


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
            valueTextSize = 0f
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



//더미데이터 !!
object DataUtil {
    fun getCSStockData(): List<CSStock> {
        return listOf(
            CSStock(
                createdAt = 0,
                open = 0f,
                close = 808.9f,
                shadowHigh = 904.8f,
                shadowLow = 0.0F
            ),
            CSStock(
                createdAt = 1,
                open = 808.9f,
                close = 1435.8f,
                shadowHigh = 1574.9f,
                shadowLow = 704.8f
            ),
            CSStock(
                createdAt = 2,
                open = 1435.8f,
                close = 1235.9f,
                shadowHigh = 1394.2f,
                shadowLow = 1132.4f
            ),
            CSStock(
                createdAt = 3,
                open = 1235.9f,
                close = 864.2F,
                shadowHigh = 1290.5F,
                shadowLow = 804.9F
            ),
            CSStock(
                createdAt = 4,
                open = 864.2F,
                close = 1003.4F,
                shadowHigh = 1231.9F,
                shadowLow = 803.4F
            ),
            CSStock(
                createdAt = 5,
                open = 1003.4F,
                close = 1231.9F,
                shadowHigh = 1493.2F,
                shadowLow = 987.4F
            ),
            CSStock(
                createdAt = 6,
                open = 1231.9f,
                close = 2034.9f,
                shadowHigh = 2034.9f,
                shadowLow = 1231.9F
            ),
            CSStock(
                createdAt = 7,
                open = 2034.9f,
                close = 2904.3f,
                shadowHigh = 2904.3f,
                shadowLow = 1804.2F
            ),
            CSStock(
                createdAt = 8,
                open = 2904.3F,
                close = 2304.2F,
                shadowHigh = 3014.2F,
                shadowLow = 2204.2F
            ),
            CSStock(
                createdAt = 9,
                open = 2304.2F,
                close = 1702.3F,
                shadowHigh = 2404.1F,
                shadowLow = 1700.4F
            ),
            CSStock(
                createdAt = 10,
                open = 1702.3f,
                close = 1503.3f,
                shadowHigh = 1842.2f,
                shadowLow = 1213.2f
            )/*,
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
            )*/
        )
    }
}
