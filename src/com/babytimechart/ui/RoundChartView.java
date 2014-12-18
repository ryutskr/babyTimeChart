package com.babytimechart.ui;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import android.widget.Toast;

import com.activity.babytimechart.R;
import com.babytimechart.db.BabyTimeDbOpenHelper;
import com.babytimechart.db.Dbinfo;

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
	private long mTodayLastTime = 0l;

	private ArrayList<ChartInfomation> mListChartInfo = new ArrayList<ChartInfomation>();

	public RoundChartView (Context context) {
		super(context);
	}
	public RoundChartView (Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public RoundChartView (Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
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

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		init(canvas);

		for( ChartInfomation data : mListChartInfo )
		{
			for(int i=0; i< data.getData().size();i++)
			{
				canvas.drawArc(mDefaultRect, data.getData().get(i).getStartAngle(), 
						data.getData().get(i).getSweepAngle(), true, data.getData().get(i).getPaint());
			}
		}
		customeCircle(canvas);
	}

	public void drawChart(){
		BabyTimeDbOpenHelper dbhelper = new BabyTimeDbOpenHelper(getContext());
		SQLiteDatabase db = dbhelper.getReadableDatabase();

		String strSelection = "";

		SimpleDateFormat queryDateformat = new SimpleDateFormat("yyyy-MM-dd");
		String strToday = queryDateformat.format(new Date(System.currentTimeMillis()));

		strSelection = "date ='"+ strToday +"'";
		Cursor cursor = db.query(Dbinfo.DB_TABLE_NAME, null, strSelection, null, null, null, null);		
		if( cursor != null && cursor.getCount() > 0)
		{
			mListChartInfo.add(new ChartInfomation(getContext(), cursor));
			invalidate();
			cursor.moveToLast();
			mTodayLastTime = cursor.getLong(cursor.getColumnIndex(Dbinfo.DB_E_TIME));
			Log.i("1111", "drawChart - mTodayLastTime : " + new SimpleDateFormat("yyyy-MM-dd:HH:mm").format(new Date(mTodayLastTime)) );
		}
		else
			Toast.makeText(getContext(), getResources().getString(R.string.empty_data), Toast.LENGTH_SHORT).show();
	}
	
	public long getLasttime(){ return mTodayLastTime; }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}
}















