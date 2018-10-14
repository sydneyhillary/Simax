package com.simax.si_max;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
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
import com.simax.si_max.Interface.RecyclerViewClickListener;
import com.simax.si_max.model.FavMovie;
import com.simax.si_max.model.Genre;
import com.simax.si_max.model.Movie;

import java.util.ArrayList;
import java.util.List;


public class FavAdapter extends RecyclerView.Adapter<FavAdapter.MovieViewHolder> {

    private List<Movie> movies;
    private List<Movie> movieList;
    private Context mContext;
    private List<Genre> allGenres;
    private OnFavoritesCallback callback;
    private String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500";
    private Movie[] mDataSource;
    private Movie[] moviesList;


    private RecyclerViewClickListener itemListener;

    int mId;
    String mTitle;
    String mRate;
    String mOverView;
    String mBackdrop;
    String mDate;
    //String mTitle = movie.getTitle();

    public FavAdapter(List<Movie> movies, OnFavoritesCallback favoritesCallback) {
        this.movies = movies;
        this.callback = favoritesCallback;

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




    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView releaseDate;
        TextView title;
        TextView rating;
        TextView genres;
        ImageView imageView;
        Movie movie;
        private RecyclerViewClickListener listener;

        void setDataSource(ArrayList<Movie> dataSource) {
            mDataSource = dataSource.toArray(new Movie[0]);
            notifyDataSetChanged();

        }

        public MovieViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            this.listener = listener;
            //releaseDate = itemView.findViewById(R.id.item_movie_release_date);
            title = itemView.findViewById(R.id.title);
            rating = itemView.findViewById(R.id.user_rating);
            imageView = itemView.findViewById(R.id.thumbnail);
            //genres = itemView.findViewById(R.id.item_movie_genre);

          itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onClick(movie);
                   /* int id = mId;
                    String title = mTitle;
                    Intent intent;
                    intent = new Intent(mContext, DetailsFavoriteActivity.class);
                    intent.putExtra("id",id);
                    intent.putExtra(String.valueOf(id), DetailsFavoriteActivity.FAVS_ID);
                    intent.putExtra("name", mTitle);
                    mContext.startActivity(intent);*/
                }
            });
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

        @Override
        public void onClick(View v) {



            // check if item still exists
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
    public void clearMovies(List<Movie> movies) {
        this.movies.clear();


        notifyDataSetChanged();
    }

}


