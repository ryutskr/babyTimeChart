package com.babytimechart.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.babytimechart.utils.Utils;
import com.ryutskr.babytimechart.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class Fragment_Eating extends Fragment {
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

	private RadioButton mRadio_mm = null;
	private RadioButton mRadio_mp = null;
	private RadioButton mRadio_bf = null;

	private RadioButton mRadio_direct = null;
	private RadioButton mRadio_bottle = null;

	private LinearLayout mLinearLayout_radio = null;
	private LinearLayout mLinearLayout_volume = null;

	private Button mButton_ml_minus_small = null;
	private Button mButton_ml_minus_big = null;
	private Button mButton_ml_plus_small = null;
	private Button mButton_ml_plus_big = null;
	private EditText mEditeText_ml = null;

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
	public static Fragment_Eating newInstance(int sectionNumber, long lastMillsTime) {
		Fragment_Eating fragment = new Fragment_Eating();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		args.putLong(ARG_TODAY_LAST_TIME, lastMillsTime);
		fragment.setArguments(args);
		return fragment;
	}

	public Fragment_Eating() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if( getArguments() != null )
			mLastMillsTime = getArguments().getLong(ARG_TODAY_LAST_TIME, 0);
		
		View rootView = inflater.inflate(R.layout.fragment_eating, container, false);
		initView(rootView);
		return rootView;
	}

	public void initView(View rootView)
	{
		mRadio_mm  = (RadioButton)rootView.findViewById(R.id.rBtn_Eating_mm);
		mRadio_mp  = (RadioButton)rootView.findViewById(R.id.rBtn_Eating_mp);
		mRadio_bf  = (RadioButton)rootView.findViewById(R.id.rBtn_Eating_bf);
		mRadio_mm.setOnCheckedChangeListener(mOnCheckedChangeListener);
		mRadio_mp.setOnCheckedChangeListener(mOnCheckedChangeListener);
		mRadio_bf.setOnCheckedChangeListener(mOnCheckedChangeListener);

		mRadio_direct = (RadioButton)rootView.findViewById(R.id.rBtn_Eating_direct);
		mRadio_bottle = (RadioButton)rootView.findViewById(R.id.rBtn_Eating_bottle);
		mRadio_direct.setOnCheckedChangeListener(mOnCheckedChangeListener);
		mRadio_bottle.setOnCheckedChangeListener(mOnCheckedChangeListener);

		mLinearLayout_radio = (LinearLayout)rootView.findViewById(R.id.linear_Eating_mm_radio);
		mLinearLayout_volume = (LinearLayout)rootView.findViewById(R.id.linear_Eating_volume);

		mButton_ml_minus_small = (Button)rootView.findViewById(R.id.btn_Eating_minus_small_ml);
		mButton_ml_minus_big = (Button)rootView.findViewById(R.id.btn_Eating_minus_big_ml);
		mButton_ml_plus_small = (Button)rootView.findViewById(R.id.btn_Eating_plus_small_ml);
		mButton_ml_plus_big = (Button)rootView.findViewById(R.id.btn_Eating_plus_big_ml);
		
		mButton_ml_minus_small.setOnClickListener(mOnClickListener);
		mButton_ml_minus_big.setOnClickListener(mOnClickListener);
		mButton_ml_plus_small.setOnClickListener(mOnClickListener);
		mButton_ml_plus_big.setOnClickListener(mOnClickListener);

		mEditeText_ml = (EditText)rootView.findViewById(R.id.editText_Eating_ml);

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

		mRadio_mm.setChecked(true);
	}

	OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if( !isChecked )
				return;

			switch( buttonView.getId())
			{
			case R.id.rBtn_Eating_mm:
				mLinearLayout_radio.setVisibility(View.VISIBLE);
				mLinearLayout_volume.setVisibility(View.GONE);

				mRadio_mp.setChecked(false);
				mRadio_bf.setChecked(false);
				mRadio_direct.setChecked(true);
				mRadio_bottle.setChecked(false);

				break;
			case R.id.rBtn_Eating_mp:
				mLinearLayout_radio.setVisibility(View.GONE);
				mLinearLayout_volume.setVisibility(View.VISIBLE);

				mRadio_mm.setChecked(false);
				mRadio_bf.setChecked(false);
				break;
			case R.id.rBtn_Eating_bf:
				mLinearLayout_radio.setVisibility(View.GONE);
				mLinearLayout_volume.setVisibility(View.VISIBLE);

				mRadio_mm.setChecked(false);
				mRadio_mp.setChecked(false);
				break;
			case R.id.rBtn_Eating_direct:
				mLinearLayout_volume.setVisibility(View.GONE);
				break;
			case R.id.rBtn_Eating_bottle:
				mLinearLayout_volume.setVisibility(View.VISIBLE);
				break;
			}
		}
	};

	OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int iValue;
			Utils utils = new Utils();
			switch(v.getId()){
			case R.id.btn_Eating_minus_small_ml:
				iValue = Integer.parseInt( mEditeText_ml.getText().toString().replace("ml", ""));
				mEditeText_ml.setText("" + (iValue - 20) + "ml");
				break;
			case R.id.btn_Eating_minus_big_ml:
				iValue = Integer.parseInt( mEditeText_ml.getText().toString().replace("ml", ""));
				mEditeText_ml.setText("" + (iValue - 10) + "ml");
				break;
			case R.id.btn_Eating_plus_small_ml:
				iValue = Integer.parseInt( mEditeText_ml.getText().toString().replace("ml", ""));
				mEditeText_ml.setText("" + (iValue + 10) + "ml");
				break;
			case R.id.btn_Eating_plus_big_ml:
				iValue = Integer.parseInt( mEditeText_ml.getText().toString().replace("ml", ""));
				mEditeText_ml.setText("" + (iValue + 20) + "ml");
				break;
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
				mIsSTimeClick= true;
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
			//             if( mLastMillsTime > mMillsSTime - SPACE_IN_TIME_BIG)
			//                 utils.makeToast(getActivity(), getResources().getString(R.string.time_err1));
			//             else{
			if( (mMillsSTime%timeSpace) != 0 )
				mMillsSTime =  ((mMillsSTime/timeSpace))*timeSpace;
			else
				mMillsSTime =  ((mMillsSTime/timeSpace)-1)*timeSpace;

			mTextView_stime.setText( dateformat.format(new Date(mMillsSTime)) );
			mTextView_stime.setContentDescription("" + mMillsSTime);
			//             }
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

















