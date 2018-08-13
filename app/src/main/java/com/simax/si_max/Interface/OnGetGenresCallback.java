package com.simax.si_max.Interface;

import com.simax.si_max.model.Genre;
import com.simax.si_max.model.Movie;

import java.util.List;

public interface OnGetGenresCallback {
    void onSuccess(List<Genre> genres);

    void onError();
}
