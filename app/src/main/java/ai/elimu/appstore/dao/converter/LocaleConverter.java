package ai.elimu.appstore.dao.converter;

import android.util.Log;

import org.greenrobot.greendao.converter.PropertyConverter;
import ai.elimu.model.enums.Locale;

public class LocaleConverter implements PropertyConverter<Locale, String> {

    @Override
    public Locale convertToEntityProperty(String databaseValue) {
        Log.d(getClass().getName(), "convertToEntityProperty");

        Locale entityProperty = Locale.valueOf(databaseValue);
        Log.d(getClass().getName(), "entityProperty: " + entityProperty);
        return entityProperty;
    }

    @Override
    public String convertToDatabaseValue(Locale entityProperty) {
        Log.d(getClass().getName(), "convertToDatabaseValue");

        String databaseValue = entityProperty.toString();
        Log.d(getClass().getName(), "databaseValue: " + databaseValue);
        return databaseValue;
    }
}
