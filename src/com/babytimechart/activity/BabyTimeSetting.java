package com.babytimechart.activity;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.activity.babytimechart.R;
import com.babytimechart.db.BabyTimeDbOpenHelper;
import com.babytimechart.db.Dbinfo;
import com.babytimechart.ui.BabyTimeSettingMenuAdapter;
import com.babytimechart.ui.ColorPickerSwatch;
import com.babytimechart.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BabyTimeSetting extends ListActivity {

    private static final int MENU_EAT 		= 100;
    private static final int MENU_PLAY 		= 101;
    private static final int MENU_SLEEP 	= 102;
    private static final int MENU_ETC 		= 103;
    private static final int MENU_PROFILE 	= 104;

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

    // dialog profile
    private AlertDialog mAlertDialog = null;  // profile or datepicker
    private EditText mEditTextName = null;		 // set Date
    private TextView mTextViewBirthday = null;		 // set Date
    private DatePicker mDatePicker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActinbar();
        initMenu();
        mContext = this;
    }

    private void setActinbar() {
        getActionBar().setTitle(getString(R.string.setting));
        getActionBar().setDisplayShowCustomEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(true);
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

        // manage
        mAdapter.addHeader(R.string.manege);
        mAdapter.addItem(MENU_BACKUP_DATA, R.string.data_backup, R.string.data_backup_explain, 0);
        mAdapter.addItem(MENU_RESTORE_DATA, R.string.data_restore, R.string.data_restore_eplain, 0);
        mAdapter.addItem(MENU_INITIALIZATION_DATA, R.string.data_initialization, R.string.data_initialization_explain, 0);

        setListAdapter(mAdapter);
        getListView().setDividerHeight(0);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        mLastPosition = position;
        BabyTimeSettingMenuAdapter.MenuItemModel item = mAdapter.getItem(position);
        switch ( item._id ){
            case MENU_EAT:
            case MENU_PLAY:
            case MENU_SLEEP:
            case MENU_ETC:

                ColorPickerDialog colorcalendar = ColorPickerDialog.newInstance(
                        R.string.color_picker_default_title, mColorChoices,
                        item.colorSquare, 5,ColorPickerDialog.SIZE_SMALL);

                colorcalendar.setOnColorSelectedListener(colorcalendarListener);
                colorcalendar.show(getFragmentManager(), "");
                break;
            case MENU_PROFILE:
            case MENU_BACKUP_DATA:
            case MENU_RESTORE_DATA:
            case MENU_INITIALIZATION_DATA:
                createAlerDialog(item._id);
                break;
        }

    }

    OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) { createAlerDialog(DATE_PICKER); }
    };

    public void createAlerDialog(int id){
        View view = null;
        String title = null;
        int dividerColor = 0;
        mSelectedDialog = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        switch ( id ){
            case MENU_PROFILE:
                view = getLayoutInflater().inflate(R.layout.activity_profile, null);
                title = mContext.getString(R.string.baby_profile);
                dividerColor = Color.BLACK;
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
                    mTextViewBirthday.setText(new SimpleDateFormat("yyyy / MM / dd").format(new Date(System.currentTimeMillis())));
                break;
            case MENU_BACKUP_DATA:
                title = mContext.getString(R.string.data_backup);
                dividerColor = Color.BLACK;
                builder.setPositiveButton(mContext.getString(R.string.confirm), mOnDialogBtnClickListener)
                        .setNegativeButton(mContext.getString(R.string.cancel), mOnDialogBtnClickListener)
                        .setMessage(R.string.data_backup_message);
                break;
            case MENU_RESTORE_DATA:
                title = mContext.getString(R.string.data_restore);
                dividerColor = Color.BLACK;
                builder.setPositiveButton(mContext.getString(R.string.confirm), mOnDialogBtnClickListener)
                        .setNegativeButton(mContext.getString(R.string.cancel), mOnDialogBtnClickListener)
                        .setMessage(R.string.data_restore_message);
                break;
            case MENU_INITIALIZATION_DATA:
                title = mContext.getString(R.string.data_initialization);
                dividerColor = Color.BLACK;
                builder.setPositiveButton(mContext.getString(R.string.confirm), mOnDialogBtnClickListener)
                        .setNegativeButton(mContext.getString(R.string.cancel), mOnDialogBtnClickListener)
                        .setMessage(R.string.data_initialization_message);
                break;
            case DATE_PICKER:
                view = getLayoutInflater().inflate(R.layout.activity_profile_datepicker, null);
                title = mContext.getString(R.string.selectdate);
                dividerColor = Color.BLACK;
                builder.setPositiveButton(mContext.getString(R.string.save), mOnDialogBtnClickListener)
                        .setNegativeButton(mContext.getString(R.string.cancel), mOnDialogBtnClickListener)
                        .setView(view);

                mDatePicker = (DatePicker)view.findViewById(R.id.datePicker);
                break;
        }

        mAlertDialog = builder
                .setTitle(title)
                .create();
        mAlertDialog.show();

        int titleId = getResources().getIdentifier("alertTitle", "id", "android");
        ((TextView)mAlertDialog.findViewById(titleId)).setTextColor(Color.BLACK);

        int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
        mAlertDialog.findViewById(titleDividerId).setBackgroundColor(dividerColor);
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

                        String strTemp = mDatePicker.getYear() + " / ";
                        if (mDatePicker.getYear() < 10)
                            strTemp = strTemp + "0" + (mDatePicker.getMonth() + 1) + " / ";
                        else
                            strTemp = strTemp + (mDatePicker.getMonth() + 1) + " / ";

                        if (mDatePicker.getDayOfMonth() < 10)
                            strTemp = strTemp + "0" + mDatePicker.getDayOfMonth();
                        else
                            strTemp = strTemp + mDatePicker.getDayOfMonth();

                        mTextViewBirthday.setText(strTemp);
                    }
                    mSelectedDialog = MENU_PROFILE;
                    break;
                case MENU_BACKUP_DATA:
                    mSelectedDialog = 0;
                    break;
                case MENU_RESTORE_DATA:
                    mSelectedDialog = 0;
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


    // Implement listener to get selected color value
    ColorPickerSwatch.OnColorSelectedListener colorcalendarListener = new ColorPickerSwatch.OnColorSelectedListener(){
        @Override
        public void onColorSelected(int color) {
            BabyTimeSettingMenuAdapter.MenuItemModel item = mAdapter.getItem(mLastPosition);
            if (item!=null){
                item.colorSquare = color;
                mAdapter.notifyDataSetChanged();
                mUtils.setChangeColor(item._id, color);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUtils.setColorsToPref(this);
    }
}





















