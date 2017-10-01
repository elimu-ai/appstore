package ai.elimu.appstore.dao.converter;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.Calendar;

import timber.log.Timber;

public class CalendarConverter implements PropertyConverter<Calendar, Long> {

    @Override
    public Calendar convertToEntityProperty(Long databaseValue) {
        Timber.d("convertToEntityProperty");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(databaseValue);
        Timber.d("calendar.getTime(): " + calendar.getTime());
        return calendar;
    }

    @Override
    public Long convertToDatabaseValue(Calendar entityProperty) {
        Timber.d("convertToDatabaseValue");

        Long databaseValue = entityProperty.getTimeInMillis();
        Timber.d("databaseValue: " + databaseValue);
        return databaseValue;
    }
}
