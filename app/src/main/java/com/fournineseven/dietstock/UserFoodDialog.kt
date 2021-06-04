package com.fournineseven.dietstock

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.fournineseven.dietstock.api.RetrofitService
import com.fournineseven.dietstock.model.getFoodLogs.FoodLogResult
import com.fournineseven.dietstock.model.getFoodLogs.GetFoodLogsRequest
import com.fournineseven.dietstock.model.getFoodLogs.GetFoodLogsResponse
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserFoodDialog(): DialogFragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //return super.onCreateView(inflater, container, savedInstanceState)
        var rootView:View = inflater.inflate(R.layout.user_food_dialog,container,false)
        var piechart_rolemodel_detail: PieChart = rootView.findViewById(R.id.food_piechart)
        piechart_rolemodel_detail.setDescription(null)

        piechart_rolemodel_detail.setEntryLabelColor(Color.BLACK)

        var sharedpreferences = this.activity?.getSharedPreferences(LoginState.SHARED_PREFS,
            Context.MODE_PRIVATE)



        var userNumber = sharedpreferences?.getString(LoginState.USER_NUMBER,"0")!!.toInt()

        val getFoodLogsService = App.retrofit.create(
            RetrofitService::class.java
        )

        var foodLogResult = ArrayList<FoodLogResult>()


        Log.d("CHCH","${userNumber}")
        val callGetFoodLogs = getFoodLogsService.getFoodLogs(GetFoodLogsRequest(userNumber))
        callGetFoodLogs.enqueue(object : Callback<GetFoodLogsResponse> {
            override fun onResponse(
                call: Call<GetFoodLogsResponse>,
                response: Response<GetFoodLogsResponse>
            ) {
                Log.d("확인", response.body().toString())
                val getFoodLogsResponse = response.body()
                if (getFoodLogsResponse!!.isSuccess) {
                    foodLogResult = getFoodLogsResponse!!.result
                    val entries = ArrayList<PieEntry>()

                    // NOTE: The order of the entries when being added to the entries array determines their position around the center of
                    // the chart.
                    for (i in foodLogResult.indices) {
                        entries.add(
                            PieEntry(
                                foodLogResult.get(i).getCnt().toFloat(),
                                foodLogResult.get(i).getName()
                            )
                        )
                    }
                    val dataSet = PieDataSet(entries, "영양 성분")
                    dataSet.setDrawIcons(false)
                    dataSet.setDrawValues(false)
                    dataSet.sliceSpace = 3f
                    dataSet.iconsOffset = MPPointF(0.0f, 40.0f)
                    dataSet.selectionShift = 5f

                    // add a lot of colors
                    val colors = ArrayList<Int>()
                    for (c in ColorTemplate.VORDIPLOM_COLORS) colors.add(c)
                    for (c in ColorTemplate.JOYFUL_COLORS) colors.add(c)
                    for (c in ColorTemplate.COLORFUL_COLORS) colors.add(c)
                    for (c in ColorTemplate.LIBERTY_COLORS) colors.add(c)
                    for (c in ColorTemplate.PASTEL_COLORS) colors.add(c)
                    colors.add(ColorTemplate.getHoloBlue())
                    dataSet.colors = colors
                    //dataSet.setSelectionShift(0f);
                    val data = PieData(dataSet)
                    data.setValueFormatter(PercentFormatter())
                    data.setValueTextSize(11f)
                    data.setValueTextColor(Color.BLACK)
                    piechart_rolemodel_detail.setData(data)

                    // undo all highlights
                    piechart_rolemodel_detail.highlightValues(null)
                    piechart_rolemodel_detail.invalidate()
                }
            }

            override fun onFailure(call: Call<GetFoodLogsResponse>, t: Throwable) {
                Log.d("debug", "onFailure$t")
            }
        })




        return rootView
    }
}