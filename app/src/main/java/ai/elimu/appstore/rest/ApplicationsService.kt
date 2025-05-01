package ai.elimu.appstore.rest

import ai.elimu.model.v2.gson.application.ApplicationGson
import retrofit2.Call
import retrofit2.http.GET

interface ApplicationsService {

    @GET("applications")
    fun listApplications(): Call<List<ApplicationGson>>
}
