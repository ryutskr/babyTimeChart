package com.babytimechart.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import com.activity.babytimechart.R;

public class Utils{

	public static int mEatColor;
	public static int mPlayColor;
	public static int mSleepColor;
	public static int mEtcColor;
	
	// ColorPicker Colors
	public static int[] getColorsForPicker(Context context){
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
	
	public static void setColorsToPref(Context context){
		SharedPreferences pref = context.getSharedPreferences("Setting", Activity.MODE_PRIVATE);
		pref.edit()
		.putInt("eatcolor", mEatColor)
		.putInt("playcolor", mPlayColor)
		.putInt("sleepcolor", mSleepColor)
		.putInt("etccolor", mEtcColor).apply();
	}
	
	public static void getColorFromPref(Context context){
		SharedPreferences pref = context.getSharedPreferences("Setting", Activity.MODE_PRIVATE);
		mEatColor = pref.getInt("eatcolor", context.getResources().getColor(R.color.eat_default));
		mPlayColor = pref.getInt("playcolor", context.getResources().getColor(R.color.play_default));
		mSleepColor = pref.getInt("sleepcolor", context.getResources().getColor(R.color.sleep_default));
		mEtcColor = pref.getInt("etccolor", context.getResources().getColor(R.color.etc_default));
	}
	
	public static void setChangeColor(int id, int color){
		
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
}
















