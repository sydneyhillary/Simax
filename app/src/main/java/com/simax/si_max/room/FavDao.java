package com.simax.si_max.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.simax.si_max.model.Movie;

import java.util.List;

@Dao
public interface FavDao {
    @Query("SELECT * FROM favorites WHERE id=:id")
    List<Movie> getMovieById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE )
    void insert(Movie... movie);

    @Query("DELETE FROM favorites")
    void deleteAll();

    @Delete
    void deleteMovie(Movie movie);


    @Query("SELECT * from favorites")
    LiveData<List<Movie>> getAllFavorites();
}
