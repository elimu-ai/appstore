package ai.elimu.appstore.synchronization;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ai.elimu.appstore.BaseApplication;
import ai.elimu.appstore.BuildConfig;
import ai.elimu.appstore.R;
import ai.elimu.appstore.dao.ApplicationVersionDao;
import ai.elimu.appstore.model.Application;
import ai.elimu.appstore.model.ApplicationVersion;
import ai.elimu.appstore.receiver.InstallCompleteReceiver;
import ai.elimu.appstore.util.UserPrefsHelper;
import ai.elimu.model.enums.admin.ApplicationStatus;
import timber.log.Timber;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

    private List<Application> applications;

    private Context context;

    private ApplicationVersionDao applicationVersionDao;

    private InstallCompleteReceiver installCompleteReceiver;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public AppListAdapter(List<Application> applications,
                          @NonNull InstallCompleteReceiver installCompleteReceiver) {
        this.applications = applications;
        this.installCompleteReceiver = Preconditions.checkNotNull(installCompleteReceiver);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Application application = applications.get(position);
        holder.mTextPkgName.setText(application.getPackageName());

        if (application.getApplicationStatus() != ApplicationStatus.ACTIVE) {
            // Do not allow APK download
            holder.mTextVersion.setText("ApplicationStatus: " + application
                    .getApplicationStatus());
            holder.mBtnDownload.setVisibility(View.VISIBLE);
            holder.mBtnDownload.setEnabled(false);
            // TODO: hide applications that are not active?
        } else {
            holder.mBtnDownload.setEnabled(true);

            // Fetch the latest APK version
            List<ApplicationVersion> applicationVersions = applicationVersionDao.queryBuilder()
                    .where(ApplicationVersionDao.Properties.ApplicationId.eq(application.getId()))
                    .orderDesc(ApplicationVersionDao.Properties.VersionCode)
                    .list();
            final ApplicationVersion applicationVersion = applicationVersions.get(0);

            holder.mTextVersion.setText(context.getText(R.string.version) + ": " +
                    applicationVersion.getVersionCode() + " (" + (applicationVersion
                    .getFileSizeInKb() / 1024) + " MB)");

            // Check if the APK file has already been downloaded to the SD card
            String language = UserPrefsHelper.getLocale(context).getLanguage();
            String fileName = applicationVersion.getApplication().getPackageName() + "-" +
                    applicationVersion.getVersionCode() + ".apk";
            File apkDirectory = new File(Environment.getExternalStorageDirectory() + "/" +
                    ".elimu-ai/appstore/apks/" + language);
            File existingApkFile = new File(apkDirectory, fileName);
            Timber.i("existingApkFile: " + existingApkFile);
            Timber.i("existingApkFile.exists(): " + existingApkFile.exists());
            if (existingApkFile.exists()) {
                holder.mBtnDownload.setVisibility(View.GONE);
                holder.mBtnInstall.setVisibility(View.VISIBLE);
            } else {
                holder.mBtnDownload.setVisibility(View.VISIBLE);
                holder.mBtnInstall.setVisibility(View.GONE);
            }

            // Check if the APK file has already been installed
            PackageManager packageManager = context.getPackageManager();
            boolean isAppInstalled = true;
            try {
                packageManager.getApplicationInfo(application.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                isAppInstalled = false;
            }
            Timber.i("isAppInstalled: " + isAppInstalled);

            if (isAppInstalled) {
                holder.mBtnInstall.setVisibility(View.GONE);

                // Check if update is available for download
                try {
                    PackageInfo packageInfo = packageManager.getPackageInfo(application
                            .getPackageName(), 0);
                    int versionCodeInstalled = packageInfo.versionCode;
                    Timber.i("versionCodeInstalled: " + versionCodeInstalled);
                    if (applicationVersion.getVersionCode() > versionCodeInstalled) {
                        // Update is available for download/install

                        // Display version of the application currently installed
                        holder.mTextVersion.setText(holder.mTextVersion.getText() +
                                ". Installed: " + versionCodeInstalled);

                        // Change the button text
                        if (!existingApkFile.exists()) {
                            holder.mBtnDownload.setVisibility(View.VISIBLE);
                            holder.mBtnDownload.setText(R.string.download_update);
                        } else {
                            holder.mBtnInstall.setVisibility(View.VISIBLE);
                            holder.mBtnInstall.setText(R.string.install_update);
                        }
                    } else {
                        holder.mBtnDownload.setVisibility(View.GONE);
                        holder.mBtnInstall.setVisibility(View.GONE);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Timber.e(e, null);
                }
            }

            holder.mBtnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Timber.i("buttonDownload onClick");

                    Timber.i("Downloading " + application.getPackageName() + " (version " +
                            applicationVersion.getVersionCode() + ")...");

                    holder.mBtnDownload.setVisibility(View.GONE);
                    holder.mPbDownload.setVisibility(View.VISIBLE);
                    holder.mTextDownloadProgress.setVisibility(View.VISIBLE);

                    // Initiate download of the latest APK version
                    Timber.i("applicationVersion: " + applicationVersion);
                    new DownloadApplicationAsyncTask(
                            context.getApplicationContext(),
                            holder.mPbDownload,
                            holder.mTextDownloadProgress,
                            holder.mBtnInstall,
                            holder.mBtnDownload
                    ).execute(applicationVersion);
                }
            });

            holder.mBtnInstall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Timber.i("mBtnInstall onClick");

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

                    InstallCompleteReceiver.InstallCompleteCallback installCompleteCallback = new
                            InstallCompleteReceiver.InstallCompleteCallback() {
                                @Override
                                public void onInstallComplete(@NonNull String packageName) {
                                    Timber.i("onInstallComplete, package: " + packageName);
                                    holder.mBtnDownload.setVisibility(View.GONE);
                                    holder.mBtnInstall.setVisibility(View.GONE);
                                    executorService.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            for (final File file : apkFiles) {
                                                file.delete();
                                                Timber.i("APK " + file.getAbsolutePath() + " is deleted successfully");
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onUninstallComplete(@NonNull String packageName) {
                                    notifyDataSetChanged();
                                }
                            };
                    installCompleteReceiver.setInstallCompleteCallback(installCompleteCallback);


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

        private final TextView mTextPkgName;

        private final TextView mTextVersion;

        private final Button mBtnDownload;

        private final Button mBtnInstall;

        private final ProgressBar mPbDownload;

        private final TextView mTextDownloadProgress;

        public ViewHolder(View itemView) {
            super(itemView);

            mTextPkgName = itemView.findViewById(R.id.textViewPackageName);
            mTextVersion = itemView.findViewById(R.id.textViewVersion);
            mBtnDownload = itemView.findViewById(R.id.buttonDownload);
            mBtnInstall = itemView.findViewById(R.id.buttonInstall);
            mPbDownload = itemView.findViewById(R.id.progressBarDownloadProgress);
            mTextDownloadProgress = itemView.findViewById(R.id.textViewDownloadProgress);
        }

    }
}
