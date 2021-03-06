package com.babytimechart.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import java.util.ArrayList;

public class BabyTimeDbOpenHelper extends SQLiteOpenHelper {

	public BabyTimeDbOpenHelper(Context context) {
		super(context, Dbinfo.DB_NAME, null, Dbinfo.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Dbinfo.DB_TABLE_NAME + "(" +
				"_id 	INTEGER PRIMARY KEY AUTOINCREMENT, " +
				Dbinfo.DB_TYPE 		+ " TEXT, " +
				Dbinfo.DB_DATE		+ " DATE, " + 
				Dbinfo.DB_S_TIME 	+ " INTEGER, " +			
				Dbinfo.DB_E_TIME 	+ " INTEGER, " +			
				Dbinfo.DB_MEMO 		+ " TEXT );");		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop table, DataBackup
		if( newVersion > oldVersion ){
			ArrayList<Bundle> arrBundle = new ArrayList<Bundle>();

            Cursor cursor = db.query(Dbinfo.DB_TABLE_NAME, null, null, null, null, null, "_id ASC");

			if( cursor != null )
			{
                while ( cursor.moveToNext()){
					Bundle bundle = new Bundle();
					bundle.putString(Dbinfo.DB_TYPE, cursor.getString(cursor.getColumnIndex(Dbinfo.DB_TYPE)));
					bundle.putString(Dbinfo.DB_DATE, cursor.getString(cursor.getColumnIndex(Dbinfo.DB_DATE)));
					bundle.putString(Dbinfo.DB_S_TIME, cursor.getString(cursor.getColumnIndex(Dbinfo.DB_S_TIME)));
					bundle.putString(Dbinfo.DB_E_TIME, cursor.getString(cursor.getColumnIndex(Dbinfo.DB_E_TIME)));
					bundle.putString(Dbinfo.DB_MEMO, cursor.getString(cursor.getColumnIndex(Dbinfo.DB_MEMO)));
					arrBundle.add(bundle);
				}

				cursor.close();

				db.execSQL("DROP TABLE IF EXISTS " + Dbinfo.DB_TABLE_NAME);
				onCreate(db);
				
				for(Bundle bundleData : arrBundle)
				{
					ContentValues cv = new ContentValues();
					cv.put(Dbinfo.DB_TYPE, bundleData.getString(Dbinfo.DB_TYPE));
					cv.put(Dbinfo.DB_DATE, bundleData.getString(Dbinfo.DB_DATE));
					cv.put(Dbinfo.DB_S_TIME, bundleData.getString(Dbinfo.DB_S_TIME));
					cv.put(Dbinfo.DB_E_TIME, bundleData.getString(Dbinfo.DB_E_TIME));
					cv.put(Dbinfo.DB_MEMO, bundleData.getString(Dbinfo.DB_MEMO));
					
					db.insert(Dbinfo.DB_TABLE_NAME, null, cv);
				}

			}else{
				db.execSQL("DROP TABLE IF EXISTS " + Dbinfo.DB_TABLE_NAME );
				onCreate(db);
			}
		}
	}
}














