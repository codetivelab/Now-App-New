package com.buzzware.nowapp.Screens.General.filters.holder;

import androidx.recyclerview.widget.RecyclerView;
import com.buzzware.nowapp.databinding.ItemFilterBinding;

public class FiltersViewHolder extends RecyclerView.ViewHolder {

    public ItemFilterBinding binding;

    public FiltersViewHolder(ItemFilterBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
