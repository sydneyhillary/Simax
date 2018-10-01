package com.simax.si_max;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.simax.si_max.Interface.OnGetMovieCallback;
import com.simax.si_max.Interface.OnGetReviewsCallback;
import com.simax.si_max.Interface.OnGetTrailersCallback;
import com.simax.si_max.model.Movie;
import com.simax.si_max.model.MoviesRepository;
import com.simax.si_max.model.Review;
import com.simax.si_max.model.Trailer;
import com.simax.si_max.room.FavModel;
import com.simax.si_max.room.FavRoomDb;

import java.util.List;

public class DetailsFavoriteActivity extends AppCompatActivity {

    public static String FAVS_ID = "movie_id";

    public int intGotPosition;
    private static String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w500";
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
    public String favImage;

    boolean isFavourite=false;
    Button mButton;

    private MoviesRepository moviesRepository;
    private int movieId;
    private Movie favorite;
    private FavModel favModel;
    private FavRoomDb favRoomDb;
    private final AppCompatActivity activity = DetailsFavoriteActivity.this;

    Movie movie;
    private String mMovieTitle;
    private int mMovieId;
    private String mMoviePlot;
    private String mMovieReaseDate;
    private float mMovieAverageVote;
    private String mMoviePosterPath;

    int id;
    String rate;
    String date;
    String overview;

    private  Toast mFavoritesToast;
    String thumbnail;
    String movieName;
    String synopsis;
    float rating;
    String dateOfRelease;
    String posterImage;
    int movie_id;

    String name;
    private static final int DEFAULT_POSITION = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_details);
        favRoomDb=FavRoomDb.getDatabase(getApplicationContext());

        favModel = ViewModelProviders.of(this).get(FavModel.class);
      //movieId = getIntent().getIntExtra(FAVS_ID, movie.getId());


        Intent iin= getIntent();
        int position = iin.getIntExtra("id",0);
        name = iin.getStringExtra("name");
        Movie newMovies = new Movie(position);
        thumbnail = newMovies.getBackdrop();
        movieName = newMovies.getTitle();
        synopsis  = newMovies.getOverview();
        rating = newMovies.getRating();
        dateOfRelease = newMovies.getReleaseDate();
        Movie movie = getIntent().getParcelableExtra(FAVS_ID);

        if (movie != null) {
            mMovieTitle = movie.getTitle();
            mMovieId = movie.getId();
            mMoviePlot = movie.getOverview();
            mMovieReaseDate = movie.getReleaseDate();
            mMovieAverageVote = movie.getRating();
            mMoviePosterPath = POSTER_BASE_URL+movie.getPosterPath();
            favImage =  movie.getPosterPath();
            }

        moviesRepository = MoviesRepository.getInstance();


        setupToolbar();

        initUI();

        //getMovie();

        final MaterialFavoriteButton favoriteButton = (MaterialFavoriteButton) findViewById(R.id.add_favorite);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        /*favoriteButton.setOnFavoriteChangeListener(
                new MaterialFavoriteButton.OnFavoriteChangeListener(){
                    @Override
                    public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite){
                        if (favorite){
                            addToFavorites();
                            Snackbar.make(buttonView, "Added to Favorite",
                                    Snackbar.LENGTH_SHORT).show();
                        }else{
                           removeFromFavorites();
                            Snackbar.make(buttonView, "Removed from Favorite",
                                    Snackbar.LENGTH_SHORT).show();
                        }

                    }
                }
        );*/
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
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

        movieTitle.setText(name);
       // movieRating.setRating(Float.parseFloat(rate));
        movieReleaseDate.setText(dateOfRelease);
        movieOverview.setText(synopsis);
    }
    public void getDetails(){

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        movieTitle.setText("");
        return true;
    }


    private void showError() {
        Toast.makeText(DetailsFavoriteActivity.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
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
                    Glide.with(DetailsFavoriteActivity.this)
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
