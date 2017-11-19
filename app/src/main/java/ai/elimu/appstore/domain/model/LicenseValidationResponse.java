package ai.elimu.appstore.domain.model;

import com.google.gson.annotations.SerializedName;

public class LicenseValidationResponse {

    @SerializedName("result")
    private String result;

    @SerializedName("appCollectionId")
    private int appCollectionId;

    public String getResult() {
        return result;
    }

    public int getAppCollectionId() {
        return appCollectionId;
    }
}
