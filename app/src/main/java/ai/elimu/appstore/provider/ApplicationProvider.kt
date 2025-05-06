package ai.elimu.appstore.provider

import ai.elimu.appstore.BuildConfig
import ai.elimu.appstore.room.RoomDb
import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import timber.log.Timber

class ApplicationProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        Timber.i("onCreate")

        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        Timber.i("query")

        Timber.i("uri: $uri")
        Timber.i("projection: $projection")
        Timber.i("selection: $selection")
        Timber.i("selectionArgs: $selectionArgs")
        Timber.i("sortOrder: $sortOrder")

        val code = MATCHER.match(uri)
        if (code == CODE_APPLICATIONS) {
            val context = context ?: return null
            val roomDb = RoomDb.getDatabase(getContext())
            val applicationDao = roomDb.applicationDao()
            val cursor = applicationDao.loadAllAsCursor()
            cursor.setNotificationUri(context.contentResolver, uri)
            return cursor
        } else {
            throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun getType(uri: Uri): String? {
        Timber.i("getType")

        when (MATCHER.match(uri)) {
            CODE_APPLICATIONS -> return "vnd.android.cursor.dir/$AUTHORITY.$TABLE_APPLICATIONS"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        Timber.i("insert")

        return null
    }

    override fun delete(uri: Uri, s: String?, strings: Array<String>?): Int {
        Timber.i("delete")

        return 0
    }

    override fun update(
        uri: Uri,
        contentValues: ContentValues?,
        s: String?,
        strings: Array<String>?
    ): Int {
        Timber.i("update")

        return 0
    }

    companion object {
        const val AUTHORITY: String = BuildConfig.APPLICATION_ID + ".provider.application_provider"

        private const val TABLE_APPLICATIONS = "applications"
        private const val CODE_APPLICATIONS = 1

        private val MATCHER = UriMatcher(UriMatcher.NO_MATCH)

        init {
            MATCHER.addURI(AUTHORITY, TABLE_APPLICATIONS, CODE_APPLICATIONS)
        }
    }
}
