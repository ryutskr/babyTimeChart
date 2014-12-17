package com.babytimechart.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class HeightWrappingViewPager extends ViewPager {

	public HeightWrappingViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public HeightWrappingViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}
}
