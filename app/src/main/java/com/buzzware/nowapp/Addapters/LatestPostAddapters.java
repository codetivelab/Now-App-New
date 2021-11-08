package com.buzzware.nowapp.Addapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.nowapp.Models.PostsModel;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Sessions.UserSessions;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.LatestPostsItemLayBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LatestPostAddapters extends RecyclerView.Adapter<LatestPostAddapters.imageViewHolder>{
    public Context context;
    List<PostsModel> homeListModels;
    ItemClickListener itemClickListener;
    View view;

    public LatestPostAddapters(Context context, List<PostsModel> homeListModels, ItemClickListener itemClickListener) {
        this.context = context;
        this.homeListModels = homeListModels;
        this.itemClickListener= itemClickListener;
    }

    @Override
    public imageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LatestPostAddapters.imageViewHolder(LatestPostsItemLayBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(final imageViewHolder holder, final int position) {
        final PostsModel homeListModel= homeListModels.get(position);

        Picasso.with(context).load(homeListModel.getUserPostThumbnail()).fit().into(holder.binding.imageV, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                holder.binding.imageV.setImageResource(R.drawable.dummy_post_image);
            }
        });

        holder.binding.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.Listener(homeListModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return homeListModels.size();
    }

    public class imageViewHolder extends RecyclerView.ViewHolder {

        LatestPostsItemLayBinding binding;

        public imageViewHolder(@NonNull LatestPostsItemLayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface ItemClickListener{
        void Listener(PostsModel postsModel);
    }
}
