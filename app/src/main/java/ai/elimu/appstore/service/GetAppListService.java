package ai.elimu.appstore.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit integration with the REST API for downloading applications list
 */
public interface GetAppListService {

    @GET("application/list")
    Call<ResponseBody> getApplicationList(@Query("deviceId") String deviceId,
                                          @Query("checksum") String checkSum,
                                          @Query("locale") String locale,
                                          @Query("deviceModel") String deviceModel,
                                          @Query("osVersion") int osVersion,
                                          @Query("applicationId") String applicationId,
                                          @Query("appVersionCode") int appVersionCode);
}
