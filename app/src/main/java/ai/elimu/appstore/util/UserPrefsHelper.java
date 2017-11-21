package ai.elimu.appstore.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import ai.elimu.appstore.onboarding.LicenseNumberActivity;
import ai.elimu.appstore.onboarding.LocaleActivity;
import ai.elimu.model.enums.Locale;

public class UserPrefsHelper {

    public static Locale getLocale(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String localeAsString = sharedPreferences.getString(LocaleActivity.PREF_LOCALE, null);
        if (TextUtils.isEmpty(localeAsString)) {
            Long appCollectionId = sharedPreferences.getLong(LicenseNumberActivity.PREF_APP_COLLECTION_ID, 0);
            if (appCollectionId > 0) {
                // A Custom Project does not have a Locale, so use fallback to English to prevent
                // NullPointerExceptions in activities using this method.
                return Locale.EN;
            } else {
                return null;
            }
        } else {
            Locale locale = Locale.valueOf(localeAsString);
            return locale;
        }
    }

    public static String getLicenseEmail(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String licenseEmail = sharedPreferences.getString(LicenseNumberActivity.PREF_LICENSE_EMAIL, null);
        return licenseEmail;
    }

    public static String getLicenseNumber(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String licenseNumber = sharedPreferences.getString(LicenseNumberActivity.PREF_LICENSE_NUMBER, null);
        return licenseNumber;
    }
}
