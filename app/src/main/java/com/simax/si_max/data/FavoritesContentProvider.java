package com.simax.si_max.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class FavoritesContentProvider extends ContentProvider {
    FavoritesDbHelper mDBHelper;

    public static final int FAVORITES = 100;
    public static final int FAVORITES_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    //FAVORITES_WITH_ID has not  implemented yet and is never sent from any where as a uri
    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavoritesContract.AUTHORITY,
                FavoritesContract.FavoritesEntry.TABLE_NAME, FAVORITES);
        uriMatcher.addURI(FavoritesContract.AUTHORITY,
                FavoritesContract.FavoritesEntry.TABLE_NAME + "/#", FAVORITES_WITH_ID);
        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDBHelper = new FavoritesDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] strings,
                        @Nullable String s,
                        @Nullable String[] strings1,
                        @Nullable String s1) {
        final SQLiteDatabase db = mDBHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor returnCursor = null;
        switch (match) {
            case FAVORITES:

                break;
            case FAVORITES_WITH_ID:
                String movie_id = uri.getPathSegments().get(1);
                s = "movie_id=" + movie_id;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);
        }
        returnCursor = db.query(FavoritesContract.FavoritesEntry.TABLE_NAME,
                strings,
                s,
                strings1,
                null,
                null,
                s1);
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case FAVORITES:
                long id = db.insert(FavoritesContract.FavoritesEntry.TABLE_NAME,
                        null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(uri, id);

                } else {
                    throw new SQLException("Failed to insert " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int numRowsDeleted;
        //passing "1" as selection will clear the data base
        if (null == selection) selection = "1";
        switch (sUriMatcher.match(uri)) {
            case FAVORITES:
                numRowsDeleted = mDBHelper.getWritableDatabase().delete(
                        FavoritesContract.FavoritesEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues contentValues,
                      @Nullable String s,
                      @Nullable String[] strings) {
        return 0;
    }
}
