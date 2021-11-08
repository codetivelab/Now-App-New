package com.buzzware.nowapp.Addapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.nowapp.Models.PostsModel;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.databinding.ItemTaggedBusinessUserBinding;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TagUserAdapters extends RecyclerView.Adapter<TagUserAdapters.imageViewHolder> {

    public Context context;

    List<PostsModel> list;

    OnPostTappedCallback callback;

    public TagUserAdapters(Context context, List<PostsModel> list, OnPostTappedCallback callback) {
        this.context = context;
        this.list = list;
        this.callback = callback;
    }

    @Override
    public imageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new imageViewHolder(ItemTaggedBusinessUserBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(final imageViewHolder holder, final int position) {

        final PostsModel model = list.get(position);

        holder.binding.userNameTV.setText(model.getUserName());
        holder.binding.one.setText(model.getUserPostComment());
        holder.binding.timeTV.setText(getDate(Long.parseLong(model.getCreatedAt())));

        holder.binding.layout.setOnClickListener(v -> callback.onPostTapped(model));
        if (model.hasImage())

            Picasso.with(context).load(model.getUserImage()).placeholder(R.drawable.dummy_post_image).fit().into(holder.binding.userIV);

    }

    private String getDate(long time) {

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);

        String date = DateFormat.format("MM,dd yyyy HH:mm ", cal).toString();

        return date;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class imageViewHolder extends RecyclerView.ViewHolder {

        ItemTaggedBusinessUserBinding binding;

        public imageViewHolder(@NonNull ItemTaggedBusinessUserBinding binding) {

            super(binding.getRoot());

            this.binding = binding;
        }
    }

    public interface OnPostTappedCallback {

        void onPostTapped(PostsModel postsModel);
    }
}
