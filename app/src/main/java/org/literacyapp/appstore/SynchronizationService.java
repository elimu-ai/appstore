package org.literacyapp.appstore;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SynchronizationService extends Service {

    public SynchronizationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
