package com.babytimechart.ui;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.activity.babytimechart.R;
import com.activity.babytimechart.R.color;
import com.babytimechart.db.BabyTimeDbOpenHelper;
import com.babytimechart.db.Dbinfo;

public class RoundChartView extends View {

	private Paint paint;
	private Paint bgpaint;
	private RectF rect;
	float percentage = 0;
	
	private ChartInfomation mChartInfo = null;
	private ArrayList<ChartInfomation> mListChartInfo = new ArrayList<ChartInfomation>();

	public RoundChartView (Context context) {
		super(context);
		init();
	}
	public RoundChartView (Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public RoundChartView (Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		paint = new Paint();
		paint.setColor(color.blueviolet);
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(5.0f);

		bgpaint = new Paint();
		bgpaint.setColor(Color.WHITE);
		bgpaint.setAntiAlias(true);
		bgpaint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeWidth(5.0f);
		rect = new RectF();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//draw background circle anyway
		int left = 20;
		int width = getWidth()- 40;
		int top = 20;
		rect.set(left, top, left+width, top + width); 
		canvas.drawArc(rect, -90, 360, true, bgpaint);
		if(percentage!=0) {
			rect.set(left, top, left+width, top + width);
			canvas.drawArc(rect, -90, (360*percentage), true, paint);
		}
	}
	
	public void drawChart(){
		BabyTimeDbOpenHelper dbhelper = new BabyTimeDbOpenHelper(getContext());
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		
		String strSelection = "";
		
		SimpleDateFormat queryDateformat = new SimpleDateFormat("yyyy-MM-dd");
		String strToday = queryDateformat.format(new Date(System.currentTimeMillis()));
		String strYesterday = queryDateformat.format(new Date(System.currentTimeMillis()-(24*60*60*1000)));
		
		strSelection = "date ='"+ strToday +"' OR ( date ='" + strYesterday +"' AND e_time='00:00')";
		Log.i("1111", "drawChart strSelection : " + strSelection );
		// db query
		Cursor cursor = db.query(Dbinfo.DB_TABLE_NAME, null, strSelection, null, null, null, null);		
		
		if( cursor != null && cursor.getCount() > 0)
		{
			mListChartInfo.add(new ChartInfomation(cursor, getWidth()));
			invalidate();
		}
		else
			Toast.makeText(getContext(), getResources().getString(R.string.empty_data), Toast.LENGTH_LONG);
	}
	
	public void setPercentage(float percentage) {
		this.percentage = percentage / 100;
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}
}















