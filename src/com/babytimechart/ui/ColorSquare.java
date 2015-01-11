package com.babytimechart.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.QuickContactBadge;

import com.ryutskr.babytimechart.R;
public class ColorSquare extends QuickContactBadge {

    public ColorSquare(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorSquare(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setBackgroundColor(int color) {
        Drawable[] colorDrawable = new Drawable[] {
                getContext().getResources().getDrawable(R.drawable.calendar_color_square) };
        setImageDrawable(new ColorStateDrawable(colorDrawable, color));
    }
}