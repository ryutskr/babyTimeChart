package com.babytimechart.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import com.ryutskr.babytimechart.R;

public class HeightWrappingViewPager extends ViewPager {

//    private boolean mIsAdview = false;
	public HeightWrappingViewPager(Context context) { super(context); }

	public HeightWrappingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

//        mIsAdview = context.obtainStyledAttributes( attrs, R.styleable.HeightWrappingViewPager )
//                .getBoolean( R.styleable.HeightWrappingViewPager_isAdView, false );
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        if( mIsAdview ){
//            DisplayMetrics metrics = getResources().getDisplayMetrics();
//            float iAdViewSize = ((50) * metrics.density); // BANNER Height SIZE =50 ; LARGE BANNER 100
//            super.onMeasure(widthMeasureSpec, (int)(heightMeasureSpec -(int)iAdViewSize));
//        }else
		    super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}
}
