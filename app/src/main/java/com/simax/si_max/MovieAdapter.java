package com.simax.si_max;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
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
import com.simax.si_max.Interface.OnMoviesCallback;
import com.simax.si_max.model.Genre;
import com.simax.si_max.model.Movie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movies;
    private List<Movie> movieList;
    private Context mContext;
    private List<Genre> allGenres;
    private OnMoviesCallback callback;
    private String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500";
    private Movie[] mDataSource;
    private Movie[] moviesList;

    public MovieAdapter(List<Movie> movies, OnMoviesCallback callback) {
        this.movies = movies;
        this.callback = callback;
        this.allGenres = allGenres;
        
    }

    public MovieAdapter(List<Movie> movies) {
        this.movies = movies;
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
    }

    @Override
    public int getItemCount() {
        return movies == null ? 0 : movies.size();

    }

    public void clearMovies(List<Movie> movies) {
        movies.clear();
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


        public MovieViewHolder(View itemView) {
            super(itemView);
            //releaseDate = itemView.findViewById(R.id.item_movie_release_date);
            title = itemView.findViewById(R.id.title);
            rating = itemView.findViewById(R.id.user_rating);
            imageView = itemView.findViewById(R.id.thumbnail);
            //genres = itemView.findViewById(R.id.item_movie_genre);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        callback.onClick(movie);


                }
            });
        }

        void bind(Movie movie) {
            //releaseDate.setText(movie.getReleaseDate().split("-")[0]);
            title.setText(movie.getTitle());
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


