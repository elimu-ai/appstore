package ai.elimu.appstore.model;

import android.support.annotation.NonNull;

import ai.elimu.appstore.service.DownloadCompleteCallback;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class DownloadTaskInfo {

    private Call<ResponseBody> call;
    private ApplicationVersion applicationVersion;
    private DownloadCompleteCallback downloadCompleteCallback;

    public DownloadTaskInfo(@NonNull Call<ResponseBody> call,
                            @NonNull ApplicationVersion applicationVersion,
                            @NonNull DownloadCompleteCallback downloadCompleteCallback) {
        this.call = call;
        this.applicationVersion = applicationVersion;
        this.downloadCompleteCallback = downloadCompleteCallback;
    }

    public Call<ResponseBody> getCall() {
        return call;
    }

    public ApplicationVersion getApplicationVersion() {
        return applicationVersion;
    }

    public DownloadCompleteCallback getDownloadCompleteCallback() {
        return downloadCompleteCallback;
    }
}
