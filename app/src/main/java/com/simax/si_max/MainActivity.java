package com.simax.si_max;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.simax.si_max.Interface.OnFavoritesCallback;
import com.simax.si_max.Interface.OnMoviesCallback;
import com.simax.si_max.Interface.RecyclerViewClickListener;
import com.simax.si_max.Interface.onGetMoviesCallback;

import com.simax.si_max.model.Movie;
import com.simax.si_max.model.MoviesRepository;
import com.simax.si_max.room.FavModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.simax.si_max.DetailsActivity.MOVIE_ID;
import static com.simax.si_max.DetailsFavoriteActivity.FAVS_ID;

public class MainActivity extends AppCompatActivity implements RecyclerViewClickListener {

    public final static String LIST_STATE_KEY = "recycler_list_state";
    Parcelable listState;

    private String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500";
    private MovieAdapter adapter;
    private MovieAdapter fAdapter;
    Context context;
    private MoviesRepository moviesRepository;
    private String sortBy = MoviesRepository.POPULAR;
    public static ContentResolver contentResolver;
    GridLayoutManager manager;

    private ArrayList<Movie> movieList;

    private AppCompatActivity activity = MainActivity.this;

    private boolean isFetchingMovies;
    private int currentPage = 1;

    private FavModel favModel;
    private boolean isFav;

    Movie movie;
    List<Movie> movies;

    static final String SOME_VALUE = "int_value";
    static final String SOME_OTHER_VALUE = "string_value";

    int someIntValue;
    String someStringValue;
    LinearLayoutManager mLayoutManager;


    //String myApiKey = BuildConfig.API_KEY;

    //@BindView(R.id.moviesBar)
    //ProgressBar mProgressBar;

    @BindView(R.id.mGridView)
    RecyclerView recyclerView;
    FavAdapter favAdapter;

    private Parcelable savedRecyclerLayoutState;
    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT,
                manager.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null)
            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listState != null) {
            mLayoutManager.onRestoreInstanceState(listState);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fAdapter = new MovieAdapter(movies);
        movie = getIntent().getParcelableExtra(MOVIE_ID);
        favModel = ViewModelProviders.of(this).get(FavModel.class);

        if(savedRecyclerLayoutState!=null){
           manager.onRestoreInstanceState(savedRecyclerLayoutState);
        }
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
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        moviesRepository = MoviesRepository.getInstance();
        fAdapter = new MovieAdapter(movies);

        adapter = getMovies(currentPage);
        recyclerView.setAdapter(adapter);

        isFav = true;

        setupOnScrollListener();

    }



    private void setupOnScrollListener() {
        int numberOfColumns = 2;
        manager = new GridLayoutManager(this, numberOfColumns);
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
            case R.id.clear_data:
                favModel.deleteAllMovie();
                Toast.makeText(this, "Clearing favorites....", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void getFavs(){
        //isFetchingMovies = false;


        favModel = ViewModelProviders.of(this).get(FavModel.class);

        favModel.getAllFavs().observe(this, new Observer<List<Movie>>() {

            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                    FavAdapter adapter;

                    adapter = new FavAdapter(movies,favoritesCallback);


                    recyclerView.setAdapter(adapter);

                    //adapter.appendMovies(movies);

                //isFetchingMovies = true;


                //deleteBtn.setVisibility(View.VISIBLE);
            }
        }
        );
        //recyclerView.setAdapter(adapter);
        //isFetchingMovies = true;
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
                        isFav = false;
                        sortBy = MoviesRepository.POPULAR;
                        checkFav();
                        //fAdapter.clearMovies();
                        return true;
                    case R.id.top_rated:
                        isFav = false;
                        sortBy = MoviesRepository.TOP_RATED;
                        checkFav();
                        return true;
                    case R.id.upcoming:
                        isFav = false;
                        sortBy = MoviesRepository.UPCOMING;
                        checkFav();
                        return true;
                    case R.id.favorite:
                        isFav = true;
                        sortBy = MoviesRepository.FAVORITE;
                        checkFav();
                        //setTitle("Favorites");
                       /* Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
                        startActivity(intent);*/
                        return true;
                    default:
                        //recyclerView.setAdapter(adapter);
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
        //isFav = false;
        moviesRepository.getMovies(page,sortBy, new onGetMoviesCallback() {
            @Override
            public void onSuccess(int page, List<Movie> movies) {
                Log.d("MoviesRepository", "Current Page = " + page);

                if (adapter == null  ) {
                    adapter = new MovieAdapter(movies, callback);
                    recyclerView.setAdapter(adapter);
                }
                   else  {
                        if (page == 1 ) {
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
            intent.putExtra("btn", movie.getFav());
            startActivity(intent);
        }
    };
    OnFavoritesCallback favoritesCallback = new OnFavoritesCallback() {
        @Override
        public void onClick(Movie movie) {
            Intent intent = new Intent(MainActivity.this, DetailsFavoriteActivity.class);
            intent.putExtra(FAVS_ID, movie.getId());
            startActivity(intent);
        }

        @Override
        public void onSuccess(Movie movie) {

        }

        @Override
        public void onError() {

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
    public void checkFav(){
        if (isFav){
            getFavs();
            setTitle("Favorites");
        }else{
            adapter = getMovies(currentPage);
        }
    }

    private void showError() {
        Toast.makeText(MainActivity.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void recyclerViewListClicked(int position) {

    }
}
