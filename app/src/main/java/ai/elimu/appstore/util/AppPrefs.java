package ai.elimu.appstore.util;

import android.support.annotation.NonNull;

import ai.elimu.appstore.BaseApplication;

/**
 * Helper class for saving/getting app's shared preference values
 */
public class AppPrefs {

    public static final String PREF_APP_VERSION_CODE = "pref_app_version_code";

    public static final String PREF_LICENSE_OPTION = "pref_license_option";

    public static final String PREF_LOCALE = "pref_locale";

    public static final String PREF_IS_REGISTERED = "pref_is_registered";

    public static final String PREF_LICENSE_EMAIL = "pref_license_email";

    public static final String PREF_LICENSE_NUMBER = "pref_license_number";

    public static final String PREF_APP_COLLECTION_ID = "pref_app_collection_id";

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
     * Save user's license option
     *
     * @param licenseOption The input license option (yes/no)
     */
    public static void saveLicenseOption(String licenseOption) {
        BaseApplication.getSharedPreferences().edit().putString(PREF_LICENSE_OPTION,
                licenseOption).apply();
    }

    /**
     * Get user's license option
     *
     * @return User's license option
     */
    public static String getLicenseOption() {
        return BaseApplication.getSharedPreferences().getString(PREF_LICENSE_OPTION, null);
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
    public static void saveRegisterStatus(boolean isRegistered) {
        BaseApplication.getSharedPreferences().edit().putBoolean(PREF_IS_REGISTERED,
                isRegistered).apply();
    }

    /**
     * Get device's registration status
     *
     * @return true if already registered, false if not
     */
    public static boolean getRegisterStatus() {
        return BaseApplication.getSharedPreferences().getBoolean(PREF_IS_REGISTERED, false);
    }

    /**
     * Save license email
     *
     * @param licenseEmail The licensed email
     */
    public static void saveLicenseEmail(@NonNull String licenseEmail) {
        BaseApplication.getSharedPreferences().edit().putString(PREF_LICENSE_EMAIL, licenseEmail)
                .apply();
    }

    /**
     * Get licensed email stored in device
     *
     * @return The stored license
     */
    public static String getLicenseEmail() {
        return BaseApplication.getSharedPreferences().getString(PREF_LICENSE_EMAIL, null);
    }

    /**
     * Save license number
     *
     * @param licenseNumber The input license number
     */
    public static void saveLicenseNumber(@NonNull String licenseNumber) {
        BaseApplication.getSharedPreferences().edit().putString(PREF_LICENSE_NUMBER,
                licenseNumber).apply();
    }

    /**
     * Get user's license number
     *
     * @return User's license number
     */
    public static String getLicenseNumber() {
        return BaseApplication.getSharedPreferences().getString(PREF_LICENSE_NUMBER, null);
    }

    /**
     * Save app collection id for synchronizing apps
     *
     * @param appCollectionId App collection id
     */
    public static void saveAppCollectionId(long appCollectionId) {
        BaseApplication.getSharedPreferences().edit().putLong(PREF_APP_COLLECTION_ID,
                appCollectionId).apply();
    }

    /**
     * Get app collection id stored in device
     *
     * @return Stored app collection id
     */
    public static long getAppCollectionId() {
        return BaseApplication.getSharedPreferences().getLong(PREF_APP_COLLECTION_ID, 0);
    }

}
