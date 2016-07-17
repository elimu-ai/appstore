package org.literacyapp.appstore.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.apache.log4j.Logger;

public class AlarmReceiver extends BroadcastReceiver {

    private Logger logger = Logger.getLogger(getClass());

    @Override
    public void onReceive(Context context, Intent intent) {
        logger.info("onReceive");


    }
}
