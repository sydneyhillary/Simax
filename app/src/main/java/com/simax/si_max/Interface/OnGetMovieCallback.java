package com.simax.si_max.Interface;

import com.simax.si_max.model.Movie;

public interface OnGetMovieCallback {
    void onSuccess(Movie movie);

    void onError();
}
