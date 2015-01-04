package com.babytimechart.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Toast;

import com.activity.babytimechart.R;

import java.util.Calendar;

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

            return count + "일째";
    }

    public String getBabyName(Context context) {
        SharedPreferences pref = context.getSharedPreferences("Setting", Activity.MODE_PRIVATE);
        String strBabyName = pref.getString("babyname", "No Data");

        return "";
    }
}














