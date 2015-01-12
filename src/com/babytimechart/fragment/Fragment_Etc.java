package com.babytimechart.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.babytimechart.utils.Utils;
import com.ryutskr.babytimechart.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class Fragment_Etc extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final String ARG_TODAY_LAST_TIME = "lasttime";
	private static final int SPACE_IN_TIME = 30 * 60 * 1000;
	private static final int ONE_MIN = 1 * 60 * 1000;
	private static final int SPACE_IN_TIME_SMALL 	= 5 * 60 * 1000;
	private static final int SPACE_IN_TIME_BIG 		= 20 * 60 * 1000;

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
	public static Fragment_Etc newInstance(int sectionNumber, long lastMillsTime) {
		Fragment_Etc fragment = new Fragment_Etc();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		args.putLong(ARG_TODAY_LAST_TIME, lastMillsTime);
		fragment.setArguments(args);
		return fragment;
	}

	public Fragment_Etc() {
	}

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if( getArguments() != null )
			mLastMillsTime = getArguments().getLong(ARG_TODAY_LAST_TIME, 0);
		View rootView = inflater.inflate(R.layout.fragment_etc, container, false);
		initView(rootView);
		return rootView;
	}

	public void initView(View rootView)
	{
		mTextView_stime = (TextView)rootView.findViewById(R.id.txtView_Etc_stime);
		mTextView_etime = (TextView)rootView.findViewById(R.id.txtView_Etc_etime);    
		mTextView_stime.setOnClickListener(mOnClickListener);
		mTextView_etime.setOnClickListener(mOnClickListener);

		if( mLastMillsTime != 0)
		{
			mMillsSTime = mLastMillsTime;
			
			SimpleDateFormat datedd = new SimpleDateFormat("dd");
			int iNextDay = Integer.parseInt( datedd.format(new Date(mMillsSTime + SPACE_IN_TIME)) );
			int iNowDay = Integer.parseInt( datedd.format(new Date(mMillsSTime)));
			
			if( iNextDay > iNowDay ){
				mMillsETime = mMillsSTime + ONE_MIN;
			}else if( mLastMillsTime > System.currentTimeMillis() )
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

		mTextView_stime.setBackgroundResource(R.drawable.green_btn_default_focused_holo_light);

		mButton_time_minus_small = (Button)rootView.findViewById(R.id.btn_Etc_minus_small_time);
		mButton_time_minus_big = (Button)rootView.findViewById(R.id.btn_Etc_minus_big_time);
		mButton_time_plus_small = (Button)rootView.findViewById(R.id.btn_Etc_plus_small_time);
		mButton_time_plus_big = (Button)rootView.findViewById(R.id.btn_Etc_plus_big_time);
		mButton_time_minus_small.setOnClickListener(mOnClickListener);
		mButton_time_minus_big.setOnClickListener(mOnClickListener);
		mButton_time_plus_small.setOnClickListener(mOnClickListener);
		mButton_time_plus_big.setOnClickListener(mOnClickListener);

	}

	OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm");
			Utils utils = new Utils();
			switch(v.getId()){
			case R.id.btn_Etc_minus_small_time:
				if( mTextView_stime.isFocused() )
				{
					if( mLastMillsTime > mMillsSTime - SPACE_IN_TIME_BIG)
						utils.makeToast(getActivity(), getResources().getString(R.string.time_err1));
					else{
						mMillsSTime =  mMillsSTime - SPACE_IN_TIME_BIG;
						mTextView_stime.setText( dateformat.format(new Date(mMillsSTime)) );
						mTextView_stime.setContentDescription("" + mMillsSTime);
					}
				}else if( mTextView_etime.isFocused() ){
					if( mMillsSTime > mMillsETime - SPACE_IN_TIME_BIG)
						utils.makeToast(getActivity(), getResources().getString(R.string.time_err2));
					else{
						mMillsETime =  mMillsETime - SPACE_IN_TIME_BIG; 
						mTextView_etime.setText( dateformat.format(new Date(mMillsETime)) );
						mTextView_etime.setContentDescription("" + mMillsETime);
					}
				}
				break;
			case R.id.btn_Etc_minus_big_time:
				if( mTextView_stime.isFocused() )
				{
					if( mLastMillsTime > mMillsSTime - SPACE_IN_TIME_SMALL)
						utils.makeToast(getActivity(), getResources().getString(R.string.time_err1));
					else{
						mMillsSTime =  mMillsSTime - SPACE_IN_TIME_SMALL;
						mTextView_stime.setText( dateformat.format(new Date(mMillsSTime)) );
						mTextView_stime.setContentDescription("" + mMillsSTime);
					}
				}else if( mTextView_etime.isFocused() ){
					if( mMillsSTime > mMillsETime - SPACE_IN_TIME_SMALL)
						utils.makeToast(getActivity(), getResources().getString(R.string.time_err2));
					else{
						mMillsETime =  mMillsETime - SPACE_IN_TIME_SMALL; 
						mTextView_etime.setText( dateformat.format(new Date(mMillsETime)) );
						mTextView_etime.setContentDescription("" + mMillsETime);
					}
				}
				break;
			case R.id.btn_Etc_plus_small_time:
				if( mTextView_stime.isFocused() )
				{
					if( mMillsSTime + SPACE_IN_TIME_SMALL > mMillsETime )
						utils.makeToast(getActivity(), getResources().getString(R.string.time_err3));
					else{
						mMillsSTime =  mMillsSTime + SPACE_IN_TIME_SMALL; 
						mTextView_stime.setText( dateformat.format(new Date(mMillsSTime)) );
						mTextView_stime.setContentDescription("" + mMillsSTime);
					}
				}else if( mTextView_etime.isFocused() ){
					SimpleDateFormat datedd = new SimpleDateFormat("dd");
					int iNextDay = Integer.parseInt( datedd.format(new Date(mMillsETime + SPACE_IN_TIME_SMALL)) );
					int iNowDay = Integer.parseInt( datedd.format(new Date(mMillsETime)));

					if( iNextDay > iNowDay )
						utils.makeToast(getActivity(), getResources().getString(R.string.time_err4));
					else{
						mMillsETime =  mMillsETime + SPACE_IN_TIME_SMALL; 
						mTextView_etime.setText( dateformat.format(new Date(mMillsETime)) );
						mTextView_etime.setContentDescription("" + mMillsETime);
					}
				}
				break;
			case R.id.btn_Etc_plus_big_time:
				if( mTextView_stime.isFocused() )
				{
					if( mMillsSTime + SPACE_IN_TIME_SMALL > mMillsETime )
						utils.makeToast(getActivity(), getResources().getString(R.string.time_err3));
					else{
						mMillsSTime =  mMillsSTime + SPACE_IN_TIME_BIG; 
						mTextView_stime.setText( dateformat.format(new Date(mMillsSTime)) );
						mTextView_stime.setContentDescription("" + mMillsSTime);
					}
				}else if( mTextView_etime.isFocused() ){
					SimpleDateFormat datedd = new SimpleDateFormat("dd");
					int iNextDay = Integer.parseInt( datedd.format(new Date(mMillsETime + SPACE_IN_TIME_BIG)) );
					int iNowDay = Integer.parseInt( datedd.format(new Date(mMillsETime)));

					if( iNextDay > iNowDay )
						utils.makeToast(getActivity(), getResources().getString(R.string.time_err4));
					else{
						mMillsETime =  mMillsETime + SPACE_IN_TIME_BIG; 
						mTextView_etime.setText( dateformat.format(new Date(mMillsETime)) );
						mTextView_etime.setContentDescription("" + mMillsETime);
					}
				}
				break;

			case R.id.txtView_Etc_stime:
				mTextView_stime.setBackgroundResource(R.drawable.green_btn_default_focused_holo_light);
				mTextView_etime.setBackgroundColor(getActivity().getResources().getColor(R.color.fragment_background));
				break;
			case R.id.txtView_Etc_etime:
				mTextView_stime.setBackgroundColor(getActivity().getResources().getColor(R.color.fragment_background));
				mTextView_etime.setBackgroundResource(R.drawable.green_btn_default_focused_holo_light);
				break;
			}
		}
	};

}

















