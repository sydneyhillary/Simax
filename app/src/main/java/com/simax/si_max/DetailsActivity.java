package com.simax.si_max;

import android.app.Application;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
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
import com.simax.si_max.model.FavMovie;
import com.simax.si_max.model.Movie;
import com.simax.si_max.model.MoviesRepository;
import com.simax.si_max.model.Review;
import com.simax.si_max.model.Trailer;
import com.simax.si_max.room.FavModel;
import com.simax.si_max.room.FavRoomDb;

import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    public static String MOVIE_ID = "movie_id";

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
    private final AppCompatActivity activity = DetailsActivity.this;

    Movie movie;
    private String mMovieTitle;
    private int mMovieId;
    private String mMoviePlot;
    private String mMovieReaseDate;
    private float mMovieAverageVote;
    private String mMoviePosterPath;
    String favText;
    String text;

    private  Toast mFavoritesToast;
    String thumbnail;
    String movieName;
    String synopsis;
    float rating;
    String dateOfRelease;
    String posterImage;
    int movie_id;
    String newText;
    Button favoriteButton;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

            final Button favoriteButton = (Button) findViewById(R.id.add_favorite);

            String saveState = favoriteButton.getText().toString();

            outState.putString("save_state",saveState);


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

            final Button favoriteButton = (Button) findViewById(R.id.add_favorite);
            String saveState = savedInstanceState.getString("save_state");
            favoriteButton.setText(saveState);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        favRoomDb = FavRoomDb.getDatabase(getApplicationContext());



        //String gotPosition=getIntent().getStringExtra(MOVIE_ID);
        //intGotPosition=Integer.parseInt(gotPosition);
        favModel = ViewModelProviders.of(this).get(FavModel.class);
        movieId = getIntent().getIntExtra(MOVIE_ID, movieId);
        Movie movie = getIntent().getParcelableExtra(MOVIE_ID);
        if (movie != null) {
            mMovieTitle = movie.getTitle();
            mMovieId = movie.getId();
            mMoviePlot = movie.getOverview();
            mMovieReaseDate = movie.getReleaseDate();
            mMovieAverageVote = movie.getRating();
            mMoviePosterPath = POSTER_BASE_URL + movie.getPosterPath();
            favImage = movie.getPosterPath();

        }


        moviesRepository = MoviesRepository.getInstance();


        setupToolbar();

        initUI();

        getMovie();

        favoriteButton = (Button) findViewById(R.id.add_favorite);





        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text  = favoriteButton.getText().toString();

                if (text.equals("Mark Favorite")) {
                    addToFavorites();
                    Snackbar.make(v, "Added to Favorite",
                            Snackbar.LENGTH_SHORT).show();
                    favoriteButton.setText("Unmark Favorite");
                } else if (text.equals("Unmark Favorite")) {
                    removeFromFavorites();

                    favoriteButton.setText("Mark Favorite");

                    Toast toast = Toast.makeText(DetailsActivity.this, R.string.string_message_id, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.FILL_HORIZONTAL, 0, 0);
                    View view = toast.getView();
                    view.setBackgroundResource(R.drawable.custom_view);
                    TextView text1 = (TextView) view.findViewById(android.R.id.message);
                    text1.setTextColor(Color.parseColor("#C0C0C0"));
                    /*Here you can do anything with above textview like text.setTextColor(Color.parseColor("#000000"));*/
                    toast.show();
                }

            }
        });
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
                posterImage = movie.getPosterPath();
                favText = movie.getFav();
                if (favText == null){
                    favoriteButton.setText("Mark Favorite");
                }else {
                    favoriteButton.setText(favText);
                }


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
    private void removeFromFavorites() {

       favRoomDb.favDao().deleteMovie(movie);
    }

    private void addToFavorites() {
        int id = movieId;
        String name = movieTitle.getText().toString();
        String release_date = movieReleaseDate.getText().toString();
        float rating = movieRating.getRating();
        String overview = movieOverview.getText().toString();
        String poster_path = posterImage;

        FavMovie newMovie = new FavMovie();
        newMovie.setId(id);
        newMovie.setTitle(name);
        newMovie.setPosterPath(poster_path);
        newMovie.setRating(rating);
        newMovie.setOverview(overview);
        newMovie.setReleaseDate(release_date);
        newMovie.setFav(favoriteButton.getText().toString());




        movie = new Movie(id, name, release_date, rating, overview, poster_path);

        favRoomDb.favDao().insert(movie);
        //Toast.makeText(this, "add successful", Toast.LENGTH_SHORT).show();


    }
}
