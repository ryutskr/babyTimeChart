package com.babytimechart.fragment;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activity.babytimechart.R;
import com.babytimechart.chartdata.AbstractDemoChart;

/**
 * A placeholder fragment containing a simple view.
 */
public class DoughChartFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	/** The chart view that displays the data. */
	private GraphicalView mChartView;
	private DefaultRenderer mRenderer;

	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static DoughChartFragment newInstance(int sectionNumber) {
		DoughChartFragment fragment = new DoughChartFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public DoughChartFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_chart_pie, container,
				false);
		TextView textView = (TextView) rootView.findViewById(R.id.section_label);
		textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));

//		Button btn = (Button)rootView.findViewById(R.id.test_btn);
//
//		btn.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent intent = null;
//				intent = new Intent(getActivity(), PieChartBuilder.class);
//				startActivity(intent);
//			}
//		});


		List<double[]> values = new ArrayList<double[]>();
		values.add(new double[] { 12, 10  });
		List<String[]> titles = new ArrayList<String[]>();
		titles.add(new String[] { "Project1", "Project2"});
		int[] colors = new int[] { Color.BLUE, Color.RED };

		mRenderer = AbstractDemoChart.buildCategoryRenderer(colors);
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.WHITE);
		mRenderer.setLabelsColor(Color.BLACK);
		mRenderer.setStartAngle(0);
		mRenderer.setDisplayValues(true);
		
		mRenderer.setClickEnabled(true);
		
		mChartView = ChartFactory.getDoughnutChartView(getActivity(), 
				AbstractDemoChart.buildMultipleCategoryDataset("Project budget", titles, values), mRenderer);
		
		
		mChartView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SeriesSelection seriesSelection = ((GraphicalView)v).getCurrentSeriesAndPoint();
					for (int i = 0; i < 5; i++) {
//						mRenderer.getSeriesRendererAt(i).setHighlighted(i == seriesSelection.getPointIndex());
						Log.d("1111", "mRenderer.getSeriesRendererCount() : " +mRenderer.getSeriesRendererCount());
						Log.d("1111", "seriesSelection : " +seriesSelection);
						
					}
					mChartView.repaint();
				}
		});
		mChartView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		((LinearLayout)rootView).addView(mChartView);
		return rootView;
	}
}















