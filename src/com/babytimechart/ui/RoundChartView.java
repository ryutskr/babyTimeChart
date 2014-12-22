package com.babytimechart.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.activity.babytimechart.R;
import com.babytimechart.db.BabyTimeDbOpenHelper;
import com.babytimechart.db.Dbinfo;
import com.babytimechart.ui.ChartInfomation.Data;

public class RoundChartView extends View {

	private static final int DEFAULT_CIRCLE_LEFT_MARGIN = 20;
	private static final int DEFAULT_CIRCLE_TOP_MARGIN = 20;
	private static final int DEFAULT_CIRCLE_COLOR = Color.WHITE;

	private static final int CUSTOME_CIRCLE_COLOR = Color.BLACK;
	private static final int CUSTOME_CIRCLE_STROKE_WIDTH = 5; 	// dip
	private static final int CUSTOME_CENTER_CIRCLE_COLOR = Color.BLACK;
	private static final int CUSTOME_CENTER_CIRCLE_RADIUS = 15;
	private static final int CUSTOME_DOT_LINE_WIDTH = 1; 		// dip
	private static final int CUSTOME_DOT_LINE_INTERVALS = 5; 	// dip
	private static final int CUSTOME_DOT_LINE_ALPHA = 120; 	// 0 ~ 255

	private Paint mDefaultPaint;
	private RectF mDefaultRect;
	private long mTodayLastTime = 0;
	private int mSelectArc = 0;
	private boolean mDeleteColumn = false;

	private ChartInfomation mChartInfo = null;

	private String mSelection = "";
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
			{
				canvas.drawArc(mDefaultRect, mChartInfo.getData().get(i).getStartAngle(), 
						mChartInfo.getData().get(i).getSweepAngle(), true, mChartInfo.getData().get(i).getPaint());
			}
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

	public void drawChart(String selection){
		mSelection = selection;
		BabyTimeDbOpenHelper dbhelper = new BabyTimeDbOpenHelper(getContext());
		SQLiteDatabase db = dbhelper.getReadableDatabase();

		String strSelection = "";

		strSelection = "date ='"+ selection +"'";
		Cursor cursor = db.query(Dbinfo.DB_TABLE_NAME, null, strSelection, null, null, null, null);
		if( cursor != null && cursor.getCount() > 0)
		{
			mChartInfo = new ChartInfomation(getContext(), cursor);
			cursor.moveToLast();
			mTodayLastTime = cursor.getLong(cursor.getColumnIndex(Dbinfo.DB_E_TIME));
			cursor.close();
		}
		else{
			mChartInfo = null;
			Toast.makeText(getContext(), getResources().getString(R.string.empty_data), Toast.LENGTH_SHORT).show();
		}
			
		
		invalidate();
	}

	public long getLasttime(){ return mTodayLastTime; }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// P1 ( mDefaultRect.centerX() , mDefaultRect.centerY() )
		// P2 ( event.getX() , event.getY() )
		if( mChartInfo == null )
			return super.onTouchEvent(event);
		
		if( event.getAction() == MotionEvent.ACTION_MOVE ){
			Log.i("1111", "MotionEvent.ACTION_MOVE" );
			int r = (getWidth()- (DEFAULT_CIRCLE_LEFT_MARGIN*2))/2;
			
			double distance = Math.sqrt(Math.pow(Math.abs(mDefaultRect.centerX() - event.getX()), 2) 
					+ Math.pow(Math.abs(mDefaultRect.centerY() - event.getY()),2));
			
			
			Log.i("1111", "distance : " + distance  + "  r : "+ r);
			if( distance > r )
				mDeleteColumn = true;
			Log.i("1111", "mDeleteColumn : " + mDeleteColumn  + "  mSelectArc : "+ mSelectArc);
			
			
		}else if( event.getAction() == MotionEvent.ACTION_DOWN ){
			Log.i("1111", "MotionEvent.ACTION_DOWN" );
			float x = event.getX() - mDefaultRect.centerX();
			float y = event.getY() -  mDefaultRect.centerY();
			
			 double dAngle = Math.toDegrees( Math.atan2(y, x) );
			
			if( dAngle < 0 )
				dAngle = 360 + dAngle;
			
			String strMemo =  "";
			
			for( Data data : mChartInfo.getData() ){
				if ( dAngle > data.mStartAngle && dAngle < (data.mStartAngle+data.mSweepAngle) ){
					strMemo = data.mMemo;
					mSelectArc = data.mId;
					break;
				}
			}
			((TextView) ((View)getParent()).findViewById(R.id.textViewMemo)).setText(strMemo);
		}else if( event.getAction() == MotionEvent.ACTION_UP ){
			Log.i("1111", "MotionEvent.ACTION_UP" );
			if( mDeleteColumn ){
				BabyTimeDbOpenHelper dbhelper = new BabyTimeDbOpenHelper(getContext());
				SQLiteDatabase db = dbhelper.getReadableDatabase();
				int iret = db.delete(Dbinfo.DB_TABLE_NAME, "_id="+mSelectArc, null);
				Log.i("1111", "MotionEvent.ACTION_UP ret : " + iret );
				
				db.close();
				drawChart(mSelection);
			}
		}
		return true;
	}
}















