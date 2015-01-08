package com.babytimechart.ui;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.babytimechart.db.BabyTimeDbOpenHelper;
import com.babytimechart.db.Dbinfo;
import com.babytimechart.ui.DrawArcData.ArcData;
import com.babytimechart.utils.Utils;
import com.ryutskr.babytimechart.R;

import java.util.ArrayList;

public class RoundChartView extends View {

    private static final int DEFAULT_CIRCLE_LEFT_MARGIN = 20;
    private static final int DEFAULT_CIRCLE_TOP_MARGIN = 20;
    private static final int DEFAULT_CIRCLE_COLOR = Color.WHITE;

    private static final int CUSTOME_CIRCLE_COLOR = Color.BLACK;
    private static final int CUSTOME_CIRCLE_STROKE_WIDTH = 3; 	// dip
    private static final int CUSTOME_CENTER_CIRCLE_COLOR = Color.BLACK;
    private static final int CUSTOME_CENTER_CIRCLE_RADIUS = 15;
    private static final int CUSTOME_DOT_LINE_WIDTH = 1; 		// dip
    private static final int CUSTOME_DOT_LINE_INTERVALS = 5; 	// dip
    private static final int CUSTOME_DOT_LINE_ALPHA = 120; 	// 0 ~ 255

    private static final int SELECT_ARC_COLOR = Color.GREEN;
    private static final int SELECT_ARC_STROKE_WIDTH = 2; 	// dip
    private static final int ADD_RECT_DIVISION = 6;

    private Paint mDefaultPaint;
    private RectF mDefaultRect;
    private int mSelectArcId = 0;

    private DrawArcData mChartInfo = null;
    private ArrayList<ChartData> mChartDataArrayList = new ArrayList<ChartData>();

    public class ChartData{
        RectF mRect = null;
        DrawArcData mDrawArcData = null;
        String mDate = null;

        public ChartData (RectF rect, DrawArcData arcData, String date) {
            this.mRect = rect;
            this.mDrawArcData = arcData;
            this.mDate = date;
        }
    }

    public RoundChartView (Context context) {
        super(context);
    }
    public RoundChartView (Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public RoundChartView (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        init();
        for(ChartData data : mChartDataArrayList){
            if (data.mRect != null && data.mDrawArcData != null) {
                canvas.drawOval(data.mRect, mDefaultPaint);
                for( ArcData arcData : data.mDrawArcData.getData() ) {
                    canvas.drawArc(data.mRect, arcData.getStartAngle(),arcData.getSweepAngle(), true, arcData.getPaint());
                }
            }
        }
        customeCircle(canvas);
    }
    private void customeCircle(Canvas canvas)
    {
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        // Custome Stroke Draw
        Paint customePaint = new Paint();
        customePaint.setColor(CUSTOME_CIRCLE_COLOR);
        customePaint.setStyle(Paint.Style.STROKE);
        customePaint.setAntiAlias(true);
        customePaint.setStrokeWidth(metrics.density*CUSTOME_CIRCLE_STROKE_WIDTH);

        for(ChartData data : mChartDataArrayList)
            canvas.drawOval(data.mRect, customePaint);

        // Center Circle Draw
        RectF rect = new RectF();
        rect.set(mDefaultRect.centerX()-CUSTOME_CENTER_CIRCLE_RADIUS,mDefaultRect.centerY()-CUSTOME_CENTER_CIRCLE_RADIUS,
                mDefaultRect.centerX()+CUSTOME_CENTER_CIRCLE_RADIUS,mDefaultRect.centerY()+CUSTOME_CENTER_CIRCLE_RADIUS );

        customePaint.setColor(CUSTOME_CENTER_CIRCLE_COLOR);
        customePaint.setStyle(Paint.Style.FILL);
        canvas.drawOval(rect, customePaint);

        // Dot Line Draw
        customePaint.setColor(CUSTOME_CENTER_CIRCLE_COLOR);
        customePaint.setStyle(Paint.Style.STROKE);
        customePaint.setAlpha(CUSTOME_DOT_LINE_ALPHA);
        customePaint.setStrokeWidth(metrics.density * CUSTOME_DOT_LINE_WIDTH);
        customePaint.setPathEffect(new DashPathEffect(new float[] {metrics.density*CUSTOME_DOT_LINE_INTERVALS,
                metrics.density*CUSTOME_DOT_LINE_INTERVALS}, 0));
        Path path = new Path();
        // >
        path.moveTo(mDefaultRect.centerX(), mDefaultRect.centerY());
        path.lineTo(getWidth()-DEFAULT_CIRCLE_LEFT_MARGIN, mDefaultRect.centerY());
        canvas.drawPath(path, customePaint);
        // ^
        path.moveTo(mDefaultRect.centerX(), mDefaultRect.centerY());
        path.lineTo(mDefaultRect.centerX(), DEFAULT_CIRCLE_TOP_MARGIN);
        canvas.drawPath(path, customePaint);
        // <
        path.moveTo(mDefaultRect.centerX(), mDefaultRect.centerY());
        path.lineTo(DEFAULT_CIRCLE_LEFT_MARGIN, mDefaultRect.centerY());
        canvas.drawPath(path, customePaint);
        // v
        path.moveTo(mDefaultRect.centerX(), mDefaultRect.centerY());
        path.lineTo(mDefaultRect.centerX(), getWidth()-DEFAULT_CIRCLE_TOP_MARGIN);
        canvas.drawPath(path, customePaint);
    }

    private void selectArc(Canvas canvas) {

        for( ArcData data : mChartInfo.getData() ) {

            if( data.mId == mSelectArcId ) {

                DisplayMetrics metrics = getResources().getDisplayMetrics();
                Paint selectArcPaint = new Paint();
                selectArcPaint.setColor(SELECT_ARC_COLOR);
                selectArcPaint.setStyle(Paint.Style.STROKE);
                selectArcPaint.setAntiAlias(true);
                selectArcPaint.setStrokeWidth(metrics.density*SELECT_ARC_STROKE_WIDTH);

                RectF rect = new RectF();
                int width = getWidth()- (DEFAULT_CIRCLE_LEFT_MARGIN + CUSTOME_CIRCLE_STROKE_WIDTH);

                rect.set(DEFAULT_CIRCLE_LEFT_MARGIN + CUSTOME_CIRCLE_STROKE_WIDTH, DEFAULT_CIRCLE_TOP_MARGIN + CUSTOME_CIRCLE_STROKE_WIDTH,
                        width, width);
                canvas.drawArc(rect, data.getStartAngle(), data.getSweepAngle(), true, selectArcPaint);
            }
        }
    }
    private void init() {

        if( mChartDataArrayList.size() == 0) {
            mDefaultPaint = new Paint();
            mDefaultPaint.setColor(DEFAULT_CIRCLE_COLOR);
            mDefaultPaint.setAntiAlias(true);
            mDefaultPaint.setStyle(Paint.Style.FILL);
            mDefaultRect = new RectF();

            int width = getWidth() - (DEFAULT_CIRCLE_LEFT_MARGIN);
            mDefaultRect.set(DEFAULT_CIRCLE_LEFT_MARGIN, DEFAULT_CIRCLE_TOP_MARGIN, width, width);
            mChartDataArrayList.add(new ChartData(mDefaultRect, null, null));
        }

    }
    public void addChart(int chartIndex, String lastSelecteDate){
        if( mChartDataArrayList.size() == (chartIndex+1))
            return;

        RectF rect =  new RectF();
        rect.set(mChartDataArrayList.get(chartIndex-1).mRect.left+ mChartDataArrayList.get(chartIndex-1).mRect.width()/ADD_RECT_DIVISION,
                mChartDataArrayList.get(chartIndex-1).mRect.top + mChartDataArrayList.get(chartIndex-1).mRect.width()/ADD_RECT_DIVISION,
                mChartDataArrayList.get(chartIndex-1).mRect.right - mChartDataArrayList.get(chartIndex-1).mRect.width()/ADD_RECT_DIVISION,
                mChartDataArrayList.get(chartIndex-1).mRect.bottom- mChartDataArrayList.get(chartIndex-1).mRect.width()/ADD_RECT_DIVISION);
        mChartDataArrayList.add(new ChartData(rect, null, lastSelecteDate));

        makeChartDatas();
    }

    public void removeChart(int chartIndex){

        if( mChartDataArrayList.size() < (chartIndex+1))
            return;
        mChartDataArrayList.remove(chartIndex);
        invalidate();
    }

    public void changeChartDate(int chartIndex, String selecteDate ){
        init();

        if( (chartIndex+1) > mChartDataArrayList.size())
            return;

        mChartDataArrayList.get(chartIndex).mDate = selecteDate;
        makeChartDatas();
    }

    public void makeChartDatas(){
        try {
            BabyTimeDbOpenHelper dbhelper = new BabyTimeDbOpenHelper(getContext());
            SQLiteDatabase db = dbhelper.getReadableDatabase();

            String strSelection;
            Cursor cursor;

            for (int i=0; i<mChartDataArrayList.size();i++) {
                strSelection = "date ='" + mChartDataArrayList.get(i).mDate + "'";
                cursor = db.query(Dbinfo.DB_TABLE_NAME, null, strSelection, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    mChartDataArrayList.get(i).mDrawArcData = new DrawArcData(cursor);
                    cursor.close();
                } else
                    mChartDataArrayList.get(i).mDrawArcData = null;
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if (mChartDataArrayList.size() > 0 && mChartDataArrayList.get(0).mDrawArcData == null) {
                new Utils().makeToast(getContext(), getResources().getString(R.string.empty_data));
            }
            invalidate();
        }
    }

    public void drawChart(String selection){

        try {
            BabyTimeDbOpenHelper dbhelper = new BabyTimeDbOpenHelper(getContext());
            SQLiteDatabase db = dbhelper.getReadableDatabase();

            String strSelection;

            strSelection = "date ='"+ selection +"'";
            Cursor cursor = db.query(Dbinfo.DB_TABLE_NAME, null, strSelection, null, null, null, null);
            if( cursor != null && cursor.getCount() > 0)
            {
                mChartInfo = new DrawArcData(cursor);
                cursor.moveToLast();
                Utils.mLastTime = cursor.getLong(cursor.getColumnIndex(Dbinfo.DB_E_TIME));
                cursor.close();
            }else
                mChartInfo = null;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if( mChartInfo == null ){
                new Utils().makeToast(getContext(), getResources().getString(R.string.empty_data) );
            }
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // P1 ( mDefaultRect.centerX() , mDefaultRect.centerY() )
        // P2 ( event.getX() , event.getY() )
        if( mChartInfo == null )
            return super.onTouchEvent(event);

        mSelectArcId = 0;
        float x = event.getX() - mDefaultRect.centerX();
        float y = event.getY() -  mDefaultRect.centerY();

        double dAngle = Math.toDegrees( Math.atan2(y, x) );

        if( dAngle < 0 )
            dAngle = 360 + dAngle;

        String strMemo =  "";

        for( ArcData data : mChartInfo.getData() ){
            if ( dAngle > data.mStartAngle && dAngle < (data.mStartAngle+data.mSweepAngle) ){
                strMemo = data.mMemo;
                mSelectArcId = data.mId;
                break;
            }
        }
        ((TextView) ((View)getParent()).findViewById(R.id.textViewMemo)).setText(strMemo);
        invalidate();

        return super.onTouchEvent(event);
    }
}















