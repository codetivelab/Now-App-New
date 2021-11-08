package com.buzzware.nowapp.Addapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.nowapp.Fragments.BuisnessFragments.Application.FirstTabFragment;
import com.buzzware.nowapp.Models.SelectionModel;
import com.buzzware.nowapp.databinding.BuisnessTypeItemLayBinding;

import java.util.List;

public class BuisnessTypeListAddapters extends RecyclerView.Adapter<BuisnessTypeListAddapters.imageViewHolder>{
    public Context context;
    List<SelectionModel> typeList;
    ClickListener clickListener;
    View view;

    public BuisnessTypeListAddapters(Context context, List<SelectionModel> typeList, ClickListener clickListener) {
        this.context = context;
        this.typeList = typeList;
        this.clickListener= clickListener;
    }

    @Override
    public imageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BuisnessTypeListAddapters.imageViewHolder(BuisnessTypeItemLayBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(final imageViewHolder holder, final int position) {
        final SelectionModel selectionModel= typeList.get(position);
        holder.binding.nameTV.setText(selectionModel.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.OnClick(selectionModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return typeList.size();
    }

    public class imageViewHolder extends RecyclerView.ViewHolder {

        BuisnessTypeItemLayBinding binding;

        public imageViewHolder(@NonNull BuisnessTypeItemLayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface ClickListener{
        void OnClick(SelectionModel selectionModel);
    }
}
