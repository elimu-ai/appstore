package ai.elimu.appstore.dao.converter;

import android.util.Log;

import org.greenrobot.greendao.converter.PropertyConverter;
import org.json.JSONArray;
import org.json.JSONException;
import ai.elimu.model.enums.content.LiteracySkill;
import timber.log.Timber;

import java.util.HashSet;
import java.util.Set;

public class LiteracySkillSetConverter implements PropertyConverter<Set<LiteracySkill>, String> {

    @Override
    public Set<LiteracySkill> convertToEntityProperty(String databaseValue) {
        Timber.d("convertToEntityProperty");

        Set<LiteracySkill> set = new HashSet<>();

        try {
            JSONArray jsonArray = new JSONArray(databaseValue);
            Timber.d("jsonArray: " + jsonArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                String value = jsonArray.getString(i);
                Timber.d("value: " + value);
                LiteracySkill literacySkill = LiteracySkill.valueOf(value);
                set.add(literacySkill);
            }
        } catch (JSONException e) {
            Log.e(getClass().getName(), null, e);
        }

        return set;
    }

    @Override
    public String convertToDatabaseValue(Set<LiteracySkill> entityProperty) {
        Timber.d("convertToDatabaseValue");

        String databaseValue = entityProperty.toString();
        Timber.d("databaseValue: " + databaseValue);
        return databaseValue;
    }
}
