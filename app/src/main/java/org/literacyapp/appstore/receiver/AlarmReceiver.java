package org.literacyapp.appstore.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.apache.log4j.Logger;
import org.literacyapp.appstore.R;
import org.literacyapp.appstore.task.DownloadApplicationsAsyncTask;
import org.literacyapp.appstore.util.ConnectivityHelper;

public class AlarmReceiver extends BroadcastReceiver {

    private Logger logger = Logger.getLogger(getClass());

    @Override
    public void onReceive(Context context, Intent intent) {
        logger.info("onReceive");

        boolean isWifiEnabled = ConnectivityHelper.isWifiEnabled(context);
        logger.info("isWifiEnabled: " + isWifiEnabled);
        if (!isWifiEnabled) {
            Toast.makeText(context, context.getString(R.string.wifi_needs_to_be_enabled), Toast.LENGTH_SHORT).show();
        } else {
            new DownloadApplicationsAsyncTask(context).execute();
        }
    }
}
