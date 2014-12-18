package com.babytimechart.fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.activity.babytimechart.R;
import com.babytimechart.activity.BabyTimeDataActivity;
import com.babytimechart.db.BabyTimeDbOpenHelper;
import com.babytimechart.db.Dbinfo;
import com.babytimechart.ui.RoundChartView;

/**
 * A placeholder fragment containing a simple view.
 */
public class Fragment_Chart_Pie extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	private RoundChartView mPieChart = null; 

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static Fragment_Chart_Pie newInstance(int sectionNumber) {
		Fragment_Chart_Pie fragment = new Fragment_Chart_Pie();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public Fragment_Chart_Pie() {
	}

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_chart_pie, container,false);

		mPieChart = (RoundChartView)rootView.findViewById(R.id.roundchartview);

		rootView.findViewById(R.id.feedingBtn).setOnClickListener(mOnClickListener);
		rootView.findViewById(R.id.playingBtn).setOnClickListener(mOnClickListener);
		rootView.findViewById(R.id.sleepingBtn).setOnClickListener(mOnClickListener);
		rootView.findViewById(R.id.etcBtn).setOnClickListener(mOnClickListener);
		mPieChart.drawChart();

		return rootView;
	}


	OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch( v.getId() ){
			case R.id.feedingBtn:
				Intent intent = new Intent(getActivity(), BabyTimeDataActivity.class);
				intent.putExtra("lasttime", mPieChart.getLasttime());
				startActivityForResult(intent, 1);
				break;
			case R.id.playingBtn:
				break;
			case R.id.sleepingBtn:
				break;
			case R.id.etcBtn:
				fakeDBData();
				break;
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mPieChart.drawChart();
	}

	public void fakeDBData()
	{
		long time = System.currentTimeMillis();

		SimpleDateFormat insertDateformat1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat insertDateformat2 = new SimpleDateFormat("dd");
		String strToday1 = insertDateformat1.format(new Date(time));
		String strToday2 = insertDateformat2.format(new Date(time));

		BabyTimeDbOpenHelper dbhelper = new BabyTimeDbOpenHelper(getActivity());
		SQLiteDatabase db = dbhelper.getWritableDatabase();

		Calendar today = Calendar.getInstance();
		today.set(2014, 11, Integer.parseInt(strToday2)-1, 23, 30);
		long time1 = today.getTimeInMillis();
		today.set(2014, 11, Integer.parseInt(strToday2), 8, 30);
		long time2 = today.getTimeInMillis();
		today.set(2014, 11, Integer.parseInt(strToday2), 10, 30);
		long time3 = today.getTimeInMillis();
		today.set(2014, 11, Integer.parseInt(strToday2), 13, 30);
		long time4 = today.getTimeInMillis();
		today.set(2014, 11, Integer.parseInt(strToday2), 16, 30);
		long time5 = today.getTimeInMillis();
		today.set(2014, 11, Integer.parseInt(strToday2), 17, 30);
		long time6 = today.getTimeInMillis();
		
		
		long[] arrtime = {time1,time2, time3, time4, time5, time6};
		String[] arrType = {"eat", "play", "sleep", "etc","play"};
		ContentValues contentValues = new ContentValues();
		for(int i=0; i< 5; i++)
		{
			contentValues.clear();
			contentValues.put(Dbinfo.DB_TYPE, arrType[i] );
			contentValues.put(Dbinfo.DB_DATE, strToday1 );
			contentValues.put(Dbinfo.DB_S_TIME, arrtime[i] );
			contentValues.put(Dbinfo.DB_E_TIME, arrtime[i+1] );
			contentValues.put(Dbinfo.DB_MEMO, "" + i  );
			db.insert(Dbinfo.DB_TABLE_NAME, null, contentValues);
		}
		db.close();
		
		mPieChart.drawChart();
	}
}

















