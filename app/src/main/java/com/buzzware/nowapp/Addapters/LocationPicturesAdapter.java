package com.buzzware.nowapp.Addapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.nowapp.Constants.Constant;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.databinding.ItemPlaceBinding;
import com.buzzware.nowapp.databinding.ItemPlacePicBinding;
import com.buzzware.nowapp.databinding.ItemPlacePicBindingImpl;
import com.buzzware.nowapp.response.Photo;
import com.buzzware.nowapp.response.Result;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LocationPicturesAdapter extends RecyclerView.Adapter<LocationPicturesAdapter.LocationPictureHolder> {


    List<Photo> results;
    Activity c;

    public LocationPicturesAdapter(Activity c, List<Photo> results) {
        this.c = c;
        this.results = results;
    }

    @NonNull
    @NotNull
    @Override
    public LocationPicturesAdapter.LocationPictureHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new LocationPicturesAdapter.LocationPictureHolder(ItemPlacePicBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull LocationPictureHolder holder, int position) {
        Photo result = results.get(position);

        String url = null;

        try {
            url = "https://maps.googleapis.com/maps/api/place/photo" +
                    "?maxwidth=400" +
                    "&photo_reference=" + result.getPhotoReference() +
                    "&key=" + Constant.GOOGLE_PLACES_API_KEY;
        } catch (Exception e) {
//            url = result.getHtmlAttributions().get(0);
        }

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
