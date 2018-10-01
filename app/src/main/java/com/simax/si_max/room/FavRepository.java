package com.simax.si_max.room;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.simax.si_max.model.Movie;

import java.util.List;

public class FavRepository implements AsyncResult {
    private FavDao mFavDao;
    private LiveData<List<Movie>> mAllFavs;
    private MutableLiveData<List<Movie>> searchResults = new MutableLiveData<>();

     FavRepository(Application application) {
        FavRoomDb db = FavRoomDb.getDatabase(application);
        mFavDao = db.favDao();
        mAllFavs = mFavDao.getAllFavorites();
    }

    LiveData<List<Movie>> getAllFavs() {
        return mAllFavs;
    }
    public MutableLiveData<List<Movie>> getSearchResults() {
        return searchResults;
    }

    public void insert(Movie movie) {
        new insertAsyncTask(mFavDao).execute(movie);
    }
    public void deleteAllMovie() {
        new deleteAllAsyncTask(mFavDao).execute();
    }
    public void deleteMovieSingle(Movie movie){
         new deleteMovieAsyncTask(mFavDao).execute(movie);
    }
    public void findMovie(String id) {
        queryAsyncTask task = new queryAsyncTask(mFavDao);
        task.delegate = this;
        task.execute(id);
    }

    @Override
    public void asyncFinished(List<Movie> results) {
        searchResults.setValue(results);
    }

    private static class insertAsyncTask extends AsyncTask<Movie, Void, Void> {

        private FavDao mAsyncTaskDao;

        insertAsyncTask(FavDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Movie... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class queryAsyncTask extends
            AsyncTask<String, Void, List<Movie>> {

        private FavDao asyncTaskDao;
        private FavRepository delegate = null;

        queryAsyncTask(FavDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected List<Movie> doInBackground(final String... params) {
            //return asyncTaskDao.findMovie(Integer.valueOf(params[0]));
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> result) {
            delegate.asyncFinished(result);
        }
    }
    private static class deleteMovieAsyncTask extends AsyncTask<Movie, Void, Void> {
        private FavDao mAsyncTaskDao;

        deleteMovieAsyncTask(FavDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Movie... params) {
            mAsyncTaskDao.deleteMovie(params[0]);
            return null;
        }
    }
    private static class deleteAllAsyncTask extends AsyncTask<Void, Void, Void> {

        private FavDao asyncTaskDao;

        deleteAllAsyncTask(FavDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            asyncTaskDao.deleteAll();
            return null;
        }
    }


}
