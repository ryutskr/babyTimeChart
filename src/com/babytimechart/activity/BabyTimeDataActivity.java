package com.babytimechart.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.babytimechart.db.BabyTimeDbOpenHelper;
import com.babytimechart.db.Dbinfo;
import com.babytimechart.fragment.Fragment_Eating;
import com.babytimechart.fragment.Fragment_Etc;
import com.babytimechart.fragment.Fragment_Playing;
import com.babytimechart.fragment.Fragment_Sleeping;
import com.babytimechart.ui.HeightWrappingViewPager;
import com.babytimechart.ui.SlidingTabLayout;
import com.babytimechart.utils.Utils;
import com.ryutskr.babytimechart.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class BabyTimeDataActivity extends Activity{

	private static final String ARG_SECTION_NUMBER = "section_number";

	private HeightWrappingViewPager mViewPager = null;
	private SectionsPagerAdapter mSectionsPagerAdapter = null;
	private SlidingTabLayout mSlidingTabLayout = null;
	private List<ViewPagerItem> mTabs = new ArrayList<ViewPagerItem>();
	private Button mBtnSave = null;
	private Button mBtnCancel = null;
	private int mFragmentIndex = 0;
	private long mLastMillsTime = 0;

	static class ViewPagerItem {
		private final CharSequence mTitle;
		private final int mIndicatorColor;
		private final int mDividerColor;

		ViewPagerItem(CharSequence title, int indicatorColor, int dividerColor) {
			mTitle = title;
			mIndicatorColor = indicatorColor;
			mDividerColor = dividerColor;
		}

		/**
		 * @return the title which represents this tab. In this sample this is used directly by
		 * {@link android.support.v4.view.PagerAdapter#getPageTitle(int)}
		 */
		CharSequence getTitle() {
			return mTitle;
		}

		/**
		 * @return the color to be used for indicator on the {@link SlidingTabLayout}
		 */
		int getIndicatorColor() {
			return mIndicatorColor;
		}

		/**
		 * @return the color to be used for right divider on the {@link SlidingTabLayout}
		 */
		int getDividerColor() {
			return mDividerColor;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data);

		if( getIntent() != null )
			mFragmentIndex = getIntent().getIntExtra(ARG_SECTION_NUMBER, 0);

		addTabs();

        RelativeLayout mainLayout =  (RelativeLayout)findViewById(R.id.main_layout);
		mBtnSave = (Button)findViewById(R.id.btn_Activity_Data_Save);
		mBtnCancel = (Button)findViewById(R.id.btn_Activity_Data_Cancel);
		mBtnSave.setOnClickListener(mOnClickListener);
		mBtnCancel.setOnClickListener(mOnClickListener);

		getLastTimeToday();
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		mViewPager = (HeightWrappingViewPager) findViewById(R.id.viewPager_Activity_Data);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setCurrentItem(mFragmentIndex);

		mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
		mSlidingTabLayout.setViewPager(mViewPager);

		mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

			@Override
			public int getIndicatorColor(int position) {
				return mTabs.get(position).getIndicatorColor();
			}

			@Override
			public int getDividerColor(int position) {
				return mTabs.get(position).getDividerColor();
			}

		});

		setActinbar();
        new Utils().addBanner(this, mainLayout);
	}

	private void getLastTimeToday(){
		try{
			BabyTimeDbOpenHelper dbOpenHelper = new BabyTimeDbOpenHelper(this);
			SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
			String selection = "date ='"+ new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())) +"'";
			Cursor cursor = db.query(Dbinfo.DB_TABLE_NAME, new String[]{Dbinfo.DB_E_TIME},
					selection, null, Dbinfo.DB_E_TIME, null, Dbinfo.DB_E_TIME + " DESC");

			if (cursor!= null && cursor.moveToFirst()){
				mLastMillsTime = cursor.getLong(cursor.getColumnIndex(Dbinfo.DB_E_TIME));
			}
			cursor.close();
			db.close();

		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private void setActinbar() {
		getActionBar().setTitle(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
		getActionBar().setDisplayShowCustomEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(true);
	}

	OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btn_Activity_Data_Save:
				addFragmentDataToDB();
				setResult(RESULT_OK);
				finish();
				break;
			case R.id.btn_Activity_Data_Cancel:
				finish();
				break;
			}
		}
	};

	public void addFragmentDataToDB()
	{
		int viewPagerIndex = mViewPager.getCurrentItem();
		switch (viewPagerIndex){
		case 0:
			getEatingData();
			return;
		case 1:
			getPlayingData();
			return;
		case 2:
			getSleepingData();
			return;
		case 3:
			getEtcData();
			return;
		}
	}

	private void getEatingData() {

		Fragment_Eating fragmentE = (Fragment_Eating) mSectionsPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());

		String strMemo = "";
		String SEPERATOR = " / ";

		if( ((ToggleButton)fragmentE.getView().findViewById(R.id.tBtn_Eating_mm)).isChecked() )
			strMemo = getResources().getString(R.string.mothersmilk);
		else if( ((ToggleButton)fragmentE.getView().findViewById(R.id.tBtn_Eating_mp)).isChecked() )
			strMemo = getResources().getString(R.string.milkpowder);
		else if( ((ToggleButton)fragmentE.getView().findViewById(R.id.tBtn_Eating_bf)).isChecked() )
			strMemo = getResources().getString(R.string.babyfood);

		LinearLayout linearLayout_radio = (LinearLayout)fragmentE.getView().findViewById(R.id.linear_Eating_mm_radio);  
		LinearLayout linearLayout_volume = (LinearLayout)fragmentE.getView().findViewById(R.id.linear_Eating_volume); 

		if( linearLayout_radio.getVisibility() == View.VISIBLE )
		{
			if( ((RadioButton)fragmentE.getView().findViewById(R.id.rBtn_Eating_direct)).isChecked() )
				strMemo = strMemo + SEPERATOR + getResources().getString(R.string.eating_type_direct);
			else if( ((RadioButton)fragmentE.getView().findViewById(R.id.rBtn_Eating_bottle)).isChecked() )
				strMemo = strMemo + SEPERATOR + getResources().getString(R.string.eating_type_bottle);
		}

		if( linearLayout_volume.getVisibility() == View.VISIBLE )
		{
			EditText mEditeText_ml = (EditText) fragmentE.getView().findViewById(R.id.editText_Eating_ml);

			if( mEditeText_ml.getText().toString().contains("ml") )
				strMemo = strMemo + SEPERATOR + mEditeText_ml.getText();
			else
				strMemo = strMemo + SEPERATOR + mEditeText_ml.getText() + "ml";
		}

		EditText mEditeText_Eating_memo = (EditText)fragmentE.getView().findViewById(R.id.editText_Eating_Memo);
		strMemo =  strMemo +"\n"+ mEditeText_Eating_memo.getText();

		TextView mTextView_stime = (TextView)fragmentE.getView().findViewById(R.id.txtView_Eating_stime);
		TextView mTextView_etime = (TextView)fragmentE.getView().findViewById(R.id.txtView_Eating_etime);

		long stime = Long.parseLong( mTextView_stime.getContentDescription().toString() );
		long etime = Long.parseLong( mTextView_etime.getContentDescription().toString() );

		SimpleDateFormat insertDateformat = new SimpleDateFormat("yyyy-MM-dd");
		String strEtime = insertDateformat.format(new Date(etime));

		BabyTimeDbOpenHelper dbhelper = new BabyTimeDbOpenHelper(this);
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(Dbinfo.DB_TYPE, Dbinfo.DB_TYPE_EAT );
		contentValues.put(Dbinfo.DB_DATE, strEtime );
		contentValues.put(Dbinfo.DB_S_TIME, stime );
		contentValues.put(Dbinfo.DB_E_TIME, etime );
		contentValues.put(Dbinfo.DB_MEMO, strMemo );
		db.insert(Dbinfo.DB_TABLE_NAME, null, contentValues);
		db.close();
	}


	private void getPlayingData() {
		Fragment_Playing fragmentP = (Fragment_Playing) mSectionsPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());

		String strMemo = "";
		String SEPERATOR = " / ";

		if( ((ToggleButton)fragmentP.getView().findViewById(R.id.tBtn_Playing_mom)).isChecked() )
			strMemo = getResources().getString(R.string.mom);
		if( ((ToggleButton)fragmentP.getView().findViewById(R.id.tBtn_Playing_daddy)).isChecked() )
			strMemo = strMemo + SEPERATOR + getResources().getString(R.string.daddy);
		if( ((ToggleButton)fragmentP.getView().findViewById(R.id.tBtn_Playing_family)).isChecked() )
			strMemo = strMemo + SEPERATOR + getResources().getString(R.string.family);
		if( ((ToggleButton)fragmentP.getView().findViewById(R.id.tBtn_Playing_friend)).isChecked() )
			strMemo = strMemo + SEPERATOR + getResources().getString(R.string.friend);

		EditText mEditeText_Eating_memo = (EditText)fragmentP.getView().findViewById(R.id.editText_Playing_Memo);
		strMemo =  strMemo +"\n"+ mEditeText_Eating_memo.getText();

		TextView mTextView_stime = (TextView)fragmentP.getView().findViewById(R.id.txtView_Playing_stime);
		TextView mTextView_etime = (TextView)fragmentP.getView().findViewById(R.id.txtView_Playing_etime);



		long stime = Long.parseLong( mTextView_stime.getContentDescription().toString() );
		long etime = Long.parseLong( mTextView_etime.getContentDescription().toString() );

		SimpleDateFormat insertDateformat = new SimpleDateFormat("yyyy-MM-dd");
		String strEtime = insertDateformat.format(new Date(etime));

		BabyTimeDbOpenHelper dbhelper = new BabyTimeDbOpenHelper(this);
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(Dbinfo.DB_TYPE, Dbinfo.DB_TYPE_PLAY );
		contentValues.put(Dbinfo.DB_DATE, strEtime );
		contentValues.put(Dbinfo.DB_S_TIME, stime );
		contentValues.put(Dbinfo.DB_E_TIME, etime );
		contentValues.put(Dbinfo.DB_MEMO, strMemo );
		db.insert(Dbinfo.DB_TABLE_NAME, null, contentValues);
		db.close();
	}

	private void getSleepingData() {
		Fragment_Sleeping fragmentS = (Fragment_Sleeping) mSectionsPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());

		String strMemo = "";
		String SEPERATOR = " / ";

		if( ((RadioButton)fragmentS.getView().findViewById(R.id.rBtn_Sleeping_nap)).isChecked() )
			strMemo = strMemo + getResources().getString(R.string.nap);
		else if( ((RadioButton)fragmentS.getView().findViewById(R.id.rBtn_Sleeping_night)).isChecked() )
			strMemo = strMemo + getResources().getString(R.string.night_sleep);

		EditText mEditeText_Eating_memo = (EditText)fragmentS.getView().findViewById(R.id.editText_Sleeping_Memo);
		strMemo =  strMemo +"\n"+ mEditeText_Eating_memo.getText();

		TextView mTextView_stime = (TextView)fragmentS.getView().findViewById(R.id.txtView_Sleeping_stime);
		TextView mTextView_etime = (TextView)fragmentS.getView().findViewById(R.id.txtView_Sleeping_etime);

		long stime = Long.parseLong( mTextView_stime.getContentDescription().toString() );
		long etime = Long.parseLong( mTextView_etime.getContentDescription().toString() );

		SimpleDateFormat insertDateformat = new SimpleDateFormat("yyyy-MM-dd");
		String strEtime = insertDateformat.format(new Date(etime));

		BabyTimeDbOpenHelper dbhelper = new BabyTimeDbOpenHelper(this);
		SQLiteDatabase db = dbhelper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(Dbinfo.DB_TYPE, Dbinfo.DB_TYPE_SLEEP );
		contentValues.put(Dbinfo.DB_DATE, strEtime );
		contentValues.put(Dbinfo.DB_S_TIME, stime );
		contentValues.put(Dbinfo.DB_E_TIME, etime );
		contentValues.put(Dbinfo.DB_MEMO, strMemo );
		db.insert(Dbinfo.DB_TABLE_NAME, null, contentValues);
		db.close();
	}

	private void getEtcData() {
		Fragment_Etc fragmentE = (Fragment_Etc) mSectionsPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());

		String strMemo = "";
		String SEPERATOR = " / ";

		if( ((CheckBox)fragmentE.getView().findViewById(R.id.cBox_Etc_bath)).isChecked() )
			strMemo = getResources().getString(R.string.bath);
		if( ((CheckBox)fragmentE.getView().findViewById(R.id.cBox_Etc_bath)).isChecked() )
			strMemo = strMemo + SEPERATOR + getResources().getString(R.string.babypoop);

		EditText mEditeText_Eating_memo = (EditText)fragmentE.getView().findViewById(R.id.editText_Etc_Memo);
		strMemo =  strMemo +"\n"+ mEditeText_Eating_memo.getText();

		inserDataToDB(fragmentE, Dbinfo.DB_TYPE_ETC, strMemo);
	}

	public void inserDataToDB(Fragment fm, String type, String memo){

		TextView mTextView_stime = (TextView)fm.getView().findViewById(R.id.txtView_Etc_stime);
		TextView mTextView_etime = (TextView)fm.getView().findViewById(R.id.txtView_Etc_etime);

		long stime = Long.parseLong( mTextView_stime.getContentDescription().toString() );
		long etime = Long.parseLong( mTextView_etime.getContentDescription().toString() );

		SimpleDateFormat insertDateformat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateformat = new SimpleDateFormat("dd");

		String strEtime = insertDateformat.format(new Date(etime));
		String strStime = insertDateformat.format(new Date(stime));

		String strStimedd = dateformat.format(new Date(stime));
		String strEtimedd = dateformat.format(new Date(etime));

		try{
			BabyTimeDbOpenHelper dbhelper = new BabyTimeDbOpenHelper(this);
			SQLiteDatabase db = dbhelper.getWritableDatabase();
			ContentValues contentValues = new ContentValues();

			if( Integer.parseInt( strEtimedd ) != Integer.parseInt( strStimedd ) ){
				contentValues.clear();
				contentValues.put(Dbinfo.DB_TYPE, type );
				contentValues.put(Dbinfo.DB_DATE, strStime );
				contentValues.put(Dbinfo.DB_S_TIME, stime );
				contentValues.put(Dbinfo.DB_E_TIME, etime );
				contentValues.put(Dbinfo.DB_MEMO, memo );
				db.insert(Dbinfo.DB_TABLE_NAME, null, contentValues);
			}

			contentValues.clear();
			contentValues.put(Dbinfo.DB_TYPE, type );
			contentValues.put(Dbinfo.DB_DATE, strEtime );
			contentValues.put(Dbinfo.DB_S_TIME, stime );
			contentValues.put(Dbinfo.DB_E_TIME, etime );
			contentValues.put(Dbinfo.DB_MEMO, memo );
			db.insert(Dbinfo.DB_TABLE_NAME, null, contentValues);
			db.close();
		}catch (SQLException e){
			e.printStackTrace();
		}
	}

	public void addTabs(){
		mTabs.add(new ViewPagerItem(
				getString(R.string.eating), // Title
				Utils.mEatColor, // Indicator color
				getResources().getColor(R.color.tab_divider) // Divider color
				));

		mTabs.add(new ViewPagerItem(
				getString(R.string.playing), // Title
				Utils.mPlayColor, // Indicator color
				getResources().getColor(R.color.tab_divider) // Divider color
				));

		mTabs.add(new ViewPagerItem(
				getString(R.string.sleeping), // Title
				Utils.mSleepColor, // Indicator color
				getResources().getColor(R.color.tab_divider) // Divider color
				));

		mTabs.add(new ViewPagerItem(
				getString(R.string.etc), // Title
				Utils.mEtcColor, // Indicator color
				getResources().getColor(R.color.tab_divider) // Divider color
				));
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm){
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch( position ){
			case 0:
				return Fragment_Eating.newInstance(position + 1, mLastMillsTime);
			case 1:
				return Fragment_Playing.newInstance(position + 1, mLastMillsTime);
			case 2:
				return Fragment_Sleeping.newInstance(position + 1, mLastMillsTime);
			case 3:
				return Fragment_Etc.newInstance(position + 1, mLastMillsTime);
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mTabs.get(position).getTitle();
		}
	}
}
