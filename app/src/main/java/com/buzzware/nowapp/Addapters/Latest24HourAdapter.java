package com.buzzware.nowapp.Addapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.nowapp.Models.PostsModel;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.databinding.Latest24HourItemLayBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Latest24HourAdapter extends RecyclerView.Adapter<Latest24HourAdapter.imageViewHolder>{
    public Context context;
    List<PostsModel> homeListModels;
    ItemClickListener itemClickListener;
    View view;

    public Latest24HourAdapter(Context context, List<PostsModel> homeListModels, ItemClickListener itemClickListener) {
        this.context = context;
        this.homeListModels = homeListModels;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public imageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Latest24HourAdapter.imageViewHolder(Latest24HourItemLayBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(final imageViewHolder holder, final int position) {
        final PostsModel postsModel= homeListModels.get(position);

        if(!postsModel.getUserPostThumbnail().isEmpty())
            Picasso.with(context).load(postsModel.getUserPostThumbnail()).placeholder(R.drawable.dummy_post_image).into(holder.binding.imageView);


        holder.binding.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.Listener(postsModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return homeListModels.size();
    }

    public class imageViewHolder extends RecyclerView.ViewHolder {

        Latest24HourItemLayBinding binding;

        public imageViewHolder(@NonNull Latest24HourItemLayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface ItemClickListener{
        void Listener(PostsModel postsModel);
    }
}
