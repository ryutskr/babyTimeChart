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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

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


public class BabyTimeData extends Activity{

	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final String ARG_TODAY_LAST_DAY = "lastdate";
	private static final int SPACE_IN_TIME_24H 		= 24 * 60 * 60 * 1000;
	

	private HeightWrappingViewPager mViewPager = null;
	private SectionsPagerAdapter mSectionsPagerAdapter = null;
	private SlidingTabLayout mSlidingTabLayout = null;
	private List<ViewPagerItem> mTabs = new ArrayList<ViewPagerItem>();
	private Button mBtnSave = null;
	private Button mBtnCancel = null;
	private int mFragmentIndex = 0;
	private long mLastMillsTime = 0;
	public String mLastSelectedDay = "";

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

		if( getIntent() != null ){
			mFragmentIndex = getIntent().getIntExtra(ARG_SECTION_NUMBER, 0);
			mLastSelectedDay = getIntent().getStringExtra(ARG_TODAY_LAST_DAY);
		}

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

        setActionbar();
        new Utils().addBanner(this, mainLayout);
	}

	private void getLastTimeToday(){
		try{
			BabyTimeDbOpenHelper dbOpenHelper = new BabyTimeDbOpenHelper(this);
			SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
			String selection = "date ='"+ mLastSelectedDay +"'";
			Cursor cursor = db.query(Dbinfo.DB_TABLE_NAME, new String[]{Dbinfo.DB_E_TIME},
					selection, null, Dbinfo.DB_E_TIME, null, Dbinfo.DB_E_TIME + " DESC");

			if (cursor!= null && cursor.moveToFirst()){
				mLastMillsTime = cursor.getLong(cursor.getColumnIndex(Dbinfo.DB_E_TIME));
                cursor.close();
			}
			
			if( mLastMillsTime == 0 && !mLastSelectedDay.equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())))){
				long count = new Utils().compareDays(this, mLastSelectedDay);
				mLastMillsTime =  System.currentTimeMillis() - SPACE_IN_TIME_24H*count;
			}
			 
			db.close();

		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private void setActionbar() {
		if( getActionBar() != null ){
			String StrTemp;
			String strCountDay = new Utils().countDays(this, mLastSelectedDay);
			
			if( strCountDay.equals(mLastSelectedDay))
				StrTemp = strCountDay;
			else
				StrTemp = strCountDay + " ( "+mLastSelectedDay+" )";
			
			getActionBar().setTitle(StrTemp);
			getActionBar().setDisplayShowCustomEnabled(false);
			getActionBar().setDisplayShowTitleEnabled(true);
			getActionBar().setDisplayHomeAsUpEnabled(true);
			
			int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
			TextView abTitle = (TextView) findViewById(titleId);
			abTitle.setTextColor(getResources().getColor(R.color.data_ab_title));
		}
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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

		if( ((RadioButton)fragmentE.getView().findViewById(R.id.rBtn_Eating_mm)).isChecked() )
			strMemo = getResources().getString(R.string.mothersmilk);
		else if( ((RadioButton)fragmentE.getView().findViewById(R.id.rBtn_Eating_mp)).isChecked() )
			strMemo = getResources().getString(R.string.milkpowder);
		else if( ((RadioButton)fragmentE.getView().findViewById(R.id.rBtn_Eating_bf)).isChecked() )
			strMemo = getResources().getString(R.string.babyfood);

		LinearLayout linearLayout_radio = (LinearLayout)fragmentE.getView().findViewById(R.id.linear_Eating_mm_radio);  
		RelativeLayout linearLayout_volume = (RelativeLayout)fragmentE.getView().findViewById(R.id.Eating_volume_layout); 

		if( linearLayout_radio.getVisibility() == View.VISIBLE )
		{
			if( ((RadioButton)fragmentE.getView().findViewById(R.id.rBtn_Eating_direct)).isChecked() )
				strMemo = strMemo + SEPERATOR + getResources().getString(R.string.eating_type_direct);
			else if( ((RadioButton)fragmentE.getView().findViewById(R.id.rBtn_Eating_bottle)).isChecked() )
				strMemo = strMemo + SEPERATOR + getResources().getString(R.string.eating_type_bottle);
		}

		if( linearLayout_volume.getVisibility() == View.VISIBLE )
		{
			EditText mEditeText_volume = (EditText) fragmentE.getView().findViewById(R.id.editText_Eating_volume);

			String measure = new Utils().getMeasureFromPref(this);
			
			if( measure.equals(getString(R.string.measure_ml)) ){
				float value = Float.parseFloat( mEditeText_volume.getText().toString() );
				strMemo = strMemo + SEPERATOR + (int)value + measure;
			}
			else{
				float value = Float.parseFloat( mEditeText_volume.getText().toString() );
				strMemo = strMemo + SEPERATOR + value +" "+ measure;
			}
		}

        insertDataToDB(fragmentE, Dbinfo.DB_TYPE_EAT, strMemo + " / ");
	}


	private void getPlayingData() {
		Fragment_Playing fragmentP = (Fragment_Playing) mSectionsPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());

		String strMemo = "";
		String SEPERATOR = " / ";

		if( ((CheckBox)fragmentP.getView().findViewById(R.id.cBox_Playing_mom)).isChecked() )
			strMemo = getResources().getString(R.string.mom) + SEPERATOR;
		if( ((CheckBox)fragmentP.getView().findViewById(R.id.cBox_Playing_daddy)).isChecked() )
			strMemo = strMemo + getResources().getString(R.string.daddy) + SEPERATOR;
		if( ((CheckBox)fragmentP.getView().findViewById(R.id.cBox_Playing_toy)).isChecked() )
			strMemo = strMemo + getResources().getString(R.string.toy) + SEPERATOR;

        insertDataToDB(fragmentP, Dbinfo.DB_TYPE_PLAY, strMemo);
	}

	private void getSleepingData() {
		Fragment_Sleeping fragmentS = (Fragment_Sleeping) mSectionsPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());

		String strMemo = "";

		if( ((RadioButton)fragmentS.getView().findViewById(R.id.rBtn_Sleeping_nap)).isChecked() )
			strMemo = strMemo + getResources().getString(R.string.nap);
		else if( ((RadioButton)fragmentS.getView().findViewById(R.id.rBtn_Sleeping_night)).isChecked() )
			strMemo = strMemo + getResources().getString(R.string.night_sleep);

        insertDataToDB(fragmentS, Dbinfo.DB_TYPE_SLEEP, strMemo + " / ");
	}

	private void getEtcData() {
		Fragment_Etc fragmentE = (Fragment_Etc) mSectionsPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
		
		String strMemo = "";
		String SEPERATOR = " / ";

		if( ((CheckBox)fragmentE.getView().findViewById(R.id.cBox_Etc_bath)).isChecked() )
			strMemo = getResources().getString(R.string.bath) + SEPERATOR;
		if( ((CheckBox)fragmentE.getView().findViewById(R.id.cBox_Etc_babypoo)).isChecked() )
			strMemo = strMemo + getResources().getString(R.string.babypoo) + SEPERATOR;
		if( ((CheckBox)fragmentE.getView().findViewById(R.id.cBox_Etc_etc)).isChecked() )
			strMemo = strMemo + getResources().getString(R.string.etc) + SEPERATOR;

        insertDataToDB(fragmentE, Dbinfo.DB_TYPE_ETC, strMemo);
	}

	public void insertDataToDB(Fragment fm, String type, String memo){

        TextView mTextView_stime = (TextView)fm.getView().findViewById(R.id.txtView_stime);
        TextView mTextView_etime = (TextView)fm.getView().findViewById(R.id.txtView_etime);

        long stime = Long.parseLong( mTextView_stime.getContentDescription().toString() );
        long etime = Long.parseLong( mTextView_etime.getContentDescription().toString() );

        memo =  memo + new SimpleDateFormat("HH:mm").format(stime) + " - " + new SimpleDateFormat("HH:mm").format(etime);

		EditText mEditText_Eating_memo = (EditText)fm.getView().findViewById(R.id.editText_Memo);
		if( mEditText_Eating_memo.getText().length() > 0)
			memo =  memo +"\n"+ mEditText_Eating_memo.getText();

		String strEtime = new SimpleDateFormat("yyyy-MM-dd").format(new Date(etime));

		try{
			BabyTimeDbOpenHelper dbhelper = new BabyTimeDbOpenHelper(this);
			SQLiteDatabase db = dbhelper.getWritableDatabase();
			ContentValues contentValues = new ContentValues();

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
