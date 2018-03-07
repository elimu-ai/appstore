package ai.elimu.appstore.service.download;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Retrofit integration with the REST API for downloading application file
 */
public interface DownloadApplicationService {

    @Streaming
    @GET
    Call<ResponseBody> downloadApplicationFile(@Url String fileUrl);
}
