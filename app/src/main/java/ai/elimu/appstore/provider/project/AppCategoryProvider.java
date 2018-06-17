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

import ai.elimu.appstore.BaseApplication;
import ai.elimu.appstore.BuildConfig;
import ai.elimu.appstore.dao.AppCategoryDao;
import ai.elimu.appstore.model.project.AppCategory;
import ai.elimu.appstore.util.AppPrefs;
import timber.log.Timber;

public class AppCategoryProvider extends ContentProvider {

    // The authority of this content provider
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";

    private static final String TABLE_APP_CATEGORY = "appCategory";
    private static final int CODE_APP_CATEGORY_DIR = 3;
    public static final Uri URI_APP_CATEGORY = Uri.parse("content://" + AUTHORITY + "/" + TABLE_APP_CATEGORY);

    // The URI matcher
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHER.addURI(AUTHORITY, TABLE_APP_CATEGORY, CODE_APP_CATEGORY_DIR);
    }

    @Override
    public boolean onCreate() {
        Timber.i("onCreate");

        Timber.i("URI_APP_CATEGORY: " + URI_APP_CATEGORY);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        Timber.i("query");

        Timber.i("uri: " + uri);

        final int code = MATCHER.match(uri);
        if (code == CODE_APP_CATEGORY_DIR) {
            Context context = getContext();
            if (context == null) {
                return null;
            }
            BaseApplication baseApplication = (BaseApplication) context;
            AppCategoryDao appCategoryDao = baseApplication.getDaoSession().getAppCategoryDao();
            Cursor cursor = appCategoryDao.queryBuilder()
                    .orderAsc(AppCategoryDao.Properties.ListOrder)
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
            case CODE_APP_CATEGORY_DIR:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + TABLE_APP_CATEGORY;
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
