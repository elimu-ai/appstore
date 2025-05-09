package ai.elimu.appstore.ui.applications

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

typealias OnDownloadComplete = () -> Unit

class DownloadCompleteReceiver() : BroadcastReceiver() {

    private val downloadListeners: MutableMap<Long, OnDownloadComplete> by lazy { mutableMapOf() }

    fun addDownloadListener(downloadId: Long, onComplete: OnDownloadComplete) {
        downloadListeners.put(downloadId, onComplete)
    }

    override fun onReceive(context: Context, intent: Intent) {
        val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)

        val query = DownloadManager.Query().setFilterById(downloadId)
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val cursor = downloadManager.query(query)

        if (cursor.moveToFirst()) {
            val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            if (statusIndex != -1) {
                val status = cursor.getInt(statusIndex)
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    downloadListeners.remove(downloadId)?.invoke()
                } else {
                    // Handle failed download if needed
                    downloadListeners.remove(downloadId)
                }
            }
        }
        cursor.close()
    }

    fun clearListeners() {
        downloadListeners.clear()
    }
}