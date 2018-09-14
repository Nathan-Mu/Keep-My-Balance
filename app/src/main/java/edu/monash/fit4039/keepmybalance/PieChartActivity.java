package edu.monash.fit4039.keepmybalance;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


//external library: https://github.com/PhilJay/MPAndroidChart
//licence: http://www.apache.org/licenses/LICENSE-2.0
public class PieChartActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private PieChart mChart;
    private String[] categories;
    private double[] rates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pie_chart);

        setTitle("Monthly Expense");

        Intent intent = getIntent();
        categories = intent.getStringArrayExtra("categories");
        rates = intent.getDoubleArrayExtra("rates");

        mChart = (PieChart) findViewById(R.id.pie_chart);
        //set percent visible
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        //set text in center of the chart
        mChart.setCenterText("Monthly Expense");
        //set inside circle of the pie chart
        mChart.setDrawHoleEnabled(true);
        //set the color of inside circle
        mChart.setHoleColor(Color.WHITE);
        //set the color of circumference of the inside circle
        mChart.setTransparentCircleColor(Color.BLACK);
        //set the radius of the inside circle
        mChart.setHoleRadius(50f);
        //set the radius of the pie chart
        mChart.setTransparentCircleRadius(61f);
        //set the center text of the pie chart
        mChart.setDrawCenterText(true);
        //set spinning highlight
        mChart.setHighlightPerTapEnabled(true);

        //when user select a part of pie chart
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e == null)
                    return;
                Log.i("VAL SELECTED",
                        "Value: " + e.getY() + ", index: " + h.getX()
                                + ", DataSet index: " + h.getDataSetIndex());
            }

            @Override
            public void onNothingSelected() {}
        });

        //set data of the chart (report)
        setData(categories.length, 100);

        //set the animation
        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        //set legend
        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        //set color of label
        mChart.setEntryLabelColor(Color.BLACK);
        //set text size of label
        mChart.setEntryLabelTextSize(12f);
    }


    //set data of the pie chart (report)
    private void setData(int count, float range) {

        float mult = range;

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        for (int i = 0; i < categories.length; i++) {
            entries.add(new PieEntry((float) rates[i], categories[i]));
        }

        //set the color set
        PieDataSet dataSet = new PieDataSet(entries, "");
        ArrayList<Integer> colors = new ArrayList<Integer>();
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

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        mChart.setData(data);
        mChart.highlightValues(null);
        mChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }
}

