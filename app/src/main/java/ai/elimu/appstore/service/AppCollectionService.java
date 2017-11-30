package ai.elimu.appstore.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit integration with the REST API for downloading applications list using app collection id
 */
public interface AppCollectionService {

    /**
     * See https://github.com/elimu-ai/webapp/blob/master/REST_API_REFERENCE.md#read-applications
     */
    @GET("project/app-collections/{appCollectionId}/applications")
    Call<ResponseBody> getApplicationListByCollectionId(@Path("appCollectionId") long appCollectionId,
                                                        @Query("licenseEmail") String licenseEmail,
                                                        @Query("licenseNumber") String licenseNumber);
}
