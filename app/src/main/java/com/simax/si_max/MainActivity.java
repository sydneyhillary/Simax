package com.simax.si_max;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.simax.si_max.data.FavoritesContract;
import com.simax.si_max.data.FavoritesDbHelper;
import com.simax.si_max.Interface.OnMoviesCallback;
import com.simax.si_max.Interface.onGetMoviesCallback;

import com.simax.si_max.model.Movie;
import com.simax.si_max.model.MoviesRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;

import static com.simax.si_max.DetailsActivity.MOVIE_ID;

public class MainActivity extends AppCompatActivity {

    //public static List<Movie> movies;
    public static String[] dates;
    public static String[] summary;
    public static String[] votes;
    public static String[] poster;
    public static String[] title;
    public static String[] id;

    private String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500";
    private MovieAdapter adapter;
    private MoviesRepository moviesRepository;
    private String sortBy = MoviesRepository.POPULAR;
    public static ContentResolver contentResolver;

    private ArrayList<Movie> movieList;
    private FavoritesDbHelper favoriteDbHelper;
    private AppCompatActivity activity = MainActivity.this;

    private boolean isFetchingMovies;
    private int currentPage = 1;

    Movie movie;


    //String myApiKey = BuildConfig.API_KEY;

    //@BindView(R.id.moviesBar)
    //ProgressBar mProgressBar;

    @BindView(R.id.mGridView)
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        movie = getIntent().getParcelableExtra(MOVIE_ID);

        //poster = movie.getPosterPath();
        //movieName = movie.getTitle();
        //summary = movie.getOverview();
        //votes = Float.toString(movie.getRating());
        //dates = movie.getReleaseDate();
        //id = movie.getId();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.mGridView);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        moviesRepository = MoviesRepository.getInstance();
        adapter = getMovies(currentPage);
        recyclerView.setAdapter(adapter);

        setupOnScrollListener();

    }


    private void setupOnScrollListener() {
        int numberOfColumns = 2;
        final GridLayoutManager manager = new GridLayoutManager(this, numberOfColumns);
        recyclerView.setLayoutManager(manager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int totalItemCount = manager.getItemCount();
                int visibleItemCount = manager.getChildCount();
                int firstVisibleItem = manager.findFirstVisibleItemPosition();

                if (firstVisibleItem + visibleItemCount >= totalItemCount / 2) {
                    if (!isFetchingMovies) {
                        getMovies(currentPage + 1);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort:
                showSortMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSortMenu() {
        PopupMenu sortMenu = new PopupMenu(this, findViewById(R.id.sort));
        sortMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                /*
                 * Every time we sort, we need to go back to page 1
                 */
                currentPage = 1;

                switch (item.getItemId()) {
                    case R.id.popular:
                        sortBy = MoviesRepository.POPULAR;
                        getMovies(currentPage);
                        return true;
                    case R.id.top_rated:
                        sortBy = MoviesRepository.TOP_RATED;
                        getMovies(currentPage);
                        return true;
                    case R.id.upcoming:
                        sortBy = MoviesRepository.UPCOMING;
                        getMovies(currentPage);
                        return true;
                    case R.id.favorite:
                        setTitle(getString(R.string.favorite));
                        new FavouriteMoviesFetchTask().execute();
                    default:
                        return false;
                }
            }
        });
        sortMenu.inflate(R.menu.menu_movies_sort);
        sortMenu.show();
    }

    private void initViews() {
        //List<Movie> movieList;
        int numberOfColumns = 2;

        recyclerView = (RecyclerView) findViewById(R.id.mGridView);

        movieList = new ArrayList<>();
        //adapter = getMovies(
        adapter = new MovieAdapter(movieList, callback);

        /*if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }*/
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        favoriteDbHelper = new FavoritesDbHelper(activity);


    }



    public Activity getActivity(){
        Context context = this;
        while (context instanceof ContextWrapper){
            if (context instanceof Activity){
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;

    }


    private MovieAdapter getMovies(int page) {
        isFetchingMovies = true;
        moviesRepository.getMovies(page,sortBy, new onGetMoviesCallback() {
            @Override
            public void onSuccess(int page, List<Movie> movies) {
                Log.d("MoviesRepository", "Current Page = " + page);
                if (adapter == null) {
                    adapter = new MovieAdapter(movies, callback);
                    recyclerView.setAdapter(adapter);
                } else {
                    if (page == 1) {
                        adapter.clearMovies();
                    }
                    adapter.appendMovies(movies);
                }
                currentPage = page;
                isFetchingMovies = false;

                setTitle();
            }

            @Override
            public void onError() {
                showError();
            }
        });
        return null;
    }
    OnMoviesCallback callback = new OnMoviesCallback() {
        @Override
        public void onClick(Movie movie) {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra(MOVIE_ID, movie.getId());
            startActivity(intent);
        }
    };
    private void setTitle() {
        switch (sortBy) {
            case MoviesRepository.POPULAR:
                setTitle(getString(R.string.popular));
                break;
            case MoviesRepository.TOP_RATED:
                setTitle(getString(R.string.top_rated));
                break;
            case MoviesRepository.UPCOMING:
                setTitle(getString(R.string.upcoming));
                break;
            case MoviesRepository.FAVORITE:
                setTitle(getString(R.string.favorite));

        }
    }
    private void showError() {
        Toast.makeText(MainActivity.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
    }



    public void getFavs() {
        Cursor cursor = getContentResolver().query(FavoritesContract.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            cursor.moveToPosition(-1);
            try {
                while (cursor.moveToNext()) {
                    String title = cursor.getString(cursor.getColumnIndex
                            (FavoritesContract.FavoritesEntry.COLUMN_TITLE));
                    String movieId = cursor.getString(cursor.getColumnIndex
                            (FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID));
                    String plot = cursor.getString(cursor.getColumnIndex
                            (FavoritesContract.FavoritesEntry.COLUMN_PLOT));
                    String date = cursor.getString(cursor.getColumnIndex
                            (FavoritesContract.FavoritesEntry.COLUMN_RELEASE_DATE));
                    String vote = cursor.getString(cursor.getColumnIndex
                            (FavoritesContract.FavoritesEntry.COLUMN_AVERAGE_VOTE));
                    String path = cursor.getString(cursor.getColumnIndex
                            (FavoritesContract.FavoritesEntry.COLUMN_POSTER_PATH));
                    //Movie movie = new Movie(movieId, title, date, vote, plot, path);
                    movieList.clear();
                    movieList.add(movie);
                }
            } finally {
                //adapter.setDataSource(movieList);
                recyclerView.setAdapter(adapter);
                //mLayoutManager.onRestoreInstanceState(listState);
            }
        }
    }

    public class FavouriteMoviesFetchTask extends AsyncTask<Void, Void, Void> {

        Movie[] movies;
        MovieAdapter newAdapter;


        @Override
        protected Void doInBackground(Void... params) {
            Uri uri = FavoritesContract.CONTENT_URI;
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            //Log.d(TAG, "doInBackground: cursor: " + cursor.getCount());

            int count = cursor.getCount();
            movies = new Movie[cursor.getCount()];
            if (count == 0) {
                return null;
            }

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID));
                    String name = cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_TITLE));
                    String posterPath = cursor.getString(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_POSTER_PATH));

                   // Log.d(TAG, "doInBackground: " + name + " and " + poster_path);

                    movies[cursor.getPosition()] = new Movie(id, posterPath);

                   // movie.setPosterPath(posterPath);
                    //movie.setTitle(name);


                } while (cursor.moveToNext());
            }

            
            cursor.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //adapter.clearMovies();
            adapter.setMovieData(movies);
            adapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);

        }
    }



}
