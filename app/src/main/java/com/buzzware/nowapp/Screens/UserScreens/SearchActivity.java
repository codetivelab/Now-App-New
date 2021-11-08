package com.buzzware.nowapp.Screens.UserScreens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.buzzware.nowapp.Addapters.HomeListAddapters;
import com.buzzware.nowapp.Fragments.UserFragments.HomeFragment;
import com.buzzware.nowapp.Models.RestaurantDataModel;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.ActivitySearchBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    ActivitySearchBinding binding;
    String isFromFilter="";
    List<RestaurantDataModel> filterList;
    Context context;
    HomeListAddapters homeListAddapters;
    String selectedFilterStar="", selectedFilterOccupation="", selectedFilterPlaces="";
    boolean isFilterClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_search);

    }

    private void Init() {
        isFromFilter= getIntent().getStringExtra("isFromFilter");
        context= this;
        filterList= new ArrayList<>();
        binding.rvSearch.setLayoutManager(new LinearLayoutManager(this));

        //setData
        if(isFromFilter.equals("false")) {
            if(HomeFragment.nearestRestaurantData.size() > 0){
                SetData(HomeFragment.nearestRestaurantData);
            }else{
                binding.info.setVisibility(View.VISIBLE);
            }

        }else {
            isFilterClicked= true;
            binding.btnFilter.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));
            binding.filterIV.setImageDrawable(getResources().getDrawable(R.drawable.filter_white));
            TypeToken<List<RestaurantDataModel>> token = new TypeToken<List<RestaurantDataModel>>() {};
            filterList= new Gson().fromJson(getIntent().getStringExtra("filterList"), token.getType());
            SetData(filterList);
        }

        //clicks
        binding.btnBack.setOnClickListener(this);
        binding.btnFilter.setOnClickListener(this);

        //search listener
        ApplySearchListener();
    }

    private void ApplySearchListener() {
        binding.searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text= s.toString().toLowerCase();
                if(text.length() >= 2) {
                    if (HomeFragment.nearestRestaurantData.size() > 0) {
                        filterList.clear();
                        for (int i = 0; i < HomeFragment.nearestRestaurantData.size(); i++) {
                            RestaurantDataModel restaurantDataModel = HomeFragment.nearestRestaurantData.get(i);
                            if (restaurantDataModel.getBusinessName().toLowerCase().contains(text)) {
                                filterList.add(restaurantDataModel);
                            }
                        }

                        ///check
                        if (filterList.size() > 0) {
                            Log.e("zxczx", filterList.size()+" Size");
                            SetData(filterList);
                        }else {
                            Log.e("zxczx", "0 Filter");
                        }
                    }else {
                        Log.e("zxczx", "0 Size");
                    }
                }else{
                    Log.e("zxczx", "Reset Size");
                    filterList.clear();
                    SetData(HomeFragment.nearestRestaurantData);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void SetData(List<RestaurantDataModel> list) {
        homeListAddapters = new HomeListAddapters(context, list);
        binding.rvSearch.setAdapter(homeListAddapters);
        homeListAddapters.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if(v == binding.btnBack){
            finish();
        }else if(v == binding.btnFilter){
            if(isFilterClicked){
                isFilterClicked= false;
                binding.btnFilter.setBackground(getResources().getDrawable(R.drawable.rounder_circle_light_blue));
                binding.filterIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_filter_));
                binding.info.setVisibility(View.GONE);
                SetData(HomeFragment.nearestRestaurantData);
            }else{
                filterList.clear();
                isFilterClicked= true;
                binding.btnFilter.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));
                binding.filterIV.setImageDrawable(getResources().getDrawable(R.drawable.filter_white));
                ShowFilterDialog();
            }
        }
    }

    private void ShowFilterDialog() {
        RelativeLayout btnOneStar, btnTwoStar, btnThreeStar, btnFourStar, btnFiveStar;
        RelativeLayout btnCrowdy, btnRoomy, btnEmpty, btnApply;
        RelativeLayout btnGlobeype, btnBarType, btnCoffeeType, btnGuestType, btnRestType;
        BottomSheetDialog bottomSheetDialog= new BottomSheetDialog(context, R.style.SheetDialog);
        View view = getLayoutInflater().inflate(R.layout.filter_bottomsheet_dialog, null);
        bottomSheetDialog.setContentView(view);
        btnOneStar= view.findViewById(R.id.btnOneStar);
        btnTwoStar= view.findViewById(R.id.btnTwoStar);
        btnThreeStar= view.findViewById(R.id.btnThreeStar);
        btnFourStar= view.findViewById(R.id.btnFourStar);
        btnFiveStar= view.findViewById(R.id.btnFiveStar);
        btnCrowdy= view.findViewById(R.id.btnCrowdy);
        btnRoomy= view.findViewById(R.id.btnRoomy);
        btnEmpty= view.findViewById(R.id.btnEmpty);

        btnGlobeype= view.findViewById(R.id.btnGlobeType);
        btnBarType= view.findViewById(R.id.btnBarType);
        btnCoffeeType= view.findViewById(R.id.btnCoffeeType);
        btnGuestType= view.findViewById(R.id.btnGuestType);
        btnRestType= view.findViewById(R.id.btnRestType);
        btnApply= view.findViewById(R.id.btnApply);

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validation(bottomSheetDialog);
            }
        });

        //clicks
        btnOneStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFilterStar= "1";
                btnOneStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));
                btnTwoStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnThreeStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnFourStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnFiveStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
            }
        });

        btnTwoStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFilterStar= "2";
                btnOneStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnTwoStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));
                btnThreeStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnFourStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnFiveStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
            }
        });

        btnThreeStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFilterStar= "3";
                btnOneStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnTwoStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnThreeStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));
                btnFourStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnFiveStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
            }
        });

        btnFourStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFilterStar= "4";
                btnOneStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnTwoStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnThreeStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnFourStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));
                btnFiveStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
            }
        });

        btnFiveStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFilterStar= "5";
                btnOneStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnTwoStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnThreeStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnFourStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnFiveStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));
            }
        });

        btnCrowdy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFilterOccupation= "2";
                btnCrowdy.setBackground(getResources().getDrawable(R.drawable.rounder_circle_golden_border));
                btnRoomy.setBackground(null);
                btnEmpty.setBackground(null);
            }
        });

        btnRoomy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFilterOccupation= "0";
                btnCrowdy.setBackground(null);
                btnRoomy.setBackground(getResources().getDrawable(R.drawable.rounder_circle_golden_border));
                btnEmpty.setBackground(null);
            }
        });

        btnEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFilterOccupation= "-2";
                btnCrowdy.setBackground(null);
                btnRoomy.setBackground(null);
                btnEmpty.setBackground(getResources().getDrawable(R.drawable.rounder_circle_golden_border));
            }
        });

        btnGlobeype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFilterPlaces= getString(R.string.nightClub);
                btnGlobeype.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));
                btnBarType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnCoffeeType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnGuestType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnRestType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
            }
        });

        btnBarType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFilterPlaces= getString(R.string.club);
                btnGlobeype.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnBarType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));
                btnCoffeeType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnGuestType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnRestType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
            }
        });

        btnCoffeeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFilterPlaces= getString(R.string.coffeeShop);
                btnGlobeype.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnBarType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnCoffeeType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));
                btnGuestType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnRestType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
            }
        });

        btnGuestType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFilterPlaces= context.getString(R.string.bar);
                btnGlobeype.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnBarType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnCoffeeType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnGuestType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));
                btnRestType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
            }
        });

        btnRestType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFilterPlaces= getString(R.string.restaurant);
                btnGlobeype.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnBarType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnCoffeeType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnGuestType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnRestType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));
            }
        });
        bottomSheetDialog.show();
    }

    private void Validation(BottomSheetDialog bottomSheetDialog) {
        if(!selectedFilterStar.equals("")){
            if(!selectedFilterOccupation.equals("")){
                if(!selectedFilterPlaces.equals("")){
                    bottomSheetDialog.dismiss();
                    FilterRestaurants();
                }else{
                    UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.select_type));
                }
            }else{
                UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.select_occupation));
            }
        }else{
            UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.select_star));
        }
    }

    private void FilterRestaurants() {
        if(HomeFragment.nearestRestaurantData.size() > 0){
            filterList.clear();
            for(int i=0; i<HomeFragment.nearestRestaurantData.size(); i++){
                RestaurantDataModel restaurantDataModels= HomeFragment.nearestRestaurantData.get(i);
                String intensity= restaurantDataModels.GetIntensity(GetCurrentDay(), GetCurrentHours());
                String finalIntensity= GetFinalIntensity(intensity);
                if (restaurantDataModels.getBusinessTotalRating().equals(selectedFilterStar) &&
                        finalIntensity.equals(selectedFilterOccupation) && restaurantDataModels.getBusinessType().equals(selectedFilterPlaces)){
                    filterList.add(restaurantDataModels);
                }
            }

            if(filterList.size() > 0) {
                binding.info.setVisibility(View.GONE);
                SetData(filterList);
            }else {
                SetData(filterList);
                binding.info.setVisibility(View.VISIBLE);
            }
        }
    }

    public String GetFinalIntensity(String intensity){
        if(intensity.equals("-2") || intensity.equals("-1")){
            return "-2";
        }else if(intensity.equals("0")){
            return "0";
        }else if(intensity.equals("1") || intensity.equals("2")){
            return "2";
        }else {
            return "-2";
        }
    }

    private int GetCurrentDay() {
        int currentDayNumber;
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        Log.e("xcz", dayOfTheWeek);
        if(dayOfTheWeek.equals("Monday")){
            currentDayNumber= 0;
        }else if(dayOfTheWeek.equals("Tuesday")){
            currentDayNumber= 1;
        }else if(dayOfTheWeek.equals("Wednesday")){
            currentDayNumber= 2;
        }else if(dayOfTheWeek.equals("Thursday")){
            currentDayNumber= 3;
        }else if(dayOfTheWeek.equals("Friday")){
            currentDayNumber= 4;
        }else if(dayOfTheWeek.equals("Saturday")){
            currentDayNumber= 5;
        }else {
            currentDayNumber= 6;
        }
        return currentDayNumber;
    }

    private int GetCurrentHours() {
        Calendar calendar= Calendar.getInstance();
        return calendar.getTime().getHours();
    }
}