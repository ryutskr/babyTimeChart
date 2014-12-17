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
public class Fragment_Etc extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	private BabyTimeDbOpenHelper mDbhelper =  null;
	private SQLiteDatabase mDb = null;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static Fragment_Etc newInstance(int sectionNumber) {
		Fragment_Etc fragment = new Fragment_Etc();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public Fragment_Etc() {
	}

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("babytime", getActivity().getComponentName() + "  / onCreateView() ");
		View rootView = inflater.inflate(R.layout.fragment_etc, container, false);
		
		rootView.findViewById(R.id.tBtn_Feeding_mm).setOnClickListener(mOnClickListener);
		rootView.findViewById(R.id.tBtn_Feeding_mp).setOnClickListener(mOnClickListener);
		rootView.findViewById(R.id.tBtn_Feeding_bf).setOnClickListener(mOnClickListener);
		rootView.findViewById(R.id.toggleButton4).setOnClickListener(mOnClickListener);
		
		return rootView;
	}
	
	
	
	OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
		}
	};

	@Override
	public void onDestroy() {
		Log.i("babytime", getActivity().getComponentName() + "  / onDestroy() ");
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("babytime", getActivity().getComponentName() + "  / onActivityResult() retCode : " + resultCode);
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}

















