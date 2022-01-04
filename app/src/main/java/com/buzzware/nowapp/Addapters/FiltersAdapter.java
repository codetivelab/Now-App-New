package com.buzzware.nowapp.Addapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.nowapp.Libraries.model.FilterModel;
import com.buzzware.nowapp.R;

import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHazeFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageVibranceFilter;

public class FiltersAdapter extends RecyclerView.Adapter<FiltersAdapter.imageViewHolder> {

    public Context context;

    View view;

    OnItemSelectedListener listener;

    public FiltersAdapter(Context context, OnItemSelectedListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public imageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter, parent, false);
        return new imageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final imageViewHolder holder, final int position) {

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.camera_dummy);
        Glide.with(context).load(R.drawable.camera_dummy).apply(new RequestOptions().centerCrop()).into(holder.mGpuIV);

        holder.itemView.setOnClickListener(view1 -> listener.onItemSelected(position));
        if (position == 0) {

            holder.mFilterNameTV.setText("Normal");

        } else if (position == 1) {

            holder.mFilterNameTV.setText("TopCrave");
//            holder.mGpuIV.setFilter(new GPUImageSaturationFilter());


        } else if (position == 2) {

            holder.mFilterNameTV.setText("Contrast");

//            holder.mGpuIV.setFilter(new GPUImageContrastFilter());

        } else if (position == 3) {
            holder.mFilterNameTV.setText("Exposure");
//            holder.mGpuIV.setFilter(new GPUImageContrastFilter());


        } else if (position == 4) {
            holder.mFilterNameTV.setText("Vibrance");
//            holder.mGpuIV.setFilter(new GPUImageVibranceFilter());


        } else if (position == 5) {
            holder.mFilterNameTV.setText("Haze");
//            holder.mGpuIV.setFilter(new GPUImageHazeFilter());

        }
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public void initView() {
    }

    public class imageViewHolder extends RecyclerView.ViewHolder {


        public ImageView mGpuIV;
        public TextView mFilterNameTV;

        public imageViewHolder(View itemView) {
            super(itemView);
            mGpuIV = itemView.findViewById(R.id.gpuIV);
            mFilterNameTV = itemView.findViewById(R.id.filterNameTV);
            view = itemView;
        }
    }

    public interface OnItemSelectedListener {

        void onItemSelected(int position);
    }
}
