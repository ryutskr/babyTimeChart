package com.babytimechart.utils;

import static com.babytimechart.db.Dbinfo.DB_DATE;
import static com.babytimechart.db.Dbinfo.DB_E_TIME;
import static com.babytimechart.db.Dbinfo.DB_MEMO;
import static com.babytimechart.db.Dbinfo.DB_S_TIME;
import static com.babytimechart.db.Dbinfo.DB_TABLE_NAME;
import static com.babytimechart.db.Dbinfo.DB_TYPE;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.babytimechart.db.BabyTimeDbOpenHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.ryutskr.babytimechart.R;

public class Utils{

    public static int mEatColor;
    public static int mPlayColor;
    public static int mSleepColor;
    public static int mEtcColor;
    public static long mLastTime;

    // ColorPicker Colors
    public int[] getColorsForPicker(Context context){
        int[] mColorChoices=null;
        String[] color_array = context.getResources().getStringArray(R.array.color_picker_values);

        if (color_array!=null && color_array.length>0) {
            mColorChoices = new int[color_array.length];
            for (int i = 0; i < color_array.length; i++) {
                mColorChoices[i] = Color.parseColor(color_array[i]);
            }
        }

        return mColorChoices;
    }

    public void setColorsToPref(Context context){
        SharedPreferences pref = context.getSharedPreferences("Setting", Activity.MODE_PRIVATE);
        pref.edit()
                .putInt("eatcolor", mEatColor)
                .putInt("playcolor", mPlayColor)
                .putInt("sleepcolor", mSleepColor)
                .putInt("etccolor", mEtcColor).apply();
    }

    public void getColorFromPref(Context context){
        int [] mColorChoices = getColorsForPicker(context);

        if(  mColorChoices.length > 0 ){
            SharedPreferences pref = context.getSharedPreferences("Setting", Activity.MODE_PRIVATE);
            mEatColor = pref.getInt("eatcolor", mColorChoices[0]);
            mPlayColor = pref.getInt("playcolor", mColorChoices[1]);
            mSleepColor = pref.getInt("sleepcolor", mColorChoices[2]);
            mEtcColor = pref.getInt("etccolor", mColorChoices[3]);
        }
    }

    public void deletePreference(Context context){
        SharedPreferences pref = context.getSharedPreferences("Setting", Activity.MODE_PRIVATE);
        pref.edit().clear().apply();
    }

    public void setChangeColor(int id, int color){

        switch(id){
            case 100:
                mEatColor = color;
                break;
            case 101:
                mPlayColor = color;
                break;
            case 102:
                mSleepColor = color;
                break;
            case 103:
                mEtcColor = color;
                break;
        }
    }

    public void makeToast(Context context, String string){
        Toast toast = Toast.makeText(context, string, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, (int)context.getResources().getDimension(R.dimen.toast_yoffset));
        toast.show();
    }

    public void clearUtilsValues(Context context){

        int [] mColorChoices = getColorsForPicker(context);

        if(  mColorChoices.length > 0 ){
            mEatColor =  mColorChoices[0];
            mPlayColor = mColorChoices[1];
            mSleepColor = mColorChoices[2];
            mEtcColor = mColorChoices[3];
        }

        mLastTime = 0;
        deletePreference(context);
    }

    public String countDays(Context context, String date) {
        long count = 0;
        SharedPreferences pref = context.getSharedPreferences("Setting", Activity.MODE_PRIVATE);
        String strBirthday = pref.getString("birthday", "No Data");

        if( strBirthday.equals("No Data") )
            return  date;

        try {
            Calendar inputDate = Calendar.getInstance();
            Calendar birthday = Calendar.getInstance();

            inputDate.set(Integer.parseInt(date.substring(0,4)), Integer.parseInt(date.substring(5,7))
                    , Integer.parseInt(date.substring(8,10)));

            birthday.set(Integer.parseInt(strBirthday.substring(0,4)), Integer.parseInt(strBirthday.substring(5,7))
                    , Integer.parseInt(strBirthday.substring(8,10)));

            long inputdayCount = inputDate.getTimeInMillis() / (24*60*60*1000);
            long birthdayCount = birthday.getTimeInMillis() / (24*60*60*1000);

            count = Math.abs( inputdayCount - birthdayCount ) + 1;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count + context.getString(R.string.count_day);
    }
    
    

    public String getBabyName(Context context) {
        SharedPreferences pref = context.getSharedPreferences("Setting", Activity.MODE_PRIVATE);
        String strBabyName = pref.getString("babyname", "");

        if( strBabyName.length() < 1)
            return "";
        else
            return strBabyName + context.getString(R.string.action_text1);
    }

    public void setBabyProfileToPref(Context context, String name, String birthday){
        SharedPreferences pref = context.getSharedPreferences("Setting", Activity.MODE_PRIVATE);
        pref.edit()
                .putString("babyname", name)
                .putString("birthday", birthday).apply();
    }

    public String getBabyNameFromPref(Context context){
        SharedPreferences pref = context.getSharedPreferences("Setting", Activity.MODE_PRIVATE);
        return pref.getString("babyname", "");
    }

    public String getBabyBirthDayFromPref(Context context){
        SharedPreferences pref = context.getSharedPreferences("Setting", Activity.MODE_PRIVATE);
        return pref.getString("birthday", "");
    }

    public void addBanner(Context context, ViewGroup viewGroup) {
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceID = telephony.getDeviceId();

        AdView adView = new AdView(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        adView.setLayoutParams(layoutParams);

        adView.setAdUnitId("ca-app-pub-5151751327714064/5326104034");
        adView.setAdSize(AdSize.BANNER);

        viewGroup.addView(adView);
        AdRequest request = new AdRequest.Builder().addTestDevice(deviceID).build();
        adView.loadAd(request);
    }

    public InterstitialAd addInterstitialAd(Context context){
    	// 삽입 광고를 만듭니다.
    	InterstitialAd interstitial = new InterstitialAd(context);
        interstitial.setAdUnitId("ca-app-pub-5151751327714064/1814234437");

        // 광고 요청을 만듭니다.
        AdRequest adRequest = new AdRequest.Builder().build();

        // 삽입 광고 로드를 시작합니다.
        interstitial.loadAd(adRequest);
        
        return interstitial;
    }
    
    public void fakeDBData(Context context)
    {
        long time = System.currentTimeMillis();

        SimpleDateFormat insertDateformat1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat insertDateformat2 = new SimpleDateFormat("dd");
        String strToday1 = "";
        String strToday2 = insertDateformat2.format(new Date(time));

        BabyTimeDbOpenHelper dbhelper = new BabyTimeDbOpenHelper(context);
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
        for(int i=5; i>0; i--)
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














