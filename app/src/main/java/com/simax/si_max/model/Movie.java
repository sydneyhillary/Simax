package com.simax.si_max.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@Entity (tableName = "favorites")
public class Movie implements Serializable {


    public Movie(int id, String name, String release_date, float rating, String overview, String poster_path) {
        this.id = id;
        this.posterPath = poster_path;
        this.title = name;
        this.rating = rating;
        this.overview = overview;
        this.releaseDate = release_date;
    }


    public Movie() {
    }

    public Movie(int position) {
        this.id = position;
    }


    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }


    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", rating='" + rating + '\'' +
                ", overview='" + overview + '\'' +
                ", backdrop='" + backdrop + '\'' +
                '}';

    }

    @SerializedName("id")
    @Expose
    @PrimaryKey
    public int id;


    @SerializedName("title")
    @Expose
    @ColumnInfo(name = "title")
    public String title;

    @SerializedName("poster_path")
    @Expose
    @ColumnInfo(name = "posterPath")
    public String posterPath;


    @SerializedName("release_date")
    @Expose
    @ColumnInfo(name = "releaseDate")
    public  String releaseDate;


    @SerializedName("vote_average")
    @Expose
    @ColumnInfo(name = "rating")
    private float rating;

    @SerializedName("genre_ids")
    @Expose
    @Ignore
    private List<Integer> genreIds;

    @ColumnInfo(name = "overview")
    @SerializedName("overview")
    @Expose
    private String overview;


    @SerializedName("backdrop_path")
    @Expose
    @ColumnInfo(name = "backdrop")
    private String backdrop;

    @SerializedName("genres")
    @Expose
    @Ignore
    private List<Genre> genres;

    public Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        posterPath = in.readString();
        releaseDate = in.readString();
        rating = in.readFloat();
        overview = in.readString();
        backdrop = in.readString();
    }



    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }


}