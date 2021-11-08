package com.buzzware.nowapp.Addapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.nowapp.Models.HomeListModel;
import com.buzzware.nowapp.Models.RestaurantDataModel;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.databinding.HomeListItemLayBinding;
import com.buzzware.nowapp.databinding.HomeListItemLayVerticalBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeTopListAddapters extends RecyclerView.Adapter<HomeTopListAddapters.imageViewHolder>{
    public Context context;
    List<RestaurantDataModel> homeListModels;
    View view;

    public HomeTopListAddapters(Context context, List<RestaurantDataModel> homeListModels) {
        this.context = context;
        this.homeListModels = homeListModels;
    }

    @Override
    public imageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HomeTopListAddapters.imageViewHolder(HomeListItemLayVerticalBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(final imageViewHolder holder, final int position) {
        final RestaurantDataModel homeListModel= homeListModels.get(position);
        String intensity = homeListModel.GetIntensity(GetCurrentDay(), GetCurrentHours());
        holder.binding.buisnessNameTV.setText(homeListModel.getBusinessName());
        holder.binding.buisnessRating.setRating(Float.parseFloat(homeListModel.getBusinessTotalRating()));
        Picasso.with(context).load(homeListModel.getBusinessBackgroundImage()).fit().into(holder.binding.buisnessImageIV, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
                holder.binding.buisnessImageIV.setImageResource(R.drawable.no_image_placeholder);
            }
        });

        ///set intensty
        if(intensity.equals("-2") || intensity.equals("-1")){
            holder.binding.buisnessIntensity.setText("EMPTY");
            holder.binding.buisnessProgress.setProgress(20);
            holder.binding.buisnessProgress.setProgressColors(context.getResources().getColor(R.color.darkblackk),context.getResources().getColor(R.color.white));
        }else if(intensity.equals("0")){
            holder.binding.buisnessIntensity.setText("ROOMY");
            holder.binding.buisnessProgress.setProgress(50);
            holder.binding.buisnessProgress.setProgressColors(context.getResources().getColor(R.color.darkblackk),context.getResources().getColor(R.color.color_green));

        }else if(intensity.equals("1") || intensity.equals("2")){
            holder.binding.buisnessIntensity.setText("CROWDY");
            holder.binding.buisnessProgress.setProgress(90);
            holder.binding.buisnessProgress.setProgressColors(context.getResources().getColor(R.color.darkblackk),context.getResources().getColor(R.color.color_pink_primary));
        }
    }

    private int GetCurrentDay() {
        int currentDayNumber;
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        Log.e("xcz", dayOfTheWeek);
        if(dayOfTheWeek.equals("Monday")){
            currentDayNumber= 0;
        }else if(dayOfTheWeek.equals("Tuesday")){
            currentDayNumber= 1;
        }else if(dayOfTheWeek.equals("Wednesday")){
            currentDayNumber= 2;
        }else if(dayOfTheWeek.equals("Thursday")){
            currentDayNumber= 3;
        }else if(dayOfTheWeek.equals("Friday")){
            currentDayNumber= 4;
        }else if(dayOfTheWeek.equals("Saturday")){
            currentDayNumber= 5;
        }else {
            currentDayNumber= 6;
        }
        return currentDayNumber;
    }

    private int GetCurrentHours() {
        Calendar calendar= Calendar.getInstance();
        return calendar.getTime().getHours();
    }

    @Override
    public int getItemCount() {
        return homeListModels.size();
    }

    public class imageViewHolder extends RecyclerView.ViewHolder {

        View view;
        HomeListItemLayVerticalBinding binding;

        public imageViewHolder(@NonNull HomeListItemLayVerticalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
