package ai.elimu.appstore.data.network;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ai.elimu.appstore.BuildConfig;
import ai.elimu.appstore.domain.model.LicenseValidationResponse;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {


    private static final String BASE_API_URL = BuildConfig.REST_URL + "/";

    private static ApiEndpointInterface sApiEndpoint;

    private static volatile Retrofit sRetrofit;

    /**
     * Use this method to obtaining Retrofit instance
     *
     * @param context The context where Retrofit instance is used
     * @return a singleton instance of Retrofit
     */
    private static Retrofit getRetrofit(final Context context) {
        if (sRetrofit == null) {
            synchronized (ApiManager.class) {
                if (sRetrofit == null) {
                    //Set log
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    final boolean isLog = BuildConfig.DEBUG;
                    logging.setLevel(isLog ? HttpLoggingInterceptor.Level.BODY :
                            HttpLoggingInterceptor.Level.NONE);
                    //Create cache
                    File file = new File(context.getCacheDir(), "response");

                    //Add log and set time out
                    final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .readTimeout(60, TimeUnit.SECONDS)
                            .cache(new Cache(file, 10 * 1024 * 1024)) //10 MB
                            .addNetworkInterceptor(new NetworkInterceptor())
                            .addInterceptor(logging)
                            .retryOnConnectionFailure(true)
                            .build();

                    Gson gson = new GsonBuilder()
                            .setLenient()
                            .create();

                    sRetrofit = new Retrofit.Builder()
                            .baseUrl(BASE_API_URL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .client(okHttpClient).build();
                }
            }
        }

        return sRetrofit;
    }

    /**
     * Use this method to obtain the Api endpoint instance where Retrofit request definitions are
     * declared
     *
     * @param context The context where APIs are consumed
     * @return The Api endpoint instance to access network requests
     */
    private static ApiEndpointInterface getApiEndpoint(Context context) {
        if (sApiEndpoint == null) {
            synchronized (ApiManager.class) {
                if (sApiEndpoint == null) {
                    sApiEndpoint = getRetrofit(context).create(ApiEndpointInterface.class);
                }
            }
        }
        return sApiEndpoint;
    }

    /**
     * Enqueue a network request to validate project license
     *
     * @param context                           The context where API call is executed
     * @param licenseEmail                      The licensed email
     * @param licenseNumber                     The license number
     * @param licenseValidationResponseCallback The callback containing validation response
     */
    public static void validateLicense(Context context,
                                       String licenseEmail,
                                       String licenseNumber,
                                       Callback<LicenseValidationResponse>
                                               licenseValidationResponseCallback) {

        Call<LicenseValidationResponse> call = getApiEndpoint(context).validateLicense(licenseEmail,
                licenseNumber);
        call.enqueue(licenseValidationResponseCallback);

    }

    /**
     * Network Interceptor where a request body is build and custom header could be added
     */
    private static class NetworkInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {

            Request.Builder builder = chain.request().newBuilder();
//            Custom header could be added to the request here
//            builder.addHeader(Const.HEADER_KEY_ACCESS_TOKEN, MyPreferences.getAccessToken());
            return chain.proceed(builder.build());
        }
    }

}

