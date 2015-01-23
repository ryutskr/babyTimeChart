package com.babytimechart.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.babytimechart.utils.Utils;
import com.ryutskr.babytimechart.R;

public class TimeSliderView extends View {

	private final float TIME_SLIDER_PADDING;
	private final float TIME_SLIDER_PADDING_BOTTOM;
	private final float TRIANGLE_SIZE;
	private final float RECT_RADIUS;


	private float mWidth;
	private float mPixcelperHour;
	private float mPixcelpreMin;

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

	public TimeSliderView (Context context, AttributeSet attrs) {
		super(context, attrs);
		TIME_SLIDER_PADDING = getResources().getDimension(R.dimen.timeslider_padding);
		TRIANGLE_SIZE = getResources().getDimension(R.dimen.timeslider_triangle_size);
		RECT_RADIUS = getResources().getDimension(R.dimen.timeslider_rect_radius);
		TIME_SLIDER_PADDING_BOTTOM = getResources().getDimension(R.dimen.timeslider_padding_bottom); 

		setBackgroundResource(R.drawable.timeslider_background);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		startTimeMarker(canvas);
	}

	public void startTimeMarker(Canvas canvas){

		for( int i =0; i <3; i++){
			long millis = 0;
			Drawable dType = getResources().getDrawable(R.drawable.now);
			if( i==0){
				millis= System.currentTimeMillis();
				dType = getResources().getDrawable(R.drawable.now);
			}else if( i==1){
				millis = Long.parseLong( ((TextView)((View)getParent()).findViewById(R.id.txtView_stime)).getContentDescription().toString() );
				dType = getResources().getDrawable(R.drawable.stime);
			}else if( i==2){
				millis = Long.parseLong( ((TextView)((View)getParent()).findViewById(R.id.txtView_etime)).getContentDescription().toString() );
				dType = getResources().getDrawable(R.drawable.etime);
			}

			float x = makePositionInfo(millis);
			float y = (int)TIME_SLIDER_PADDING;

			// TriAngle
			Paint fillpaint =  new Paint(Paint.ANTI_ALIAS_FLAG);
			fillpaint.setStyle(Paint.Style.FILL);
			fillpaint.setColor(new Utils().mEatColor);

			Paint strokepaint =  new Paint(Paint.ANTI_ALIAS_FLAG);		
			strokepaint.setStyle(Paint.Style.STROKE);
			strokepaint.setColor(Color.BLACK);

			Path path =  new Path();
			path.setFillType(Path.FillType.EVEN_ODD);
			path.moveTo( x, y );
			path.lineTo(x-TRIANGLE_SIZE, y - TRIANGLE_SIZE );
			path.lineTo(x + TRIANGLE_SIZE, y - TRIANGLE_SIZE);
			path.lineTo(x, y);
			path.close();
			canvas.drawPath(path, fillpaint);
			canvas.drawPath(path, strokepaint);


			// vertical Line
			strokepaint.setStrokeWidth(5f);
			canvas.drawLine(x, y, x, y+getHeight()-TIME_SLIDER_PADDING - TIME_SLIDER_PADDING_BOTTOM, strokepaint);

			// Draw move
			Paint ciclepaint =  new Paint(Paint.ANTI_ALIAS_FLAG);		
			ciclepaint.setStyle(Paint.Style.FILL_AND_STROKE);
			ciclepaint.setColor(Color.WHITE);

			RectF rect = new RectF();
			rect.set(x-RECT_RADIUS, y+getHeight()-TIME_SLIDER_PADDING - TIME_SLIDER_PADDING_BOTTOM, x+RECT_RADIUS, getHeight());
			canvas.drawOval(rect, ciclepaint);

			dType.setBounds((int)(x-RECT_RADIUS), (int)(y+getHeight()-TIME_SLIDER_PADDING - TIME_SLIDER_PADDING_BOTTOM), (int)(x+RECT_RADIUS), getHeight());
			dType.draw(canvas);
		}
	}

	public float makePositionInfo(long millis){

		mWidth = getWidth() - (TIME_SLIDER_PADDING*2);
		Log.i("1111", "getWidth : " +getWidth());
		Log.i("1111", "mWidth : " +mWidth);
		mPixcelperHour = mWidth/24;
		mPixcelpreMin  = mWidth/(24*12);

		Log.i("1111", "millis : " +millis);
		int hh = Integer.parseInt(new SimpleDateFormat("HH").format(new Date(millis)));
		int mm = Integer.parseInt(new SimpleDateFormat("mm").format(new Date(millis)));

		Log.i("1111", "hh : " +hh + "   mm : " +mm);
		Log.i("1111", "mPixcelperHour" +mPixcelperHour + "   mPixcelpreMin : " +mPixcelpreMin);

		return ((hh * mPixcelperHour) + (mm/5 * mPixcelpreMin));
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {

		switch( event.getAction() ){
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		return true;
	}
}















