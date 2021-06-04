package com.fournineseven.dietstock.ui.rolemodel;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fournineseven.dietstock.App;

import com.fournineseven.dietstock.R;
import com.fournineseven.dietstock.api.RetrofitService;
import com.fournineseven.dietstock.config.TaskServer;
import com.fournineseven.dietstock.model.getFoodLogs.FoodLogResult;
import com.fournineseven.dietstock.model.getFoodLogs.GetFoodLogsRequest;
import com.fournineseven.dietstock.model.getFoodLogs.GetFoodLogsResponse;
import com.fournineseven.dietstock.model.getKcalByWeek.GetKcalByWeekRequest;
import com.fournineseven.dietstock.model.getKcalByWeek.GetKcalByWeekResponse;
import com.fournineseven.dietstock.model.getKcalByWeek.KcalByWeekResult;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoleModelDetailActivity extends AppCompatActivity{
    private TextView textView_rolemodel_detail_name;
    private ImageView imageview_rolemodel_detail_image;
    private CandleStickChart candlestickchart_rolemodel_detail;
    private PieChart piechart_rolemodel_detail;
    private Button button_rolemodel_detail1, button_rolemodel_detail2;

    int user_no;
    ArrayList<FoodLogResult> foodLogResult;

    ArrayList<KcalByWeekResult> kcalByWeekResult;


    /*int[] colorArray = new int[]{Color.LTGRAY, Color.BLUE, Color.RED, Color.green}*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_rolemodel_detail);
        init();
    }

    public void init() {
        Intent intent = getIntent();
        user_no = intent.getIntExtra("user_no", 0);
        String name = intent.getStringExtra("name");
        float weight_gap = intent.getFloatExtra("weight_gap", 0);
        String afterImage = intent.getStringExtra("after_image");
        textView_rolemodel_detail_name = (TextView)findViewById(R.id.textview_rolemodel_detail_name);
        textView_rolemodel_detail_name.setText(name);
        imageview_rolemodel_detail_image = (ImageView)findViewById(R.id.imageview_rolemodel_detail_image);
        Glide.with(this).load(TaskServer.base_url+afterImage).error(R.drawable.hindoongi)
                .placeholder(R.drawable.hindoongi).into(imageview_rolemodel_detail_image);
        button_rolemodel_detail1 = (Button)findViewById(R.id.buttn_rolemodel_detail1);
        button_rolemodel_detail1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                candlestickchart_rolemodel_detail.setVisibility(View.VISIBLE);
                piechart_rolemodel_detail.setVisibility(View.GONE);
            }
        });
        button_rolemodel_detail2 = (Button)findViewById(R.id.buttn_rolemodel_detail2);
        button_rolemodel_detail2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                candlestickchart_rolemodel_detail.setVisibility(View.GONE);
                piechart_rolemodel_detail.setVisibility(View.VISIBLE);
            }
        });
        candlestickchartInit();
        piechartInit();
    }

    private void candlestickchartInit(){

        RetrofitService getKcalByWeekService = App.retrofit.create(RetrofitService.class);

        candlestickchart_rolemodel_detail = (CandleStickChart)findViewById(R.id.candlestickchart_rolemodel_detail);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        candlestickchart_rolemodel_detail.setBackgroundColor(Color.WHITE);
        candlestickchart_rolemodel_detail.getDescription().setEnabled(false);
        //최대 보여질 엔트리 개수
        candlestickchart_rolemodel_detail.setMaxVisibleValueCount(60);
        candlestickchart_rolemodel_detail.setDrawGridBackground(false);

        candlestickchart_rolemodel_detail.setDoubleTapToZoomEnabled(false);

        XAxis xAxis = candlestickchart_rolemodel_detail.getXAxis();
        YAxis yAxis = candlestickchart_rolemodel_detail.getAxisLeft();
        YAxis rightAxis = candlestickchart_rolemodel_detail.getAxisRight();
        yAxis.setDrawGridLines(false);
        rightAxis.setDrawGridLines(false);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.GRAY);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        rightAxis.setTextColor(Color.GRAY);
        yAxis.setDrawLabels(false);
        xAxis.setGranularity(1f);//x축간격
        xAxis.setGranularityEnabled(true);
        xAxis.setAvoidFirstLastClipping(true);

        Legend l = candlestickchart_rolemodel_detail.getLegend();
        l.setEnabled(false);


        Call<GetKcalByWeekResponse> callGetKcalByMonth =
                getKcalByWeekService.getKcalByWeek(new GetKcalByWeekRequest(user_no));

        /**
         * Constructor.
         *
         * @param x The value on the x-axis
         * @param shadowH The (shadow) high value
         * @param shadowL The (shadow) low value
         * @param open The open value
         * @param close The close value
         */


        callGetKcalByMonth.enqueue(new Callback<GetKcalByWeekResponse>() {
            @Override
            public void onResponse(Call<GetKcalByWeekResponse> call, Response<GetKcalByWeekResponse> response) {
                Log.d("debug", response.body().toString());
                GetKcalByWeekResponse getKcalByWeekResponse = (GetKcalByWeekResponse)response.body();
                if(getKcalByWeekResponse.isSuccess()) {
                    kcalByWeekResult = getKcalByWeekResponse.getResults();
                    ArrayList<CandleEntry> yValsCandleStick= new ArrayList<CandleEntry>();
                    // NOTE: The order of the entries when being added to the entries array determines their position around the center of
                    // the chart.
                    /*for (int i = 1; i <= 12; i++) {
                        yValsCandleStick.add(new CandleEntry(i, 0, 0, 0, 0));
                    }*/
                    Log.d("debug+++++++++++++++++++++", kcalByWeekResult.toString());
                    for(int i=0; i<kcalByWeekResult.size(); i++) {
                        yValsCandleStick.add(new CandleEntry(i+1,
                                (float) kcalByWeekResult.get(i).getHigh(),
                                (float) kcalByWeekResult.get(i).getLow(),
                                (float) kcalByWeekResult.get(i).getStart_kcal(),
                                (float) kcalByWeekResult.get(i).getEnd_kcal()));
                    }

                    CandleDataSet set1 = new CandleDataSet(yValsCandleStick, "DataSet 1");
                    set1.setColor(Color.rgb(80, 80, 80));
                    set1.setShadowColor(Color.GRAY);
                    set1.setShadowWidth(1f);
                    set1.setDecreasingColor(Color.BLUE);
                    set1.setDecreasingPaintStyle(Paint.Style.FILL);
                    set1.setIncreasingColor(Color.RED);
                    set1.setIncreasingPaintStyle(Paint.Style.FILL);
                    set1.setNeutralColor(Color.LTGRAY);

                    set1.setDrawValues(true);


                    CandleData data = new CandleData(set1);

                    candlestickchart_rolemodel_detail.setData(data);
                    candlestickchart_rolemodel_detail.invalidate();
                }
            }

            @Override

            public void onFailure(Call<GetKcalByWeekResponse> call, Throwable t) {

                Log.d("debug", "onFailure"+t.toString());
            }
        });
    }
    private void piechartInit(){
        piechart_rolemodel_detail = (PieChart)findViewById(R.id.piechart_rolemodel_detail);
        piechart_rolemodel_detail.setDescription(null);

        piechart_rolemodel_detail.setEntryLabelColor(Color.BLACK);

        RetrofitService getFoodLogsService = App.retrofit.create(RetrofitService.class);
        Call<GetFoodLogsResponse> callGetFoodLogs = getFoodLogsService.getFoodLogs(new GetFoodLogsRequest(user_no));
        callGetFoodLogs.enqueue(new Callback<GetFoodLogsResponse>() {
            @Override
            public void onResponse(Call<GetFoodLogsResponse> call, Response<GetFoodLogsResponse> response) {
                Log.d("debug", response.body().toString());
                GetFoodLogsResponse getFoodLogsResponse = (GetFoodLogsResponse)response.body();
                if(getFoodLogsResponse.isSuccess()) {
                    foodLogResult = getFoodLogsResponse.getResult();

                    ArrayList<PieEntry> entries = new ArrayList<>();

                    // NOTE: The order of the entries when being added to the entries array determines their position around the center of
                    // the chart.
                    for(int i=0; i<foodLogResult.size(); i++) {
                        entries.add(new PieEntry(foodLogResult.get(i).getCnt(), foodLogResult.get(i).getName()));
                    }

                    PieDataSet dataSet = new PieDataSet(entries, "영양 성분");

                    dataSet.setDrawIcons(false);

                    dataSet.setDrawValues(false);

                    dataSet.setSliceSpace(3f);
                    dataSet.setIconsOffset(new MPPointF(0, 40));
                    dataSet.setSelectionShift(5f);

                    // add a lot of colors

                    ArrayList<Integer> colors = new ArrayList<>();

                    for (int c : ColorTemplate.VORDIPLOM_COLORS)
                        colors.add(c);

                    for (int c : ColorTemplate.JOYFUL_COLORS)
                        colors.add(c);

                    for (int c : ColorTemplate.COLORFUL_COLORS)
                        colors.add(c);

                    for (int c : ColorTemplate.LIBERTY_COLORS)
                        colors.add(c);

                    for (int c : ColorTemplate.PASTEL_COLORS)
                        colors.add(c);

                    colors.add(ColorTemplate.getHoloBlue());

                    dataSet.setColors(colors);
                    //dataSet.setSelectionShift(0f);

                    PieData data = new PieData(dataSet);
                    data.setValueFormatter(new PercentFormatter());
                    data.setValueTextSize(11f);
                    data.setValueTextColor(Color.BLACK);
                    piechart_rolemodel_detail.setData(data);

                    // undo all highlights
                    piechart_rolemodel_detail.highlightValues(null);

                    piechart_rolemodel_detail.invalidate();
                }
            }

            @Override
            public void onFailure(Call<GetFoodLogsResponse> call, Throwable t) {
                Log.d("debug", "onFailure"+t.toString());
            }
        });
    }


}
