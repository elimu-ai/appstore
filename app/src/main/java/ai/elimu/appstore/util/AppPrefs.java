package ai.elimu.appstore.util;

import android.support.annotation.NonNull;

import ai.elimu.appstore.BaseApplication;

/**
 * Helper class for saving/getting app's shared preference values
 */
public class AppPrefs {

    public static final String PREF_APP_VERSION_CODE = "pref_app_version_code";

    public static final String PREF_LOCALE = "pref_locale";

    public static final String PREF_IS_REGISTERED = "pref_is_registered";

    public static final String PREF_LAST_SYNCHRONIZATION = "pref_last_synchronization";

    /**
     * Save application version code
     *
     * @param appVersionCode The version code which needs to be saved
     */
    public static void saveAppVersionCode(int appVersionCode) {
        BaseApplication.getSharedPreferences().edit().putInt(PREF_APP_VERSION_CODE,
                appVersionCode).apply();
    }

    /**
     * Get application version code
     *
     * @return The current application version code
     */
    public static int getAppVersionCode() {
        return BaseApplication.getSharedPreferences().getInt(PREF_APP_VERSION_CODE, 0);
    }

    /**
     * Save device's locale
     *
     * @param locale The input locale
     */
    public static void saveLocale(String locale) {
        BaseApplication.getSharedPreferences().edit().putString(PREF_LOCALE, locale).apply();
    }

    /**
     * Get current locale
     *
     * @return Current device's locale
     */
    public static String getLocale() {
        return BaseApplication.getSharedPreferences().getString(PREF_LOCALE, null);
    }

    /**
     * Save device's registration status
     *
     * @param isRegistered true if device is already registered, false if not
     */
    public static void setDeviceRegistered(boolean isRegistered) {
        BaseApplication.getSharedPreferences().edit().putBoolean(PREF_IS_REGISTERED,
                isRegistered).apply();
    }

    /**
     * Get device's registration status
     *
     * @return true if already registered, false if not
     */
    public static boolean isDeviceRegistered() {
        return BaseApplication.getSharedPreferences().getBoolean(PREF_IS_REGISTERED, false);
    }

    /**
     * Save last time that app synchronization was executed
     *
     * @param lastSyncTime The last synchronization time in millisecond
     */
    public static void saveLastSyncTime(long lastSyncTime) {
        BaseApplication.getSharedPreferences().edit().putLong(PREF_LAST_SYNCHRONIZATION,
                lastSyncTime).apply();
    }

    /**
     * Get application's last synchronization time
     *
     * @return Last synchronization time in millisecond
     */
    public static long getLastSyncTime() {
        return BaseApplication.getSharedPreferences().getLong(PREF_LAST_SYNCHRONIZATION, 0);
    }

}
