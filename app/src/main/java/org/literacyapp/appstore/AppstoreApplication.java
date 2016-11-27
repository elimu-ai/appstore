package org.literacyapp.appstore;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.greenrobot.greendao.database.Database;
import org.literacyapp.appstore.dao.CustomDaoMaster;
import org.literacyapp.appstore.dao.DaoSession;
import org.literacyapp.appstore.util.VersionHelper;

public class AppstoreApplication extends Application {

    public static final String PREF_APP_VERSION_CODE = "pref_app_version_code";

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate();

        // greenDAO config
        CustomDaoMaster.DevOpenHelper helper = new CustomDaoMaster.DevOpenHelper(this, "appstore-db");
        Database db = helper.getWritableDb();
        daoSession = new CustomDaoMaster(db).newSession();

        // Check if the application's versionCode was upgraded
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int oldVersionCode = sharedPreferences.getInt(PREF_APP_VERSION_CODE, 0);
        int newVersionCode = VersionHelper.getAppVersionCode(getApplicationContext());
        if ((oldVersionCode > 0) && (oldVersionCode < newVersionCode)) {
            Log.i(getClass().getName(), "Upgrading application from version " + oldVersionCode + " to " + newVersionCode);
//            if (newVersionCode == ???) {
//                // Put relevant tasks required for upgrading here
//            }
            sharedPreferences.edit().putInt(PREF_APP_VERSION_CODE, newVersionCode).commit();
        }
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
