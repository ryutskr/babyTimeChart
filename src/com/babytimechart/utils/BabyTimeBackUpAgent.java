package com.babytimechart.utils;

import java.io.File;
import java.io.IOException;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class BabyTimeBackUpAgent extends BackupAgentHelper {

	static final String PREFS = "Setting";
	static final String PREFS_BACKUP_KEY = "Setting_prefs";

	static final String DB_FILE = "BabyTimeChat.db";
	static final String DB_FILE_BACKUP_KEY = "BabyTimeChat_db";

	String DATABASE_NAME = "madcontrollerdb";
	String DATABASE_FILE_NAME = "madcontrollerdb.db";

	@Override
	public void onCreate() {
		super.onCreate();

		SharedPreferencesBackupHelper prefhelper = new SharedPreferencesBackupHelper(this, PREFS);
		addHelper(PREFS_BACKUP_KEY, prefhelper);

		FileBackupHelper filehelper = new FileBackupHelper(this, DB_FILE);
		addHelper(DB_FILE_BACKUP_KEY, filehelper);
	}

	@Override
	public File getFilesDir() {
		File path = getDatabasePath(DB_FILE);
		Log.i("1111", "getFilesDir");
		return path.getParentFile();
	}

	@Override
	public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
			ParcelFileDescriptor newState) throws IOException {
		super.onBackup(oldState, data, newState);
		Log.i("1111", "BabyTimeBackUpAgent onBackup");
	}

	@Override
	public void onRestore(BackupDataInput data, int appVersionCode,
			ParcelFileDescriptor newState) throws IOException {

		Log.i("1111", "BabyTimeBackUpAgent onRestore");

		PackageInfo info;
		try {
			String name = getPackageName();
			info = getPackageManager().getPackageInfo(name,0);
		} catch (NameNotFoundException nnfe) {
			info = null;
		}

		int version;
		if (info != null) {
			version = info.versionCode;
		}
		super.onRestore(data, appVersionCode, newState);
	}

}
