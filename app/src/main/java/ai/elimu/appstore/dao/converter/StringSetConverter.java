package ai.elimu.appstore.dao.converter;

import android.util.Log;

import org.greenrobot.greendao.converter.PropertyConverter;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashSet;
import java.util.Set;

import timber.log.Timber;

public class StringSetConverter implements PropertyConverter<Set, String> {

    @Override
    public Set convertToEntityProperty(String databaseValue) {
        Timber.d("convertToEntityProperty");

        Set<String> set = new HashSet<>();

        try {
            JSONArray jsonArray = new JSONArray(databaseValue);
            Timber.d("jsonArray: " + jsonArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                String value = jsonArray.getString(i);
                Timber.d("value: " + value);
                set.add(value);
            }
        } catch (JSONException e) {
            Log.e(getClass().getName(), null, e);
        }

        return set;
    }

    @Override
    public String convertToDatabaseValue(Set entityProperty) {
        Timber.d("convertToDatabaseValue");

        String databaseValue = entityProperty.toString();
        Timber.d("databaseValue: " + databaseValue);
        return databaseValue;
    }
}
