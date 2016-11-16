package org.literacyapp.appstore.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import org.literacyapp.appstore.task.DownloadApplicationsAsyncTask;
import org.literacyapp.appstore.util.ConnectivityHelper;

public class ApplicationSynchronizationService extends JobService {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.i(getClass().getName(), "onStartJob");

        boolean isWifiEnabled = ConnectivityHelper.isWifiEnabled(getApplicationContext());
        Log.i(getClass().getName(), "isWifiEnabled: " + isWifiEnabled);

        boolean isWifiConnected = ConnectivityHelper.isWifiConnected(getApplicationContext());
        Log.i(getClass().getName(), "isWifiConnected: " + isWifiConnected);

        // Start processing work
        new DownloadApplicationsAsyncTask(getApplicationContext()).execute();
        // TODO: call jobFinished once AsyncTask completes

        boolean isWorkProcessingPending = false;
        return isWorkProcessingPending;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i(getClass().getName(), "onStopJob");

        // Job execution stopped, even before jobFinished was called.
        // TODO: stop processing work?

        boolean isJobToBeRescheduled = false;
        return isJobToBeRescheduled;
    }
}
