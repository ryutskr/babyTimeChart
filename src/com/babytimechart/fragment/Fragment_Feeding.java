package com.babytimechart.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.ToggleButton;

import com.activity.babytimechart.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class Fragment_Feeding extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final int SPACE_IN_TIME = 30 * 60 * 1000;
	private static final int SPACE_IN_TIME_SMALL 	= 5 * 60 * 1000;
	private static final int SPACE_IN_TIME_BIG 		= 20 * 60 * 1000;

	private ToggleButton tbtn_mm = null;
	private ToggleButton tbtn_mp = null;
	private ToggleButton tbtn_bf = null;
	private ToggleButton tbtn_4  = null;

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


	private Button mButton_time_minus_small = null;
	private Button mButton_time_minus_big = null;
	private Button mButton_time_plus_small = null;
	private Button mButton_time_plus_big = null;
	
	private long mMillsSTime = 0;
	private long mMillsETime = 0;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static Fragment_Feeding newInstance(int sectionNumber) {
		Fragment_Feeding fragment = new Fragment_Feeding();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public Fragment_Feeding() {
	}

	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("babytime", getActivity().getComponentName() + "  / onCreateView() ");
		View rootView = inflater.inflate(R.layout.fragment_feeding, container, false);

		initView(rootView);

		return rootView;
	}

	public void initView(View rootView)
	{
		tbtn_mm  = (ToggleButton)rootView.findViewById(R.id.tBtn_Feeding_mm);
		tbtn_mp  = (ToggleButton)rootView.findViewById(R.id.tBtn_Feeding_mp);
		tbtn_bf  = (ToggleButton)rootView.findViewById(R.id.tBtn_Feeding_bf);
		tbtn_4  = (ToggleButton)rootView.findViewById(R.id.toggleButton4);
		tbtn_mm.setOnCheckedChangeListener(mOnCheckedChangeListener);
		tbtn_mp.setOnCheckedChangeListener(mOnCheckedChangeListener);
		tbtn_bf.setOnCheckedChangeListener(mOnCheckedChangeListener);
		tbtn_4.setOnCheckedChangeListener(mOnCheckedChangeListener);

		mRadio_direct = (RadioButton)rootView.findViewById(R.id.rBtn_Feeding_direct);
		mRadio_bottle = (RadioButton)rootView.findViewById(R.id.rBtn_Feeding_bottle);
		mRadio_direct.setOnCheckedChangeListener(mOnCheckedChangeListener);
		mRadio_bottle.setOnCheckedChangeListener(mOnCheckedChangeListener);

		mLinearLayout_radio = (LinearLayout)rootView.findViewById(R.id.linear_Feeding_mm_radio);  
		mLinearLayout_volume = (LinearLayout)rootView.findViewById(R.id.linear_Feeding_volume); 

		mButton_ml_minus_small = (Button)rootView.findViewById(R.id.btn_Feeding_minus_small_ml);
		mButton_ml_minus_big = (Button)rootView.findViewById(R.id.btn_Feeding_minus_big_ml);
		mButton_ml_plus_small = (Button)rootView.findViewById(R.id.btn_Feeding_plus_small_ml);
		mButton_ml_plus_big = (Button)rootView.findViewById(R.id.btn_Feeding_plus_big_ml); 
		mButton_ml_minus_small.setOnClickListener(mOnClickListener);
		mButton_ml_minus_big.setOnClickListener(mOnClickListener);
		mButton_ml_plus_small.setOnClickListener(mOnClickListener);
		mButton_ml_plus_big.setOnClickListener(mOnClickListener);
		
		mEditeText_ml = (EditText)rootView.findViewById(R.id.editText_Feeding_ml);

		mTextView_stime = (TextView)rootView.findViewById(R.id.txtView_Feeding_stime);
		mTextView_etime = (TextView)rootView.findViewById(R.id.txtView_Feeding_etime);    
		mTextView_stime.setOnClickListener(mOnClickListener);
		mTextView_etime.setOnClickListener(mOnClickListener);
		
		mMillsSTime = System.currentTimeMillis();
		mMillsETime = mMillsSTime + SPACE_IN_TIME;
		SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm");
		String sTime = dateformat.format(new Date(mMillsSTime));
		String eTime = dateformat.format(new Date(mMillsETime));
		mTextView_stime.setText(sTime);
		mTextView_etime.setText(eTime);
		mTextView_stime.setContentDescription("" + mMillsSTime);
		mTextView_etime.setContentDescription("" + mMillsETime);
		
		mTextView_stime.setBackgroundColor(getActivity().getResources().getColor(R.color.peachpuff));

		mButton_time_minus_small = (Button)rootView.findViewById(R.id.btn_Feeding_minus_small_time);
		mButton_time_minus_big = (Button)rootView.findViewById(R.id.btn_Feeding_minus_big_time);
		mButton_time_plus_small = (Button)rootView.findViewById(R.id.btn_Feeding_plus_small_time);
		mButton_time_plus_big = (Button)rootView.findViewById(R.id.btn_Feeding_plus_big_time);
		mButton_time_minus_small.setOnClickListener(mOnClickListener);
		mButton_time_minus_big.setOnClickListener(mOnClickListener);
		mButton_time_plus_small.setOnClickListener(mOnClickListener);
		mButton_time_plus_big.setOnClickListener(mOnClickListener);
		
		tbtn_mm.setChecked(true);
	}

	OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if( !isChecked )
				return;

			switch( buttonView.getId())
			{
			case R.id.tBtn_Feeding_mm:
				mLinearLayout_volume.setVisibility(View.GONE);
				mLinearLayout_radio.setVisibility(View.VISIBLE);

				tbtn_mp.setChecked(false);
				tbtn_bf.setChecked(false);
				tbtn_4.setChecked(false);

				break;
			case R.id.tBtn_Feeding_mp:
				mLinearLayout_volume.setVisibility(View.VISIBLE);
				mLinearLayout_radio.setVisibility(View.GONE);

				tbtn_mm.setChecked(false);
				tbtn_bf.setChecked(false);
				tbtn_4.setChecked(false);
				break;
			case R.id.tBtn_Feeding_bf:
				mLinearLayout_volume.setVisibility(View.VISIBLE);
				mLinearLayout_radio.setVisibility(View.GONE);
				
				tbtn_mm.setChecked(false);
				tbtn_mp.setChecked(false);
				tbtn_4.setChecked(false);
				break;
			case R.id.toggleButton4:
				tbtn_mm.setChecked(false);
				tbtn_mp.setChecked(false);
				tbtn_bf.setChecked(false);
				break;
			case R.id.rBtn_Feeding_direct:
				mLinearLayout_volume.setVisibility(View.GONE);
				break;
			case R.id.rBtn_Feeding_bottle:
				mLinearLayout_volume.setVisibility(View.VISIBLE);
				break;
			}
		}
	};

	OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int iValue = 0;
			SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm");
			switch(v.getId()){
			case R.id.btn_Feeding_minus_small_ml:
				iValue = Integer.parseInt( mEditeText_ml.getText().toString().replace("ml", ""));
				mEditeText_ml.setText("" + (iValue - 20) + "ml");
				break;
			case R.id.btn_Feeding_minus_big_ml:
				iValue = Integer.parseInt( mEditeText_ml.getText().toString().replace("ml", ""));
				mEditeText_ml.setText("" + (iValue - 10) + "ml");
				break;
			case R.id.btn_Feeding_plus_small_ml:
				iValue = Integer.parseInt( mEditeText_ml.getText().toString().replace("ml", ""));
				mEditeText_ml.setText("" + (iValue + 10) + "ml");
				break;
			case R.id.btn_Feeding_plus_big_ml:
				iValue = Integer.parseInt( mEditeText_ml.getText().toString().replace("ml", ""));
				mEditeText_ml.setText("" + (iValue + 20) + "ml");
				break;
				
			case R.id.btn_Feeding_minus_small_time:
				if( mTextView_stime.isFocused() )
				{
					mMillsSTime =  mMillsSTime - SPACE_IN_TIME_BIG; 
					mTextView_stime.setText( dateformat.format(new Date(mMillsSTime)) );
					mTextView_stime.setContentDescription("" + mMillsSTime);
				}else if( mTextView_etime.isFocused() ){
					mMillsETime =  mMillsETime - SPACE_IN_TIME_BIG; 
					mTextView_etime.setText( dateformat.format(new Date(mMillsETime)) );
					mTextView_etime.setContentDescription("" + mMillsETime);
				}
				break;
			case R.id.btn_Feeding_minus_big_time:
				if( mTextView_stime.isFocused() )
				{
					mMillsSTime =  mMillsSTime - SPACE_IN_TIME_SMALL; 
					mTextView_stime.setText( dateformat.format(new Date(mMillsSTime)) );
					mTextView_stime.setContentDescription("" + mMillsSTime);
				}else if( mTextView_etime.isFocused() ){
					mMillsETime =  mMillsETime - SPACE_IN_TIME_SMALL; 
					mTextView_etime.setText( dateformat.format(new Date(mMillsETime)) );
					mTextView_etime.setContentDescription("" + mMillsETime);
				}
				break;
			case R.id.btn_Feeding_plus_small_time:
				if( mTextView_stime.isFocused() )
				{
					mMillsSTime =  mMillsSTime + SPACE_IN_TIME_SMALL; 
					mTextView_stime.setText( dateformat.format(new Date(mMillsSTime)) );
					mTextView_stime.setContentDescription("" + mMillsSTime);
				}else if( mTextView_etime.isFocused() ){
					mMillsETime =  mMillsETime + SPACE_IN_TIME_SMALL; 
					mTextView_etime.setText( dateformat.format(new Date(mMillsETime)) );
					mTextView_etime.setContentDescription("" + mMillsETime);
				}
				break;
			case R.id.btn_Feeding_plus_big_time:
				if( mTextView_stime.isFocused() )
				{
					mMillsSTime =  mMillsSTime + SPACE_IN_TIME_BIG; 
					mTextView_stime.setText( dateformat.format(new Date(mMillsSTime)) );
					mTextView_stime.setContentDescription("" + mMillsSTime);
				}else if( mTextView_etime.isFocused() ){
					mMillsETime =  mMillsETime + SPACE_IN_TIME_BIG; 
					mTextView_etime.setText( dateformat.format(new Date(mMillsETime)) );
					mTextView_etime.setContentDescription("" + mMillsETime);
				}
				break;
				
			case R.id.txtView_Feeding_stime:
				mTextView_stime.setBackgroundColor(getActivity().getResources().getColor(R.color.peachpuff));
				mTextView_etime.setBackgroundColor(getActivity().getResources().getColor(R.color.papayawhip));
				break;
			case R.id.txtView_Feeding_etime:
				mTextView_stime.setBackgroundColor(getActivity().getResources().getColor(R.color.papayawhip));
				mTextView_etime.setBackgroundColor(getActivity().getResources().getColor(R.color.peachpuff));
				break;
			}
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

















