package org.literacyapp.appstore.dao.converter;

import android.util.Log;

import org.greenrobot.greendao.converter.PropertyConverter;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashSet;
import java.util.Set;

public class StringSetConverter implements PropertyConverter<Set, String> {

    @Override
    public Set convertToEntityProperty(String databaseValue) {
        Log.i(getClass().getName(), "convertToEntityProperty");

        Set<String> set = new HashSet<>();

        try {
            JSONArray jsonArray = new JSONArray(databaseValue);
            Log.i(getClass().getName(), "jsonArray: " + jsonArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                String value = jsonArray.getString(i);
                Log.i(getClass().getName(), "value: " + value);
                set.add(value);
            }
        } catch (JSONException e) {
            Log.e(getClass().getName(), null, e);
        }

        return set;
    }

    @Override
    public String convertToDatabaseValue(Set entityProperty) {
        Log.i(getClass().getName(), "convertToDatabaseValue");

        String databaseValue = entityProperty.toString();
        Log.i(getClass().getName(), "databaseValue: " + databaseValue);
        return databaseValue;
    }
}
