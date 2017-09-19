package ai.elimu.appstore.dao.converter;

import android.util.Log;

import org.greenrobot.greendao.converter.PropertyConverter;
import ai.elimu.model.enums.Locale;
import timber.log.Timber;

public class LocaleConverter implements PropertyConverter<Locale, String> {

    @Override
    public Locale convertToEntityProperty(String databaseValue) {
        Timber.d("convertToEntityProperty");

        Locale entityProperty = Locale.valueOf(databaseValue);
        Timber.d("entityProperty: " + entityProperty);
        return entityProperty;
    }

    @Override
    public String convertToDatabaseValue(Locale entityProperty) {
        Timber.d("convertToDatabaseValue");

        String databaseValue = entityProperty.toString();
        Timber.d("databaseValue: " + databaseValue);
        return databaseValue;
    }
}
