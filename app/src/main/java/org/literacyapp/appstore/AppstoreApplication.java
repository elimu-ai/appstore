package org.literacyapp.appstore;

import android.app.Application;
import android.util.Log;

public class AppstoreApplication extends Application {

//    private SQLiteDatabase db;
//    private DaoMaster daoMaster;
//    private DaoSession daoSession;

    @Override
    public void onCreate() {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate();

        // TODO: greenDAO config
    }
}
