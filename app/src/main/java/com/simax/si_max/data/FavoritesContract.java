package com.simax.si_max.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoritesContract {
    public static final class FavoritesEntry implements BaseColumns {

        public static final String TABLE_NAME = "favoritesTable";
        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_TITLE = "name";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_AVERAGE_VOTE = "rating";
        public static final String COLUMN_PLOT = "overview";
        public static final String COLUMN_POSTER_PATH = "poster_path";


    }

    // URI's used by contentProvider
    public static final String AUTHORITY = "com.simax.si_max";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    //below is what we use in our queries
    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(FavoritesEntry.TABLE_NAME).build();


}
