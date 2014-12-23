package com.babytimechart.activity;

import android.app.ListActivity;
import android.os.Bundle;
import com.activity.babytimechart.R;
import com.babytimechart.ui.BabyTimeSettingMenuAdapter;

public class BabyTimeSetting extends ListActivity {
	
	BabyTimeSettingMenuAdapter mAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initMenu();
	}

	private void initMenu() {
		mAdapter = new BabyTimeSettingMenuAdapter(this);

		// nomal
		mAdapter.addHeader(R.string.normal);
		mAdapter.addItem(100, R.string.eating, R.string.eat_eplain, getResources().getColor(R.color.chocolate));
		mAdapter.addItem(101, R.string.playing, R.string.play_eplain, getResources().getColor(R.color.chocolate));
		mAdapter.addItem(102, R.string.sleeping, R.string.sleep_eplain, getResources().getColor(R.color.chocolate));
		mAdapter.addItem(103, R.string.etc, R.string.eat_eplain, getResources().getColor(R.color.chocolate));
		mAdapter.addItem(104, R.string.baby_profile, R.string.baby_profile_eplain, 0);
		
		// manage
		mAdapter.addHeader(R.string.manege);
		mAdapter.addItem(201, R.string.data_backup, R.string.data_backup_explain, 0);
		mAdapter.addItem(202, R.string.data_restore, R.string.data_restore_eplain, 0);
		mAdapter.addItem(203, R.string.data_initialization, R.string.data_initialization_explain, 0);
		
		setListAdapter(mAdapter);
		getListView().setDividerHeight(0);
	}
}
