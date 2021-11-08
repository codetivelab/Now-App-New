package com.buzzware.nowapp.Addapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.nowapp.Models.FilterModel;
import com.buzzware.nowapp.R;

import java.util.List;

public class FilterListAddapters extends RecyclerView.Adapter<FilterListAddapters.imageViewHolder>{
    public Context context;
    List<FilterModel> homeListModels;
    View view;

    public FilterListAddapters(Context context, List<FilterModel> homeListModels) {
        this.context = context;
        this.homeListModels = homeListModels;
    }

    @Override
    public imageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_item_lay, parent, false);
        return new imageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final imageViewHolder holder, final int position) {
        final FilterModel homeListModel= homeListModels.get(position);

    }

    @Override
    public int getItemCount() {
        return homeListModels.size();
    }

    public class imageViewHolder extends RecyclerView.ViewHolder {

        View view;

        public imageViewHolder(View itemView) {
            super(itemView);
            view= itemView;
        }
    }
}
