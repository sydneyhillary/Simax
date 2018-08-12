package com.simax.si_max.Interface;

import com.simax.si_max.model.Movie;

import java.util.List;

public interface onGetMoviesCallback {
    void onSuccess(int page, List<Movie> movies);

    void onError();
}
