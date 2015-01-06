package com.babytimechart.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.util.Log;

import com.babytimechart.db.Dbinfo;
import com.babytimechart.utils.Utils;

public class ChartInfomation {

    private static final int 	ANGLE_PER_HOUR = 15;
    private static final float  ANGLE_PER_TENMIN = (float)15/6 ;
    private static final float  STANDARD_ANGLE = 6 ;

    private static final int ARC_ALPHA = 120; 	// 0 ~ 255


    private Context mContext = null;
    private Cursor mCursor = null;
    private ArrayList<Data> mListData = new ArrayList<Data>();

    public class Data
    {
        int mId;
        String mType;
        long mStime;
        long mEtime;
        String mMemo;

        Paint mPaint;
        float mStartAngle;
        float mSweepAngle;

        Paint getPaint(){ return mPaint; }
        float getStartAngle(){ return mStartAngle; }
        float getSweepAngle(){ return mSweepAngle; }
    }

    public ChartInfomation(Context context, Cursor cursor)
    {
        mContext = context;
        mCursor = cursor;

        getDataFromCursor();
    }

    public ArrayList<Data> getData(){ return mListData; }

    private void getDataFromCursor()
    {
        while( mCursor.moveToNext() ){
            Data data = new Data();
            data.mId    = mCursor.getInt(mCursor.getColumnIndex(Dbinfo.DB_ID));
            data.mType  = mCursor.getString(mCursor.getColumnIndex(Dbinfo.DB_TYPE));
            data.mStime = mCursor.getLong(mCursor.getColumnIndex(Dbinfo.DB_S_TIME));
            data.mEtime = mCursor.getLong(mCursor.getColumnIndex(Dbinfo.DB_E_TIME));
            data.mMemo  = mCursor.getString(mCursor.getColumnIndex(Dbinfo.DB_MEMO));

            mListData.add(data);
        }
        makeArcData();
//		LogData();
    }

    private void makeArcData()
    {
        for(Data data : mListData )
        {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setAlpha(ARC_ALPHA);
            paint.setStyle(Paint.Style.FILL);

            if( data.mType.equals(Dbinfo.DB_TYPE_EAT) )
                paint.setColor(Utils.mEatColor);
            else if( data.mType.equals(Dbinfo.DB_TYPE_PLAY) )
                paint.setColor(Utils.mPlayColor);
            else if( data.mType.equals(Dbinfo.DB_TYPE_SLEEP))
                paint.setColor(Utils.mSleepColor);
            else if( data.mType.equals(Dbinfo.DB_TYPE_ETC))
                paint.setColor(Utils.mEtcColor);

            data.mPaint = paint;

            data.mStartAngle = ((Integer.parseInt(new SimpleDateFormat("HH").format(new Date(data.mStime))) - STANDARD_ANGLE )* ANGLE_PER_HOUR) +
                    Math.round(Integer.parseInt(new SimpleDateFormat("mm").format(new Date(data.mStime)))/10) * ANGLE_PER_TENMIN;

            float fEndAngle = ((Integer.parseInt(new SimpleDateFormat("HH").format(new Date(data.mEtime))) - STANDARD_ANGLE )* ANGLE_PER_HOUR) +
                    Math.round(Integer.parseInt(new SimpleDateFormat("mm").format(new Date(data.mEtime)))/10) * ANGLE_PER_TENMIN;

            if( data.mStartAngle < 0 )
                data.mStartAngle = 360 + data.mStartAngle;

            if( fEndAngle < 0 )
                fEndAngle = 360 + fEndAngle;

            if( data.mStartAngle > fEndAngle )
                data.mSweepAngle = Math.abs( (360 + fEndAngle) - data.mStartAngle );
            else
                data.mSweepAngle = Math.abs( fEndAngle - data.mStartAngle );

        }
    }

    void LogData()
    {
        for(Data data : mListData )
        {
            Log.i("1111", "=============================");
            Log.i("1111", "TYPE: "+data.mType);
            Log.i("1111", "STIME: "+data.mStime + "  | " + new SimpleDateFormat("HH:mm").format(new Date(data.mStime)));
            Log.i("1111", "ETIME: " + data.mEtime + "  | " + new SimpleDateFormat("HH:mm").format(new Date(data.mEtime)));
            Log.i("1111", "MEMO: " + data.mMemo);
            Log.i("1111", "StartAngle: " + data.mStartAngle);
            Log.i("1111", "SweepAngle: " + data.mSweepAngle);
            Log.i("1111", "Paint: " + data.mPaint.getColor());
            Log.i("1111", "=============================");
        }
    }


}



















