package org.literacyapp.appstore;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.apache.log4j.Logger;

public class SynchronizationService extends Service {

    private Logger logger = Logger.getLogger(getClass());

    public SynchronizationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        logger.info("onBind");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
