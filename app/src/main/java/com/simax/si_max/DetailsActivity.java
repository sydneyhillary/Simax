package com.simax.si_max;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.simax.si_max.Interface.OnGetGenresCallback;
import com.simax.si_max.Interface.OnGetMovieCallback;
import com.simax.si_max.Interface.OnGetReviewsCallback;
import com.simax.si_max.Interface.OnGetTrailersCallback;
import com.simax.si_max.database.FavoriteDbHelper;
import com.simax.si_max.model.Genre;
import com.simax.si_max.model.Movie;
import com.simax.si_max.model.MoviesRepository;
import com.simax.si_max.model.Review;
import com.simax.si_max.model.Trailer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.simax.si_max.MainActivity.contentResolver;

public class DetailsActivity extends AppCompatActivity {

    public static String MOVIE_ID = "movie_id";

    public int intGotPosition;

    private static String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w780";
    private static String YOUTUBE_VIDEO_URL = "http://www.youtube.com/watch?v=%s";
    private static String YOUTUBE_THUMBNAIL_URL = "http://img.youtube.com/vi/%s/0.jpg";

    private ImageView movieBackdrop;
    private TextView movieTitle;
    private TextView movieGenres;
    private TextView movieOverview;
    private TextView movieOverviewLabel;
    private TextView movieReleaseDate;
    private RatingBar movieRating;
    private LinearLayout movieTrailers;
    private LinearLayout movieReviews;
    private TextView trailersLabel;
    private TextView reviewsLabel;

    boolean isFavourite=false;
    Button mButton;

    private MoviesRepository moviesRepository;
    private int movieId;
    private FavoriteDbHelper favoriteDbHelper;
    private Movie favorite;
    private final AppCompatActivity activity = DetailsActivity.this;

    Movie movie;
    String thumbnail;
    String movieName;
    String synopsis;
    float rating;
    String dateOfRelease;
    int movie_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //String gotPosition=getIntent().getStringExtra(MOVIE_ID);
        //intGotPosition=Integer.parseInt(gotPosition);

        movieId = getIntent().getIntExtra(MOVIE_ID, movieId);

        moviesRepository = MoviesRepository.getInstance();


        setupToolbar();

        initUI();

        getMovie();

        final MaterialFavoriteButton favoriteButton = (MaterialFavoriteButton) findViewById(R.id.add_favorite);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        favoriteButton.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener(){
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite){
                        if (favorite){
                            SharedPreferences.Editor editor = getSharedPreferences("com.delaroystudios.movieapp.DetailActivity", MODE_PRIVATE).edit();
                            editor.putBoolean("Favorite Added", true);
                            editor.commit();
                            saveToFavorite();
                            Snackbar.make(buttonView, "Added to Favorite",
                                    Snackbar.LENGTH_SHORT).show();
                        }else{
                            int movie_id = getIntent().getExtras().getInt("id");
                            favoriteDbHelper = new FavoriteDbHelper(DetailsActivity.this);
                            favoriteDbHelper.deleteFavorite(movie_id);

                            SharedPreferences.Editor editor = getSharedPreferences("com.delaroystudios.movieapp.DetailActivity", MODE_PRIVATE).edit();
                            editor.putBoolean("Favorite Removed", true);
                            editor.commit();
                            Snackbar.make(buttonView, "Removed from Favorite",
                                    Snackbar.LENGTH_SHORT).show();
                        }

                    }
                }
        );

    }

    private void saveToFavorite() {
        FavoriteDbHelper favoriteDbHelper = new FavoriteDbHelper(this);
        movie = getIntent().getParcelableExtra(MOVIE_ID);

        //thumbnail = movie.getPosterPath();
        //movieName = movie.getTitle();
        //synopsis = movie.getOverview();
        //rating = Float.toString(movie.getRating());
        //dateOfRelease = movie.getReleaseDate();
        //movie_id = movie.getId();

        Movie favorite = new Movie();
        //int movie_id = getIntent().getExtras().getInt("id");
        //Float  rate = movie.getRating();
        //String poster = getIntent().getExtras().getString("poster_path");

        favorite.setId(movieId);
        favorite.setTitle(movieName);
        favorite.setPosterPath(thumbnail);
        favorite.setRating(rating);
        favorite.setOverview(synopsis);

        favoriteDbHelper.addFavorite(favorite);
    }



    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void initUI() {
        movieBackdrop = findViewById(R.id.movieDetailsBackdrop);
        movieTitle = findViewById(R.id.movieDetailsTitle);
        movieGenres = findViewById(R.id.movieDetailsGenres);
        movieOverview = findViewById(R.id.movieDetailsOverview);
        movieOverviewLabel = findViewById(R.id.summaryLabel);
        movieReleaseDate = findViewById(R.id.movieDetailsReleaseDate);
        movieRating = findViewById(R.id.movieDetailsRating);
        movieTrailers = findViewById(R.id.movieTrailers);
        movieReviews = findViewById(R.id.movieReviews);
        trailersLabel = findViewById(R.id.trailersLabel);
        reviewsLabel = findViewById(R.id.reviewsLabel);
    }

    public void getMovie() {
        moviesRepository.getMovie(movieId, new OnGetMovieCallback() {
            @Override
            public void onSuccess(Movie movie) {
                movieName = movie.getTitle();
                rating = movie.getRating();
                dateOfRelease = movie.getReleaseDate();
                synopsis = movie.getOverview();


                movieTitle.setText(movieName);
                movieOverviewLabel.setVisibility(View.VISIBLE);
                movieOverview.setText(synopsis);

                movieRating.setVisibility(View.VISIBLE);
                movieRating.setRating(rating / 2);
                getTrailers(movie);
                getReviews(movie);
                movieReleaseDate.setText(dateOfRelease);
                if (!isFinishing()) {
                    Glide.with(DetailsActivity.this)
                            .load(IMAGE_BASE_URL + movie.getBackdrop())
                            .apply(RequestOptions.placeholderOf(R.color.colorPrimary))
                            .into(movieBackdrop);
                }
            }

            @Override
            public void onError() {
                finish();
            }
        });
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showError() {
        Toast.makeText(DetailsActivity.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
    }
    private void getTrailers(Movie movie) {
        moviesRepository.getTrailers(movie.getId(), new OnGetTrailersCallback() {
            @Override
            public void onSuccess(List<Trailer> trailers) {
                trailersLabel.setVisibility(View.VISIBLE);
                movieTrailers.removeAllViews();
                for (final Trailer trailer : trailers) {
                    View parent = getLayoutInflater().inflate(R.layout.thumbnail_trailer, movieTrailers, false);
                    ImageView thumbnail = parent.findViewById(R.id.thumbnail);
                    thumbnail.requestLayout();
                    thumbnail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showTrailer(String.format(YOUTUBE_VIDEO_URL, trailer.getKey()));
                        }
                    });
                    Glide.with(DetailsActivity.this)
                            .load(String.format(YOUTUBE_THUMBNAIL_URL, trailer.getKey()))
                            .apply(RequestOptions.placeholderOf(R.color.newColor).centerCrop())
                            .into(thumbnail);
                    movieTrailers.addView(parent);
                }
            }

            @Override
            public void onError() {
                // Do nothing
                trailersLabel.setVisibility(View.GONE);
            }
        });
    }

    private void showTrailer(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
    private void getReviews(Movie movie) {
        moviesRepository.getReviews(movie.getId(), new OnGetReviewsCallback() {
            @Override
            public void onSuccess(List<Review> reviews) {
                reviewsLabel.setVisibility(View.VISIBLE);
                movieReviews.removeAllViews();
                for (Review review : reviews) {
                    View parent = getLayoutInflater().inflate(R.layout.review, movieReviews, false);
                    TextView author = parent.findViewById(R.id.reviewAuthor);
                    TextView content = parent.findViewById(R.id.reviewContent);
                    author.setText(review.getAuthor());
                    content.setText(review.getContent());
                    movieReviews.addView(parent);
                }
            }

            @Override
            public void onError() {
                // Do nothing
            }
        });
    }


}
