package ai.elimu.appstore.ui.applications

import ai.elimu.appstore.BuildConfig
import ai.elimu.appstore.R
import ai.elimu.appstore.room.entity.Application
import ai.elimu.appstore.room.entity.ApplicationVersion
import ai.elimu.appstore.ui.applications.ApplicationListAdapter.ApplicationViewHolder
import ai.elimu.appstore.util.FileHelper
import ai.elimu.appstore.util.FileHelper.getApkFile
import ai.elimu.appstore.util.InstallationHelper.getVersionCodeOfInstalledApplication
import ai.elimu.appstore.util.InstallationHelper.isApplicationInstalled
import ai.elimu.appstore.util.SharedPreferencesHelper.getLanguage
import ai.elimu.model.v2.enums.admin.ApplicationStatus
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber
import java.io.File
import androidx.core.net.toUri
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ApplicationListAdapter(
    private val context: Context,
    private val downloadReceiver: DownloadCompleteReceiver
) :
    RecyclerView.Adapter<ApplicationViewHolder>() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    private var applications: List<Application>? = null

    private var applicationVersions: List<ApplicationVersion>? = null
    private val registeredReceivers: MutableSet<BroadcastReceiver> by lazy { mutableSetOf() }

    private val downloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationViewHolder {
        Timber.i("onCreateViewHolder")
        val itemView =
            layoutInflater.inflate(R.layout.activity_application_list_item, parent, false)
        return ApplicationViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: ApplicationViewHolder, position: Int) {
        Timber.i("onBindViewHolder")
        if (applications != null) {
            // Reset button state
            viewHolder.launchButton.visibility = View.INVISIBLE
            viewHolder.installButton.visibility = View.INVISIBLE
            viewHolder.downloadButton.visibility = View.INVISIBLE
            viewHolder.installUpdateButton.visibility = View.INVISIBLE
            viewHolder.downloadUpdateButton.visibility = View.INVISIBLE
            viewHolder.downloadProgressBar.visibility = View.INVISIBLE

            // Populate TextViews with Application details
            val application = applications!![position]
            Timber.i("application.getPackageName(): ${application.packageName}")
            viewHolder.textViewFirstLine.text = application.packageName
            viewHolder.textViewSecondLine.text = application.applicationStatus.toString()

            // Use 50% transparency if an Application has no corresponding APK files
            if (application.applicationStatus != ApplicationStatus.ACTIVE) {
                viewHolder.textViewFirstLine.alpha = 0.5f
                viewHolder.textViewSecondLine.alpha = 0.5f
            }

            // Check if any application versions (APKs) have been uploaded to the webapp
            val newestApplicationVersion = getNewestApplicationVersion(
                application,
                applicationVersions!!
            )
            Timber.i("newestApplicationVersion: $newestApplicationVersion")
            if (newestApplicationVersion != null) {
                // Display a button matching the current state of the application
                // "Download", "Install", "Launch", "Download update", "Install update"

                if (isApplicationInstalled(application.packageName, context)) {
                    // The APK has been installed

                    // Check if an update is available for download

                    val versionCodeInstalled =
                        getVersionCodeOfInstalledApplication(application.packageName, context)
                    Timber.i("versionCodeInstalled: $versionCodeInstalled")
                    val applicationVersion = getNewestApplicationVersion(
                        application,
                        applicationVersions!!
                    )
                    Timber.i(
                        "applicationVersion.getVersionCode(): %s",
                        applicationVersion!!.versionCode
                    )
                    if (versionCodeInstalled < applicationVersion.versionCode) {
                        // An update is available for download

                        // If the APK has been downloaded (but not yet installed), display the "Install update" button

                        val apkFile = getApkFile(
                            application.packageName,
                            applicationVersion.versionCode,
                            context
                        )
                        Timber.i("apkFile: $apkFile")
                        Timber.i("apkFile.exists(): %s", apkFile!!.exists())
                        if (apkFile.exists()) {
                            viewHolder.installUpdateButton.visibility = View.VISIBLE
                            val onClickListener = View.OnClickListener { _: View? ->
                                Timber.i("viewHolder.installUpdateButton onClick")
                                // Initiate installation of the APK file
                                val intentFilter = IntentFilter(Intent.ACTION_PACKAGE_ADDED)
                                intentFilter.addDataScheme("package")
                                val packageAddedReceiver = PackageAddedReceiver(position)
                                context.registerReceiver(
                                    packageAddedReceiver,
                                    intentFilter
                                )
                                registeredReceivers.add(packageAddedReceiver)
                                val apkUri = FileProvider.getUriForFile(
                                    context, BuildConfig.APPLICATION_ID + ".apk.provider",
                                    apkFile
                                )
                                val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
                                intent.setData(apkUri)
                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                context.startActivity(intent)
                            }
                            viewHolder.installUpdateButton.setOnClickListener(onClickListener)
                        }

                        // If the APK has not been downloaded, display the "Download update" button
                        if (!apkFile.exists()) {
                            viewHolder.downloadUpdateButton.visibility = View.VISIBLE
                            viewHolder.downloadUpdateButton.setOnClickListener {
                                Timber.i("viewHolder.downloadUpdateButton onClick")
                                // Initiate download of the APK file
                                Timber.i("finalApplicationVersion.fileUrl: ${applicationVersion.fileUrl}")
                                val request =
                                    DownloadManager.Request(applicationVersion.fileUrl.toUri())
                                val destinationInExternalFilesDir =
                                    File.separator + "lang-" + getLanguage(
                                        context
                                    )!!
                                        .isoCode + File.separator + "apks" + File.separator + apkFile.name
                                request.setDestinationInExternalFilesDir(
                                    context,
                                    null,
                                    destinationInExternalFilesDir
                                )
                                val downloadId = downloadManager.enqueue(request)
                                if (downloadId != -1L) {
                                    downloadReceiver.addDownloadListener(downloadId) {
                                        handleDownloadComplete(itemPosition = position,
                                            relativeFilePath = destinationInExternalFilesDir,
                                            checkSum = applicationVersion.checksumMd5
                                        )
                                    }
                                    Timber.i("downloadId: $downloadId")
                                }

                                // Replace download button with progress bar
                                viewHolder.downloadUpdateButton.visibility =
                                    View.INVISIBLE
                                viewHolder.downloadProgressBar.visibility =
                                    View.VISIBLE
                            }
                        }
                    } else {
                        // The installed APK is up-to-date

                        // Display the "Launch" button

                        viewHolder.launchButton.visibility = View.VISIBLE
                        viewHolder.launchButton.setOnClickListener {
                            Timber.i("onClick")
                            Timber.i("Launching ${application.packageName}")
                            val packageManager = context.packageManager
                            val launchIntent =
                                packageManager.getLaunchIntentForPackage(application.packageName)
                            Timber.i("launchIntent: $launchIntent")
                            context.startActivity(launchIntent)
                        }
                    }
                } else {
                    // The APK has not been installed

                    // Fetch information about the newest APK file

                    val applicationVersion = getNewestApplicationVersion(
                        application,
                        applicationVersions!!
                    )
                    if (applicationVersion == null) {
                        return
                    }
                    Timber.i(
                        "applicationVersion.getVersionCode(): %s",
                        applicationVersion.versionCode
                    )

                    // If the APK has been downloaded (but not yet installed), display the "Install" button
                    val apkFile =
                        getApkFile(application.packageName, applicationVersion.versionCode, context)
                    Timber.i("apkFile: $apkFile")
                    Timber.i("apkFile.exists(): %s", apkFile!!.exists())
                    if (apkFile.exists()) {
                        viewHolder.installButton.visibility = View.VISIBLE
                        val onClickListener = View.OnClickListener { _: View? ->
                            Timber.i("viewHolder.installButton onClick")
                            // Initiate installation of the APK file
                            val intentFilter = IntentFilter(Intent.ACTION_PACKAGE_ADDED)
                            intentFilter.addDataScheme("package")
                            val packageAddedReceiver = PackageAddedReceiver(position)
                            context.registerReceiver(packageAddedReceiver, intentFilter)
                            val apkUri = FileProvider.getUriForFile(
                                context, BuildConfig.APPLICATION_ID + ".apk.provider",
                                apkFile
                            )
                            registeredReceivers.add(packageAddedReceiver)
                            val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
                            intent.setData(apkUri)
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            context.startActivity(intent)
                        }
                        viewHolder.installButton.setOnClickListener(onClickListener)
                    }

                    // If the APK has not been downloaded, display the "Download" button
                    if (!apkFile.exists()) {
                        viewHolder.downloadButton.visibility = View.VISIBLE
                        val finalApplicationVersion: ApplicationVersion = applicationVersion
                        viewHolder.downloadButton.setOnClickListener {
                            Timber.i("viewHolder.downloadButton onClick")
                            // Initiate download of the APK file
                            Timber.i("finalApplicationVersion.fileUrl: ${finalApplicationVersion.fileUrl}")
                            val request =
                                DownloadManager.Request(finalApplicationVersion.fileUrl.toUri())
                            val destinationInExternalFilesDir =
                                File.separator + "lang-" + getLanguage(
                                    context
                                )!!
                                    .isoCode + File.separator + "apks" + File.separator + apkFile.name
                            Timber.i("destinationInExternalFilesDir: $destinationInExternalFilesDir")

                            request.setDestinationInExternalFilesDir(
                                context,
                                null,
                                destinationInExternalFilesDir
                            )
                            val downloadId = downloadManager.enqueue(request)
                            downloadReceiver.addDownloadListener(downloadId) {
                                handleDownloadComplete(itemPosition = position,
                                    relativeFilePath = destinationInExternalFilesDir,
                                    checkSum = applicationVersion.checksumMd5
                                )
                            }

                            Timber.i("downloadId: $downloadId")

                            // Replace download button with progress bar
                            viewHolder.downloadButton.visibility = View.INVISIBLE
                            viewHolder.downloadProgressBar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun getNewestApplicationVersion(
        application: Application,
        applicationVersions: List<ApplicationVersion>,
    ): ApplicationVersion? {
        var applicationVersion: ApplicationVersion? = null
        for (appVersion in applicationVersions) {
            if (appVersion.applicationId == application.id) {
                applicationVersion = appVersion
                break
            }
        }
        return applicationVersion
    }

    private fun handleDownloadComplete(
        itemPosition: Int,
        relativeFilePath: String,
        checkSum: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val downloadedFile = context.getExternalFilesDir(relativeFilePath)
            val downloadedFileCheckSum =
                FileHelper.calculateMD5Checksum(downloadedFile?.absolutePath ?: "")
            withContext(Dispatchers.Main) {
                if (downloadedFileCheckSum != checkSum) {
                    downloadedFile?.delete()
                    val activity = context as? ApplicationListActivity ?: return@withContext
                    Snackbar.make(
                        activity.window.decorView,
                        context.getString(R.string.download_error),
                        Snackbar.LENGTH_LONG
                    ).setBackgroundTint(ContextCompat.getColor(context, R.color.red)).show()
                }
                notifyItemChanged(itemPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        Timber.i("getItemCount")
        return if (applications == null) {
            0
        } else {
            applications!!.size
        }
    }

    fun setApplications(applications: List<Application>?) {
        this.applications = applications
    }

    fun setApplicationVersions(applicationVersions: List<ApplicationVersion>?) {
        this.applicationVersions = applicationVersions
    }

    fun unregisterReceiver(context: Context) {
        for (receiver in registeredReceivers) {
            try {
                context.unregisterReceiver(receiver)
            } catch (e: IllegalArgumentException) {
                Timber.e(e)
            }
        }
    }

    inner class ApplicationViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val textViewFirstLine: TextView
        val textViewSecondLine: TextView

        val launchButton: Button
        val installButton: Button
        val downloadButton: Button
        val installUpdateButton: Button
        val downloadUpdateButton: Button
        val downloadProgressBar: ProgressBar

        init {
            Timber.i("ApplicationViewHolder")

            textViewFirstLine = itemView.findViewById(R.id.textViewFirstLine)
            textViewSecondLine = itemView.findViewById(R.id.textViewSecondLine)

            launchButton = itemView.findViewById(R.id.list_item_launch_button)
            installButton = itemView.findViewById(R.id.list_item_install_button)
            downloadButton = itemView.findViewById(R.id.list_item_download_button)
            installUpdateButton = itemView.findViewById(R.id.list_item_install_update_button)
            downloadUpdateButton = itemView.findViewById(R.id.list_item_download_update_button)
            downloadProgressBar = itemView.findViewById(R.id.list_item_download_progressbar)
        }
    }

    private inner class PackageAddedReceiver(private val itemPosition: Int) : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Timber.i("onReceive")

            Timber.i("intent: $intent")
            Timber.i("intent.getData(): %s", intent.data)
            notifyItemChanged(itemPosition)
            context.unregisterReceiver(this)
            registeredReceivers.remove(this)
        }
    }
}
