package ai.elimu.appstore.provider;

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
import ai.elimu.appstore.dao.ApplicationDao;
import ai.elimu.appstore.dao.DaoSession;
import ai.elimu.model.enums.admin.ApplicationStatus;
import timber.log.Timber;

public class ApplicationProvider extends ContentProvider {

    // The authority of this content provider
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";

    private static final String TABLE_APPLICATION = "application";
    private static final int CODE_APPLICATION_DIR = 2;
    public static final Uri URI_APPLICATION = Uri.parse("content://" + AUTHORITY + "/" + TABLE_APPLICATION);

    // The URI matcher
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHER.addURI(AUTHORITY, TABLE_APPLICATION, CODE_APPLICATION_DIR);
    }

    @Override
    public boolean onCreate() {
        Timber.i("onCreate");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Timber.i("query");

        final int code = MATCHER.match(uri);
        if (code == CODE_APPLICATION_DIR) {
            Context context = getContext();
            if (context == null) {
                return null;
            }
            BaseApplication baseApplication = (BaseApplication) context;
            DaoSession daoSession = baseApplication.getDaoSession();
            ApplicationDao applicationDao = daoSession.getApplicationDao();
            Cursor cursor = applicationDao.queryBuilder()
                    .where(
//                            ApplicationDao.Properties.Locale.eq(AppPrefs.getLocale()),
                            ApplicationDao.Properties.ApplicationStatus.eq(ApplicationStatus.ACTIVE)
                    )
                    .orderAsc(ApplicationDao.Properties.ListOrder)
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
            case CODE_APPLICATION_DIR:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + TABLE_APPLICATION;
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
