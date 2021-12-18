package ai.elimu.appstore.room;

import android.text.TextUtils;

import androidx.room.TypeConverter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashSet;
import java.util.Set;

import ai.elimu.model.v2.enums.admin.ApplicationStatus;
import ai.elimu.model.v2.enums.content.LiteracySkill;
import ai.elimu.model.v2.enums.content.NumeracySkill;
import timber.log.Timber;

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

    @TypeConverter
    public static Set<LiteracySkill> fromLiteracySkills(String value) {
        Set<LiteracySkill> literacySkills = new HashSet<>();
        if (!TextUtils.isEmpty(value)) {
            try {
                JSONArray jsonArray = new JSONArray(value);
                Timber.d("jsonArray: " + jsonArray);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String literacySkillAsString = jsonArray.getString(i);
                    Timber.d("literacySkillAsString: " + literacySkillAsString);
                    LiteracySkill literacySkill = LiteracySkill.valueOf(literacySkillAsString);
                    literacySkills.add(literacySkill);
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
        return literacySkills;
    }

    @TypeConverter
    public static String toLiteracySkills(Set<LiteracySkill> literacySkills) {
        String value = null;
        if (literacySkills != null) {
            value = literacySkills.toString();
        }
        Timber.i("value: " + value);
        return value;
    }

    @TypeConverter
    public static Set<NumeracySkill> fromNumeracySkills(String value) {
        Set<NumeracySkill> numeracySkills = new HashSet<>();
        if (!TextUtils.isEmpty(value)) {
            try {
                JSONArray jsonArray = new JSONArray(value);
                Timber.d("jsonArray: " + jsonArray);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String numeracySkillAsString = jsonArray.getString(i);
                    Timber.d("numeracySkillAsString: " + numeracySkillAsString);
                    NumeracySkill numeracySkill = NumeracySkill.valueOf(numeracySkillAsString);
                    numeracySkills.add(numeracySkill);
                }
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
        return numeracySkills;
    }

    @TypeConverter
    public static String toNumeracySkills(Set<NumeracySkill> numeracySkills) {
        String value = null;
        if (numeracySkills != null) {
            value = numeracySkills.toString();
        }
        Timber.i("value: " + value);
        return value;
    }
}
