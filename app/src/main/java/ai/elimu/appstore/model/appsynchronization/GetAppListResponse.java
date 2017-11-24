package ai.elimu.appstore.model.appsynchronization;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetAppListResponse {

    @SerializedName("result")
    private String result;

    @SerializedName("applications")
    private List<ApplicationDetailModel> applicationDetails;

    public String getResult() {
        return result;
    }

    public List<ApplicationDetailModel> getApplicationDetails() {
        return applicationDetails;
    }
}
