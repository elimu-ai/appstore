package org.literacyapp.appstore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.apache.log4j.Logger;
import org.literacyapp.appstore.service.SynchronizationService;

public class BootReceiver extends BroadcastReceiver {

    private Logger logger = Logger.getLogger(getClass());

    @Override
    public void onReceive(Context context, Intent intent) {
        logger.info("onReceive");

        logger.info("Starting SynchronizationService...");
        Intent synchronizationServiceIntent = new Intent(context, SynchronizationService.class);
        context.startService(synchronizationServiceIntent);
    }
}
