package com.simax.si_max.room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.simax.si_max.model.FavMovie;
import com.simax.si_max.model.Movie;

@Database(entities = {Movie.class}, version = 2, exportSchema = false)
public abstract class FavRoomDb extends RoomDatabase{
    public abstract FavDao favDao();

    private static volatile FavRoomDb INSTANCE;

    public static FavRoomDb getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (FavRoomDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FavRoomDb.class, "favorites_database")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    static final Migration MIGRATION_1_2 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Book "
                    + " ADD COLUMN fav STRING");
        }
    };
    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final FavDao mDao;

        PopulateDbAsync(FavRoomDb db) {
            mDao = db.favDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            //mDao.deleteAll();

            return null;
        }
    }


}
