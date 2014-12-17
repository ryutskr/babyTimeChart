package com.babytimechart.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.activity.babytimechart.R;
import com.babytimechart.activity.BabyTimeDataActivity;
import com.babytimechart.db.BabyTimeDbOpenHelper;
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
		
		TextView textView = (TextView) rootView.findViewById(R.id.section_label);
		textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));

		mPieChart = (RoundChartView)rootView.findViewById(R.id.roundchartview);
		
		rootView.findViewById(R.id.feedingBtn).setOnClickListener(mOnClickListener);
		rootView.findViewById(R.id.playingBtn).setOnClickListener(mOnClickListener);
		rootView.findViewById(R.id.sleepingBtn).setOnClickListener(mOnClickListener);
		rootView.findViewById(R.id.etcBtn).setOnClickListener(mOnClickListener);
		
		return rootView;
	}
	
	
	OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch( v.getId() ){
			case R.id.feedingBtn:
				Intent intent = new Intent(getActivity(), BabyTimeDataActivity.class);
				startActivityForResult(intent, 1);
				break;
			case R.id.playingBtn:
				break;
			case R.id.sleepingBtn:
				break;
			case R.id.etcBtn:
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
		Log.i("1111", "onActivityResult " );
		mPieChart.setBackgroundColor(Color.GRAY);
		mPieChart.setPercentage(40);
		mPieChart.drawChart();
	}
	
}

















