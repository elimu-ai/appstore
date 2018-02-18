package ai.elimu.appstore.synchronization;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.util.Preconditions;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ai.elimu.appstore.BaseApplication;
import ai.elimu.appstore.BuildConfig;
import ai.elimu.appstore.R;
import ai.elimu.appstore.dao.ApplicationVersionDao;
import ai.elimu.appstore.model.AppDownloadStatus;
import ai.elimu.appstore.model.Application;
import ai.elimu.appstore.model.ApplicationVersion;
import ai.elimu.appstore.receiver.PackageUpdateReceiver;
import ai.elimu.appstore.util.UserPrefsHelper;
import ai.elimu.model.enums.Locale;
import ai.elimu.model.enums.admin.ApplicationStatus;
import timber.log.Timber;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

    private List<Application> applications;

    private List<AppDownloadStatus> appDownloadStatus;

    private Context context;

    private ApplicationVersionDao applicationVersionDao;

    private PackageUpdateReceiver packageUpdateReceiver;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public AppListAdapter(List<Application> applications,
                          @NonNull PackageUpdateReceiver packageUpdateReceiver) {
        this.applications = applications;
        this.packageUpdateReceiver = Preconditions.checkNotNull(packageUpdateReceiver);
        initAppDownloadStatus();
    }

    private void initAppDownloadStatus() {
        appDownloadStatus = new ArrayList<>(applications.size());
        for (int i = 0; i < applications.size(); i++) {
            appDownloadStatus.add(new AppDownloadStatus());
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Application application = applications.get(position);
        final AppDownloadStatus downloadStatus = appDownloadStatus.get(position);
        holder.textPkgName.setText(application.getPackageName());
        holder.textDownloadProgress.setText(downloadStatus.getDownloadProgressText());
        holder.progressBarDownload.setProgress(downloadStatus.getDownloadProgress());

        if (downloadStatus.isDownloading()) {
            holder.progressBarDownload.setVisibility(View.VISIBLE);
            holder.textDownloadProgress.setVisibility(View.VISIBLE);
            holder.btnDownload.setVisibility(View.GONE);
        } else {
            holder.progressBarDownload.setVisibility(View.GONE);
            holder.textDownloadProgress.setVisibility(View.GONE);
            holder.btnDownload.setVisibility(View.VISIBLE);
        }

        if (application.getApplicationStatus() != ApplicationStatus.ACTIVE) {
            // Do not allow APK download
            holder.textVersion.setText("ApplicationStatus: " + application
                    .getApplicationStatus());
            holder.btnDownload.setVisibility(View.VISIBLE);
            holder.btnDownload.setEnabled(false);
            holder.imageAppIcon.setImageDrawable(context.getDrawable(R.drawable.ic_launcher));
            holder.btnInstall.setVisibility(View.GONE);
            // TODO: hide applications that are not active?
        } else {
            holder.btnDownload.setEnabled(true);

            // Fetch the latest APK version
            List<ApplicationVersion> applicationVersions = applicationVersionDao.queryBuilder()
                    .where(ApplicationVersionDao.Properties.ApplicationId.eq(application.getId()))
                    .orderDesc(ApplicationVersionDao.Properties.VersionCode)
                    .list();
            final ApplicationVersion applicationVersion = applicationVersions.get(0);

            holder.textVersion.setText(context.getText(R.string.version) + ": " +
                    applicationVersion.getVersionCode() + " (" + (applicationVersion
                    .getFileSizeInKb() / 1024) + " MB)");

            // Check if the APK file has already been downloaded to the SD card
            Locale locale = UserPrefsHelper.getLocale(context);
            if (locale == null) {
                // The user typed a License for a custom Project, which does not use a specific Locale.
                // Fall back to English
                locale = Locale.EN;
            }
            String language = locale.getLanguage();
            String fileName = applicationVersion.getApplication().getPackageName() + "-" +
                    applicationVersion.getVersionCode() + ".apk";
            File apkDirectory = new File(Environment.getExternalStorageDirectory() + "/" +
                    ".elimu-ai/appstore/apks/" + language);
            final File existingApkFile = new File(apkDirectory, fileName);
            Timber.i("existingApkFile: " + existingApkFile);
            Timber.i("existingApkFile.exists(): " + existingApkFile.exists());
            if (existingApkFile.exists()) {
                holder.btnDownload.setVisibility(View.GONE);
                holder.btnInstall.setVisibility(View.VISIBLE);
            } else {
                if (!downloadStatus.isDownloading()) {
                    holder.btnDownload.setVisibility(View.VISIBLE);
                }
                holder.btnInstall.setVisibility(View.GONE);
            }

            // Check if the APK file has already been installed
            final PackageManager packageManager = context.getPackageManager();
            boolean isAppInstalled = true;
            try {
                packageManager.getApplicationInfo(application.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                isAppInstalled = false;
            }
            Timber.i("isAppInstalled: " + isAppInstalled);

            if (isAppInstalled) {
                holder.btnInstall.setVisibility(View.GONE);

                // Check if update is available for download
                try {
                    PackageInfo packageInfo = packageManager.getPackageInfo(application
                            .getPackageName(), 0);
                    int versionCodeInstalled = packageInfo.versionCode;
                    Timber.i("versionCodeInstalled: " + versionCodeInstalled);
                    if (applicationVersion.getVersionCode() > versionCodeInstalled) {
                        // Update is available for download/install

                        // Display version of the application currently installed
                        holder.textVersion.setText(holder.textVersion.getText() +
                                ". Installed: " + versionCodeInstalled);

                        // Change the button text
                        if (!existingApkFile.exists()) {
                            holder.btnDownload.setVisibility(View.VISIBLE);
                            holder.btnDownload.setText(R.string.download_update);
                        } else {
                            holder.btnInstall.setVisibility(View.VISIBLE);
                            holder.btnInstall.setText(R.string.install_update);
                        }
                    } else {
                        holder.btnDownload.setVisibility(View.GONE);
                        holder.btnInstall.setVisibility(View.GONE);
                    }

                    /**
                     * Extract icon from installed application
                     */
                    ApplicationInfo applicationInfo = packageManager.getApplicationInfo
                            (application.getPackageName(), PackageManager.GET_META_DATA);
                    Resources resources = packageManager.getResourcesForApplication(application
                            .getPackageName());
                    Drawable appIcon = resources.getDrawableForDensity(applicationInfo.icon,
                            resources.getDisplayMetrics().densityDpi, null);
                    holder.imageAppIcon.setImageDrawable(appIcon);
                } catch (PackageManager.NameNotFoundException e) {
                    Timber.e(e, null);
                }
            } else if (existingApkFile.exists()) {  //Extract app icon from downloaded APK if found
                PackageInfo packageInfo = packageManager.getPackageArchiveInfo(existingApkFile
                        .getAbsolutePath(), 0);
                packageInfo.applicationInfo.sourceDir = existingApkFile.getAbsolutePath();
                packageInfo.applicationInfo.publicSourceDir = existingApkFile.getAbsolutePath();
                Drawable appIcon = packageInfo.applicationInfo.loadIcon(packageManager);
                holder.imageAppIcon.setImageDrawable(appIcon);
            } else {
                holder.imageAppIcon.setImageDrawable(context.getDrawable(R.drawable.ic_launcher));
            }

            holder.btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Timber.i("buttonDownload onClick");

                    Timber.i("Downloading " + application.getPackageName() + " (version " +
                            applicationVersion.getVersionCode() + ")...");

                    holder.btnDownload.setVisibility(View.GONE);
                    holder.progressBarDownload.setVisibility(View.VISIBLE);
                    holder.textDownloadProgress.setVisibility(View.VISIBLE);
                    downloadStatus.setDownloading(true);
                    appDownloadStatus.set(position, downloadStatus);

                    // Initiate download of the latest APK version
                    Timber.i("applicationVersion: " + applicationVersion);

                    /**
                     * Listen to download completed event to update app icon
                     */
                    DownloadApplicationAsyncTask.DownloadCompleteCallback
                            downloadCompleteCallback = new DownloadApplicationAsyncTask
                            .DownloadCompleteCallback() {
                        @Override
                        public void onDownloadCompleted() {
                            downloadStatus.setDownloading(false);
                            appDownloadStatus.set(position, downloadStatus);

                            /**
                             * Set app icon upon download completion
                             */
                            PackageInfo packageInfo = packageManager.getPackageArchiveInfo
                                    (existingApkFile
                                    .getAbsolutePath(), 0);
                            packageInfo.applicationInfo.sourceDir = existingApkFile
                                    .getAbsolutePath();
                            packageInfo.applicationInfo.publicSourceDir = existingApkFile
                                    .getAbsolutePath();
                            Drawable appIcon = packageInfo.applicationInfo.loadIcon(packageManager);
                            holder.imageAppIcon.setImageDrawable(appIcon);
                        }
                    };

                    /**
                     * Listen to download progress update to reflect progress in data and UI,
                     * in case adapter is refreshed
                     */
                    DownloadApplicationAsyncTask.ProgressUpdateCallback progressUpdateCallback =
                            new DownloadApplicationAsyncTask.ProgressUpdateCallback() {
                                @Override
                                public void onProgressUpdated(String progressText, int progress) {
                                    downloadStatus.setDownloadProgressText(progressText);
                                    downloadStatus.setDownloadProgress(progress);
                                    appDownloadStatus.set(position, downloadStatus);
                                    holder.textDownloadProgress.setText(downloadStatus
                                            .getDownloadProgressText());
                                    holder.progressBarDownload.setProgress(progress);
                                }
                            };
                    DownloadApplicationAsyncTask downloadApplicationAsyncTask = new
                            DownloadApplicationAsyncTask(
                            context.getApplicationContext(),
                            holder.progressBarDownload,
                            holder.textDownloadProgress,
                            holder.btnInstall,
                            holder.btnDownload,
                            downloadCompleteCallback,
                            progressUpdateCallback
                    );
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        downloadApplicationAsyncTask.executeOnExecutor(AsyncTask
                                .THREAD_POOL_EXECUTOR, applicationVersion);
                    } else {
                        downloadApplicationAsyncTask.execute(applicationVersion);
                    }
                }
            });

            holder.btnInstall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Timber.i("btnInstall onClick");

                    // Initiate installation of the latest APK version
                    Timber.i("Installing " + applicationVersion.getApplication().getPackageName()
                            + " (version " + applicationVersion.getVersionCode() + ")...");

                    String fileName = applicationVersion.getApplication().getPackageName() + "-"
                            + applicationVersion.getVersionCode() + ".apk";
                    Timber.i("fileName: " + fileName);

                    String language = UserPrefsHelper.getLocale(context).getLanguage();
                    File apkDirectory = new File(Environment.getExternalStorageDirectory() + "/" +
                            ".elimu-ai/appstore/apks/" + language);

                    final File apkFile = new File(apkDirectory, fileName);
                    Timber.i("apkFile: " + apkFile);

                    /**
                     * Get all local versions of current APK file for deleting
                     */
                    final File[] apkFiles = apkDirectory.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File file, String name) {
                            return (name.startsWith(applicationVersion.getApplication()
                                    .getPackageName())
                                    && name.endsWith(".apk"));
                        }
                    });

                    PackageUpdateReceiver.PackageUpdateCallback packageUpdateCallback = new
                            PackageUpdateReceiver.PackageUpdateCallback() {
                                @Override
                                public void onInstallComplete(@NonNull String packageName) {
                                    Timber.i("onInstallComplete, package: " + packageName);
                                    holder.btnDownload.setVisibility(View.GONE);
                                    holder.btnInstall.setVisibility(View.GONE);
                                    executorService.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            for (final File file : apkFiles) {
                                                file.delete();
                                                Timber.i("APK " + file.getAbsolutePath() + " is " +
                                                        "deleted successfully");
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onUninstallComplete(@NonNull String packageName) {
                                    notifyDataSetChanged();
                                }
                            };
                    packageUpdateReceiver.setPackageUpdateCallback(packageUpdateCallback);


                    // Install APK file
                    // TODO: Check for root access. If root access, install APK without prompting
                    // for user confirmation.
                    if (Build.VERSION.SDK_INT >= 24) {
                        // See https://developer.android.com/guide/topics/permissions/requesting
                        // .html#install-unknown-apps
                        Uri apkUri = FileProvider.getUriForFile(context, BuildConfig
                                .APPLICATION_ID + ".provider", apkFile);
                        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                        intent.setData(apkUri);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        context.startActivity(intent);
                    } else {
                        Uri apkUri = Uri.fromFile(apkFile);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (applications != null) {
            return applications.size();
        } else {
            return 0;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        context = parent.getContext();
        BaseApplication baseApplication = (BaseApplication) context.getApplicationContext();
        applicationVersionDao = baseApplication.getDaoSession().getApplicationVersionDao();

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_app_list_item,
                parent, false);

        return new ViewHolder(view);
    }

    public void replaceData(List<Application> apps) {
        applications = apps;
        notifyDataSetChanged();
    }

    public List<Application> getData() {
        return this.applications;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textPkgName;

        private final TextView textVersion;

        private final Button btnDownload;

        private final Button btnInstall;

        private final ProgressBar progressBarDownload;

        private final TextView textDownloadProgress;

        private final ImageView imageAppIcon;

        public ViewHolder(View itemView) {
            super(itemView);

            textPkgName = itemView.findViewById(R.id.textViewPackageName);
            textVersion = itemView.findViewById(R.id.textViewVersion);
            btnDownload = itemView.findViewById(R.id.buttonDownload);
            btnInstall = itemView.findViewById(R.id.buttonInstall);
            progressBarDownload = itemView.findViewById(R.id.progressBarDownloadProgress);
            textDownloadProgress = itemView.findViewById(R.id.textViewDownloadProgress);
            imageAppIcon = itemView.findViewById(R.id.iv_app_icon);
        }

    }
}
