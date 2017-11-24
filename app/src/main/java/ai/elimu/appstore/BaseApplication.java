package ai.elimu.appstore;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.greendao.database.Database;

import ai.elimu.appstore.dao.CustomDaoMaster;
import ai.elimu.appstore.dao.DaoSession;
import ai.elimu.appstore.util.VersionHelper;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class BaseApplication extends Application {

    public static final String PREF_APP_VERSION_CODE = "pref_app_version_code";

    private DaoSession daoSession;

    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();

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
        CustomDaoMaster.DevOpenHelper helper = new CustomDaoMaster.DevOpenHelper(this, "appstore-db");
        Database db = helper.getWritableDb();
        daoSession = new CustomDaoMaster(db).newSession();

        // Check if the application's versionCode was upgraded
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int oldVersionCode = sharedPreferences.getInt(PREF_APP_VERSION_CODE, 0);
        int newVersionCode = VersionHelper.getAppVersionCode(getApplicationContext());
        if (oldVersionCode == 0) {
            sharedPreferences.edit().putInt(PREF_APP_VERSION_CODE, newVersionCode).commit();
            oldVersionCode = newVersionCode;
        }
        if (oldVersionCode < newVersionCode) {
            Timber.i("Upgrading application from version " + oldVersionCode + " to " + newVersionCode);
//            if (newVersionCode == ???) {
//                // Put relevant tasks required for upgrading here
//            }
            sharedPreferences.edit().putInt(PREF_APP_VERSION_CODE, newVersionCode).commit();
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

        /**
         * Gson object used in convert factory for serialization and deserialization of objects
         */
        Gson gson = new GsonBuilder().setLenient().create();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.REST_URL + "/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build();
        }

        return retrofit;
    }
}
