package ai.elimu.appstore;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.securepreferences.SecurePreferences;

import org.greenrobot.greendao.database.Database;

import ai.elimu.appstore.dao.CustomDaoMaster;
import ai.elimu.appstore.dao.DaoSession;
import ai.elimu.appstore.util.AppPrefs;
import ai.elimu.appstore.util.VersionHelper;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import timber.log.Timber;

public class BaseApplication extends Application {

    //Name of the shared pref file. If null use the default shared prefs
    private static final String PREF_FILE_NAME = "app_store_preferences.xml";

    //user password/code used to generate encryption key.
    private static final String PREF_PASSWORD = "appstore123";

    private DaoSession daoSession;

    private Retrofit retrofit;

    private static SecurePreferences sSecurePreferences;

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        BaseApplication.sContext = getApplicationContext();
        // Log config
        if (BuildConfig.DEBUG) {
            // Log everything
            Timber.plant(new Timber.DebugTree());
        } else {
            // Only log warnings and errors
            Timber.plant(new Timber.Tree() {
                @Override
                protected void log(int priority, String tag, String message, Throwable throwable) {
                    if (priority == Log.WARN) {
                        Log.w(tag, message);
                    } else if (priority == Log.ERROR) {
                        Log.e(tag, message);
                    }
                }
            });
        }
        Timber.i("onCreate");

        // greenDAO config
        CustomDaoMaster.DevOpenHelper helper = new CustomDaoMaster.DevOpenHelper(this,
                "appstore-db");
        Database db = helper.getWritableDb();
        daoSession = new CustomDaoMaster(db).newSession();

        // Check if the application's versionCode was upgraded
        int oldVersionCode = AppPrefs.getAppVersionCode();
        int newVersionCode = VersionHelper.getAppVersionCode(getApplicationContext());
        if (oldVersionCode == 0) {
            AppPrefs.saveAppVersionCode(newVersionCode);
            oldVersionCode = newVersionCode;
        }
        if (oldVersionCode < newVersionCode) {
            Timber.i("Upgrading application from version " + oldVersionCode + " to " +
                    newVersionCode);
//            if (newVersionCode == ???) {
//                // Put relevant tasks required for upgrading here
//            }
            AppPrefs.saveAppVersionCode(newVersionCode);
        }
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    /**
     * Initializes Retrofit and makes it available to all activities.
     */
    public Retrofit getRetrofit() {
        Timber.i("getRetrofit");

        /**
         * Adding logging interceptor for printing out Retrofit request url
         * in debug mode
         */
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY :
                HttpLoggingInterceptor.Level.NONE);
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .retryOnConnectionFailure(true)
                .build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.REST_URL + "/")
                    .client(okHttpClient)
                    .build();
        }

        return retrofit;
    }

    /**
     * Provides {@SecurePreferences} singleton instance to access shared preferences
     *
     * @return A single instance of {@SecurePreferences}
     */
    public static SharedPreferences getSharedPreferences() {
        if (sSecurePreferences == null) {
            synchronized (BaseApplication.class) {
                if (sSecurePreferences == null) {
                    sSecurePreferences = new SecurePreferences(getAppContext(), PREF_PASSWORD,
                            PREF_FILE_NAME);
                    SecurePreferences.setLoggingEnabled(BuildConfig.DEBUG);
                }
            }
        }
        return sSecurePreferences;
    }

    public static Context getAppContext() {
        return BaseApplication.sContext;
    }
}
