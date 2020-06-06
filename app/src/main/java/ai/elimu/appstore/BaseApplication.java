package ai.elimu.appstore;

import android.app.Application;
import android.util.Log;

import ai.elimu.appstore.util.VersionHelper;
import timber.log.Timber;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        Log.i(getClass().getName(), "onCreate");
        super.onCreate();

        // Log config
        Timber.plant(new Timber.DebugTree());
        Timber.i("onCreate");

        VersionHelper.updateAppVersion(getApplicationContext());
    }
}
