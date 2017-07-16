package ai.elimu.appstore.dao.converter;

import android.util.Log;

import org.greenrobot.greendao.converter.PropertyConverter;
import ai.elimu.model.enums.admin.ApplicationStatus;

public class ApplicationStatusConverter implements PropertyConverter<ApplicationStatus, String> {

    @Override
    public ApplicationStatus convertToEntityProperty(String databaseValue) {
        Log.d(getClass().getName(), "convertToEntityProperty");

        ApplicationStatus entityProperty = ApplicationStatus.valueOf(databaseValue);
        Log.d(getClass().getName(), "entityProperty: " + entityProperty);
        return entityProperty;
    }

    @Override
    public String convertToDatabaseValue(ApplicationStatus entityProperty) {
        Log.d(getClass().getName(), "convertToDatabaseValue");

        String databaseValue = entityProperty.toString();
        Log.d(getClass().getName(), "databaseValue: " + databaseValue);
        return databaseValue;
    }
}
