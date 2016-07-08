package org.literacyapp.appstore.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.apache.log4j.Logger;

public class SynchronizationService extends Service {

    private Logger logger = Logger.getLogger(getClass());

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logger.info("onStartCommand");



        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        logger.info("onBind");
        return null;
    }
}
