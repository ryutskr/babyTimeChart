package com.babytimechart.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.TextView;

import com.activity.babytimechart.R;

public class BabyTimeProfile extends Activity {

	Context mContext;
	TextView mTextViewbirthday;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		setTitle(getString(R.string.baby_profile));
		setTitleColor(Color.BLACK);
		int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
        findViewById(titleDividerId).setBackgroundColor(Color.BLACK);
        
        mTextViewbirthday = (TextView)findViewById(R.id.textview_birthbay);
        mTextViewbirthday.setText(new SimpleDateFormat("yyyy / MM / dd").format(new Date(System.currentTimeMillis())));
        mTextViewbirthday.setOnClickListener(mOncClickListener);
		
		mContext= this;
	}
	
	OnClickListener mOncClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
//			Calendar calendar = Calendar.getInstance();
//			
//			DatePickerDialog dialog = new DatePickerDialog(mContext, mOnDateSetListener, 
//					calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
//			
//			dialog.show();
			
			
			View view = getLayoutInflater().inflate(R.layout.activity_profile_datepicker, null);
			
			AlertDialog mAlertDialog = new AlertDialog.Builder(mContext)
			.setTitle(mContext.getString(R.string.baby_profile))
			.setView(view)
			.setNegativeButton(mContext.getString(R.string.save), mOnClickListener)
			.setPositiveButton(mContext.getString(R.string.cancel), mOnClickListener)
			.create();
	        
			mAlertDialog.show();
			
			
			int titleId = getResources().getIdentifier("alertTitle", "id", "android");
			((TextView)mAlertDialog.findViewById(titleId)).setTextColor(Color.BLACK);
			int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
	        mAlertDialog.findViewById(titleDividerId).setBackgroundColor(Color.BLACK);
		}
	};
	
	android.content.DialogInterface.OnClickListener mOnClickListener = new android.content.DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Log.i("1111", "which : " + which);
			
		}
	};
	
	OnDateSetListener mOnDateSetListener = new OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			String strTemp = year+" / ";
			if( monthOfYear < 10)
				strTemp = strTemp + "0"+(monthOfYear+1) + " / ";
			else
				strTemp = strTemp + (monthOfYear+1) + " / ";
				
			if( dayOfMonth < 10) 
				strTemp = strTemp + "0"+dayOfMonth;
			else
				strTemp = strTemp + dayOfMonth;
			
			mTextViewbirthday.setText(strTemp);
			
		}
	};
}
















