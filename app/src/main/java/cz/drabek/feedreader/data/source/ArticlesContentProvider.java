package cz.drabek.feedreader.data.source;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import cz.drabek.feedreader.data.source.local.ArticlesDbHelper;
import cz.drabek.feedreader.data.source.local.DbPersistenceContract;

import static cz.drabek.feedreader.articles.ArticlesActivity.PACKAGE_NAME;

public class ArticlesContentProvider extends ContentProvider {

    private ArticlesDbHelper mDbHelper;
    public static final String AUTHORITY = PACKAGE_NAME;
    private static final String BASE_PATH = "articles";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
    public static final Uri CONTENT_SOURCES_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH + "/sources");
    private static final int ARTICLE_LIST = 1;
    private static final int ARTICLE_ID = 2;
    private static final int SOURCE_LIST = 3;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, ARTICLE_LIST);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", ARTICLE_ID);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/sources", SOURCE_LIST);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new ArticlesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int uriType = sURIMatcher.match(uri);
        Cursor retCursor;

        switch (uriType) {
            case ARTICLE_LIST:
                retCursor = mDbHelper.getReadableDatabase().query(
                        DbPersistenceContract.ArticleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case ARTICLE_ID:
                String[] where = {uri.getLastPathSegment()};
                retCursor = mDbHelper.getReadableDatabase().query(
                        DbPersistenceContract.ArticleEntry.TABLE_NAME,
                        projection,
                        DbPersistenceContract.ArticleEntry._ID + " = ?",
                        where,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int uriType = sURIMatcher.match(uri);
        Uri returnUri;

        switch (uriType) {
            case ARTICLE_ID:
                Cursor exists = db.query(
                        DbPersistenceContract.ArticleEntry.TABLE_NAME,
                        new String[]{DbPersistenceContract.ArticleEntry._ID},
                        DbPersistenceContract.ArticleEntry._ID + " = ?",
                        new String[]{contentValues.getAsString(DbPersistenceContract.ArticleEntry._ID)},
                        null,
                        null,
                        null
                );
                if (exists.moveToLast()) {
                    long _id = db.update(
                            DbPersistenceContract.ArticleEntry.TABLE_NAME, contentValues,
                            DbPersistenceContract.ArticleEntry._ID + " = ?",
                            new String[]{contentValues.getAsString(DbPersistenceContract.ArticleEntry._ID)}
                    );
                    if (_id > 0) {
                        returnUri = Uri.parse(BASE_PATH + "/" + _id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    }
                } else {
                    long _id = db.insert(DbPersistenceContract.ArticleEntry.TABLE_NAME, null, contentValues);
                    if (_id > 0) {
                        returnUri = Uri.parse(BASE_PATH + "/" + _id);
                    } else {
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    }
                }
                exists.close();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int uriType = sURIMatcher.match(uri);
        int rowsDeleted;

        switch (uriType) {
            case ARTICLE_ID:
                rowsDeleted = db.delete(
                        DbPersistenceContract.ArticleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sURIMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case ARTICLE_ID:
                rowsUpdated = db.update(DbPersistenceContract.ArticleEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }
}