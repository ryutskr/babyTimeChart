package com.babytimechart.activity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.babytimechart.ui.BabyTimeSettingMenuAdapter;
import com.ryutskr.babytimechart.R;

public class BabyTimeHelp extends ListActivity {

	private BabyTimeSettingMenuAdapter mAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setActionbar();
		initMenu();
	}

	private void setActionbar() {
		if( getActionBar() != null ){
			getActionBar().setTitle(getString(R.string.action_help));
			getActionBar().setDisplayShowCustomEnabled(false);
			getActionBar().setDisplayShowTitleEnabled(true);
			getActionBar().setDisplayHomeAsUpEnabled(true);
			
			int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
			TextView abTitle = (TextView) findViewById(titleId);
			abTitle.setTextColor(getResources().getColor(R.color.setting_ab_title));
		}
	}

	private void initMenu() {
        String version;
        PackageInfo i = null;
        try {
            i = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = i.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

		mAdapter = new BabyTimeSettingMenuAdapter(this);

		// nomal
		mAdapter.addItem(1, R.string.developer, R.string.developer_explain, 0 );
		mAdapter.addItem(2, R.string.license, R.string.license_explain, 0 );
		mAdapter.addItem(3, R.string.version, R.string.version_explain, 0 );

		setListAdapter(mAdapter);
		getListView().setDividerHeight(0);
		getListView().setBackgroundColor(getResources().getColor(R.color.fragment_background));
	}

	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		switch ((int)id){
		case 0:
            Intent email = new Intent(Intent.ACTION_SENDTO);
            email.setData(Uri.parse("mailto:ryutskr@gmail.com"));
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{"ryutskr@gmail.com"});

            startActivity(email);
			break;

		case 1:
			View view = LayoutInflater.from(this).inflate(R.layout.license, null);
			new AlertDialog.Builder(this)
			.setTitle(getString(R.string.license))
			.setView(view)
			.create().show();
			
			break;
		}
		
		super.onListItemClick(l, v, position, id);
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id ){
        	case android.R.id.home :
            finish();
            break;
        }
        return true;
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}





















