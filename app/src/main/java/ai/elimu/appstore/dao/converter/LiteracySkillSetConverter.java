package ai.elimu.appstore.dao.converter;

import android.util.Log;

import org.greenrobot.greendao.converter.PropertyConverter;
import org.json.JSONArray;
import org.json.JSONException;
import ai.elimu.model.enums.content.LiteracySkill;

import java.util.HashSet;
import java.util.Set;

public class LiteracySkillSetConverter implements PropertyConverter<Set<LiteracySkill>, String> {

    @Override
    public Set<LiteracySkill> convertToEntityProperty(String databaseValue) {
        Log.d(getClass().getName(), "convertToEntityProperty");

        Set<LiteracySkill> set = new HashSet<>();

        try {
            JSONArray jsonArray = new JSONArray(databaseValue);
            Log.d(getClass().getName(), "jsonArray: " + jsonArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                String value = jsonArray.getString(i);
                Log.d(getClass().getName(), "value: " + value);
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
        Log.d(getClass().getName(), "convertToDatabaseValue");

        String databaseValue = entityProperty.toString();
        Log.d(getClass().getName(), "databaseValue: " + databaseValue);
        return databaseValue;
    }
}
