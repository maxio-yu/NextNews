package com.example.nextnews;

import java.util.ArrayList;
import com.example.nextnews.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ReadStatusPage extends Activity {
	
	PieChart pieChart;
//	LineChart pieChart;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read_status_chart);
		Intent intent = getIntent();
		
		pieChart = (PieChart)findViewById(R.id.pie_chart);
		pieChart.setDescription("阅读分类概览");
		pieChart.setDrawYValues(true);

		ArrayList<Entry> readData = new ArrayList<Entry>();
		Entry societyData = new Entry(intent.getIntExtra("society", 0), 0); 
		Entry sportsData = new Entry(intent.getIntExtra("sports", 0), 1); 
		Entry entertainmentData = new Entry(intent.getIntExtra("entertainment", 0), 2); 
		Entry technologyData = new Entry(intent.getIntExtra("technology", 0), 3); 
		Entry uncategoryData = new Entry(intent.getIntExtra("uncategory", 0), 4); 
		readData.add(societyData);
		readData.add(sportsData);
		readData.add(entertainmentData);
		readData.add(technologyData);
		readData.add(uncategoryData);
		
		PieDataSet dataSet = new PieDataSet(readData, "entry");
		
		dataSet.setColors(new int[] { android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_purple }, this);
		
		ArrayList<PieDataSet> chartSet = new ArrayList<PieDataSet>();
		chartSet.add(dataSet);
		
		ArrayList<String> xVals = new ArrayList<String>();
	    xVals.add("社会");xVals.add("体育");xVals.add("娱乐");xVals.add("科技");xVals.add("其它");
	    
	    PieData data = new PieData(xVals, dataSet);
		
	    pieChart.setData(data);
		
	
	}
}
