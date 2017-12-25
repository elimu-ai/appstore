package ai.elimu.appstore;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.securepreferences.SecurePreferences;

import org.greenrobot.greendao.database.Database;

import java.io.File;
import java.io.FilenameFilter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ai.elimu.appstore.dao.CustomDaoMaster;
import ai.elimu.appstore.dao.DaoSession;
import ai.elimu.appstore.util.UserPrefsHelper;
import ai.elimu.appstore.util.VersionHelper;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import timber.log.Timber;

public class BaseApplication extends Application {

    //Name of the shared pref file. If null use the default shared prefs
    private static final String PREF_FILE_NAME = "app_store_preferences.xml";

    //user password/code used to generate encryption key.
    private static String PREF_PASSWORD;

    private DaoSession daoSession;

    private Retrofit retrofit;

    private static SecurePreferences securePreferences;

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        PREF_PASSWORD = getKeyHash();
        context = getApplicationContext();

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

        VersionHelper.updateAppVersion(getApplicationContext());

        /**
         * Check and delete existing APKs to avoid manually deleting corrupt files before upgrading to version 2.0.9
         * of the Appstore application
         */
        int newVersionCode = VersionHelper.getAppVersionCode(context);
        if (newVersionCode < 2000009) {
            deleteExistingApks();
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
        if (securePreferences == null) {
            synchronized (BaseApplication.class) {
                if (securePreferences == null) {
                    securePreferences = new SecurePreferences(getAppContext(), PREF_PASSWORD,
                            PREF_FILE_NAME);
                    SecurePreferences.setLoggingEnabled(BuildConfig.DEBUG);
                }
            }
        }
        return securePreferences;
    }

    public static Context getAppContext() {
        return context;
    }

    /**
     * Get keystore hash value to use as secure preferences' password
     * @return The hash value generated from signing key
     */
    private String getKeyHash(){
        String keyHash = "";
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }

            return keyHash;
        }
        catch (PackageManager.NameNotFoundException e) {
            Timber.e(e);
            return "";
        }
        catch (NoSuchAlgorithmException e) {
            Timber.e(e);
            return "";
        }
    }

    private void deleteExistingApks() {

        /**
         * Skip checking & deleting APK files in the first start-up of application
         */
        if (UserPrefsHelper.getLocale(context) == null) {
            return;
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        /**
         * Get APKs directory
         */
        String language = UserPrefsHelper.getLocale(context).getLanguage();
        File apkDirectory = new File(Environment.getExternalStorageDirectory() + "/" +
                ".elimu-ai/appstore/apks/" + language);

        /**
         * Get all APK files for deleting
         */
        final File[] apkFiles = apkDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                return (name.endsWith(".apk"));
            }
        });

        /**
         * Delete APK files in background thread since this operation is timely unpredictable
         */
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for (final File file : apkFiles) {
                    file.delete();
                    Timber.i("APK " + file.getAbsolutePath() + " is deleted successfully");
                }
            }
        });

    }
}
