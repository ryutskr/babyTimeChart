package com.babytimechart.ui;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.babytimechart.db.BabyTimeDbOpenHelper;
import com.babytimechart.db.Dbinfo;
import com.babytimechart.ui.DrawArcData.ArcData;
import com.babytimechart.utils.Utils;
import com.ryutskr.babytimechart.R;

public class RoundChartView extends View {

	private final float TIME_CIRCLE_FILL;
	private final float TIME_CIRCLE_STROKE_WIDTH;
	private final int 	TIME_CIRCLE_STROKE_COLOR;
	private final int 	TIME_CIRCLE_FILL_COLOR;

	private final int 	CIRCLE_COLOR_FILL;

	private final int 	CIRCLE_COLOR_STROKE;
	private final float CIRCLE_STROKE_WIDTH;
	private final int 	CENTER_CIRCLE_COLOR;
	private final float CENTER_CIRCLE_RADIUS;
	private final float CENTER_REMOVE_CIRCLE_RADIUS;
	private final float DOT_LINE_WIDTH;
	private final float DOT_LINE_INTERVALS;

	private final int   SELECT_ARC_COLOR;
	private final float SELECT_ARC_STROKE_WIDTH;

	private final int ADD_RECT_DIVISION = 4;

	private Paint mDefaultPaint;
	private RectF mDefaultRect;
	private int mSelectArcId = 0;

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

	public RoundChartView (Context context, AttributeSet attrs) {
		super(context, attrs);
		TIME_CIRCLE_FILL = getResources().getDimension(R.dimen.time_circle_fill);
		TIME_CIRCLE_STROKE_WIDTH = getResources().getDimension(R.dimen.time_circle_stroke);
		TIME_CIRCLE_STROKE_COLOR = getResources().getColor(R.color.time_circle_stroke);
		TIME_CIRCLE_FILL_COLOR   = getResources().getColor(R.color.time_circle_fill);
		
		CIRCLE_COLOR_FILL = getResources().getColor(R.color.circle_fill);
		CIRCLE_COLOR_STROKE = getResources().getColor(R.color.circle_strok);
		CIRCLE_STROKE_WIDTH = getResources().getDimension(R.dimen.circle_stroke); 	
		
		CENTER_CIRCLE_COLOR = getResources().getColor(R.color.center_circle_fill_strok);
		CENTER_CIRCLE_RADIUS = getResources().getDimension(R.dimen.center_circle_radius); 	
		CENTER_REMOVE_CIRCLE_RADIUS = getResources().getDimension(R.dimen.center_remove_circle_radius); 	 
		
		DOT_LINE_WIDTH = getResources().getDimension(R.dimen.dot_line_width); 		
		DOT_LINE_INTERVALS = getResources().getDimension(R.dimen.dot_line_interval); 	
		
		SELECT_ARC_COLOR = getResources().getColor(R.color.arc_select);
		SELECT_ARC_STROKE_WIDTH = getResources().getDimension(R.dimen.arc_select_stroke);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		if( widthMeasureSpec > heightMeasureSpec)
			super.onMeasure(heightMeasureSpec, heightMeasureSpec);
		else
			super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		drawTimeCircle(canvas);
		if( mDefaultRect != null )
			canvas.drawOval(mDefaultRect, mDefaultPaint);

		for(ChartData data : mChartDataArrayList){
			if (data.mRect != null && data.mDrawArcData != null) {
				canvas.drawOval(data.mRect, mDefaultPaint);
				for( ArcData arcData : data.mDrawArcData.getData() ) {
					canvas.drawArc(data.mRect, arcData.getStartAngle(),arcData.getSweepAngle(), true, arcData.getPaint());

					// selected ARC
					if( arcData.mId == mSelectArcId ) {
						Paint selectArcPaint = new Paint();
						selectArcPaint.setColor(SELECT_ARC_COLOR);
						selectArcPaint.setStyle(Paint.Style.STROKE);
						selectArcPaint.setAntiAlias(true);
						selectArcPaint.setStrokeWidth(SELECT_ARC_STROKE_WIDTH);

						RectF rect = new RectF();
						float width = getWidth()- (CIRCLE_STROKE_WIDTH);

						rect.set(CIRCLE_STROKE_WIDTH, CIRCLE_STROKE_WIDTH,
								width, width);
						canvas.drawArc(rect, arcData.getStartAngle(), arcData.getSweepAngle(), true, selectArcPaint);
					}
				}
			}
		}
		
		if( mChartDataArrayList.size() != 0)
			customeCircle(canvas);
	}
	private void customeCircle(Canvas canvas)
	{
		// Custome Stroke Draw
		Paint customePaint = new Paint();
		customePaint.setColor(CIRCLE_COLOR_STROKE);
		customePaint.setStyle(Paint.Style.STROKE);
		customePaint.setAntiAlias(true);
		customePaint.setStrokeWidth(CIRCLE_STROKE_WIDTH);

		for(ChartData data : mChartDataArrayList)
			canvas.drawOval(data.mRect, customePaint);

		// Dot Line Draw
		customePaint.setColor(CENTER_CIRCLE_COLOR);
		customePaint.setStyle(Paint.Style.STROKE);
		customePaint.setAlpha(120);
		customePaint.setStrokeWidth(DOT_LINE_WIDTH);
		customePaint.setPathEffect(new DashPathEffect(new float[] {DOT_LINE_INTERVALS,DOT_LINE_INTERVALS}, 0));
		Path path = new Path();
		// >
		path.moveTo(mDefaultRect.centerX(), mDefaultRect.centerY());
		path.lineTo(getWidth() - TIME_CIRCLE_FILL, mDefaultRect.centerY());
		canvas.drawPath(path, customePaint);
		// ^
		path.reset();
		path.moveTo(mDefaultRect.centerX(), mDefaultRect.centerY());
		path.lineTo(mDefaultRect.centerX(), TIME_CIRCLE_FILL);
		canvas.drawPath(path, customePaint);
		// <
		path.reset();
		path.moveTo(mDefaultRect.centerX(), mDefaultRect.centerY());
		path.lineTo(TIME_CIRCLE_FILL, mDefaultRect.centerY());
		canvas.drawPath(path, customePaint);
		// v
		path.reset();
		path.moveTo(mDefaultRect.centerX(), mDefaultRect.centerY());
		path.lineTo(mDefaultRect.centerX(), getWidth()-TIME_CIRCLE_FILL);
		canvas.drawPath(path, customePaint);

		RectF rect = new RectF();
		if( mSelectArcId > 0 ){
			rect.set(mDefaultRect.centerX()-CENTER_REMOVE_CIRCLE_RADIUS,mDefaultRect.centerY()-CENTER_REMOVE_CIRCLE_RADIUS,
					mDefaultRect.centerX()+CENTER_REMOVE_CIRCLE_RADIUS,mDefaultRect.centerY()+CENTER_REMOVE_CIRCLE_RADIUS );

			customePaint.setColor(CENTER_CIRCLE_COLOR);
			customePaint.setStyle(Paint.Style.FILL);
			canvas.drawOval(rect, customePaint);
			// Draw Image
			Drawable d = getResources().getDrawable(R.drawable.arc_remove);
			d.setBounds((int)(mDefaultRect.centerX()-CENTER_REMOVE_CIRCLE_RADIUS),(int)(mDefaultRect.centerY()-CENTER_REMOVE_CIRCLE_RADIUS),
					(int)(mDefaultRect.centerX()+CENTER_REMOVE_CIRCLE_RADIUS),(int)(mDefaultRect.centerY()+CENTER_REMOVE_CIRCLE_RADIUS) );
			d.draw(canvas);
		}else{
			// Center Circle Draw
			rect.set(mDefaultRect.centerX()-CENTER_CIRCLE_RADIUS,mDefaultRect.centerY()-CENTER_CIRCLE_RADIUS,
					mDefaultRect.centerX()+CENTER_CIRCLE_RADIUS,mDefaultRect.centerY()+CENTER_CIRCLE_RADIUS );

			customePaint.setColor(CENTER_CIRCLE_COLOR);
			customePaint.setStyle(Paint.Style.FILL);
			canvas.drawOval(rect, customePaint);
		}


	}

	private void init() {
		if( mChartDataArrayList.size() == 0) {
			mDefaultPaint = new Paint();
			mDefaultPaint.setColor(CIRCLE_COLOR_FILL);
			mDefaultPaint.setAntiAlias(true);
			mDefaultPaint.setStyle(Paint.Style.FILL);

			mDefaultRect = new RectF();
			float width = getWidth() - TIME_CIRCLE_FILL;
			mDefaultRect.set(TIME_CIRCLE_FILL, TIME_CIRCLE_FILL, width, width);
			mChartDataArrayList.add(new ChartData(mDefaultRect, null, null));
		}
	}

	private void drawTimeCircle(Canvas canvas){
		Paint paint = new Paint();
		paint = new Paint();
		paint.setColor(TIME_CIRCLE_FILL_COLOR);
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);

		RectF rect = new RectF();
		rect.set(TIME_CIRCLE_STROKE_WIDTH, TIME_CIRCLE_STROKE_WIDTH, 
				getWidth()-TIME_CIRCLE_STROKE_WIDTH, getWidth()-TIME_CIRCLE_STROKE_WIDTH);
		canvas.drawOval(rect, paint);

		paint.setColor(TIME_CIRCLE_STROKE_COLOR);
		paint.setStrokeWidth(TIME_CIRCLE_STROKE_WIDTH);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawOval(rect, paint);
		
		
		Paint textPaint = new Paint();
		paint.setAntiAlias(true);
		textPaint.setColor(getResources().getColor(R.color.time_text_color));
		textPaint.setFlags(Paint.FAKE_BOLD_TEXT_FLAG | Paint.LINEAR_TEXT_FLAG);
		textPaint.setTextSize(getResources().getDimension(R.dimen.time_text));
		
		canvas.drawText("24", rect.centerX()-(textPaint.getTextSize()/2),
				rect.top + (TIME_CIRCLE_FILL/3)*2, textPaint);
		canvas.drawText("6", rect.right -(TIME_CIRCLE_FILL/3)*2,
				rect.centerY()+(textPaint.getTextSize()/3), textPaint);
		canvas.drawText("12", rect.centerX()-(textPaint.getTextSize()/2),
				rect.bottom - (TIME_CIRCLE_FILL/3), textPaint);
		
		canvas.drawText("18", rect.left + (TIME_CIRCLE_FILL/6),
				rect.centerY()+(textPaint.getTextSize()/3), textPaint);
		
		Paint dotPaint = new Paint();
		dotPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		dotPaint.setAntiAlias(true);
		
		RectF timeRect = new RectF();
		float fRadius = rect.centerX() - (TIME_CIRCLE_FILL/2);
		String[] time_dot = getResources().getStringArray(R.array.time_dot);
		int iColorIndex = 0;
		for( int i =1; i<25; i++){
			if( i%6 != 0 ){
			timeRect.set( rect.centerX() + fRadius*(float)Math.cos(i*Math.PI/12) - getResources().getDimension(R.dimen.time_dot)/2,
					rect.centerY() + fRadius*(float)Math.sin(i*Math.PI/12) - getResources().getDimension(R.dimen.time_dot)/2,
					rect.centerX() + fRadius*(float)Math.cos(i*Math.PI/12) + getResources().getDimension(R.dimen.time_dot)/2,  
					rect.centerY() + fRadius*(float)Math.sin(i*Math.PI/12) + getResources().getDimension(R.dimen.time_dot)/2);
			dotPaint.setColor(Color.parseColor(time_dot[iColorIndex++]));
			canvas.drawOval(timeRect, dotPaint);
			}
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
		mSelectArcId = 0;
	}

	public void changeChartDate(int chartIndex, String selecteDate ){
		init();

		if( (chartIndex+1) > mChartDataArrayList.size())
			return;

		mChartDataArrayList.get(chartIndex).mDate = selecteDate;
		makeChartDatas();
		mSelectArcId = 0;
	}

	public void refreshChart(){	
		for( ChartData data : mChartDataArrayList){
			if( data.mDrawArcData != null )
				data.mDrawArcData.refreshPaint();
		}
		invalidate();  
		mSelectArcId = 0;
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
			if (mChartDataArrayList.size() == 1 && mChartDataArrayList.get(0).mDrawArcData == null) {
				new Utils().makeToast(getContext(), getResources().getString(R.string.empty_data));
			}
		}
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// P1 ( mDefaultRect.centerX() , mDefaultRect.centerY() )
		// P2 ( event.getX() , event.getY() )
		if( mChartDataArrayList.size() == 1
				&& mChartDataArrayList.get(0).mDrawArcData != null){

			float x = event.getX() - mDefaultRect.centerX();
			float y = event.getY() -  mDefaultRect.centerY();

			if( Math.abs(x) < CENTER_REMOVE_CIRCLE_RADIUS && Math.abs(y) < CENTER_REMOVE_CIRCLE_RADIUS){
				new Handler().post(new Runnable(){

					@Override
					public void run() {

						BabyTimeDbOpenHelper dbOpenHelper = new BabyTimeDbOpenHelper(getContext());
						SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
						db.delete(Dbinfo.DB_TABLE_NAME, "_id=" + mSelectArcId, null);
						db.close();
						makeChartDatas();
						mSelectArcId = 0;

					}
				});

				((TextView) ((View)getParent()).findViewById(R.id.textViewMemo)).setText("");
				return super.onTouchEvent(event);
			}

			mSelectArcId = 0;
			double dAngle = Math.toDegrees( Math.atan2(y, x) );

			if( dAngle < 0 )
				dAngle = dAngle +360;

			double dTempAngle = 0;
			String strMemo =  "";
			for( ArcData data :  mChartDataArrayList.get(0).mDrawArcData.getData()){

				if( data.mStartAngle > dAngle )
					dTempAngle = dAngle + 360;
				else 
					dTempAngle = dAngle;
				if ( (dTempAngle - data.mStartAngle)>0 && (dTempAngle - data.mStartAngle) <data.mSweepAngle ){
					strMemo = data.mMemo;
					mSelectArcId = data.mId;
					break;
				}
			}
			((TextView) ((View)getParent()).findViewById(R.id.textViewMemo)).setText(strMemo);
			invalidate();
		}

		return super.onTouchEvent(event);
	}
}















