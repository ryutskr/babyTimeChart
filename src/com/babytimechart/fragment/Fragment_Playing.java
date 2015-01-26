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
public class Fragment_Playing extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final String ARG_TODAY_LAST_TIME = "lasttime";
	private static final int SPACE_IN_TIME_30 = 30 * 60 * 1000;
	private static final int SPACE_IN_TIME_5 		= 5 * 60 * 1000;
	private static final int SPACE_IN_TIME_20 		= 20 * 60 * 1000;
	private static final int SPACE_IN_TIME_60 		= 60 * 60 * 1000;

	private TextView mTextView_stime = null;
	private TextView mTextView_etime = null;

	private Button mButton_time_minus_3 = null;
	private Button mButton_time_minus_2 = null;
	private Button mButton_time_minus_1 = null;
	private Button mButton_time_plus_1 = null;
	private Button mButton_time_plus_2 = null;
	private Button mButton_time_plus_3 = null;

	private long mMillsSTime = 0;
	private long mMillsETime = 0;
	private long mLastMillsTime = 0;
	private SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm");
	private boolean mIsSTimeClick = true;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static Fragment_Playing newInstance(int sectionNumber, long lastMillsTime) {
		Fragment_Playing fragment = new Fragment_Playing();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		args.putLong(ARG_TODAY_LAST_TIME, lastMillsTime);
		fragment.setArguments(args);
		return fragment;
	}

	public Fragment_Playing() {
	}

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if( getArguments() != null )
			mLastMillsTime = getArguments().getLong(ARG_TODAY_LAST_TIME, 0);
		View rootView = inflater.inflate(R.layout.fragment_playing, container, false);
		initView(rootView);
		return rootView;
	}

	public void initView(View rootView)
	{
		mTextView_stime = (TextView)rootView.findViewById(R.id.txtView_stime);
		mTextView_etime = (TextView)rootView.findViewById(R.id.txtView_etime);    
		mTextView_stime.setOnClickListener(mOnClickListener);
		mTextView_etime.setOnClickListener(mOnClickListener);

		if( mLastMillsTime != 0)
			mMillsSTime = mLastMillsTime;
		else
			mMillsSTime = System.currentTimeMillis();

		mMillsETime = mMillsSTime + SPACE_IN_TIME_30;

		SimpleDateFormat dateHH = new SimpleDateFormat("HH");
		SimpleDateFormat datemm = new SimpleDateFormat("mm");

		int iHH = Integer.parseInt( dateHH.format(new Date(mMillsSTime)) );
		int imm = Integer.parseInt( datemm.format(new Date(mMillsSTime)));

		if( iHH >= 23 && imm >=55  )
			mMillsETime = mMillsSTime;
		else if(iHH >= 23 && imm >=30 )
			mMillsETime = mMillsSTime + SPACE_IN_TIME_5;
		else if( mMillsSTime > System.currentTimeMillis() )
			mMillsETime = mMillsSTime + SPACE_IN_TIME_30;

		String sTime = dateformat.format(new Date(mMillsSTime));
		String eTime = dateformat.format(new Date(mMillsETime));

		mTextView_stime.setText(sTime);
		mTextView_etime.setText(eTime);
		mTextView_stime.setContentDescription("" + mMillsSTime);
		mTextView_etime.setContentDescription("" + mMillsETime);

		mTextView_stime.setBackgroundResource(R.drawable.rounded_timebackground);

		mButton_time_minus_3 = (Button)rootView.findViewById(R.id.btn_minus_3);
		mButton_time_minus_2 = (Button)rootView.findViewById(R.id.btn_minus_2);
		mButton_time_minus_1 = (Button)rootView.findViewById(R.id.btn_minus_1);
		mButton_time_plus_1  = (Button)rootView.findViewById(R.id.btn_plus_1);
		mButton_time_plus_2  = (Button)rootView.findViewById(R.id.btn_plus_2);
		mButton_time_plus_3  = (Button)rootView.findViewById(R.id.btn_plus_3);
		
		mButton_time_minus_3.setOnClickListener(mOnClickListener);
		mButton_time_minus_2.setOnClickListener(mOnClickListener);
		mButton_time_minus_1.setOnClickListener(mOnClickListener);
		mButton_time_plus_1.setOnClickListener(mOnClickListener);
		mButton_time_plus_2.setOnClickListener(mOnClickListener);
		mButton_time_plus_3.setOnClickListener(mOnClickListener);

	}


	OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Utils utils = new Utils();
			switch(v.getId()){
			case R.id.btn_minus_3:
				minusTime(SPACE_IN_TIME_60);
				break;
			case R.id.btn_minus_2:
				minusTime(SPACE_IN_TIME_20);
				break;
			case R.id.btn_minus_1:
				minusTime(SPACE_IN_TIME_5);
				break;
			case R.id.btn_plus_1:
				plusTime(SPACE_IN_TIME_5);
				break;
			case R.id.btn_plus_2:
				plusTime(SPACE_IN_TIME_20);
				break;
			case R.id.btn_plus_3:
				plusTime(SPACE_IN_TIME_60);
				break;
			case R.id.txtView_stime:
				mIsSTimeClick = true;
				mTextView_stime.setBackgroundResource(R.drawable.rounded_timebackground);
				mTextView_etime.setBackgroundColor(getActivity().getResources().getColor(R.color.fragment_background));
				break;
			case R.id.txtView_etime:
				mIsSTimeClick = false;
				mTextView_stime.setBackgroundColor(getActivity().getResources().getColor(R.color.fragment_background));
				mTextView_etime.setBackgroundResource(R.drawable.rounded_timebackground);
				break;
			}
		}
	};

	public void minusTime(int timeSpace){
		if( mIsSTimeClick ){
			//               if( mLastMillsTime > mMillsSTime - SPACE_IN_TIME_BIG)
			//                   utils.makeToast(getActivity(), getResources().getString(R.string.time_err1));
			//               else{
			if( (mMillsSTime%timeSpace) != 0 )
				mMillsSTime =  ((mMillsSTime/timeSpace))*timeSpace;
			else
				mMillsSTime =  ((mMillsSTime/timeSpace)-1)*timeSpace;

			mTextView_stime.setText( dateformat.format(new Date(mMillsSTime)) );
			mTextView_stime.setContentDescription("" + mMillsSTime);
			//               }
		}else {
			if( mMillsSTime > mMillsETime - timeSpace)
				new Utils().makeToast(getActivity(), getResources().getString(R.string.time_err2));
			else{
				if( (mMillsETime%timeSpace) != 0 )
					mMillsETime =  ((mMillsETime/timeSpace))*timeSpace;
				else
					mMillsETime =  ((mMillsETime/timeSpace)-1)*timeSpace;

				mTextView_etime.setText( dateformat.format(new Date(mMillsETime)) );
				mTextView_etime.setContentDescription("" + mMillsETime);
			}
		}
	}

	public void plusTime(int timeSpace){
		if( mIsSTimeClick ){
			if( mMillsSTime + timeSpace > mMillsETime )
				new Utils().makeToast(getActivity(), getResources().getString(R.string.time_err3));
			else{
				mMillsSTime =  ((mMillsSTime/timeSpace)+1)*timeSpace;

				mTextView_stime.setText( dateformat.format(new Date(mMillsSTime)) );
				mTextView_stime.setContentDescription("" + mMillsSTime);
			}
		}else {
			SimpleDateFormat datedd = new SimpleDateFormat("dd");
			int iNextDay = Integer.parseInt( datedd.format(new Date(mMillsETime + timeSpace)) );
			int iNowDay = Integer.parseInt( datedd.format(new Date(System.currentTimeMillis())));

			if( iNextDay > iNowDay )
				new Utils().makeToast(getActivity(), getResources().getString(R.string.time_err4));
			else{
				mMillsETime =  ((mMillsETime/timeSpace)+1)*timeSpace;

				mTextView_etime.setText( dateformat.format(new Date(mMillsETime)) );
				mTextView_etime.setContentDescription("" + mMillsETime);
			}
		}
	}
}

















