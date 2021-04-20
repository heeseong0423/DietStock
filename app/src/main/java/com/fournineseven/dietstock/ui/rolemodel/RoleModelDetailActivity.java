package com.fournineseven.dietstock.ui.rolemodel;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fournineseven.dietstock.R;
import com.fournineseven.dietstock.config.TaskServer;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;

public class RoleModelDetailActivity extends AppCompatActivity{
    private TextView textView_rolemodel_detail_name;
    private ImageView imageview_rolemodel_detail_image;
    private CandleStickChart candlestickchart_rolemodel_detail;
    private PieChart piechart_rolemodel_detail;
    private Button button_rolemodel_detail1, button_rolemodel_detail2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rolemodel_detail);
        init();
    }

    public void init() {
        Intent intent = getIntent();
        int user_no = intent.getIntExtra("user_no", 0);
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

        /**
         * Constructor.
         *
         * @param x The value on the x-axis
         * @param shadowH The (shadow) high value
         * @param shadowL The (shadow) low value
         * @param open The open value
         * @param close The close value
         */
        ArrayList<CandleEntry> yValsCandleStick= new ArrayList<CandleEntry>();
        yValsCandleStick.add(new CandleEntry((float)1, (float)225.0, (float)219.84, (float)224.94, (float)221.07));
        yValsCandleStick.add(new CandleEntry((float)2, (float)228.35, (float)222.57, (float)223.52, (float)226.41));
        yValsCandleStick.add(new CandleEntry((float)3, (float)226.84,  (float)222.52, (float)225.75, (float)223.84));
        yValsCandleStick.add(new CandleEntry((float)4, (float)222.95, (float)217.27, (float)222.15, (float)217.88));
        yValsCandleStick.add(new CandleEntry((float)5, (float)222.95, (float)217.27, (float)222.15, (float)217.88));
        yValsCandleStick.add(new CandleEntry((float)12, (float)222.95, (float)217.27, (float)222.15, (float)217.88));




        CandleDataSet set1 = new CandleDataSet(yValsCandleStick, "DataSet 1");
        set1.setColor(Color.rgb(80, 80, 80));
        set1.setShadowColor(Color.GRAY);
        set1.setShadowWidth(1f);
        set1.setDecreasingColor(Color.BLUE);
        set1.setDecreasingPaintStyle(Paint.Style.FILL);
        set1.setIncreasingColor(Color.RED);
        set1.setIncreasingPaintStyle(Paint.Style.FILL);
        set1.setNeutralColor(Color.LTGRAY);
        set1.setDrawValues(false);

        CandleData data = new CandleData(set1);

        candlestickchart_rolemodel_detail.setData(data);
        candlestickchart_rolemodel_detail.invalidate();
    }
    private void piechartInit(){
        piechart_rolemodel_detail = (PieChart)findViewById(R.id.piechart_rolemodel_detail);
        piechart_rolemodel_detail.setUsePercentValues(true);
        piechart_rolemodel_detail.getDescription().setEnabled(false);
        piechart_rolemodel_detail.setExtraOffsets(5, 10, 5, 5);

        piechart_rolemodel_detail.setDragDecelerationFrictionCoef(0.95f);

        piechart_rolemodel_detail.setCenterText("일일 누적\n영양성분");

        piechart_rolemodel_detail.setDrawHoleEnabled(true);
        piechart_rolemodel_detail.setHoleColor(Color.WHITE);

        piechart_rolemodel_detail.setTransparentCircleColor(Color.WHITE);
        piechart_rolemodel_detail.setTransparentCircleAlpha(110);

        piechart_rolemodel_detail.setHoleRadius(58f);
        piechart_rolemodel_detail.setTransparentCircleRadius(61f);

        piechart_rolemodel_detail.setDrawCenterText(true);

        piechart_rolemodel_detail.setRotationAngle(0);
        // enable rotation of the chart by touch
        piechart_rolemodel_detail.setRotationEnabled(true);
        piechart_rolemodel_detail.setHighlightPerTapEnabled(true);

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        piechart_rolemodel_detail.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });

        Legend l = piechart_rolemodel_detail.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        piechart_rolemodel_detail.setEntryLabelColor(Color.BLACK);
        piechart_rolemodel_detail.setEntryLabelTextSize(12f);
        setData(5, 10);
    }

    private void setData(int count, float range){
        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        entries.add(new PieEntry((float)5, "탄수화물"));
        entries.add(new PieEntry((float) 11,"단백질"));
        entries.add(new PieEntry((float) 16,"지방"));
        entries.add(new PieEntry((float)14,"당"));
        entries.add(new PieEntry((float) 1,"나트륨"));
        entries.add(new PieEntry((float) 37,"콜레스테롤"));

        PieDataSet dataSet = new PieDataSet(entries, "영양 성분");

        dataSet.setDrawIcons(false);

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
