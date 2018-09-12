package com.simax.si_max;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.simax.si_max.Interface.OnMoviesCallback;
import com.simax.si_max.Interface.onGetMoviesCallback;
import com.simax.si_max.database.FavoriteDbHelper;
import com.simax.si_max.model.Movie;
import com.simax.si_max.model.MoviesRepository;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.simax.si_max.DetailsActivity.MOVIE_ID;

public class MainActivity extends AppCompatActivity {

    //public static List<Movie> movies;
    public static String[] dates;
    public static String[] summary;
    public static String[] votes;
    public static String[] poster;
    public static String[] backdrop;
    public static String[] id;

    private MovieAdapter adapter;
    private MoviesRepository moviesRepository;
    private String sortBy = MoviesRepository.POPULAR;
    public static ContentResolver contentResolver;

    private List<Movie> movieList;
    private FavoriteDbHelper favoriteDbHelper;
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
                        initViews();
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
        favoriteDbHelper = new FavoriteDbHelper(activity);

        getAllFavorite();
    }


    private void getAllFavorite() {
        new  AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                movieList.clear();
                movieList.addAll(favoriteDbHelper.getAllFavorite());
                return null;

            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                adapter.notifyDataSetChanged();
            }
        }.execute();
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

}
