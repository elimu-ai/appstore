package ai.elimu.appstore.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ai.elimu.appstore.util.AppPrefs;

/**
 * Created by sladomic on 12.03.18.
 */

public class AppCollectionProvider extends ContentProvider {
    private static final String TABLE_APP_COLLECTION = "appCollection";

    /** The authority of this content provider. */
    public static final String AUTHORITY = "ai.elimu.appstore.provider";

    /** The URI for the AppCollection table. */
    public static final Uri URI_APP_COLLECTIOIN = Uri.parse(
    //        "content://" + AUTHORITY + "/" + Cheese.TABLE_NAME);
            "content://" + AUTHORITY + "/" + TABLE_APP_COLLECTION);

    /** The match code for some items in the Cheese table. */
    private static final int CODE_APP_COLLECTION_DIR = 1;

    /** The URI matcher. */
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHER.addURI(AUTHORITY, TABLE_APP_COLLECTION, CODE_APP_COLLECTION_DIR);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        final int code = MATCHER.match(uri);
        if (code == CODE_APP_COLLECTION_DIR) {
            final Context context = getContext();
            if (context == null) {
                return null;
            }
            final MatrixCursor cursor;
            cursor = new MatrixCursor(new String[]{"_ID", "appCollectionId"});
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
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
