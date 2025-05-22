package ai.elimu.appstore.ui.applications

import ai.elimu.appstore.databinding.ActivityApplicationListBinding
import ai.elimu.appstore.room.RoomDb
import android.app.DownloadManager
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import timber.log.Timber

class ApplicationListActivity : AppCompatActivity() {

    private val TAG = "ApplicationListActivity"
    private lateinit var appListAdapter: ApplicationListAdapter
    private val downloadReceiver: DownloadCompleteReceiver = DownloadCompleteReceiver()
    private lateinit var binding: ActivityApplicationListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i("onCreate")
        super.onCreate(savedInstanceState)

        binding = ActivityApplicationListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.toolbarLayout.title = title
    }

    override fun onStart() {
        Timber.i("onStart")
        super.onStart()

        // Configure list adapter
        val recyclerView = binding.layoutAppList.recyclerView
        appListAdapter = ApplicationListAdapter(this, downloadReceiver)
        recyclerView.adapter = appListAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        val dividerItemDecoration =
            DividerItemDecoration(recyclerView.context, linearLayoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)

        // Fetch all Applications from database, and update the list adapter
        val roomDb = RoomDb.getDatabase(applicationContext)
        val applicationDao = roomDb.applicationDao()
        val applicationVersionDao = roomDb.applicationVersionDao()
        RoomDb.databaseWriteExecutor.execute {
            val applications =
                applicationDao.loadAll()
            Timber.tag(TAG).d("applications.size(): %s", applications.size)
            appListAdapter.setApplications(applications)

            val applicationVersions =
                applicationVersionDao.loadAll()
            Timber.tag(TAG).d("applicationVersions.size(): %s", applicationVersions.size)
            appListAdapter.setApplicationVersions(applicationVersions)
            recyclerView.post(appListAdapter::notifyDataSetChanged)
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(downloadReceiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(downloadReceiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }

    }

    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(downloadReceiver)
        } catch (e: IllegalArgumentException) {
            Timber.e(e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        appListAdapter.unregisterReceiver(this)
        downloadReceiver.clearListeners()
    }
}
