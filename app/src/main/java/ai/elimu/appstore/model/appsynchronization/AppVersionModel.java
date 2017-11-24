package ai.elimu.appstore.model.appsynchronization;

import com.google.gson.annotations.SerializedName;

public class AppVersionModel {

    @SerializedName("application")
    private AppStatusModel appStatusModel;

    @SerializedName("timeUploaded")
    private AppUploadTimeModel appUploadTimeModel;

    @SerializedName("fileSizeInKb")
    private int fileSizeInKb;

    @SerializedName("fileUrl")
    private String fileUrl;

    @SerializedName("id")
    private long versionId;

    @SerializedName("contentType")
    private String contentType;

    @SerializedName("versionCode")
    private int versionCode;

    public AppStatusModel getAppStatusModel() {
        return appStatusModel;
    }

    public AppUploadTimeModel getAppUploadTimeModel() {
        return appUploadTimeModel;
    }

    public int getFileSizeInKb() {
        return fileSizeInKb;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public long getVersionId() {
        return versionId;
    }

    public String getContentType() {
        return contentType;
    }

    public int getVersionCode() {
        return versionCode;
    }
}
