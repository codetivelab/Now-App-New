package com.buzzware.nowapp.Addapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.nowapp.Models.HomeListModel;
import com.buzzware.nowapp.Models.RestaurantDataModel;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.databinding.HomeListItemLayBinding;
import com.buzzware.nowapp.databinding.SearchHistoryItemBinding;

import java.util.List;

public class HomeSearchListAddapters extends RecyclerView.Adapter<HomeSearchListAddapters.imageViewHolder>{
    public Context context;
    List<RestaurantDataModel> homeListModels;
    View view;

    public HomeSearchListAddapters(Context context, List<RestaurantDataModel> homeListModels) {
        this.context = context;
        this.homeListModels = homeListModels;
    }

    @Override
    public imageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HomeSearchListAddapters.imageViewHolder(SearchHistoryItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(final imageViewHolder holder, final int position) {
        final RestaurantDataModel homeListModel= homeListModels.get(position);
        holder.binding.nameTV.setText(homeListModel.getBusinessName());
    }

    @Override
    public int getItemCount() {
        return homeListModels.size();
    }

    public class imageViewHolder extends RecyclerView.ViewHolder {

        View view;
        SearchHistoryItemBinding binding;

        public imageViewHolder(@NonNull SearchHistoryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
