package com.babytimechart.activity;

import static com.babytimechart.db.Dbinfo.DB_DATE;
import static com.babytimechart.db.Dbinfo.DB_E_TIME;
import static com.babytimechart.db.Dbinfo.DB_MEMO;
import static com.babytimechart.db.Dbinfo.DB_S_TIME;
import static com.babytimechart.db.Dbinfo.DB_TABLE_NAME;
import static com.babytimechart.db.Dbinfo.DB_TYPE;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.activity.babytimechart.R;
import com.babytimechart.db.BabyTimeDbOpenHelper;
import com.babytimechart.db.Dbinfo;
import com.babytimechart.ui.BabyTimeSettingMenuAdapter;
import com.babytimechart.ui.ColorPickerSwatch;
import com.babytimechart.utils.Utils;

public class BabyTimeSetting extends ListActivity {

	private static final int MENU_EAT 		= 100;
	private static final int MENU_PLAY 		= 101;
	private static final int MENU_SLEEP 	= 102;
	private static final int MENU_ETC 		= 103;
	private static final int MENU_PROFILE 	= 104;

	private static final int MENU_BACKUP_DATA 	= 200;
	private static final int MENU_RESTORE_DATA 	= 201;
	private static final int MENU_INITIALIZATION_DATA 	= 202;

	BabyTimeSettingMenuAdapter mAdapter = null;
	int mLastPosition = 0;
	int[] mColorChoices = null;
	Utils mUtils = null;
	Context mContext = null;

	// dialog
	AlertDialog mAlertDialogProfile = null;  // profile or datepicker
	TextView mTextViewbirthday = null;		 // set Date	
	DatePicker mDatePicker = null;

	// Selected colors

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActinbar();
		initMenu();
		mContext = this;
	}

	private void setActinbar() {
		getActionBar().setTitle(getString(R.string.setting));
		getActionBar().setDisplayShowCustomEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(true);
	}

	private void initMenu() {
		mUtils = new Utils();
		mColorChoices = mUtils.getColorsForPicker(this);
		mAdapter = new BabyTimeSettingMenuAdapter(this);

		// nomal
		mAdapter.addHeader(R.string.normal);
		mAdapter.addItem(MENU_EAT, R.string.eating, R.string.eat_eplain, Utils.mEatColor );
		mAdapter.addItem(MENU_PLAY, R.string.playing, R.string.play_eplain, Utils.mPlayColor );
		mAdapter.addItem(MENU_SLEEP, R.string.sleeping, R.string.sleep_eplain, Utils.mSleepColor );
		mAdapter.addItem(MENU_ETC, R.string.etc, R.string.eat_eplain, Utils.mEtcColor );
		mAdapter.addItem(MENU_PROFILE, R.string.baby_profile, R.string.baby_profile_eplain, 0);

		// manage
		mAdapter.addHeader(R.string.manege);
		mAdapter.addItem(MENU_BACKUP_DATA, R.string.data_backup, R.string.data_backup_explain, 0);
		mAdapter.addItem(MENU_RESTORE_DATA, R.string.data_restore, R.string.data_restore_eplain, 0);
		mAdapter.addItem(MENU_INITIALIZATION_DATA, R.string.data_initialization, R.string.data_initialization_explain, 0);

		setListAdapter(mAdapter);
		getListView().setDividerHeight(0);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		mLastPosition = position;
		BabyTimeSettingMenuAdapter.MenuItemModel item = mAdapter.getItem(position);
		switch ( item._id ){
		case MENU_EAT:
		case MENU_PLAY:
		case MENU_SLEEP:
		case MENU_ETC:

			ColorPickerDialog colorcalendar = ColorPickerDialog.newInstance(
					R.string.color_picker_default_title, mColorChoices,
					item.colorSquare, 5,ColorPickerDialog.SIZE_SMALL);

			colorcalendar.setOnColorSelectedListener(colorcalendarListener);
			colorcalendar.show(getFragmentManager(), "");
			break;
		case MENU_PROFILE:
			//			startActivity(new Intent(this, BabyTimeProfile.class));
			createAlerDialog();
			break;
		case MENU_BACKUP_DATA:
			fakeDBData();
			break;
		case MENU_RESTORE_DATA:
		case MENU_INITIALIZATION_DATA:
			try {
				// clear DB
				BabyTimeDbOpenHelper dbhelper = new BabyTimeDbOpenHelper(this);
				SQLiteDatabase db = dbhelper.getWritableDatabase();
				db.delete(Dbinfo.DB_TABLE_NAME, null, null);
				db.close();

				// clear Utils
				mUtils.clearUtilsValues(this);
			}catch (SQLException e){
				e.printStackTrace();
			}
			break;
		}

		setResult(RESULT_OK);
	}

	public void createAlerDialog(){

		View view = getLayoutInflater().inflate(R.layout.activity_profile, null);

		mAlertDialogProfile = new AlertDialog.Builder(mContext)
		.setTitle(mContext.getString(R.string.baby_profile))
		.setView(view)
		.setNegativeButton(mContext.getString(R.string.save), mOnDialogBtnClickListener)
		.create();

		mAlertDialogProfile.show();

		int titleId = getResources().getIdentifier("alertTitle", "id", "android");
		((TextView)mAlertDialogProfile.findViewById(titleId)).setTextColor(Color.BLACK);
		int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
		mAlertDialogProfile.findViewById(titleDividerId).setBackgroundColor(Color.BLACK);

		mTextViewbirthday = (TextView)view.findViewById(R.id.textview_birthbay);
		mTextViewbirthday.setText(new SimpleDateFormat("yyyy / MM / dd").format(new Date(System.currentTimeMillis())));
		mTextViewbirthday.setOnClickListener(mOnClickListener);
	}

	OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			View view = getLayoutInflater().inflate(R.layout.activity_profile_datepicker, null);

			AlertDialog mAlertDialogDatePicker = new AlertDialog.Builder(mContext)
			.setTitle(mContext.getString(R.string.baby_profile))
			.setView(view)
			.setNegativeButton(mContext.getString(R.string.save), mOnDialogBtnClickListener)
			.setPositiveButton(mContext.getString(R.string.cancel), mOnDialogBtnClickListener)
			.create();

			mAlertDialogDatePicker.show();


			mDatePicker = (DatePicker)mAlertDialogDatePicker.findViewById(R.id.datePicker);

			int titleId = getResources().getIdentifier("alertTitle", "id", "android");
			((TextView)mAlertDialogDatePicker.findViewById(titleId)).setTextColor(Color.BLACK);
			int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
			mAlertDialogDatePicker.findViewById(titleDividerId).setBackgroundColor(Color.BLACK);

		}
	};
	android.content.DialogInterface.OnClickListener mOnDialogBtnClickListener = new android.content.DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			Log.i("1111", "dialog  : " + dialog + " which : " + which);
			if( dialog.equals(mAlertDialogProfile)){
				// pref에 생년 write

			}else{
				if( mDatePicker != null ){

					String strTemp = mDatePicker.getYear() +" / ";
					if( mDatePicker.getYear() < 10)
						strTemp = strTemp + "0"+(mDatePicker.getMonth()+1) + " / ";
					else
						strTemp = strTemp + (mDatePicker.getMonth()+1) + " / ";

					if( mDatePicker.getDayOfMonth() < 10) 
						strTemp = strTemp + "0"+mDatePicker.getDayOfMonth();
					else
						strTemp = strTemp + mDatePicker.getDayOfMonth();

					mTextViewbirthday.setText(strTemp);
				}
			}
		}
	};


	// Implement listener to get selected color value
	ColorPickerSwatch.OnColorSelectedListener colorcalendarListener = new ColorPickerSwatch.OnColorSelectedListener(){
		@Override
		public void onColorSelected(int color) {
			BabyTimeSettingMenuAdapter.MenuItemModel item = mAdapter.getItem(mLastPosition);
			if (item!=null){
				item.colorSquare = color;
				mAdapter.notifyDataSetChanged();
				mUtils.setChangeColor(item._id, color);
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mUtils.setColorsToPref(this);
	}

	public void fakeDBData()
	{
		long time = System.currentTimeMillis();

		SimpleDateFormat insertDateformat1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat insertDateformat2 = new SimpleDateFormat("dd");
		String strToday1 = "";
		String strToday2 = insertDateformat2.format(new Date(time));

		BabyTimeDbOpenHelper dbhelper = new BabyTimeDbOpenHelper(this);
		SQLiteDatabase db = dbhelper.getWritableDatabase();

		Calendar today = Calendar.getInstance();
		today.set(2014, 11, Integer.parseInt(strToday2)-1, 23, 30);
		long time1 = today.getTimeInMillis();

		today.set(2014, 11, Integer.parseInt(strToday2), 3, 20);
		long time2 = today.getTimeInMillis();

		today.set(2014, 11, Integer.parseInt(strToday2), 5, 40);
		long time3 = today.getTimeInMillis();

		today.set(2014, 11, Integer.parseInt(strToday2), 6, 30);
		long time4 = today.getTimeInMillis();

		today.set(2014, 11, Integer.parseInt(strToday2), 8, 10);
		long time5 = today.getTimeInMillis();

		today.set(2014, 11, Integer.parseInt(strToday2), 9, 50);
		long time6 = today.getTimeInMillis();


		long[] arrtime = {time1,time2, time3, time4, time5, time6};
		String[] arrType = {"eat", "play", "sleep", "etc","play"};
		ContentValues contentValues = new ContentValues();
		for(int i=0; i< 5; i++)
		{
			strToday1 = insertDateformat1.format(new Date(time -(i* 24*60*60*1000)));
			for(int k=0; k<5; k++){
				contentValues.clear();
				contentValues.put(DB_TYPE, arrType[k] );
				contentValues.put(DB_DATE, strToday1 );
				contentValues.put(DB_S_TIME, arrtime[k]+(k*60*60*1000) + (i*60*60*1000) );
				contentValues.put(DB_E_TIME, arrtime[k+1] + (k*60*60*1000) + (i*60*60*1000) );
				contentValues.put(DB_MEMO, "" + i +k  );
				db.insert(DB_TABLE_NAME, null, contentValues);
			}
		}
		db.close();
	}
}





















