package com.simax.si_max.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@Entity (tableName = "favorites")
public class FavMovie implements Parcelable{


    public FavMovie(int id, String name, String release_date, float rating, String overview, String poster_path) {
        this.id = id;
        this.posterPath = poster_path;
        this.title = name;
        this.rating = rating;
        this.overview = overview;
        this.releaseDate = release_date;
    }


    public FavMovie() {
    }

    public FavMovie(int position) {
        this.id = position;
    }

    public FavMovie(String fav){
        this.fav = fav;
    }



    protected FavMovie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        posterPath = in.readString();
        releaseDate = in.readString();
        rating = in.readFloat();
        overview = in.readString();
        backdrop = in.readString();
    }

    public static final Creator<FavMovie> CREATOR = new Creator<FavMovie>() {
        @Override
        public FavMovie createFromParcel(Parcel in) {
            return new FavMovie(in);
        }

        @Override
        public FavMovie[] newArray(int size) {
            return new FavMovie[size];
        }
    };

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


    @PrimaryKey
    public int id;



    @ColumnInfo(name = "title")
    public String title;


    @ColumnInfo(name = "posterPath")
    public String posterPath;



    @ColumnInfo(name = "releaseDate")
    public  String releaseDate;



    @ColumnInfo(name = "rating")
    private float rating;


    @Ignore
    private List<Integer> genreIds;

    @ColumnInfo(name = "overview")
    private String overview;



    @ColumnInfo(name = "backdrop")
    private String backdrop;


    @Ignore
    private List<Genre> genres;


    public String getFav() {
        return fav;
    }

    public void setFav(String fav) {
        this.fav = fav;
    }

    @ColumnInfo(name = "fav")
    public String fav;


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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(releaseDate);
        dest.writeFloat(rating);
        dest.writeString(overview);
        dest.writeString(backdrop);
    }
}