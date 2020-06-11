package ai.elimu.appstore.rest;

import java.util.List;

import ai.elimu.model.v2.gson.application.ApplicationGson;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApplicationsService {

    @GET("applications")
    Call<List<ApplicationGson>> listApplications();
}
