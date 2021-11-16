package com.buzzware.nowapp.Addapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.nowapp.Constants.Constant;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Screens.General.PlaceActivity;
import com.buzzware.nowapp.databinding.ItemPlacePicBinding;
import com.buzzware.nowapp.response.Photo;
import com.buzzware.nowapp.response.Result;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RestaurantsPicturesAdapter extends RecyclerView.Adapter<RestaurantsPicturesAdapter.LocationPictureHolder> {


    List<Result> results;
    Activity c;

    public RestaurantsPicturesAdapter(Activity c, List<Result> results) {
        this.c = c;
        this.results = results;
    }

    @NonNull
    @NotNull
    @Override
    public RestaurantsPicturesAdapter.LocationPictureHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new RestaurantsPicturesAdapter.LocationPictureHolder(ItemPlacePicBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull LocationPictureHolder holder, int position) {
        Result result = results.get(position);

        String url = null;

        try {
            url = "https://maps.googleapis.com/maps/api/place/photo" +
                    "?maxwidth=400" +
                    "&photo_reference=" + result.getPhotos().get(0).getPhotoReference() +
                    "&key=" + Constant.GOOGLE_PLACES_API_KEY;
        } catch (Exception e) {

        }

        if (result.getName() != null) {
            holder.binding.nameIV.setText(result.getName());
        }

        holder.binding.getRoot().setOnClickListener(view -> {
            String json = new Gson().toJson(result);

            c.startActivity(new Intent(c, PlaceActivity.class)
            .putExtra("place",json));

        });

        Glide.with(c).load(url).apply(new RequestOptions().centerCrop().placeholder(R.drawable.no_image_placeholder)).into(holder.binding.locationPic);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    class LocationPictureHolder extends RecyclerView.ViewHolder {
        View view;
        ItemPlacePicBinding binding;

        public LocationPictureHolder(@NonNull ItemPlacePicBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
