package ai.elimu.appstore.util;

import ai.elimu.appstore.BaseApplication;

/**
 * Helper class for saving/getting app's shared preference values
 */
public class AppPrefs {

    public static final String PREF_APP_VERSION_CODE = "pref_app_version_code";

    public static void saveAppVersionCode(int appVersionCode) {
        BaseApplication.getSharedPreferences().edit().putInt(PREF_APP_VERSION_CODE, appVersionCode).apply();
    }

    public static int getAppVersionCode() {
        return BaseApplication.getSharedPreferences().getInt(PREF_APP_VERSION_CODE, 0);
    }

}
