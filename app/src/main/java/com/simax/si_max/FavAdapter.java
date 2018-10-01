package com.simax.si_max;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.simax.si_max.Interface.OnFavoritesCallback;
import com.simax.si_max.Interface.OnMoviesCallback;
import com.simax.si_max.model.Genre;
import com.simax.si_max.model.Movie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.simax.si_max.DetailsActivity.MOVIE_ID;
import static com.simax.si_max.DetailsFavoriteActivity.FAVS_ID;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.MovieViewHolder> {

    private List<Movie> movies;
    private List<Movie> movieList;
    private Context mContext;
    private List<Genre> allGenres;
    private OnFavoritesCallback callback;
    private String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500";
    private Movie[] mDataSource;
    private Movie[] moviesList;

    int mId;
    String mTitle;
    String mRate;
    String mOverView;
    String mBackdrop;
    String mDate;
    //String mTitle = movie.getTitle();

    public FavAdapter(List<Movie> movies, OnFavoritesCallback callback) {
        this.movies = movies;
        this.callback = callback;
        this.allGenres = allGenres;

    }

    public FavAdapter() {

    }

    public void setMovieData(Movie[] movieData) {
        this.movies = Arrays.asList(movieData);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(movies.get(position));
       /*  holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent;
                intent = new Intent(mContext, DetailsFavoriteActivity.class);
                intent.putExtra("id",mId);
                intent.putExtra("name", mTitle);
                mContext.startActivity(intent);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return movies == null ? 0 : movies.size();

    }




    class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView releaseDate;
        TextView title;
        TextView rating;
        TextView genres;
        ImageView imageView;
        Movie movie;

        void setDataSource(ArrayList<Movie> dataSource) {
            mDataSource = dataSource.toArray(new Movie[0]);
            notifyDataSetChanged();

        }
        public void favs(){

        }

        public MovieViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            //releaseDate = itemView.findViewById(R.id.item_movie_release_date);
            title = itemView.findViewById(R.id.title);
            rating = itemView.findViewById(R.id.user_rating);
            imageView = itemView.findViewById(R.id.thumbnail);
            //genres = itemView.findViewById(R.id.item_movie_genre);

          /* itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent;
                    intent = new Intent(mContext, DetailsFavoriteActivity.class);
                    intent.putExtra("id",mId);
                    intent.putExtra("name", mTitle);
                    mContext.startActivity(intent);
                }
            });*/
        }


        void bind(Movie movie) {

            mId = movie.getId();
            mTitle = movie.getTitle();
            mRate = String.valueOf(movie.getRating());
            mOverView = movie.getOverview();
            mBackdrop = movie.getBackdrop();
            mDate = movie.getReleaseDate();
            //String mTitle = movie.getTitle();
            //releaseDate.setText(movie.getReleaseDate().split("-")[0]);
            title.setText(mTitle);
            rating.setText(String.valueOf(movie.getRating()));
            //genres.setText("");
            Glide.with(itemView)
                    .load(IMAGE_BASE_URL + movie.getPosterPath())
                    .apply(RequestOptions.placeholderOf(R.color.colorPrimary))
                    .into(imageView);

            this.movie = movie;
        }
        private String getGenres(List<Integer> genreIds) {
            List<String> movieGenres = new ArrayList<>();
            for (Integer genreId : genreIds) {
                for (Genre genre : allGenres) {
                    if (genre.getId() == genreId) {
                        movieGenres.add(genre.getName());
                        break;
                    }
                }
            }
            return TextUtils.join(", ", movieGenres);
        }
    }
    void setFavorites(List<Movie> movies){
        this.movies = movies;
        notifyDataSetChanged();
    }
    public void appendMovies(List<Movie> moviesToAppend) {
        movies.addAll(moviesToAppend);
        notifyDataSetChanged();
    }
    public void clearMovies() {
        movies.clear();

        notifyDataSetChanged();
    }

}


