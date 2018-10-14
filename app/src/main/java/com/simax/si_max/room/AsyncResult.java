package com.simax.si_max.room;

import com.simax.si_max.model.FavMovie;
import com.simax.si_max.model.Movie;

import java.util.List;

public interface AsyncResult {
    void asyncFinished(List<Movie> results);
}
