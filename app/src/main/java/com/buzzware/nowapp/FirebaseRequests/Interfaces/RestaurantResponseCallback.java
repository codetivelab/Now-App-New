package com.buzzware.nowapp.FirebaseRequests.Interfaces;

import com.buzzware.nowapp.Models.RestaurantDataModel;

import java.util.List;

public interface RestaurantResponseCallback {
    void onResponse(List<RestaurantDataModel> list, boolean isError, String message);
}
