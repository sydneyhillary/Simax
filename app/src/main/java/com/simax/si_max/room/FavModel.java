package com.simax.si_max.room;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.simax.si_max.model.Movie;

import java.util.List;

public class FavModel extends AndroidViewModel {
    private FavRepository mRepository;
    private LiveData<List<Movie>> mAllFavs;
    private MutableLiveData<List<Movie>> searchResults;

    public FavModel(@NonNull Application application) {
        super(application);
        mRepository = new FavRepository(application);
        mAllFavs = mRepository.getAllFavs();
        searchResults = mRepository.getSearchResults();
    }
    public MutableLiveData<List<Movie>> getSearchResults() {
        return searchResults;
    }

    public LiveData<List<Movie>> getAllFavs() {
        return mAllFavs;}

    public void insertMovie (Movie movie) {
        mRepository.insert(movie);
    }
    public int findMovie(int id) {
        mRepository.findMovie(id);
        return id;
    }

    public void deleteAllMovie() {
        mRepository.deleteAllMovie();
    }
    public void deleteMovie(Movie movie)  {
        mRepository.deleteMovieSingle(movie);
    }

}
