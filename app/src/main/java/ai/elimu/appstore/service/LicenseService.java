package ai.elimu.appstore.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit integration with the REST API for downloading License information.
 */
public interface LicenseService {

    /**
     * See https://github.com/elimu-ai/webapp/blob/master/REST_API_REFERENCE.md#read
     */
    @GET("project/licenses")
    Call<ResponseBody> getLicense(@Query("licenseEmail") String licenseEmail, @Query("licenseNumber") String licenseNumber);
}
