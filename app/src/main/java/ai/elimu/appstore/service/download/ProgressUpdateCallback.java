package ai.elimu.appstore.service.download;

public interface ProgressUpdateCallback {

    void onProgressUpdated(String progressText, int progress);
}
