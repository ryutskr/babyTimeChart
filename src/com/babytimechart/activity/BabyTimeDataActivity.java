package com.babytimechart.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.activity.babytimechart.R;
import com.babytimechart.db.BabyTimeDbOpenHelper;
import com.babytimechart.db.Dbinfo;
import com.babytimechart.fragment.Fragment_Etc;
import com.babytimechart.fragment.Fragment_Feeding;
import com.babytimechart.fragment.Fragment_Playing;
import com.babytimechart.fragment.Fragment_Sleeping;
import com.babytimechart.ui.HeightWrappingViewPager;
import com.babytimechart.ui.SlidingTabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class BabyTimeDataActivity extends Activity{

	private HeightWrappingViewPager mViewPager = null;
	private SectionsPagerAdapter mSectionsPagerAdapter = null;
	private SlidingTabLayout mSlidingTabLayout = null;
	private List<SamplePagerItem> mTabs = new ArrayList<SamplePagerItem>();
	private Button mBtnSave = null;
	private Button mBtnCancel = null;


	static class SamplePagerItem {
		private final CharSequence mTitle;
		private final int mIndicatorColor;
		private final int mDividerColor;

		SamplePagerItem(CharSequence title, int indicatorColor, int dividerColor) {
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
		Log.i("babytime", getComponentName() + "  / onCreate() ");

		addTabs();

		mBtnSave = (Button)findViewById(R.id.btn_Activity_Data_Save);
		mBtnCancel = (Button)findViewById(R.id.btn_Activity_Data_Cancel);
		mBtnSave.setOnClickListener(mOnClickListener);
		mBtnCancel.setOnClickListener(mOnClickListener);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		mViewPager = (HeightWrappingViewPager) findViewById(R.id.viewPager_Activity_Data);
		mViewPager.setAdapter(mSectionsPagerAdapter);

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

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		Log.i("babytime", getComponentName() + "  / onDestroy() ");
		super.onDestroy();
	}

	public void addFragmentDataToDB()
	{
		int viewPagerIndex = mViewPager.getCurrentItem();
		switch (viewPagerIndex){
		case 0:
			getFeedingData(viewPagerIndex);
			return;
		case 1:
			Fragment_Playing fragment_playing = (Fragment_Playing) mSectionsPagerAdapter.instantiateItem(mViewPager,viewPagerIndex);
		case 2:
			Fragment_Sleeping fragment_sleeping = (Fragment_Sleeping) mSectionsPagerAdapter.instantiateItem(mViewPager,viewPagerIndex);
		case 3:
			Fragment_Etc fragment_etc = (Fragment_Etc) mSectionsPagerAdapter.instantiateItem(mViewPager,viewPagerIndex);
		}

	}

	private void getFeedingData(int index) {

		Fragment_Feeding fragment_feeding = (Fragment_Feeding) mSectionsPagerAdapter.instantiateItem(mViewPager, index);

		String strMemo = "";
		String SEPERATOR = " / ";

		if( ((ToggleButton)fragment_feeding.getView().findViewById(R.id.tBtn_Feeding_mm)).isChecked() )
			strMemo = getResources().getString(R.string.mothersmilk);
		else if( ((ToggleButton)fragment_feeding.getView().findViewById(R.id.tBtn_Feeding_mp)).isChecked() )
			strMemo = getResources().getString(R.string.milkpowder);
		else if( ((ToggleButton)fragment_feeding.getView().findViewById(R.id.tBtn_Feeding_bf)).isChecked() )
			strMemo = getResources().getString(R.string.babyfood);
		else if( ((ToggleButton)fragment_feeding.getView().findViewById(R.id.toggleButton4)).isChecked() )
			strMemo = getResources().getString(R.string.babypoop);

		LinearLayout linearLayout_radio = (LinearLayout)fragment_feeding.getView().findViewById(R.id.linear_Feeding_mm_radio);  
		LinearLayout linearLayout_volume = (LinearLayout)fragment_feeding.getView().findViewById(R.id.linear_Feeding_volume); 

		if( linearLayout_radio.getVisibility() == View.VISIBLE )
		{
			if( ((RadioButton)fragment_feeding.getView().findViewById(R.id.rBtn_Feeding_direct)).isChecked() )
				strMemo = strMemo + SEPERATOR + getResources().getString(R.string.feeding_type_direct);
			else if( ((RadioButton)fragment_feeding.getView().findViewById(R.id.rBtn_Feeding_bottle)).isChecked() )
				strMemo = strMemo + SEPERATOR + getResources().getString(R.string.feeding_type_bottle);
		}

		if( linearLayout_volume.getVisibility() == View.VISIBLE )
		{
			EditText mEditeText_ml = (EditText) fragment_feeding.getView().findViewById(R.id.editText_Feeding_ml);

			if( mEditeText_ml.getText().toString().contains("ml") )
				strMemo = strMemo + SEPERATOR + mEditeText_ml.getText();
			else
				strMemo = strMemo + SEPERATOR + mEditeText_ml.getText() + "ml";
		}

		TextView mTextView_stime = (TextView)fragment_feeding.getView().findViewById(R.id.txtView_Feeding_stime);
		TextView mTextView_etime = (TextView)fragment_feeding.getView().findViewById(R.id.txtView_Feeding_etime);

		EditText mEditeText_feeding_memo = (EditText)fragment_feeding.getView().findViewById(R.id.editText_Feeding_Memo);
		strMemo =  strMemo +"\n"+ mEditeText_feeding_memo.getText();

		long stime = Long.parseLong( mTextView_stime.getContentDescription().toString() );
		long etime = Long.parseLong( mTextView_etime.getContentDescription().toString() );

		SimpleDateFormat calcDateformat = new SimpleDateFormat("dd");
		int iStime = Integer.parseInt( calcDateformat.format(new Date(stime)) );
		int iEtime = Integer.parseInt( calcDateformat.format(new Date(etime)) );

		SimpleDateFormat insertDateformat = new SimpleDateFormat("yyyy-MM-dd");
		String strStime = insertDateformat.format(new Date(stime));
		String strEtime = insertDateformat.format(new Date(etime));

		BabyTimeDbOpenHelper dbhelper = new BabyTimeDbOpenHelper(this);
		SQLiteDatabase db = dbhelper.getWritableDatabase();

		if( (iEtime - iStime) > 0 )
		{
			ContentValues contentValues = new ContentValues();
			contentValues.put(Dbinfo.DB_TYPE, Dbinfo.DB_TYPE_EAT );
			contentValues.put(Dbinfo.DB_DATE, strStime );
			contentValues.put(Dbinfo.DB_S_TIME, mTextView_stime.getText().toString() );
			contentValues.put(Dbinfo.DB_E_TIME, "0");
			contentValues.put(Dbinfo.DB_MEMO, strMemo );

			db.insert(Dbinfo.DB_TABLE_NAME, null, contentValues);

			contentValues.clear();
			contentValues.put(Dbinfo.DB_TYPE, Dbinfo.DB_TYPE_EAT );
			contentValues.put(Dbinfo.DB_DATE, strEtime );
			contentValues.put(Dbinfo.DB_S_TIME, "0" );
			contentValues.put(Dbinfo.DB_E_TIME, mTextView_etime.getText().toString() );
			contentValues.put(Dbinfo.DB_MEMO, strMemo );

			db.insert(Dbinfo.DB_TABLE_NAME, null, contentValues);
		}else{
			ContentValues contentValues = new ContentValues();
			contentValues.put(Dbinfo.DB_TYPE, Dbinfo.DB_TYPE_EAT );
			contentValues.put(Dbinfo.DB_DATE, strStime );
			contentValues.put(Dbinfo.DB_S_TIME, mTextView_stime.getText().toString() );
			contentValues.put(Dbinfo.DB_E_TIME, mTextView_etime.getText().toString());
			contentValues.put(Dbinfo.DB_MEMO, strMemo );
			db.insert(Dbinfo.DB_TABLE_NAME, null, contentValues);
		}

		db.close();
	}

	public int calcDate(String stime_dec, String etime_dec){
		return 0;
	}
	public void addTabs(){
		mTabs.add(new SamplePagerItem(
				getString(R.string.tab_1), // Title
				Color.BLUE, // Indicator color
				Color.GRAY // Divider color
				));

		mTabs.add(new SamplePagerItem(
				getString(R.string.tab_2), // Title
				Color.RED, // Indicator color
				Color.GRAY // Divider color
				));

		mTabs.add(new SamplePagerItem(
				getString(R.string.tab_3), // Title
				Color.YELLOW, // Indicator color
				Color.GRAY // Divider color
				));

		mTabs.add(new SamplePagerItem(
				getString(R.string.tab_4), // Title
				Color.GREEN, // Indicator color
				Color.GRAY // Divider color
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
				return Fragment_Feeding.newInstance(position + 1);
			case 1:
				return Fragment_Playing.newInstance(position + 1);
			case 2:
				return Fragment_Sleeping.newInstance(position + 1);
			case 3:
				return Fragment_Etc.newInstance(position + 1);
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
