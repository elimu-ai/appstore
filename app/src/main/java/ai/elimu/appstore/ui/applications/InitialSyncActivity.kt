package ai.elimu.appstore.ui.applications

import ai.elimu.appstore.BaseApplication
import ai.elimu.appstore.databinding.ActivityInitialSyncBinding
import ai.elimu.appstore.rest.ApplicationsService
import ai.elimu.appstore.room.GsonToRoomConverter
import ai.elimu.appstore.room.RoomDb
import ai.elimu.model.v2.enums.admin.ApplicationStatus
import ai.elimu.model.v2.gson.application.ApplicationGson
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.concurrent.Executors

class InitialSyncActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInitialSyncBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i("onCreate")
        super.onCreate(savedInstanceState)

        binding = ActivityInitialSyncBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        Timber.i("onStart")
        super.onStart()

        // Download list of Applications from REST API
        val baseApplication = application as BaseApplication
        val retrofit = baseApplication.retrofit
        val applicationsService = retrofit.create(
            ApplicationsService::class.java
        )
        val call = applicationsService.listApplications()
        Timber.i("call.request(): " + call.request())
        binding.initialSyncTextview.text = "Connecting to " + call.request().url()
        call.enqueue(object : Callback<List<ApplicationGson>> {
            override fun onResponse(
                call: Call<List<ApplicationGson>>,
                response: Response<List<ApplicationGson>>
            ) {
                Timber.i("onResponse")

                Timber.i("response: $response")

                // Parse the JSON response
//                Snackbar.make(textView, "Synchronizing database...", Snackbar.LENGTH_LONG).show();
                val applicationGsons = response.body()!!
                Timber.i("applicationGsons.size(): " + applicationGsons.size)
                if (applicationGsons.isNotEmpty()) {
                    processResponseBody(applicationGsons)
                }
            }

            override fun onFailure(call: Call<List<ApplicationGson>>, t: Throwable) {
                Timber.e(t, "onFailure")

                Timber.e(t, "t.getCause(): " + t.cause)

                // Handle error
                Snackbar.make(binding.initialSyncTextview, t.cause.toString(), Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun processResponseBody(applicationGsons: List<ApplicationGson>) {
        Timber.i("processResponseBody")

        val executorService = Executors.newSingleThreadExecutor()
        executorService.execute {
            Timber.i("run")
            val roomDb = RoomDb.getDatabase(applicationContext)
            val applicationDao = roomDb.applicationDao()
            val applicationVersionDao = roomDb.applicationVersionDao()

            for (applicationGson in applicationGsons) {
                Timber.i("applicationGson.getId(): " + applicationGson.id)

                // Check if the Application has already been stored in the database
                var application = applicationDao.load(applicationGson.id)
                Timber.i("application: $application")
                if (application == null) {
                    // Store the new Application in the database
                    application = GsonToRoomConverter.getApplication(applicationGson)
                    applicationDao.insert(application)
                    Timber.i("Stored Application \"" + application.packageName + "\" in database with ID " + application.id)

                    if (applicationGson.applicationStatus == ApplicationStatus.ACTIVE) {
                        // Store the Application's ApplicationVersions in the database
                        val applicationVersionGsons = applicationGson.applicationVersions
                        Timber.i("applicationVersionGsons.size(): " + applicationVersionGsons.size)
                        for (applicationVersionGson in applicationVersionGsons) {
                            val applicationVersion = GsonToRoomConverter.getApplicationVersion(
                                applicationGson,
                                applicationVersionGson
                            )
                            applicationVersionDao.insert(applicationVersion)
                            Timber.i("Stored ApplicationVersion " + applicationVersion?.versionCode + " in database with ID " + applicationVersion?.id)
                        }
                    }
                } else {
                    // Update the existing Application in the database
                    application = GsonToRoomConverter.getApplication(applicationGson)
                    applicationDao.update(application)
                    Timber.i("Updated Application \"" + application.packageName + "\" in database with ID " + application.id)

                    // Delete all the Application's ApplicationVersions (in case deletions have been made on the server-side)
                    applicationVersionDao.delete(applicationGson.id)

                    if (applicationGson.applicationStatus == ApplicationStatus.ACTIVE) {
                        // Store the Application's ApplicationVersions in the database
                        val applicationVersionGsons = applicationGson.applicationVersions
                        Timber.i("applicationVersionGsons.size(): " + applicationVersionGsons.size)
                        for (applicationVersionGson in applicationVersionGsons) {
                            val applicationVersion = GsonToRoomConverter.getApplicationVersion(
                                applicationGson,
                                applicationVersionGson
                            )
                            applicationVersionDao.insert(applicationVersion)
                            Timber.i("Stored ApplicationVersion " + applicationVersion?.versionCode + " in database with ID " + applicationVersion?.id)
                        }
                    }
                }
            }

            // Redirect to the list of Applications
            val intent = Intent(applicationContext, ApplicationListActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
