package com.babytimechart.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.activity.babytimechart.R;
import com.babytimechart.db.BabyTimeDbOpenHelper;
import com.babytimechart.db.Dbinfo;
import com.babytimechart.fragment.Fragment_Chart_Pie;
import com.babytimechart.ui.RoundChartView;

public class BabyTimeMainActivity extends Activity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pagermain);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		getActionBar().setCustomView(R.layout.activity_main_actionbar);
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		
		getActionBar().getCustomView().findViewById(R.id.activity_spinner2).setVisibility(View.GONE);
		
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pie, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			fakeDBData();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm){
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return Fragment_Chart_Pie.newInstance(position + 1);
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.x).toUpperCase(l);
			case 1:
				return getString(R.string.y).toUpperCase(l);
				//			case 2:
				//				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}
	
	public void fakeDBData()
	{
		long time = System.currentTimeMillis();

		SimpleDateFormat insertDateformat1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat insertDateformat2 = new SimpleDateFormat("dd");
		String strToday1 = insertDateformat1.format(new Date(time));
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
			contentValues.clear();
			contentValues.put(Dbinfo.DB_TYPE, arrType[i] );
			contentValues.put(Dbinfo.DB_DATE, strToday1 );
			contentValues.put(Dbinfo.DB_S_TIME, arrtime[i] );
			contentValues.put(Dbinfo.DB_E_TIME, arrtime[i+1] );
			contentValues.put(Dbinfo.DB_MEMO, "" + i  );
			db.insert(Dbinfo.DB_TABLE_NAME, null, contentValues);
		}
		db.close();
		Fragment_Chart_Pie fg = (Fragment_Chart_Pie)mSectionsPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
		((RoundChartView)fg.getView().findViewById(R.id.roundchartview)).drawChart();
	}
	
}
