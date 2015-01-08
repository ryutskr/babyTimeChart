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
import com.babytimechart.ui.ChartInfomation.Data;
import com.babytimechart.utils.Utils;
import com.ryutskr.babytimechart.R;

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

    private Paint mDefaultPaint;
    private RectF mDefaultRect;
    private int mSelectArcId = 0;

    private DrawArcData mChartInfo = null;

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

        init(canvas);
        if( mChartInfo != null ){
            for(int i=0; i< mChartInfo.getData().size();i++)
                canvas.drawArc(mDefaultRect, mChartInfo.getData().get(i).getStartAngle(),
                        mChartInfo.getData().get(i).getSweepAngle(), true, mChartInfo.getData().get(i).getPaint());

            selectArc(canvas);
        }
        customeCircle(canvas);
    }

    private void init(Canvas canvas) {

        mDefaultPaint = new Paint();
        mDefaultPaint.setColor(DEFAULT_CIRCLE_COLOR);
        mDefaultPaint.setAntiAlias(true);
        mDefaultPaint.setStyle(Paint.Style.FILL);
        mDefaultRect = new RectF();

        int width = getWidth()- (DEFAULT_CIRCLE_LEFT_MARGIN);
        mDefaultRect.set(DEFAULT_CIRCLE_LEFT_MARGIN, DEFAULT_CIRCLE_TOP_MARGIN, width, width);
        canvas.drawOval(mDefaultRect, mDefaultPaint);
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
        canvas.drawOval(mDefaultRect, customePaint);

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

        for( Data data : mChartInfo.getData() ) {

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

    public void drawChart(String selection){

        try {
            BabyTimeDbOpenHelper dbhelper = new BabyTimeDbOpenHelper(getContext());
            SQLiteDatabase db = dbhelper.getReadableDatabase();

            String strSelection = "";

            strSelection = "date ='"+ selection +"'";
            Cursor cursor = db.query(Dbinfo.DB_TABLE_NAME, null, strSelection, null, null, null, null);
            if( cursor != null && cursor.getCount() > 0)
            {
                mChartInfo = new ChartInfomation(getContext(), cursor);
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

        for( Data data : mChartInfo.getData() ){
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















