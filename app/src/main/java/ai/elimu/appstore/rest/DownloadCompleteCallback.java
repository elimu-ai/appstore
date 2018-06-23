package ai.elimu.appstore.rest;

import android.support.annotation.NonNull;

/**
 * Callback interface for notifying download status of an apk file, whether it has completed or failed
 */
public interface DownloadCompleteCallback {

    void onDownloadCompleted(@NonNull String tempApkDir, @NonNull String apkName);

    void onDownloadFailed(Integer fileSizeInKbsDownloaded);

}
