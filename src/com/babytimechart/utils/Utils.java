package com.babytimechart.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.activity.babytimechart.R;
import com.babytimechart.db.BabyTimeDbOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.babytimechart.db.Dbinfo.DB_DATE;
import static com.babytimechart.db.Dbinfo.DB_E_TIME;
import static com.babytimechart.db.Dbinfo.DB_MEMO;
import static com.babytimechart.db.Dbinfo.DB_S_TIME;
import static com.babytimechart.db.Dbinfo.DB_TABLE_NAME;
import static com.babytimechart.db.Dbinfo.DB_TYPE;

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
        SharedPreferences pref = context.getSharedPreferences("Setting", Activity.MODE_PRIVATE);
        mEatColor = pref.getInt("eatcolor", context.getResources().getColor(R.color.eat_default));
        mPlayColor = pref.getInt("playcolor", context.getResources().getColor(R.color.play_default));
        mSleepColor = pref.getInt("sleepcolor", context.getResources().getColor(R.color.sleep_default));
        mEtcColor = pref.getInt("etccolor", context.getResources().getColor(R.color.etc_default));
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
        mEatColor =  context.getResources().getColor(R.color.eat_default);
        mPlayColor = context.getResources().getColor(R.color.play_default);
        mSleepColor = context.getResources().getColor(R.color.sleep_default);
        mEtcColor = context.getResources().getColor(R.color.etc_default);
        mLastTime = 0;
        deletePreference(context);
    }

    public String countDays(Context context, String date) {
            long count = 0;
            SharedPreferences pref = context.getSharedPreferences("Setting", Activity.MODE_PRIVATE);
            String strBirthday = pref.getString("birthday", "No Data");

            if( strBirthday.equals("No Data") )
                return  date;

        Log.i("1111", "strBirthday : " + strBirthday);
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














