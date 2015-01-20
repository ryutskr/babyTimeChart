package com.babytimechart.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.babytimechart.activity.BabyTimeDataActivity;
import com.babytimechart.activity.BabyTimeMainActivity;
import com.babytimechart.ui.RoundChartView;
import com.babytimechart.utils.Utils;
import com.google.android.gms.ads.InterstitialAd;
import com.ryutskr.babytimechart.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class Fragment_Chart extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";
	private RoundChartView mPieChart = null;
	private int mSelectionNum = -1;
	private View rootView = null;

    private ImageView mImageViewLegend_eat;
    private ImageView mImageViewLegend_play;
    private ImageView mImageViewLegend_sleep;
    private ImageView mImageViewLegend_etc;



	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static Fragment_Chart newInstance(int sectionNumber) {
		Fragment_Chart fragment = new Fragment_Chart();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public Fragment_Chart() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if( getArguments() != null )
			mSelectionNum = getArguments().getInt(ARG_SECTION_NUMBER, 0);

		rootView = inflater.inflate(R.layout.fragment_chart, container,false);

		mPieChart = (RoundChartView)rootView.findViewById(R.id.roundchartview);

        mImageViewLegend_eat = (ImageView)rootView.findViewById(R.id.imgview_legend_eat);
        mImageViewLegend_play = (ImageView)rootView.findViewById(R.id.imgview_legend_play);
        mImageViewLegend_sleep = (ImageView)rootView.findViewById(R.id.imgview_legend_sleep);
        mImageViewLegend_etc = (ImageView)rootView.findViewById(R.id.imgview_legend_etc);
        setLegendColor();

		if( mSelectionNum == 1) {
			rootView.findViewById(R.id.feedingBtn).setOnClickListener(mOnClickListener);
			rootView.findViewById(R.id.playingBtn).setOnClickListener(mOnClickListener);
			rootView.findViewById(R.id.sleepingBtn).setOnClickListener(mOnClickListener);
			rootView.findViewById(R.id.etcBtn).setOnClickListener(mOnClickListener);
		}else if( mSelectionNum == 2){
			rootView.findViewById(R.id.feedingBtn).setEnabled(false);
			rootView.findViewById(R.id.playingBtn).setEnabled(false);
			rootView.findViewById(R.id.sleepingBtn).setEnabled(false);
			rootView.findViewById(R.id.etcBtn).setEnabled(false);
		}

		return rootView;
	}

    public void setLegendColor(){
        mImageViewLegend_eat.setBackgroundColor(Utils.mEatColor);
        mImageViewLegend_play.setBackgroundColor(Utils.mPlayColor);
        mImageViewLegend_sleep.setBackgroundColor(Utils.mSleepColor);
        mImageViewLegend_etc.setBackgroundColor(Utils.mEtcColor);
    }
	public void setEnableBtn(boolean enabled){
		if( mSelectionNum == 2 )
			return;

		if( enabled ){
			rootView.findViewById(R.id.feedingBtn).setEnabled(true);
			rootView.findViewById(R.id.playingBtn).setEnabled(true);
			rootView.findViewById(R.id.sleepingBtn).setEnabled(true);
			rootView.findViewById(R.id.etcBtn).setEnabled(true);
		}else{
			rootView.findViewById(R.id.feedingBtn).setEnabled(false);
			rootView.findViewById(R.id.playingBtn).setEnabled(false);
			rootView.findViewById(R.id.sleepingBtn).setEnabled(false);
			rootView.findViewById(R.id.etcBtn).setEnabled(false);
		}
	}

	OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch( v.getId() ){
			case R.id.feedingBtn:
				Intent intent_eat = new Intent(getActivity(), BabyTimeDataActivity.class);
				intent_eat.putExtra(ARG_SECTION_NUMBER, 0);
				startActivityForResult(intent_eat, 0);
				break;
			case R.id.playingBtn:
				Intent intent_play = new Intent(getActivity(), BabyTimeDataActivity.class);
				intent_play.putExtra(ARG_SECTION_NUMBER, 1);
				startActivityForResult(intent_play, 1);
				break;
			case R.id.sleepingBtn:
				Intent intent_sleep = new Intent(getActivity(), BabyTimeDataActivity.class);
				intent_sleep.putExtra(ARG_SECTION_NUMBER, 2);
				startActivityForResult(intent_sleep, 2);
				break;
			case R.id.etcBtn:
				Intent intent_etc = new Intent(getActivity(), BabyTimeDataActivity.class);
				intent_etc.putExtra(ARG_SECTION_NUMBER, 3);
				startActivityForResult(intent_etc, 3);
				break;
			}
            if( mInterstitial == null )
                mInterstitial = new Utils().loadInterstitialAd(getActivity());
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

    private InterstitialAd mInterstitial;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if( resultCode == Activity.RESULT_OK){
			((BabyTimeMainActivity) getActivity()).setSpinnerData();
			changeChartDate(0, new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));

            if( mPieChart.isInterstitial() ){
                if (mInterstitial !=null && mInterstitial.isLoaded()) {
                    mInterstitial.show();
                    mInterstitial = new Utils().loadInterstitialAd(getActivity());
                }
            }
		}
	}

	public void addChart(int chartIndex, String lastSelecteDate){ mPieChart.addChart(chartIndex, lastSelecteDate);}
	public void removeChart(int chartIndex){ mPieChart.removeChart(chartIndex);}
	public void changeChartDate(int chartIndex, String selecteDate ){ mPieChart.changeChartDate(chartIndex, selecteDate);}
	public void refreshChart(){ mPieChart.refreshChart();}

}

















