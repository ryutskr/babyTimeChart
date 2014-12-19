package com.babytimechart.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.activity.babytimechart.R;
import com.babytimechart.activity.BabyTimeDataActivity;
import com.babytimechart.db.BabyTimeDbOpenHelper;
import com.babytimechart.ui.RoundChartView;

/**
 * A placeholder fragment containing a simple view.
 */
public class Fragment_Playing extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final String EXTRA_TODAY_LAST_TIME = "lasttime";
	private static final int SPACE_IN_TIME = 30 * 60 * 1000;
	private static final int SPACE_IN_TIME_SMALL 	= 5 * 60 * 1000;
	private static final int SPACE_IN_TIME_BIG 		= 20 * 60 * 1000;

	private ToggleButton tbtn_mom = null;

	private TextView mTextView_stime = null;
	private TextView mTextView_etime = null;

	private Button mButton_time_minus_small = null;
	private Button mButton_time_minus_big = null;
	private Button mButton_time_plus_small = null;
	private Button mButton_time_plus_big = null;

	private long mMillsSTime = 0;
	private long mMillsETime = 0;
	private long mLastMillsTime = 0;
	private SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm");

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static Fragment_Playing newInstance(int sectionNumber, long todaylasttime) {
		Fragment_Playing fragment = new Fragment_Playing();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		args.putLong(EXTRA_TODAY_LAST_TIME, todaylasttime);
		fragment.setArguments(args);
		return fragment;
	}

	public Fragment_Playing() {
	}

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mLastMillsTime = getArguments().getLong(EXTRA_TODAY_LAST_TIME, 0);
		View rootView = inflater.inflate(R.layout.fragment_playing, container, false);
		initView(rootView);
		return rootView;
	}

	public void initView(View rootView)
	{
		tbtn_mom  = (ToggleButton)rootView.findViewById(R.id.tBtn_Playing_mom);

		mTextView_stime = (TextView)rootView.findViewById(R.id.txtView_Playing_stime);
		mTextView_etime = (TextView)rootView.findViewById(R.id.txtView_Playing_etime);    
		mTextView_stime.setOnClickListener(mOnClickListener);
		mTextView_etime.setOnClickListener(mOnClickListener);

		if( mLastMillsTime != 0)
		{
			mMillsSTime = mLastMillsTime;
			if( mLastMillsTime > System.currentTimeMillis() )
				mMillsETime = mMillsSTime + SPACE_IN_TIME;
			else
				mMillsETime = System.currentTimeMillis();
		}
		else
		{
			mMillsSTime = System.currentTimeMillis();
			mMillsETime = mMillsSTime + SPACE_IN_TIME;
		}

		String sTime = dateformat.format(new Date(mMillsSTime));
		String eTime = dateformat.format(new Date(mMillsETime));

		mTextView_stime.setText(sTime);
		mTextView_etime.setText(eTime);
		mTextView_stime.setContentDescription("" + mMillsSTime);
		mTextView_etime.setContentDescription("" + mMillsETime);

		mTextView_stime.setBackgroundColor(getActivity().getResources().getColor(R.color.peachpuff));

		mButton_time_minus_small = (Button)rootView.findViewById(R.id.btn_Playing_minus_small_time);
		mButton_time_minus_big = (Button)rootView.findViewById(R.id.btn_Playing_minus_big_time);
		mButton_time_plus_small = (Button)rootView.findViewById(R.id.btn_Playing_plus_small_time);
		mButton_time_plus_big = (Button)rootView.findViewById(R.id.btn_Playing_plus_big_time);
		mButton_time_minus_small.setOnClickListener(mOnClickListener);
		mButton_time_minus_big.setOnClickListener(mOnClickListener);
		mButton_time_plus_small.setOnClickListener(mOnClickListener);
		mButton_time_plus_big.setOnClickListener(mOnClickListener);

		tbtn_mom.setChecked(true);
	}


	OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm");
			switch(v.getId()){
			case R.id.btn_Playing_minus_small_time:
				if( mTextView_stime.isFocused() )
				{
					if( mLastMillsTime > mMillsSTime - SPACE_IN_TIME_BIG)
						Toast.makeText(getActivity(), getResources().getString(R.string.time_err1), Toast.LENGTH_SHORT).show();
					else{
						mMillsSTime =  mMillsSTime - SPACE_IN_TIME_BIG;
						mTextView_stime.setText( dateformat.format(new Date(mMillsSTime)) );
						mTextView_stime.setContentDescription("" + mMillsSTime);
					}
				}else if( mTextView_etime.isFocused() ){
					if( mMillsSTime > mMillsETime - SPACE_IN_TIME_BIG)
						Toast.makeText(getActivity(), getResources().getString(R.string.time_err2), Toast.LENGTH_SHORT).show();
					else{
						mMillsETime =  mMillsETime - SPACE_IN_TIME_BIG; 
						mTextView_etime.setText( dateformat.format(new Date(mMillsETime)) );
						mTextView_etime.setContentDescription("" + mMillsETime);
					}
				}
				break;
			case R.id.btn_Playing_minus_big_time:
				if( mTextView_stime.isFocused() )
				{
					if( mLastMillsTime > mMillsSTime - SPACE_IN_TIME_SMALL)
						Toast.makeText(getActivity(), getResources().getString(R.string.time_err1), Toast.LENGTH_SHORT).show();
					else{
						mMillsSTime =  mMillsSTime - SPACE_IN_TIME_SMALL;
						mTextView_stime.setText( dateformat.format(new Date(mMillsSTime)) );
						mTextView_stime.setContentDescription("" + mMillsSTime);
					}
				}else if( mTextView_etime.isFocused() ){
					if( mMillsSTime > mMillsETime - SPACE_IN_TIME_SMALL)
						Toast.makeText(getActivity(), getResources().getString(R.string.time_err2), Toast.LENGTH_SHORT).show();
					else{
						mMillsETime =  mMillsETime - SPACE_IN_TIME_SMALL; 
						mTextView_etime.setText( dateformat.format(new Date(mMillsETime)) );
						mTextView_etime.setContentDescription("" + mMillsETime);
					}
				}
				break;
			case R.id.btn_Playing_plus_small_time:
				if( mTextView_stime.isFocused() )
				{
					if( mMillsSTime + SPACE_IN_TIME_SMALL > mMillsETime )
						Toast.makeText(getActivity(), getResources().getString(R.string.time_err3), Toast.LENGTH_SHORT).show();
					else{
						mMillsSTime =  mMillsSTime + SPACE_IN_TIME_SMALL; 
						mTextView_stime.setText( dateformat.format(new Date(mMillsSTime)) );
						mTextView_stime.setContentDescription("" + mMillsSTime);
					}
				}else if( mTextView_etime.isFocused() ){
					mMillsETime =  mMillsETime + SPACE_IN_TIME_SMALL; 
					mTextView_etime.setText( dateformat.format(new Date(mMillsETime)) );
					mTextView_etime.setContentDescription("" + mMillsETime);
				}
				break;
			case R.id.btn_Playing_plus_big_time:
				if( mTextView_stime.isFocused() )
				{
					if( mMillsSTime + SPACE_IN_TIME_SMALL > mMillsETime )
						Toast.makeText(getActivity(), getResources().getString(R.string.time_err3), Toast.LENGTH_SHORT).show();
					else{
						mMillsSTime =  mMillsSTime + SPACE_IN_TIME_BIG; 
						mTextView_stime.setText( dateformat.format(new Date(mMillsSTime)) );
						mTextView_stime.setContentDescription("" + mMillsSTime);
					}
				}else if( mTextView_etime.isFocused() ){
					mMillsETime =  mMillsETime + SPACE_IN_TIME_BIG; 
					mTextView_etime.setText( dateformat.format(new Date(mMillsETime)) );
					mTextView_etime.setContentDescription("" + mMillsETime);
				}
				break;

			case R.id.txtView_Playing_stime:
				mTextView_stime.setBackgroundColor(getActivity().getResources().getColor(R.color.peachpuff));
				mTextView_etime.setBackgroundColor(getActivity().getResources().getColor(R.color.papayawhip));
				break;
			case R.id.txtView_Playing_etime:
				mTextView_stime.setBackgroundColor(getActivity().getResources().getColor(R.color.papayawhip));
				mTextView_etime.setBackgroundColor(getActivity().getResources().getColor(R.color.peachpuff));
				break;
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}

















