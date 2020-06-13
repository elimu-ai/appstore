package ai.elimu.appstore.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import ai.elimu.appstore.BuildConfig;
import ai.elimu.appstore.room.RoomDb;
import ai.elimu.appstore.room.dao.ApplicationDao;
import timber.log.Timber;

public class ApplicationProvider extends ContentProvider {

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider.application_provider";

    private static final String TABLE_APPLICATIONS = "applications";
    private static final int CODE_APPLICATIONS = 1;

    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHER.addURI(AUTHORITY, TABLE_APPLICATIONS, CODE_APPLICATIONS);
    }

    @Override
    public boolean onCreate() {
        Timber.i("onCreate");

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Timber.i("query");

        Timber.i("uri: " + uri);
        Timber.i("projection: " + projection);
        Timber.i("selection: " + selection);
        Timber.i("selectionArgs: " + selectionArgs);
        Timber.i("sortOrder: " + sortOrder);

        final int code = MATCHER.match(uri);
        if (code == CODE_APPLICATIONS) {
            Context context = getContext();
            if (context == null) {
                return null;
            }
            RoomDb roomDb = RoomDb.getDatabase(getContext());
            ApplicationDao applicationDao = roomDb.applicationDao();
            Cursor cursor = applicationDao.loadAllAsCursor();
            cursor.setNotificationUri(context.getContentResolver(), uri);
            return cursor;
        } else {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        Timber.i("getType");

        switch (MATCHER.match(uri)) {
            case CODE_APPLICATIONS:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + TABLE_APPLICATIONS;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Timber.i("insert");

        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        Timber.i("delete");

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        Timber.i("update");

        return 0;
    }
}
