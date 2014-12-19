package com.babytimechart.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.activity.babytimechart.R;
import com.babytimechart.activity.BabyTimeDataActivity;
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
	private static final String EXTRA_TODAY_LAST_TIME = "lasttime";
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
				Intent intent_eat = new Intent(getActivity(), BabyTimeDataActivity.class);
				intent_eat.putExtra(EXTRA_TODAY_LAST_TIME, mPieChart.getLasttime());
				intent_eat.putExtra(ARG_SECTION_NUMBER, 0);
				startActivityForResult(intent_eat, 0);
				break;
			case R.id.playingBtn:
				Intent intent_play = new Intent(getActivity(), BabyTimeDataActivity.class);
				intent_play.putExtra(EXTRA_TODAY_LAST_TIME, mPieChart.getLasttime());
				intent_play.putExtra(ARG_SECTION_NUMBER, 1);
				startActivityForResult(intent_play, 1);
				break;
			case R.id.sleepingBtn:
				Intent intent_sleep = new Intent(getActivity(), BabyTimeDataActivity.class);
				intent_sleep.putExtra(EXTRA_TODAY_LAST_TIME, mPieChart.getLasttime());
				intent_sleep.putExtra(ARG_SECTION_NUMBER, 2);
				startActivityForResult(intent_sleep, 2);
				break;
			case R.id.etcBtn:
				Intent intent_etc = new Intent(getActivity(), BabyTimeDataActivity.class);
				intent_etc.putExtra(EXTRA_TODAY_LAST_TIME, mPieChart.getLasttime());
				intent_etc.putExtra(ARG_SECTION_NUMBER, 3);
				startActivityForResult(intent_etc, 3);
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
	
}

















