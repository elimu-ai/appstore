package ai.elimu.appstore.dao.converter;

import android.util.Log;

import org.greenrobot.greendao.converter.PropertyConverter;
import org.json.JSONArray;
import org.json.JSONException;
import ai.elimu.model.enums.content.NumeracySkill;
import timber.log.Timber;

import java.util.HashSet;
import java.util.Set;

public class NumeracySkillSetConverter implements PropertyConverter<Set<NumeracySkill>, String> {

    @Override
    public Set<NumeracySkill> convertToEntityProperty(String databaseValue) {
        Timber.d("convertToEntityProperty");

        Set<NumeracySkill> set = new HashSet<>();

        try {
            JSONArray jsonArray = new JSONArray(databaseValue);
            Timber.d("jsonArray: " + jsonArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                String value = jsonArray.getString(i);
                Timber.d("value: " + value);
                NumeracySkill numeracySkill = NumeracySkill.valueOf(value);
                set.add(numeracySkill);
            }
        } catch (JSONException e) {
            Log.e(getClass().getName(), null, e);
        }

        return set;
    }

    @Override
    public String convertToDatabaseValue(Set<NumeracySkill> entityProperty) {
        Timber.d("convertToDatabaseValue");

        String databaseValue = entityProperty.toString();
        Timber.d("databaseValue: " + databaseValue);
        return databaseValue;
    }
}
