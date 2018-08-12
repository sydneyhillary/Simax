package com.simax.si_max.Interface;

import com.simax.si_max.model.Review;

import java.util.List;

public interface OnGetReviewsCallback {
    void onSuccess(List<Review> reviews);

    void onError();

}
