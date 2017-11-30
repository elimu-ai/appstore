package ai.elimu.appstore.util;

import ai.elimu.appstore.BaseApplication;

/**
 * Helper class for saving/getting app's shared preference values
 */
public class AppPrefs {

    public static final String PREF_APP_VERSION_CODE = "pref_app_version_code";

    public static final String PREF_LICENSE_OPTION = "pref_license_option";

    public static final String PREF_LOCALE = "pref_locale";

    /**
     * Save application version code
     *
     * @param appVersionCode The version code which needs to be saved
     */
    public static void saveAppVersionCode(int appVersionCode) {
        BaseApplication.getSharedPreferences().edit().putInt(PREF_APP_VERSION_CODE, appVersionCode).apply();
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

}
