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
        downloadListeners[downloadId]?.invoke()
    }
}