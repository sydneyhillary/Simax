package com.simax.si_max.Interface;

import com.simax.si_max.model.Movie;

import java.util.List;

public interface OnFavoritesCallback {
    void onClick(Movie movie);

    void onSuccess(Movie movie);

    void onError();
}
