package ai.elimu.appstore.util;

import android.support.annotation.NonNull;

import ai.elimu.appstore.BaseApplication;

public class SharedPreferenceManager {

    private static final String PREF_LICENSE_EMAIL = "pref_license_email";

    private static final String PREF_LICENSE_NUMBER = "pref_license_number";

    private static final String PREF_APP_COLLECTION_ID = "pref_app_collection_id";

    public static void saveLicenseEmail(@NonNull String licenseEmail) {
        BaseApplication.getMyPreferences().edit().putString(PREF_LICENSE_EMAIL, licenseEmail).apply();
    }

    public static String getLicenseEmail() {
        return BaseApplication.getMyPreferences().getString(PREF_LICENSE_EMAIL, "");
    }

    public static void saveLicenseNumber(@NonNull String licenseNumber) {
        BaseApplication.getMyPreferences().edit().putString(PREF_LICENSE_NUMBER, licenseNumber).apply();
    }

    public static String getLicenseNumber() {
        return BaseApplication.getMyPreferences().getString(PREF_LICENSE_NUMBER, "");
    }

    public static void saveAppCollectionId(@NonNull int appCollectionId) {
        BaseApplication.getMyPreferences().edit().putInt(PREF_APP_COLLECTION_ID, appCollectionId).apply();
    }

    public static int getAppCollectionId() {
        return BaseApplication.getMyPreferences().getInt(PREF_APP_COLLECTION_ID, -1);
    }
}
