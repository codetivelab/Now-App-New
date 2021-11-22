package com.buzzware.nowapp.Screens.General;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.nowapp.Addapters.LocationPicturesAdapter;
import com.buzzware.nowapp.Constants.Constant;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.databinding.ActivityAddCommentsBinding;
import com.buzzware.nowapp.databinding.ActivityPlaceBinding;
import com.buzzware.nowapp.placeresponse.Period;
import com.buzzware.nowapp.placeresponse.PlaceDetail;
import com.buzzware.nowapp.placeresponse.PlaceDetailResponse;
import com.buzzware.nowapp.response.NearbyPlacesResponse;
import com.buzzware.nowapp.response.Result;
import com.buzzware.nowapp.retrofit.Controller;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceActivity extends AppCompatActivity {

    ActivityPlaceBinding binding;

    Result result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityPlaceBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        getExtrasFromIntent();

        setData();

        getPlaceDetail();

        setListeners();
    }

    void getPlaceDetail() {

        String url = "/maps/api/place/details/json?place_id=" + result.getPlaceId() + "&key=" + Constant.GOOGLE_PLACES_API_KEY;

        Controller.getApi().getPlaces(url, "asdasd")
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Gson gson = new Gson();

                        if (response.body() != null && response.isSuccessful()) {

                            PlaceDetailResponse placeDetail = gson.fromJson(response.body(), PlaceDetailResponse.class);

                            if (placeDetail.result.website != null)
                                binding.internetTV.setText(placeDetail.result.website);

                            if (placeDetail.result.formatted_phone_number != null)
                                binding.phoneTV.setText(placeDetail.result.formatted_phone_number);

                            String openingHours = "";

                            for (String time : placeDetail.result.opening_hours.weekday_text) {

                                openingHours = openingHours + time;

                                openingHours = openingHours + "\n";

                            }


                            if (placeDetail.result.photos != null) {
                                binding.locationPicsRV.setLayoutManager(new LinearLayoutManager(PlaceActivity.this, RecyclerView.HORIZONTAL, false));
                                binding.locationPicsRV.setAdapter(new LocationPicturesAdapter(PlaceActivity.this, placeDetail.result.photos));
                            } else {
                            }

                            binding.timingsTv.setText(openingHours);
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });
    }

    private void setListeners() {

        binding.include2.backIcon.setOnClickListener(view -> onBackPressed());
    }

    private void setData() {

        binding.include2.backAppBarTitle.setText("Place");

        Boolean open = false;
        if (result.getOpeningHours() != null && result.getOpeningHours().getOpenNow() != null)
            open = result.getOpeningHours().getOpenNow();

        String openNow = "Open";

        if (!open) {
            openNow = "Closed";
        }

        if (result.getName() != null)
            binding.nameTV.setText(result.getName());
        if (result.getRating() != null)
            binding.ratingBar.setRating(new Double(result.getRating()).floatValue());

        binding.openCloseTV.setText(openNow);
        if (result.getUserRatingsTotal() != null)
            binding.reviewsCountTV.setText("(" + result.getUserRatingsTotal() + ")");

        if (result.getVicinity() != null)
            binding.locationTV.setText(result.getVicinity());
        if (result.getPriceLevel() != null) {

            String level = "Price Level ";
            for (int i = 0; i < result.getPriceLevel(); i++) {
                level = level + "$";
            }

            binding.typeTV.setText(level);
        }
    }

    private void getExtrasFromIntent() {

        if (getIntent().getExtras() != null && getIntent().getStringExtra("place") != null) {

            String json = getIntent().getStringExtra("place");

            result = new Gson().fromJson(json, Result.class);
        }
    }
}
