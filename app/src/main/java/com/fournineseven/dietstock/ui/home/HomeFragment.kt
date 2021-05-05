package com.fournineseven.dietstock.ui.home

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.fournineseven.dietstock.DataUtil
import com.fournineseven.dietstock.R
import com.fournineseven.dietstock.User
import com.fournineseven.dietstock.room.KcalDatabase
import com.fournineseven.dietstock.room.UserKcalData
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry

private const val TAG = "HomeFragmentTag"

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        val button: Button = root.findViewById(R.id.button2)
        val button2: Button = root.findViewById(R.id.button3)
        val dietChart:CandleStickChart = root.findViewById(R.id.dietStockChart)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        val db = Room.databaseBuilder(
                root.context,
                KcalDatabase::class.java, "database-name"
        ).allowMainThreadQueries()
                .build()
        val userDao = db.kcalDao()

        button.setOnClickListener {
            var userKcalData = UserKcalData(0, User.kcal,User.PKcal,User.startKcal,User.endKcal,User.highKcal,User.lowKcal)
            userDao.insert(userKcalData)
        }

        button2.setOnClickListener {
            Log.d(TAG,"${userDao.getAll()}")
        }
        val entries = ArrayList<CandleEntry>()

        for (csStock in userDao.getAll()) {
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


        val dataSet = CandleDataSet(entries, "").apply {
            //심지 부분
            shadowColor = Color.LTGRAY
            shadowWidth = 1F

            //음봉
            decreasingColor = Color.BLUE
            decreasingPaintStyle = Paint.Style.FILL

            //양봉
            increasingColor = Color.RED
            increasingPaintStyle = Paint.Style.FILL

            neutralColor = Color.DKGRAY
            setDrawValues(true)
            //터치시 노란선 제거
            highLightColor = Color.TRANSPARENT
        }

        dietChart.axisLeft.run{
            setDrawAxisLine(false)
            setDrawGridLines(false)
            textColor = Color.TRANSPARENT
        }
        dietChart.axisRight.run {
            isEnabled = false
        }

        dietChart.xAxis.run{
            textColor = Color.TRANSPARENT
            setDrawAxisLine(false)
            setDrawGridLines(false)
            setAvoidFirstLastClipping(true)
        }
        dietChart.legend.run {
            isEnabled = false
        }

        dietChart.apply {
            this.data = CandleData(dataSet)
            description.isEnabled = false
            isHighlightPerDragEnabled = true
            requestDisallowInterceptTouchEvent(true)
            setVisibleXRangeMaximum(7f)
            moveViewToX(dietChart.xAxis.axisMaximum)
            invalidate()
        }

        return root
    }
}