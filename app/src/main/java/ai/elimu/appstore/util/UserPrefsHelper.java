package ai.elimu.appstore.util;

import android.content.Context;
import android.text.TextUtils;

import ai.elimu.model.enums.Locale;

public class UserPrefsHelper {

    public static Locale getLocale(Context context) {
        String localeAsString = AppPrefs.getLocale();
        if (TextUtils.isEmpty(localeAsString)) {
            Long appCollectionId = AppPrefs.getAppCollectionId();
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
        return AppPrefs.getLicenseEmail();
    }

    public static String getLicenseNumber(Context context) {
        return AppPrefs.getLicenseNumber();
    }
}
