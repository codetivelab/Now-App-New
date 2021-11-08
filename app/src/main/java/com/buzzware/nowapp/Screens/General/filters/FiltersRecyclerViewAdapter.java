package com.buzzware.nowapp.Screens.General.filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Screens.General.filters.holder.FiltersViewHolder;
import com.buzzware.nowapp.databinding.ItemFilterBinding;
import com.squareup.picasso.Picasso;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHazeFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageLuminanceFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSolarizeFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageToneCurveFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageVibranceFilter;

public class FiltersRecyclerViewAdapter extends RecyclerView.Adapter<FiltersViewHolder> {

    Context c;
    FilterSelectedListener listener;

    public FiltersRecyclerViewAdapter(FilterSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public FiltersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        c = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(c);

        return new FiltersViewHolder(ItemFilterBinding.inflate(inflater,
                parent, false));
    }

    @Override
    public void onBindViewHolder(FiltersViewHolder holder, int position) {

        holder.binding.gpuIV.setOnClickListener(v -> listener.onFilterSelected(position));

        switch (position) {

            case 0:
                setSaturationFilter(holder, position);
                break;

            case 1:
                setContrastFilter(holder, position);
                break;
            case 2:
                setBrightnessFilter(holder, position);
                break;
            case 3:
                setSepiaTonesionFilter(holder,position);
                break;
            case 4:
                setSolarizeFilter(holder,position);
                break;

            case 5:
                setHazeFilter(holder, position);
                break;
            case 6:
                setToneCurvedFilter(holder, position);
                break;
            case 7:
                setVibranceFilter(holder,position);
                break;
            case 8:
                setExposureFilter(holder,position);
                break;
            case 9:
                setLuminanceFilter(holder,position);
                break;
        }

    }

    private void setLuminanceFilter(FiltersViewHolder holder, int position) {
        Bitmap icon = BitmapFactory.decodeResource(c.getResources(),
                R.drawable.camera_dummy);

//        Picasso.with(c).load(R.drawable.camera_dummy).centerCrop().into(holder.binding.gpuIV);
        holder.binding.gpuIV.setImage(icon); // this loads image on the current thread, should be run in a thread
        holder.binding.gpuIV.setFilter(new GPUImageLuminanceFilter());

        holder.binding.filterNameTV.setText("Luminance");


    }

    private void setExposureFilter(FiltersViewHolder holder, int position) {

        Bitmap icon = BitmapFactory.decodeResource(c.getResources(),
                R.drawable.camera_dummy);

        holder.binding.gpuIV.setImage(icon); // this loads image on the current thread, should be run in a thread
        holder.binding.gpuIV.setFilter(new GPUImageVibranceFilter());

        holder.binding.filterNameTV.setText("Exposure");
    }

    private void setVibranceFilter(FiltersViewHolder holder, int position) {

        Bitmap icon = BitmapFactory.decodeResource(c.getResources(),
                R.drawable.camera_dummy);

        holder.binding.gpuIV.setImage(icon); // this loads image on the current thread, should be run in a thread
        holder.binding.gpuIV.setFilter(new GPUImageVibranceFilter());

        holder.binding.filterNameTV.setText("Vibrance");
    }

    private void setToneCurvedFilter(FiltersViewHolder holder, int position) {

        Bitmap icon = BitmapFactory.decodeResource(c.getResources(),
                R.drawable.camera_dummy);

        holder.binding.gpuIV.setImage(icon); // this loads image on the current thread, should be run in a thread
        holder.binding.gpuIV.setFilter(new GPUImageToneCurveFilter());

        holder.binding.filterNameTV.setText("Tone Curve");
    }

    private void setHazeFilter(FiltersViewHolder holder, int position) {

        Bitmap icon = BitmapFactory.decodeResource(c.getResources(),
                R.drawable.camera_dummy);

        holder.binding.gpuIV.setImage(icon); // this loads image on the current thread, should be run in a thread
        holder.binding.gpuIV.setFilter(new GPUImageHazeFilter());

        holder.binding.filterNameTV.setText("Haze");
    }

    private void setSolarizeFilter(FiltersViewHolder holder, int position) {

        Bitmap icon = BitmapFactory.decodeResource(c.getResources(),
                R.drawable.camera_dummy);

        holder.binding.gpuIV.setImage(icon); // this loads image on the current thread, should be run in a thread
        holder.binding.gpuIV.setFilter(new GPUImageSolarizeFilter());

        holder.binding.filterNameTV.setText("Solarize");
    }

    private void setSepiaTonesionFilter(FiltersViewHolder holder, int position) {

        Bitmap icon = BitmapFactory.decodeResource(c.getResources(),
                R.drawable.camera_dummy);

        holder.binding.gpuIV.setImage(icon); // this loads image on the current thread, should be run in a thread
        holder.binding.gpuIV.setFilter(new GPUImageSepiaToneFilter());

        holder.binding.filterNameTV.setText("Sepia");

    }

    private void setSaturationFilter(FiltersViewHolder holder, int position) {

        Bitmap icon = BitmapFactory.decodeResource(c.getResources(),
                R.drawable.camera_dummy);

        holder.binding.gpuIV.setImage(icon); // this loads image on the current thread, should be run in a thread
        holder.binding.gpuIV.setFilter(new GPUImageSaturationFilter());

        holder.binding.filterNameTV.setText("Saturation");

    }

    private void setContrastFilter(FiltersViewHolder holder, int position) {

        Bitmap icon = BitmapFactory.decodeResource(c.getResources(),
                R.drawable.camera_dummy);

        holder.binding.gpuIV.setImage(icon); // this loads image on the current thread, should be run in a thread
        holder.binding.gpuIV.setFilter(new GPUImageContrastFilter());

        holder.binding.filterNameTV.setText("Contrast");

    }

    private void setBrightnessFilter(FiltersViewHolder holder, int position) {

        Bitmap icon = BitmapFactory.decodeResource(c.getResources(),
                R.drawable.camera_dummy);

        holder.binding.gpuIV.setImage(icon); // this loads image on the current thread, should be run in a thread
        holder.binding.gpuIV.setFilter(new GPUImageBrightnessFilter());

        holder.binding.filterNameTV.setText("Brightness");

    }

    @Override
    public int getItemCount() {
        return 10;
    }

}

