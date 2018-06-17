package ai.elimu.appstore.provider.project;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ai.elimu.appstore.BaseApplication;
import ai.elimu.appstore.BuildConfig;
import ai.elimu.appstore.dao.AppGroupDao;
import timber.log.Timber;

public class AppGroupProvider extends ContentProvider {

    // The authority of this content provider
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";

    private static final String TABLE_APP_GROUP = "appGroup";
    private static final int CODE_APP_GROUP_DIR = 4;
    public static final Uri URI_APP_GROUP = Uri.parse("content://" + AUTHORITY + "/" + TABLE_APP_GROUP);

    // The URI matcher
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHER.addURI(AUTHORITY, TABLE_APP_GROUP, CODE_APP_GROUP_DIR);
    }

    @Override
    public boolean onCreate() {
        Timber.i("onCreate");

        Timber.i("URI_APP_GROUP: " + URI_APP_GROUP);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String selectionClause, @Nullable String[] selectionArgs, @Nullable String s1) {
        Timber.i("query");

        Timber.i("uri: " + uri);
        Timber.i("selectionClause: " + selectionClause);
        Timber.i("selectionArgs: " + selectionArgs);
        Timber.i("selectionArgs[0]: " + selectionArgs[0]);
        Long appCategoryId = Long.parseLong(selectionArgs[0]);
        Timber.i("appCategoryId: " + appCategoryId);

        final int code = MATCHER.match(uri);
        if (code == CODE_APP_GROUP_DIR) {
            Context context = getContext();
            if (context == null) {
                return null;
            }
            BaseApplication baseApplication = (BaseApplication) context;
            AppGroupDao appGroupDao = baseApplication.getDaoSession().getAppGroupDao();
            Cursor cursor = appGroupDao.queryBuilder()
                    .where(
                            AppGroupDao.Properties.AppCategoryId.eq(appCategoryId)
                    )
                    .orderAsc(AppGroupDao.Properties.ListOrder)
                    .buildCursor().forCurrentThread().query();
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
            case CODE_APP_GROUP_DIR:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + TABLE_APP_GROUP;
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
