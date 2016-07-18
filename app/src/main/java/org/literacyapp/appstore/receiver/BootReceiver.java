package org.literacyapp.appstore.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.apache.log4j.Logger;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    private Logger logger = Logger.getLogger(getClass());

    @Override
    public void onReceive(Context context, Intent intent) {
        logger.info("onReceive");

        // Start alarm
        Intent alarmReceiverIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendarDaily = Calendar.getInstance();
        calendarDaily.set(Calendar.HOUR_OF_DAY, 3);
        calendarDaily.set(Calendar.MINUTE, 0);
        calendarDaily.set(Calendar.SECOND, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendarDaily.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
