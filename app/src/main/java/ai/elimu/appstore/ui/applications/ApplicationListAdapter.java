package ai.elimu.appstore.ui.applications;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import ai.elimu.appstore.BaseApplication;
import ai.elimu.appstore.R;
import ai.elimu.appstore.room.entity.Application;
import ai.elimu.appstore.room.entity.ApplicationVersion;
import ai.elimu.appstore.util.FileHelper;
import ai.elimu.appstore.util.InstallationHelper;
import ai.elimu.model.enums.admin.ApplicationStatus;
import timber.log.Timber;

public class ApplicationListAdapter extends RecyclerView.Adapter<ApplicationListAdapter.ApplicationViewHolder> {

    private final LayoutInflater layoutInflater;

    private final Context context;

    private List<Application> applications;

    private List<ApplicationVersion> applicationVersions;

    public ApplicationListAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public ApplicationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Timber.i("onCreateViewHolder");
        View itemView = layoutInflater.inflate(R.layout.activity_application_list_item, parent, false);
        return new ApplicationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ApplicationViewHolder viewHolder, int position) {
        Timber.i("onBindViewHolder");
        if (applications != null) {
            Application application = applications.get(position);
            viewHolder.textViewFirstLine.setText(application.getPackageName());
            viewHolder.textViewSecondLine.setText(
//                    application.getApplicationStatus().toString() + ", " +
//                    application.getLiteracySkills() + ", " +
//                    application.getNumeracySkills()
                    application.getApplicationStatus().toString()
            );

            if (application.getApplicationStatus() != ApplicationStatus.ACTIVE) {
                viewHolder.textViewFirstLine.setAlpha(0.5f);
                viewHolder.textViewSecondLine.setAlpha(0.5f);
            }

            // If the APK has been installed, display the "Launch" button
//            if (InstallationHelper.isApplicationInstalled(application.getPackageName(), context)) {
//                viewHolder.launchButton.setVisibility(View.VISIBLE);
//                viewHolder.launchButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Timber.i("onClick");
//
//                        Timber.i("Launching \"" + application.getPackageName() + "\"");
//                        PackageManager packageManager = context.getPackageManager();
//                        Intent launchIntent = packageManager.getLaunchIntentForPackage(application.getPackageName());
//                        Timber.i("launchIntent: " + launchIntent);
//                        context.startActivity(launchIntent);
//                    }
//                });
//            } else {
                // If the APK has been downloaded, but not yet installed, display the "Install" button
                // TODO

                // If the APK has not been downloaded, display the "Download" button
                ApplicationVersion applicationVersion = null;
                for (ApplicationVersion appVersion : applicationVersions) {
                    if (appVersion.getApplicationId() == application.getId()) {
                        applicationVersion = appVersion;
                        break;
                    }
                }
                Timber.i("applicationVersion: " + applicationVersion);
                if (applicationVersion != null) {
                    Timber.i("applicationVersion.getVersionCode(): " + applicationVersion.getVersionCode());
                    File apkFile = FileHelper.getApkFile(application.getPackageName(), applicationVersion.getVersionCode(), context);
                    Timber.i("apkFile: " + apkFile);
                    Timber.i("apkFile.exists(): " + apkFile.exists());
                    if (!apkFile.exists()) {
                        viewHolder.downloadButton.setVisibility(View.VISIBLE);
                        ApplicationVersion finalApplicationVersion = applicationVersion;
                        viewHolder.downloadButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Timber.i("viewHolder.downloadButton onClick");

                                // Initiate download of the APK file
                                BaseApplication baseApplication = (BaseApplication) context.getApplicationContext();
                                String fileUrl = baseApplication.getBaseUrl() + finalApplicationVersion.getFileUrl();
                                Timber.i("fileUrl: " +  fileUrl);
                                DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
                                long downloadId = downloadManager.enqueue(request);
                                Timber.i("downloadId: " +  downloadId);

                                // Replace download button with progress bar
                                // TODO
                            }
                        });
                    }
                }
//            }
        }
    }

    @Override
    public int getItemCount() {
        Timber.i("getItemCount");
        if (applications == null) {
            return 0;
        } else {
            return applications.size();
        }
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    public void setApplicationVersions(List<ApplicationVersion> applicationVersions) {
        this.applicationVersions = applicationVersions;
    }


    class ApplicationViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewFirstLine;
        private final TextView textViewSecondLine;

        private Button launchButton;
        private Button downloadButton;

        private ApplicationViewHolder(View itemView) {
            super(itemView);
            Timber.i("ApplicationViewHolder");

            textViewFirstLine = itemView.findViewById(R.id.textViewFirstLine);
            textViewSecondLine = itemView.findViewById(R.id.textViewSecondLine);

            launchButton = itemView.findViewById(R.id.list_item_launch_button);
            downloadButton = itemView.findViewById(R.id.list_item_download_button);
        }
    }
}
