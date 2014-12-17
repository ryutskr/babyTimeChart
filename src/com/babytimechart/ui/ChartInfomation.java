package com.babytimechart.ui;

import java.util.ArrayList;

import com.activity.babytimechart.R.color;
import com.babytimechart.db.Dbinfo;

import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.RectF;

public class ChartInfomation {

	private Cursor mCursor = null;
	private int mWidth = 0;
	private ArrayList<Data> mListData = new ArrayList<ChartInfomation.Data>();

	public class Data
	{
		String mType;
		String mStime;
		String mEtime;
		String mMemo;
		
		Paint mPaint;
		int startAngle;
		int EndAngle;
	}
	
	public ChartInfomation(Cursor cursor, int width)
	{
		mCursor = cursor;
		mWidth = width;
		
		getDataFromCursor();
	}

	private void getDataFromCursor()
	{
		mCursor.moveToFirst();
		while( mCursor.moveToNext() )
		{
			Data data = new Data();
			
			data.mType  = mCursor.getString(mCursor.getColumnIndex(Dbinfo.DB_TYPE));
			data.mStime = mCursor.getString(mCursor.getColumnIndex(Dbinfo.DB_S_TIME));
			data.mEtime = mCursor.getString(mCursor.getColumnIndex(Dbinfo.DB_E_TIME));
			data.mMemo  = mCursor.getString(mCursor.getColumnIndex(Dbinfo.DB_MEMO));
			
			mListData.add(data);
		}
	}
	
	private void makeArcData()
	{
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(5.0f);
		
		for(Data data : mListData )
		{
			if( data.mType.equals(Dbinfo.DB_TYPE_EAT) )
				paint.setColor(color.blueviolet);
			else if( data.mType.equals(Dbinfo.DB_TYPE_PLAY) )
				paint.setColor(color.aqua);
			else if( data.mType.equals(Dbinfo.DB_TYPE_SLEEP))
				paint.setColor(color.chocolate);
			else if( data.mType.equals(Dbinfo.DB_TYPE_ETC))
				paint.setColor(color.crimson);
			
			data.mPaint = paint;
			
			
		}
	}
}



















