package ai.elimu.appstore.util;

import android.content.Context;
import android.text.TextUtils;

import ai.elimu.model.enums.Locale;

public class UserPrefsHelper {

    public static Locale getLocale(Context context) {
        String localeAsString = AppPrefs.getLocale();
        if (TextUtils.isEmpty(localeAsString)) {
            return null;
        } else {
            Locale locale = Locale.valueOf(localeAsString);
            return locale;
        }
    }
}
