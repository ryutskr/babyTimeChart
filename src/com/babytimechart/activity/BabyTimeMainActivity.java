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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.babytimechart.db.BabyTimeDbOpenHelper;
import com.babytimechart.db.Dbinfo;
import com.babytimechart.fragment.Fragment_Chart_Pie;
import com.babytimechart.ui.BabyTimeSpinnerAdapter;
import com.babytimechart.utils.Utils;
import com.ryutskr.babytimechart.R;


public class BabyTimeMainActivity extends Activity {

    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;
    Spinner mSpinnerToday = null;
    Spinner mSpinnerOtherDay = null;
    TextView mTextViewBabyName = null;
    Context mContext = null;
    ArrayList<ImageView> mDotIndicator = new ArrayList<ImageView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pagermain);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);

        mTextViewBabyName = (TextView)getActionBar().getCustomView().findViewById(R.id.actionbar_Babyname);
        
        mSpinnerToday = (Spinner) getActionBar().getCustomView().findViewById(R.id.actionbar_spinner_Today);
        mSpinnerOtherDay = (Spinner) getActionBar().getCustomView().findViewById(R.id.actionbar_spinner_Otherday);
        mSpinnerToday.setOnItemSelectedListener(mOnItemSelectedListener);

        addDotIndicator();

        new Utils().getColorFromPref(this);
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

    void setSpinnerData(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                BabyTimeSpinnerAdapter adapter = new BabyTimeSpinnerAdapter(getApplicationContext());
                try{
                    BabyTimeDbOpenHelper dbOpenHelper = new BabyTimeDbOpenHelper(mContext);
                    SQLiteDatabase db = dbOpenHelper.getReadableDatabase();

                    Cursor cursor = db.query(Dbinfo.DB_TABLE_NAME, new String[]{Dbinfo.DB_DATE,Dbinfo.DB_E_TIME},
                            null, null, Dbinfo.DB_DATE, null, Dbinfo.DB_DATE + " DESC");

                    while (cursor!= null && cursor.moveToNext()){
                        adapter.addItem(cursor.getString(cursor.getColumnIndex(Dbinfo.DB_DATE)));
                    }
                    cursor.close();
                    db.close();

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if( adapter.getCount() == 0 )
                        adapter.addItem(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));

                    mSpinnerToday.setAdapter(adapter);
                    mTextViewBabyName.setText(new Utils().getBabyName(getApplicationContext()));
                }
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
            Fragment_Chart_Pie fg = (Fragment_Chart_Pie)mSectionsPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
            fg.drawChart(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
        }
    }


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
}
