package ai.elimu.appstore.synchronization;

public class AppDownloadStatus {

    private boolean isDownloading = false;

    private int downloadProgress = 0;

    private String downloadProgressText;

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    public int getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(int downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    public String getDownloadProgressText() {
        return downloadProgressText;
    }

    public void setDownloadProgressText(String downloadProgressText) {
        this.downloadProgressText = downloadProgressText;
    }

}
