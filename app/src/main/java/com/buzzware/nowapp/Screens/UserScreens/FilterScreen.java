package com.buzzware.nowapp.Screens.UserScreens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.buzzware.nowapp.Addapters.FilterListAddapters;
import com.buzzware.nowapp.Models.FilterModel;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.databinding.ActivityFilterScreenBinding;

import java.util.ArrayList;
import java.util.List;

public class FilterScreen extends AppCompatActivity {

    ActivityFilterScreenBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_filter_screen);
        mBinding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FilterScreen.this, UploadPostScreen.class));
            }
        });
        ShowDummyFilters();
    }

    private void ShowDummyFilters() {
        List<FilterModel> filterModels= new ArrayList<>();
        filterModels.add(new FilterModel());
        filterModels.add(new FilterModel());
        filterModels.add(new FilterModel());
        filterModels.add(new FilterModel());
        filterModels.add(new FilterModel());

        mBinding.rvFilters.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        FilterListAddapters filterListAddapters= new FilterListAddapters(this, filterModels);
        mBinding.rvFilters.setAdapter(filterListAddapters);
        filterListAddapters.notifyDataSetChanged();
    }
}