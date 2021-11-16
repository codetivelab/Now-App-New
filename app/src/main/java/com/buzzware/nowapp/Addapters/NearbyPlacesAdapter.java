package com.buzzware.nowapp.Addapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.nowapp.Models.ReplyModel;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Screens.General.PlaceActivity;
import com.buzzware.nowapp.databinding.ItemPlaceBinding;
import com.buzzware.nowapp.response.Result;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NearbyPlacesAdapter extends RecyclerView.Adapter<NearbyPlacesAdapter.NearByPlaceHolder> {


    List<Result> results;
    Activity c;

    public NearbyPlacesAdapter(Activity c, List<Result> results) {
        this.c = c;
        this.results = results;
    }

    @NonNull
    @NotNull
    @Override
    public NearbyPlacesAdapter.NearByPlaceHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new NearbyPlacesAdapter.NearByPlaceHolder(ItemPlaceBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull NearByPlaceHolder holder, int position) {
        Result result = results.get(position);
        Boolean open = false;
        if (result.getOpeningHours() != null && result.getOpeningHours().getOpenNow() != null)
            open = result.getOpeningHours().getOpenNow();

        String openNow = "Open";

        if (!open) {
            openNow = "Closed";
        }

        if (result.getPhotos() != null) {
            holder.binding.locationPicIV.setVisibility(View.GONE);
            holder.binding.locationPicsRV.setLayoutManager(new LinearLayoutManager(c, RecyclerView.HORIZONTAL, false));
            holder.binding.locationPicsRV.setAdapter(new LocationPicturesAdapter(c, result.getPhotos()));
        } else {
            Glide.with(c).load(result.getIcon()).apply(new RequestOptions().centerCrop().placeholder(R.drawable.no_image_placeholder)).into(holder.binding.locationPicIV);
        }

        if (result.getName() != null)
            holder.binding.nameTV.setText(result.getName());
        if (result.getRating() != null)
            holder.binding.ratingBar.setRating(new Double(result.getRating()).floatValue());

        holder.binding.openCloseTV.setText(openNow);
        if (result.getUserRatingsTotal() != null)
            holder.binding.reviewsCountTV.setText("(" + result.getUserRatingsTotal() + ")");

        holder.binding.nameTV.setOnClickListener(view -> {
            String json = new Gson().toJson(result);

            c.startActivity(new Intent(c, PlaceActivity.class)
                    .putExtra("place",json));

        });
        holder.binding.openCloseTV.setOnClickListener(view -> {
            String json = new Gson().toJson(result);

            c.startActivity(new Intent(c, PlaceActivity.class)
                    .putExtra("place",json));

        });
        holder.binding.typeTV.setOnClickListener(view -> {
            String json = new Gson().toJson(result);

            c.startActivity(new Intent(c, PlaceActivity.class)
                    .putExtra("place",json));

        });
        holder.binding.ratingBar.setOnClickListener(view -> {
            String json = new Gson().toJson(result);

            c.startActivity(new Intent(c, PlaceActivity.class)
                    .putExtra("place",json));

        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    class NearByPlaceHolder extends RecyclerView.ViewHolder {
        View view;
        ItemPlaceBinding binding;

        public NearByPlaceHolder(@NonNull ItemPlaceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
