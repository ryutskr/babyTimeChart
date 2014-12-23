package com.babytimechart.activity;

import static com.babytimechart.db.Dbinfo.DB_DATE;
import static com.babytimechart.db.Dbinfo.DB_E_TIME;
import static com.babytimechart.db.Dbinfo.DB_MEMO;
import static com.babytimechart.db.Dbinfo.DB_S_TIME;
import static com.babytimechart.db.Dbinfo.DB_TABLE_NAME;
import static com.babytimechart.db.Dbinfo.DB_TYPE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.activity.babytimechart.R;
import com.babytimechart.db.BabyTimeDbOpenHelper;
import com.babytimechart.fragment.Fragment_Chart_Pie;

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
	Spinner mSpinnerToday = null;
	Spinner mSpinnerOtherDay = null;
	Context mContext = null;
    ArrayList<ImageView> mDotIndicator = new ArrayList<ImageView>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pagermain);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(mOnPageChangeListener);

		mSpinnerToday = (Spinner) getActionBar().getCustomView().findViewById(R.id.activity_spinner_Today);
		mSpinnerOtherDay = (Spinner) getActionBar().getCustomView().findViewById(R.id.activity_spinner_Otherday);
		mSpinnerToday.setOnItemSelectedListener(mOnItemSelectedListener);

        addDotIndicator();
	}

    private void addDotIndicator() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.dotindicater);

        for(int i = 0; i <mSectionsPagerAdapter.getCount(); i++){
            ImageView imageView = new ImageView(this);
            if( i== 0)
                imageView.setBackgroundResource(R.drawable.gd_page_indicator_dot_selected);
            else
                imageView.setBackgroundResource(R.drawable.gd_page_indicator_dot_selected_normal);
            linearLayout.addView(imageView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mDotIndicator.add(imageView);
        }
    }

    void setSpinnerData(){
		new Handler().post(new Runnable() {

			@Override
			public void run() {
				ArrayList<String> retValue = new ArrayList<String>();

				BabyTimeDbOpenHelper dbOpenHelper = new BabyTimeDbOpenHelper(mContext);
				SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
				Cursor cursor = db.query(DB_TABLE_NAME, new String[]{DB_DATE}, null, null, DB_DATE, null, DB_DATE + " DESC");

				while (cursor.moveToNext()){
					retValue.add(cursor.getString(cursor.getColumnIndex(DB_DATE)));
				}
				cursor.close();
				db.close();

				if( retValue.size() == 0 )
					retValue.add(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, retValue);
				adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
				mSpinnerToday.setAdapter(adapter);
			}
		});
	}

	AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
			if( view.getParent().equals(mSpinnerToday)){
				Fragment_Chart_Pie fragmentC = (Fragment_Chart_Pie) mSectionsPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
				fragmentC.drawChart(adapterView.getSelectedItem().toString());
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapterView) {

		}
	};

	ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
		@Override
		public void onPageScrolled(int i, float v, int i2) {
		}

		@Override
		public void onPageSelected(int i) {
            for(int k=0; k<mDotIndicator.size();k++){
                if( k == i)
                    mDotIndicator.get(i).setBackgroundResource(R.drawable.gd_page_indicator_dot_selected);
                else
                    mDotIndicator.get(k).setBackgroundResource(R.drawable.gd_page_indicator_dot_selected_normal);
            }

			if( i == 0 ){
				mSpinnerOtherDay.setVisibility(View.GONE);
			}else if( i == 1 ){
				mSpinnerOtherDay.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onPageScrollStateChanged(int i) {
		}
	};


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setSpinnerData();
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
			Intent intent = new Intent(this, BabyTimeSetting.class);
			startActivity(intent);
//			fakeDBData();
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
			}
			return null;
		}
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
		Fragment_Chart_Pie fg = (Fragment_Chart_Pie)mSectionsPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
		fg.drawChart(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
	}

}
