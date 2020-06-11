package ai.elimu.appstore.room;

import android.text.TextUtils;

import androidx.room.TypeConverter;

import ai.elimu.model.enums.admin.ApplicationStatus;

/**
 * See https://developer.android.com/training/data-storage/room/referencing-data
 */
public class EnumConverter {

    @TypeConverter
    public static ApplicationStatus fromApplicationStatus(String value) {
        ApplicationStatus applicationStatus = null;
        if (!TextUtils.isEmpty(value)) {
            applicationStatus = ApplicationStatus.valueOf(value);
        }
        return applicationStatus;
    }

    @TypeConverter
    public static String toApplicationStatus(ApplicationStatus applicationStatus) {
        String value = null;
        if (applicationStatus != null) {
            value = applicationStatus.toString();
        }
        return value;
    }
}
