package com.babytimechart.activity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.babytimechart.db.BabyTimeDbOpenHelper;
import com.babytimechart.db.Dbinfo;
import com.babytimechart.ui.BabyTimeSettingMenuAdapter;
import com.babytimechart.ui.ColorPickerDialog;
import com.babytimechart.ui.ColorPickerSwatch;
import com.babytimechart.utils.Utils;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.ryutskr.babytimechart.BuildConfig;
import com.ryutskr.babytimechart.R;

public class BabyTimeSetting extends Activity {

	private static final int MENU_EAT 		= 100;
	private static final int MENU_PLAY 		= 101;
	private static final int MENU_SLEEP 	= 102;
	private static final int MENU_ETC 		= 103;
	private static final int MENU_PROFILE 	= 104;
	private static final int MENU_MEASURE 	= 105;

	private static final int MENU_BACKUP_DATA 	= 200;
	private static final int MENU_RESTORE_DATA 	= 201;
	private static final int MENU_INITIALIZATION_DATA 	= 202;

	private static final int DATE_PICKER 	= 301;

	private BabyTimeSettingMenuAdapter mAdapter = null;
	private int mLastPosition = 0;
	private int[] mColorChoices = null;
	private Utils mUtils = null;
	private Context mContext = null;
	private int mSelectedDialog = 0;
	private ListView mListView = null;

	// dialog profile
	private AlertDialog mAlertDialog = null;  // profile or datepicker
	private EditText mEditTextName = null;		 // set Date
	private TextView mTextViewBirthday = null;		 // set Date
	private DatePicker mDatePicker = null;
	private RadioGroup mRadioGMeasure= null;


	// Google Drive 
	private GoogleAccountCredential mCredential;
	static final int 				REQUEST_ACCOUNT_PICKER = 1;
	private Drive 			mService;
	private Uri 				mFileUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		mListView = (ListView)findViewById(R.id.listview);
		RelativeLayout mainLayout = (RelativeLayout)findViewById(R.id.main_layout);
		setActionbar();
		initMenu();
		mContext = this;
		new Utils().addBanner(this, mainLayout);
	}

	private void setActionbar() {
		if( getActionBar() != null ){
			getActionBar().setTitle(getString(R.string.setting));
			getActionBar().setDisplayShowCustomEnabled(false);
			getActionBar().setDisplayShowTitleEnabled(true);
			getActionBar().setDisplayHomeAsUpEnabled(true);

			int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
			TextView abTitle = (TextView) findViewById(titleId);
			abTitle.setTextColor(getResources().getColor(R.color.setting_ab_title));
		}
	}

	private void initMenu() {
		mUtils = new Utils();
		mColorChoices = mUtils.getColorsForPicker(this);
		mAdapter = new BabyTimeSettingMenuAdapter(this);

		// nomal
		mAdapter.addHeader(R.string.normal);
		mAdapter.addItem(MENU_EAT, R.string.eating, R.string.eat_eplain, Utils.mEatColor );
		mAdapter.addItem(MENU_PLAY, R.string.playing, R.string.play_eplain, Utils.mPlayColor );
		mAdapter.addItem(MENU_SLEEP, R.string.sleeping, R.string.sleep_eplain, Utils.mSleepColor );
		mAdapter.addItem(MENU_ETC, R.string.etc, R.string.eat_eplain, Utils.mEtcColor );
		mAdapter.addItem(MENU_PROFILE, R.string.baby_profile, R.string.baby_profile_eplain, 0);
		mAdapter.addItem(MENU_MEASURE, R.string.measure, R.string.measure_eplain, 0);

		// manage
		mAdapter.addHeader(R.string.manege);
		mAdapter.addItem(MENU_BACKUP_DATA, R.string.data_backup, R.string.data_backup_explain, 0);
		mAdapter.addItem(MENU_RESTORE_DATA, R.string.data_restore, R.string.data_restore_eplain, 0);
		mAdapter.addItem(MENU_INITIALIZATION_DATA, R.string.data_initialization, R.string.data_initialization_explain, 0);

		mListView.setAdapter(mAdapter);
		mListView.setDividerHeight(0);
		mListView.setOnItemClickListener(mOnItemClickListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.setting_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch(id ){
		case android.R.id.home :
			finish();
			break;
		case R.id.action_help:
			startActivity(new Intent(mContext, BabyTimeHelp.class));
			break;
		}
		return true;
	}

	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			mLastPosition = position;
			BabyTimeSettingMenuAdapter.MenuItemModel item = mAdapter.getItem(position);
			switch ( item._id ){
			case MENU_EAT:
			case MENU_PLAY:
			case MENU_SLEEP:
			case MENU_ETC:

				ColorPickerDialog colorpicker = ColorPickerDialog.newInstance(
						R.string.color_picker_default_title, mColorChoices,
						item.colorSquare, 5,ColorPickerDialog.SIZE_SMALL);

				colorpicker.setOnColorSelectedListener(colorpickerListener);
				colorpicker.show(getFragmentManager(), "");
				break;
			case MENU_PROFILE:
			case MENU_MEASURE:
			case MENU_BACKUP_DATA:
			case MENU_RESTORE_DATA:
			case MENU_INITIALIZATION_DATA:
				createAlertDialog(item._id);
				break;
			}
		}
	};

	OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) { createAlertDialog(DATE_PICKER); }
	};

	public void createAlertDialog(int id){
		View view;
		String title = null;
		int dividerColor = 0;
		int titleColor = 0;
		mSelectedDialog = id;
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

		switch ( id ){
		case MENU_PROFILE:
			view = getLayoutInflater().inflate(R.layout.activity_setting_profile, null);
			title = mContext.getString(R.string.baby_profile);
			titleColor = getResources().getColor(R.color.setting_dialog_profile_title);
			dividerColor = getResources().getColor(R.color.setting_dialog_profile_divider);
			builder.setPositiveButton(mContext.getString(R.string.confirm), mOnDialogBtnClickListener)
			.setView(view);

			mTextViewBirthday = (TextView)view.findViewById(R.id.textview_birthbay);
			mEditTextName = (EditText)view.findViewById(R.id.editText_name);
			mTextViewBirthday.setOnClickListener(mOnClickListener);

			Utils utils = new Utils();
			mEditTextName.setText( utils.getBabyNameFromPref(mContext) );

			if( utils.getBabyBirthDayFromPref(mContext).length() > 0 )
				mTextViewBirthday.setText(utils.getBabyBirthDayFromPref(mContext));
			else
				mTextViewBirthday.setText(new SimpleDateFormat("yyyy/MM/dd").format(new Date(System.currentTimeMillis())));
			break;
		case DATE_PICKER:
			view = getLayoutInflater().inflate(R.layout.activity_setting_profile_datepicker, null);
			title = mContext.getString(R.string.selectdate);
			titleColor = getResources().getColor(R.color.setting_dialog_datepicker_title);
			dividerColor = getResources().getColor(R.color.setting_dialog_datepicker_divider);

			builder.setPositiveButton(mContext.getString(R.string.save), mOnDialogBtnClickListener)
			.setNegativeButton(mContext.getString(R.string.cancel), mOnDialogBtnClickListener)
			.setView(view);

			mDatePicker = (DatePicker)view.findViewById(R.id.datePicker);
			break;
		case MENU_MEASURE:
			view = getLayoutInflater().inflate(R.layout.activity_setting_measure, null);
			title = mContext.getString(R.string.measure);
			titleColor = getResources().getColor(R.color.setting_dialog_measure_title);
			dividerColor = getResources().getColor(R.color.setting_dialog_measure_divider);

			mRadioGMeasure = (RadioGroup)view.findViewById(R.id.measure);
			
			builder.setPositiveButton(mContext.getString(R.string.confirm), mOnDialogBtnClickListener)
			.setView(view);
			break;
		case MENU_BACKUP_DATA:
			title = mContext.getString(R.string.data_backup);
			titleColor = getResources().getColor(R.color.setting_dialog_backup_title);
			dividerColor = getResources().getColor(R.color.setting_dialog_backup_divider);
			builder.setPositiveButton(mContext.getString(R.string.confirm), mOnDialogBtnClickListener)
			.setNegativeButton(mContext.getString(R.string.cancel), mOnDialogBtnClickListener)
			.setMessage(R.string.data_backup_message);
			break;
		case MENU_RESTORE_DATA:
			title = mContext.getString(R.string.data_restore);
			titleColor = getResources().getColor(R.color.setting_dialog_restore_title);
			dividerColor = getResources().getColor(R.color.setting_dialog_restore_divider);
			builder.setPositiveButton(mContext.getString(R.string.confirm), mOnDialogBtnClickListener)
			.setNegativeButton(mContext.getString(R.string.cancel), mOnDialogBtnClickListener)
			.setMessage(R.string.data_restore_message);
			break;
		case MENU_INITIALIZATION_DATA:
			title = mContext.getString(R.string.data_initialization);
			titleColor = getResources().getColor(R.color.setting_dialog_reset_title);
			dividerColor = getResources().getColor(R.color.setting_dialog_reset_divider);
			builder.setPositiveButton(mContext.getString(R.string.confirm), mOnDialogBtnClickListener)
			.setNegativeButton(mContext.getString(R.string.cancel), mOnDialogBtnClickListener)
			.setMessage(R.string.data_initialization_message);
			break;
		}

		mAlertDialog = builder
				.setTitle(title)
				.create();
		mAlertDialog.show();

		int titleId = getResources().getIdentifier("alertTitle", "id", "android");
		((TextView)mAlertDialog.findViewById(titleId)).setTextColor(titleColor);

		int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
		mAlertDialog.findViewById(titleDividerId).setBackgroundColor(dividerColor);

		int btn1 = getResources().getIdentifier("button1", "id", "android");
		((Button)mAlertDialog.findViewById(btn1)).setTextColor(dividerColor);

		int btn2 = getResources().getIdentifier("button2", "id", "android");
		((Button)mAlertDialog.findViewById(btn2)).setTextColor(dividerColor);


		setResult(RESULT_OK, new Intent("DATA_CHANGE"));
	}

	android.content.DialogInterface.OnClickListener mOnDialogBtnClickListener = new android.content.DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if( which == DialogInterface.BUTTON_NEGATIVE){
				if( mSelectedDialog == DATE_PICKER )
					mSelectedDialog = MENU_PROFILE;
				else
					mSelectedDialog = 0;
				return;
			}

			switch(mSelectedDialog){
			case MENU_PROFILE:
				// pref write
				if( mEditTextName!= null && mTextViewBirthday != null) {
					String name = mEditTextName.getText().toString();
					String birthday = mTextViewBirthday.getText().toString();
					new Utils().setBabyProfileToPref(mContext, name, birthday);
				}
				mSelectedDialog = 0;
				break;
			case DATE_PICKER:
				if( mDatePicker != null ) {

					String strTemp = mDatePicker.getYear() + "/";
					if (mDatePicker.getMonth() < 9) // january : 0
						strTemp = strTemp + "0" + (mDatePicker.getMonth() + 1) + "/";
					else
						strTemp = strTemp + (mDatePicker.getMonth() + 1) + "/";

					if (mDatePicker.getDayOfMonth() < 10)
						strTemp = strTemp + "0" + mDatePicker.getDayOfMonth();
					else
						strTemp = strTemp + mDatePicker.getDayOfMonth();

					mTextViewBirthday.setText(strTemp);
				}
				mSelectedDialog = MENU_PROFILE;
				break;
			case MENU_MEASURE:
				if( mRadioGMeasure.getCheckedRadioButtonId() == R.id.rBtn_fl_oz)
					new Utils().setMeasureToPref(mContext, getString(R.string.measure_fl_oz));
				else
					new Utils().setMeasureToPref(mContext, getString(R.string.measure_ml));
				mSelectedDialog = 0;
				break;
			case MENU_BACKUP_DATA:
				googleDrive(MENU_BACKUP_DATA);
				mSelectedDialog = 0;
				break;
			case MENU_RESTORE_DATA:
				mSelectedDialog = 0;
				if( BuildConfig.DEBUG )
					new Utils().fakeDBData(mContext);
				else
					googleDrive(MENU_RESTORE_DATA);
				
				break;
			case MENU_INITIALIZATION_DATA:
				try {
					// clear DB
					BabyTimeDbOpenHelper dbhelper = new BabyTimeDbOpenHelper(mContext);
					SQLiteDatabase db = dbhelper.getWritableDatabase();
					db.delete(Dbinfo.DB_TABLE_NAME, null, null);
					db.close();

					// clear Utils
					mUtils.clearUtilsValues(mContext);
				}catch (SQLException e){
					e.printStackTrace();
				}
				mSelectedDialog = 0;
				break;
			}
		}
	};

	private void googleDrive(int requestCode) {
		mCredential = GoogleAccountCredential.usingOAuth2(this, Arrays.asList(DriveScopes.DRIVE));
		startActivityForResult(mCredential.newChooseAccountIntent(), requestCode);			
	}

	private Drive getDriveService(GoogleAccountCredential credential) {
		return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
		.build();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case MENU_BACKUP_DATA :
			if (resultCode == RESULT_OK && data != null && data.getExtras() != null) 
			{
				String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				if (accountName != null) {
					mCredential.setSelectedAccountName(accountName);
					mService = getDriveService(mCredential);
					saveFileToDrive();
				}
			}else if( resultCode != RESULT_CANCELED ){
				startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
			}

			break;
		case MENU_RESTORE_DATA :
			if (resultCode == RESULT_OK && data != null && data.getExtras() != null) 
			{
				String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				if (accountName != null) {
					mCredential.setSelectedAccountName(accountName);
					mService = getDriveService(mCredential);
					getDownloadFileFromDrive();
				}
			}else if( resultCode != RESULT_CANCELED ){
				startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
			}
			break;
		}
	}
	private void saveFileToDrive() 
	{
		Thread t = new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				try 
				{
					// Create URI from real path
					java.io.File dbFile =  mContext.getDatabasePath(Dbinfo.DB_NAME);

					mFileUri = Uri.fromFile(dbFile);

					ContentResolver cR = mContext.getContentResolver();
					// File's binary content
					java.io.File fileContent = new java.io.File(mFileUri.getPath());
					FileContent mediaContent = new FileContent(cR.getType(mFileUri), fileContent);

					// File's meta data. 
					File body = new File();
					body.setTitle(fileContent.getName());
					body.setMimeType(cR.getType(mFileUri));

					File file = mService.files().insert(body, mediaContent).execute();
					if (file != null) 
					{
						showToast(mContext.getString(R.string.upload_complete).toString());
					}
				} catch (UserRecoverableAuthIOException e) {
					startActivityForResult(e.getIntent(), MENU_BACKUP_DATA);
				} catch (IOException e) {
					e.printStackTrace();

				}
			}
		});
		t.start();
	}

	private void getDownloadFileFromDrive()
	{
		Thread t = new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				File downloadFile = null;
				Files.List request = null;
				try {
					request = mService.files().list();
					do {

						FileList files = request.execute();

						for( File file : files.getItems() ){
							if( file.getTitle().equals(Dbinfo.DB_NAME)){
								downloadFile = file;
								break;
							}
						}
						request.setPageToken(files.getNextPageToken());
					} while (request.getPageToken() != null && request.getPageToken().length() > 0);


					if (downloadFile != null && downloadFile.getDownloadUrl() != null && downloadFile.getDownloadUrl().length() >0)
					{
						try
						{
							com.google.api.client.http.HttpResponse resp = 
									mService.getRequestFactory()
									.buildGetRequest(new GenericUrl(downloadFile.getDownloadUrl()))
									.execute();
							InputStream iStream = resp.getContent();
							try 
							{
								java.io.File dbFile =  mContext.getDatabasePath(Dbinfo.DB_NAME);
								storeFile(dbFile, iStream);
							} finally {
								iStream.close();
								showToast(mContext.getString(R.string.download_complete).toString());
							}

						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				} catch (UserRecoverableAuthIOException e) {
					startActivityForResult(e.getIntent(), MENU_RESTORE_DATA);
				}catch (IOException e) {
					request.setPageToken(null);
				}
			}
		});
		t.start();
	}


	public void showToast(final String toast) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void storeFile(java.io.File file, InputStream iStream)
	{
		try 
		{
			final OutputStream oStream = new FileOutputStream(file);
			try
			{
				try
				{
					final byte[] buffer = new byte[1024];
					int read;
					while ((read = iStream.read(buffer)) != -1)
					{
						oStream.write(buffer, 0, read);
					}
					oStream.flush();
				} finally {
					oStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Implement listener to get selected color value
	ColorPickerSwatch.OnColorSelectedListener colorpickerListener = new ColorPickerSwatch.OnColorSelectedListener(){
		@Override
		public void onColorSelected(int color) {
			BabyTimeSettingMenuAdapter.MenuItemModel item = mAdapter.getItem(mLastPosition);
			if (item!=null){
				item.colorSquare = color;
				mAdapter.notifyDataSetChanged();
				mUtils.setChangeColor(item._id, color);
				setResult(RESULT_OK);
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mUtils.setColorsToPref(this);
	}
}





















