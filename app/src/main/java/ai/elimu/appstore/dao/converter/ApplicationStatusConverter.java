package ai.elimu.appstore.dao.converter;

import android.util.Log;

import org.greenrobot.greendao.converter.PropertyConverter;
import ai.elimu.model.enums.admin.ApplicationStatus;
import timber.log.Timber;

public class ApplicationStatusConverter implements PropertyConverter<ApplicationStatus, String> {

    @Override
    public ApplicationStatus convertToEntityProperty(String databaseValue) {
        Timber.d("convertToEntityProperty");

        ApplicationStatus entityProperty = ApplicationStatus.valueOf(databaseValue);
        Timber.d("entityProperty: " + entityProperty);
        return entityProperty;
    }

    @Override
    public String convertToDatabaseValue(ApplicationStatus entityProperty) {
        Timber.d("convertToDatabaseValue");

        String databaseValue = entityProperty.toString();
        Timber.d("databaseValue: " + databaseValue);
        return databaseValue;
    }
}
