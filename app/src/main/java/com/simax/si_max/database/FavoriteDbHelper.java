package com.simax.si_max.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.simax.si_max.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class FavoriteDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favorite.db";
    private static final int DATABASE_VERSION = 1;
    private static final String LOG = "FAVORITE";

    SQLiteOpenHelper dbHandler;
    SQLiteDatabase db;

    public FavoriteDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void open(){
        Log.i(LOG,"Database Opened");
        db = dbHandler.getWritableDatabase();
    }
    public void close(){
        Log.i(LOG,"Database Opened");
        dbHandler.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + FavoriteContract.Favoriteentry.TABLE_NAME + " ("
                + FavoriteContract.Favoriteentry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoriteContract.Favoriteentry.COLUMN_MOVIEID + " INTEGER," +
                FavoriteContract.Favoriteentry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoriteContract.Favoriteentry.COLUMN_USERRATING + " REAL NOT NULL, " +
                FavoriteContract.Favoriteentry.COLUMN_POSTERPATH + " TEXT NOT NULL, " +
                FavoriteContract.Favoriteentry.COLUMN_OVERVIEW + " TEXT NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteContract.Favoriteentry.TABLE_NAME);
        onCreate(db);
    }

    public  void addFavorite(Movie movie){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FavoriteContract.Favoriteentry.COLUMN_MOVIEID, movie.getId());
        values.put(FavoriteContract.Favoriteentry.COLUMN_TITLE, movie.getTitle());
        values.put(FavoriteContract.Favoriteentry.COLUMN_USERRATING, movie.getRating());
        values.put(FavoriteContract.Favoriteentry.COLUMN_POSTERPATH, movie.getPosterPath());
        values.put(FavoriteContract.Favoriteentry.COLUMN_OVERVIEW, movie.getOverview());

        db.insert(FavoriteContract.Favoriteentry.TABLE_NAME, null, values);
        db.close();
    }

    public void deleteFavorite(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(FavoriteContract.Favoriteentry.TABLE_NAME , FavoriteContract.Favoriteentry.COLUMN_MOVIEID + "=" + id,null);
    }

    public List<Movie> getAllFavorite(){
        String[] columns = {
                FavoriteContract.Favoriteentry._ID,
                FavoriteContract.Favoriteentry.COLUMN_MOVIEID,
                FavoriteContract.Favoriteentry.COLUMN_TITLE,
                FavoriteContract.Favoriteentry.COLUMN_USERRATING,
                FavoriteContract.Favoriteentry.COLUMN_POSTERPATH,
                FavoriteContract.Favoriteentry.COLUMN_OVERVIEW
        };
        String sortOrder = FavoriteContract.Favoriteentry._ID + " ASC";
        List<Movie> favoriteList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(FavoriteContract.Favoriteentry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                sortOrder);
        if (cursor.moveToFirst()){
            do {
                Movie movie = new Movie();
                movie.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(FavoriteContract.Favoriteentry.COLUMN_MOVIEID))));
                movie.setTitle(cursor.getString(cursor.getColumnIndex(FavoriteContract.Favoriteentry.COLUMN_TITLE)));
                movie.setRating(Float.parseFloat(cursor.getString(cursor.getColumnIndex(FavoriteContract.Favoriteentry.COLUMN_USERRATING))));
                movie.setPosterPath(cursor.getString(cursor.getColumnIndex(FavoriteContract.Favoriteentry.COLUMN_POSTERPATH)));
                movie.setOverview(cursor.getString(cursor.getColumnIndex(FavoriteContract.Favoriteentry.COLUMN_OVERVIEW)));

                favoriteList.add(movie);

            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

       return favoriteList;
    }

}