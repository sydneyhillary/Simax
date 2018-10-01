package com.simax.si_max;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.Nullable;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import com.simax.si_max.Interface.OnMoviesCallback;
import com.simax.si_max.Interface.onGetMoviesCallback;

import com.simax.si_max.model.Movie;
import com.simax.si_max.model.MoviesRepository;
import com.simax.si_max.room.FavModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

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

    private AppCompatActivity activity = MainActivity.this;

    private boolean isFetchingMovies;
    private int currentPage = 1;

    private FavModel favModel;

    Movie movie;

    static final String SOME_VALUE = "int_value";
    static final String SOME_OTHER_VALUE = "string_value";

    int someIntValue;
    String someStringValue;



    //String myApiKey = BuildConfig.API_KEY;

    //@BindView(R.id.moviesBar)
    //ProgressBar mProgressBar;

    @BindView(R.id.mGridView)
    RecyclerView recyclerView;

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(SOME_VALUE, someIntValue);
        editor.putString(SOME_OTHER_VALUE, someStringValue);

        editor.apply();
    }
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        // Save custom values into the bundle
        savedInstanceState.putInt(SOME_VALUE, someIntValue);
        savedInstanceState.putString(SOME_OTHER_VALUE, someStringValue);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        // Restore state members from saved instance
        someIntValue = savedInstanceState.getInt(SOME_VALUE);
        someStringValue = savedInstanceState.getString(SOME_OTHER_VALUE);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        someIntValue = settings.getInt(SOME_VALUE, 0);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);
        movie = getIntent().getParcelableExtra(MOVIE_ID);
        favModel = ViewModelProviders.of(this).get(FavModel.class);




        //poster = movie.getPosterPath();
        //movieName = movie.getTitle();
        //summary = movie.getOverview();
        //votes = Float.toString(movie.getRating());
        //dates = movie.getReleaseDate();
        //id = movie.getId();

        // Checks the orientation of the screen


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.mGridView);

        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        moviesRepository = MoviesRepository.getInstance();
        adapter = getMovies(currentPage);
        recyclerView.setAdapter(adapter);

        setupOnScrollListener();

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            int numberOfColumns = 3;
            recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            int numberOfColumns = 2;
            recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        }
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
                        Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
                        startActivity(intent);
                        return true;
                    default:
                        recyclerView.setAdapter(adapter);
                        return true;
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


        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();



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


        }
    }
    private void showError() {
        Toast.makeText(MainActivity.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
    }





}
