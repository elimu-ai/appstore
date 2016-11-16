package org.literacyapp.appstore.receiver;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.literacyapp.appstore.service.ApplicationSynchronizationService;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(getClass().getName(), "onReceive");

        // Initiate background job for synchronizing applications
        ComponentName componentName = new ComponentName(context, ApplicationSynchronizationService.class);
        JobInfo.Builder builder = new JobInfo.Builder(1, componentName);
        builder.setRequiresDeviceIdle(true);
        JobInfo jobInfo = builder.build();

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int resultId = jobScheduler.schedule(jobInfo);
        if (resultId > 0) {
            Log.i(getClass().getName(), "Job scheduled with id: " + resultId);
        } else {
            Log.w(getClass().getName(), "Job scheduling failed. Error id: " + resultId);
        }
    }
}
