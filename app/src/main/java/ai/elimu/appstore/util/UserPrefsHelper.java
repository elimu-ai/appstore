package ai.elimu.appstore.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import ai.elimu.appstore.LocaleActivity;
import ai.elimu.model.enums.Locale;

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
