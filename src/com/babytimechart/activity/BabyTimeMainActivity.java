package com.babytimechart.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.babytimechart.db.BabyTimeDbOpenHelper;
import com.babytimechart.db.Dbinfo;
import com.babytimechart.fragment.Fragment_Chart;
import com.babytimechart.ui.BabyTimeSpinnerAdapter;
import com.babytimechart.ui.HeightWrappingViewPager;
import com.babytimechart.utils.Utils;
import com.ryutskr.babytimechart.R;


public class BabyTimeMainActivity extends Activity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private HeightWrappingViewPager mViewPager;
    private Spinner mSpinnerToday = null;
    private Spinner mSpinnerOtherDay = null;
    private TextView mTextViewBabyName = null;
    private Context mContext = null;
    private String mLastSelectedToday = "";
    private String mLastSelectedOtherday = "";
    private LinearLayout mDotLinearLayout;
    
    
//    private InterstitialAd mInterstitial;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        RelativeLayout main_layout = (RelativeLayout)findViewById(R.id.main_layout);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        mViewPager = (HeightWrappingViewPager) findViewById(R.id.pagermain);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);

        mTextViewBabyName = (TextView)getActionBar().getCustomView().findViewById(R.id.actionbar_Babyname);

        mSpinnerToday = (Spinner) getActionBar().getCustomView().findViewById(R.id.actionbar_spinner_Today);
        mSpinnerOtherDay = (Spinner) getActionBar().getCustomView().findViewById(R.id.actionbar_spinner_Otherday);
        mSpinnerToday.setOnItemSelectedListener(mOnItemSelectedListener);
        mSpinnerOtherDay.setOnItemSelectedListener(mOnItemSelectedListener);

        mDotLinearLayout = (LinearLayout) findViewById(R.id.dotindicater);
        addDotIndicator();

        Utils utils = new Utils();
        utils.getColorFromPref(this);
        utils.addBanner(this, main_layout);
        setSpinnerData();
    }

    private void addDotIndicator() {

        for(int i = 0; i <mSectionsPagerAdapter.getCount(); i++){
            ImageView imageView = new ImageView(this);
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
            params.leftMargin = (int) getResources().getDimension(R.dimen.activity_vertical_margin_qhalf);
            imageView.setLayoutParams(params);
            if( i == mViewPager.getCurrentItem() )
            	imageView.setBackgroundResource(R.drawable.dot_sel);
            else
            	imageView.setBackgroundResource(R.drawable.dot_normal);
            mDotLinearLayout.addView(imageView);
        }
    }

    AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Fragment_Chart fragmentC = (Fragment_Chart) mSectionsPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
            if( adapterView.equals(mSpinnerToday)){
                mLastSelectedToday = adapterView.getSelectedItem().toString();
                fragmentC.changeChartDate(0, mLastSelectedToday);

                if( mLastSelectedToday.equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()))))
                    fragmentC.setEnableBtn(true);
                else
                    fragmentC.setEnableBtn(false);

            }else if(adapterView.equals(mSpinnerOtherDay)){
                mLastSelectedOtherday = adapterView.getSelectedItem().toString();
                fragmentC.changeChartDate(1, mLastSelectedOtherday);
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
            Fragment_Chart fragmentC = (Fragment_Chart) mSectionsPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());

            for( int k =0; k <mDotLinearLayout.getChildCount(); k++){
            	if( k == i)
            		mDotLinearLayout.getChildAt(k).setBackgroundResource(R.drawable.dot_sel);
            	else
            		mDotLinearLayout.getChildAt(k).setBackgroundResource(R.drawable.dot_normal);
            }
            
            if( i == 0 ){
            	mSpinnerToday.setSelection(0);
                mSpinnerOtherDay.setVisibility(View.GONE);
                mTextViewBabyName.setVisibility(View.VISIBLE);
                fragmentC.changeChartDate(0, mLastSelectedToday);

                if( mLastSelectedToday.equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()))))
                    fragmentC.setEnableBtn(true);
                else
                    fragmentC.setEnableBtn(false);
            }else if( i == 1 ){
                mTextViewBabyName.setVisibility(View.GONE);
                mSpinnerOtherDay.setVisibility(View.VISIBLE);
                fragmentC.addChart(1, mLastSelectedOtherday);
                fragmentC.changeChartDate(0, mLastSelectedToday);
            }

            fragmentC.setLegendColor();
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }
    };
    public void setSpinnerData(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                BabyTimeSpinnerAdapter adapter = new BabyTimeSpinnerAdapter(getApplicationContext());
                try{
                    BabyTimeDbOpenHelper dbOpenHelper = new BabyTimeDbOpenHelper(mContext);
                    SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

                    Cursor cursor = db.query(Dbinfo.DB_TABLE_NAME, new String[]{Dbinfo.DB_DATE,Dbinfo.DB_E_TIME},
                            null, null, Dbinfo.DB_DATE, null, Dbinfo.DB_DATE + " DESC");

                    while (cursor != null && cursor.moveToNext()) {
                        if( adapter.getCount() == 0 && !cursor.getString(cursor.getColumnIndex(Dbinfo.DB_DATE))
                                .equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()))) )
                            adapter.addItem(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
                        adapter.addItem(cursor.getString(cursor.getColumnIndex(Dbinfo.DB_DATE)));
                    }

                    cursor.close();
                    db.close();

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if( adapter.getCount() == 0 )
                        adapter.addItem(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
                }

                mSpinnerToday.setAdapter(adapter);
                mSpinnerOtherDay.setAdapter(adapter);
                mTextViewBabyName.setText(new Utils().getBabyName(getApplicationContext()));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, BabyTimeSetting.class);
            startActivityForResult(intent, 10);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // color Change
        if( requestCode == 10 && resultCode == RESULT_OK){
            Fragment_Chart fg = (Fragment_Chart)mSectionsPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
            fg.refreshChart();
            fg.setLegendColor();

            if( data != null && data.getAction().equals("DATA_CHANGE"))
                setSpinnerData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mInterstitial = new Utils().addInterstitialAd(this);
//
//        if (mInterstitial.isLoaded()) {
//        	mInterstitial.show();
//          }
    }



    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment_Chart fm = Fragment_Chart.newInstance(position + 1);
            return fm;
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
}
