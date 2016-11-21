package org.literacyapp.appstore.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.literacyapp.appstore.LocaleActivity;
import org.literacyapp.model.enums.Locale;

public class UserPrefsHelper {

    public static Locale getLocale(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String localeAsString = sharedPreferences.getString(LocaleActivity.PREF_LOCALE, null);
        if (TextUtils.isEmpty(localeAsString)) {
            return null;
        } else {
            Locale locale = Locale.valueOf(localeAsString);
            return locale;
        }
    }
}
