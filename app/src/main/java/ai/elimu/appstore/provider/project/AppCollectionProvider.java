package ai.elimu.appstore.provider.project;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ai.elimu.appstore.BuildConfig;
import ai.elimu.appstore.util.AppPrefs;
import timber.log.Timber;

public class AppCollectionProvider extends ContentProvider {

    // The authority of this content provider
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider.app_collection_provider";

    private static final String TABLE_APP_COLLECTION = "appCollection";
    private static final int CODE_APP_COLLECTION_DIR = 1;
    public static final Uri URI_APP_COLLECTION = Uri.parse("content://" + AUTHORITY + "/" + TABLE_APP_COLLECTION);

    // The URI matcher
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHER.addURI(AUTHORITY, TABLE_APP_COLLECTION, CODE_APP_COLLECTION_DIR);
    }

    @Override
    public boolean onCreate() {
        Timber.i("onCreate");

        Timber.i("URI_APP_COLLECTION: " + URI_APP_COLLECTION);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        Timber.i("query");

        Timber.i("uri: " + uri);

        final int code = MATCHER.match(uri);
        if (code == CODE_APP_COLLECTION_DIR) {
            Context context = getContext();
            if (context == null) {
                return null;
            }
            MatrixCursor cursor = new MatrixCursor(new String[]{"_ID", "appCollectionId"});
            MatrixCursor.RowBuilder rowBuilder = cursor.newRow();
            rowBuilder.add("appCollectionId", AppPrefs.getAppCollectionId());
            cursor.setNotificationUri(context.getContentResolver(), uri);
            return cursor;
        } else {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        Timber.i("getType");

        switch (MATCHER.match(uri)) {
            case CODE_APP_COLLECTION_DIR:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + TABLE_APP_COLLECTION;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Timber.i("insert");

        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        Timber.i("delete");

        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        Timber.i("update");

        return 0;
    }
}
