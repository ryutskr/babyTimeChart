package com.babytimechart.ui;

import java.util.Locale;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import com.activity.babytimechart.R;
import com.babytimechart.fragment.Fragment_Chart_Pie;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter_delay extends FragmentPagerAdapter {

	Context mContex = null;
	ComponentName mFrom = null;

	public SectionsPagerAdapter_delay(FragmentManager fm, Context context, ComponentName from){
		super(fm);
		mContex = context;
		mFrom = from;
		
	}

	@Override
	public Fragment getItem(int position) {
		return Fragment_Chart_Pie.newInstance(position + 1);
	}

	@Override
	public int getCount() {
		// Show 2 total pages.
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return mContex.getString(R.string.title_section1).toUpperCase(l);
		case 1:
			return mContex.getString(R.string.title_section2).toUpperCase(l);
			//			case 2:
			//				return getString(R.string.title_section3).toUpperCase(l);
		}
		return null;
	}
}