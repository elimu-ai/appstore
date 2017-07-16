package ai.elimu.appstore.receiver;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ai.elimu.appstore.service.ApplicationInstallService;
import ai.elimu.appstore.service.ApplicationSynchronizationService;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(getClass().getName(), "onReceive");

        // Initiate background job for synchronizing applications with web server
        ComponentName componentName = new ComponentName(context, ApplicationSynchronizationService.class);
        JobInfo.Builder builder = new JobInfo.Builder(1, componentName);
        builder.setPeriodic(1000 * 60 * 60); // Every hour
        JobInfo jobInfo = builder.build();

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int resultId = jobScheduler.schedule(jobInfo);
        if (resultId > 0) {
            Log.i(getClass().getName(), "Job 1 scheduled with id: " + resultId);
        } else {
            Log.w(getClass().getName(), "Job 1 scheduling failed. Error id: " + resultId);
        }

        // Initiate background job for installing applications already downloaded to the SD card
        ComponentName componentName2 = new ComponentName(context, ApplicationInstallService.class);
        JobInfo.Builder builder2 = new JobInfo.Builder(2, componentName2);
        builder2.setPeriodic(1000 * 60 * 60); // Every hour
        JobInfo jobInfo2 = builder2.build();

        JobScheduler jobScheduler2 = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int resultId2 = jobScheduler2.schedule(jobInfo2);
        if (resultId2 > 0) {
            Log.i(getClass().getName(), "Job 2 scheduled with id: " + resultId2);
        } else {
            Log.w(getClass().getName(), "Job 2 scheduling failed. Error id: " + resultId2);
        }
    }
}
