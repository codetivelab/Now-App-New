package com.buzzware.nowapp.Fragments.BuisnessFragments.Application.BuisnessDashBoard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.buzzware.nowapp.Addapters.HomeListAddapters;
import com.buzzware.nowapp.Constants.Constant;
import com.buzzware.nowapp.FirebaseRequests.FirebaseRequests;
import com.buzzware.nowapp.FirebaseRequests.Interfaces.RestaurantResponseCallback;
import com.buzzware.nowapp.Fragments.UserFragments.BaseFragment;
import com.buzzware.nowapp.Fragments.UserFragments.HomeFragment;
import com.buzzware.nowapp.Models.NormalUserModel;
import com.buzzware.nowapp.Models.RestaurantDataModel;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.ActivitySearchBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import im.delight.android.location.SimpleLocation;

import static android.content.Context.LOCATION_SERVICE;
//import static com.buzzware.nowapp.Fragments.UserFragments.HomeFragment.nearestRestaurantData;

public class SearchFragment extends BaseFragment implements View.OnClickListener {

    ActivitySearchBinding mBinding;
    String isFromFilter = "";
    List<RestaurantDataModel> filterList;

    double latitude = 0, longitude = 0;
    HomeListAddapters homeListAddapters;
    String selectedFilterStar = "", selectedFilterOccupation = "", selectedFilterPlaces = "";
    boolean isFilterClicked = false;

    private SimpleLocation location;

    String filtersListJson;
    List<RestaurantDataModel> restaurantDataModelList, nearestRestaurantData;

    public SearchFragment(String isFromFilter, String filtersList) {

        this.isFromFilter = isFromFilter;
        this.filtersListJson = filtersList;

        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.activity_search, container, false);

        Dexter.withActivity(getActivity()).withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {

                    location = new SimpleLocation(getActivity());

                    if (!location.hasLocationEnabled()) {
                        // ask the user to enable location access
                        SimpleLocation.openSettings(getActivity());
                    }

                    getLocation();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();


        mBinding.btnBack.setOnClickListener(view -> removeCurrentFragment());

        return mBinding.getRoot();
    }

    private void removeCurrentFragment() {

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).addToBackStack("home").commit();

    }

    private void ApplySearchListener() {
        mBinding.searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();

                if (text.length() == 0) {

                    SetData(restaurantDataModelList);

                    return;
                }

                filterList.clear();

                for (int i = 0; i < nearestRestaurantData.size(); i++) {

                    RestaurantDataModel restaurantDataModel = nearestRestaurantData.get(i);

                    if (restaurantDataModel.getBusinessName().toLowerCase().contains(text.toLowerCase())) {

                        filterList.add(restaurantDataModel);
                    }

                }

                SetData(filterList);

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();

        // make the device update its location
        location.beginUpdates();

        // ...
    }

    @Override
    public void onPause() {
        // stop location updates (saves battery)
        location.endUpdates();

        super.onPause();
    }

    public void getLocation() {

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        FirebaseRequests.GetFirebaseRequests(getContext()).GetRestaurant(restaurantResponseCallback, getContext());

    }


    RestaurantResponseCallback restaurantResponseCallback = new RestaurantResponseCallback() {
        @Override
        public void onResponse(List<RestaurantDataModel> list, boolean isError, String message) {
            if (!isError) {

                restaurantDataModelList = list;
                nearestRestaurantData = new ArrayList<>();

                UIUpdate.GetUIUpdate(getActivity()).DismissProgressDialog();
                GetNearestRestaurant();

            } else {
                UIUpdate.GetUIUpdate(getActivity()).DismissProgressDialog();
                UIUpdate.GetUIUpdate(getActivity()).ShowToastMessage(message);
            }
        }
    };

//    private double distance(double lat1, double lon1, double lat2, double lon2) { ///distance in miles
//        double theta = lon1 - lon2;
//        double dist = Math.sin(deg2rad(lat1))
//                * Math.sin(deg2rad(lat2))
//                + Math.cos(deg2rad(lat1))
//                * Math.cos(deg2rad(lat2))
//                * Math.cos(deg2rad(theta));
//        dist = Math.acos(dist);
//        dist = rad2deg(dist);
//        dist = dist * 60 * 1.1515;
//        return (dist);
//    }


    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private void GetNearestRestaurant() {

        nearestRestaurantData = restaurantDataModelList;
//        for (int i = 0; i < HomeFragment.restaurantDataModelList.size(); i++) {
//            RestaurantDataModel restaurantDataModel = HomeFragment.restaurantDataModelList.get(i);
//            double diatanceBetween = distance(latitude, longitude, Double.parseDouble(restaurantDataModel.getBusinessLatitude()), Double.parseDouble(restaurantDataModel.getBusinessLongitude()));
//            if (diatanceBetween <= 7) { /// 7 miles = 1.6 * 7= 11.2 KM
//                nearestRestaurantData.add(restaurantDataModel);
//            }
//        }
        Init();
    }


    private void Init() {

        filterList = new ArrayList<>();
        mBinding.rvSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        ApplySearchListener();

        //setData
        if (filtersListJson == null) {
            if (nearestRestaurantData.size() > 0) {
                SetData(nearestRestaurantData);
            } else {
                mBinding.info.setVisibility(View.VISIBLE);
            }

        } else {
            isFilterClicked = true;
            mBinding.btnFilter.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));
            mBinding.filterIV.setImageDrawable(getResources().getDrawable(R.drawable.filter_white));
            TypeToken<List<RestaurantDataModel>> token = new TypeToken<List<RestaurantDataModel>>() {
            };
            filterList = new Gson().fromJson(filtersListJson, token.getType());
            SetData(filterList);
        }

        //clicks
//        mBinding.btnBack.setOnClickListener(this);
        mBinding.btnFilter.setOnClickListener(this);

        //search listener
    }


    private void getUsersList(List<RestaurantDataModel> list) {

        FirebaseFirestore.getInstance().collection(Constant.GetConstant().getUsersCollection())
                .addSnapshotListener(getActivity(), (value, error) -> {

                    for (DocumentSnapshot document : value.getDocuments()) {

                        NormalUserModel user = document.toObject(NormalUserModel.class);

                        if (user != null) {

                            user.id = document.getId();

                            for (int i = 0; i < list.size(); i++) {

                                if (list.get(i).getId().equalsIgnoreCase(user.id)) {

                                    list.get(i).userDeleted = false;
                                }

                            }

                        }

                    }

                    for (int i = 0; i < list.size(); i++) {

                        if (list.get(i).userDeleted) {

                            list.remove(i);

                        }

                    }

                    homeListAddapters = new HomeListAddapters(getActivity(), list);

                    mBinding.rvSearch.setAdapter(homeListAddapters);


                });

    }


    private void SetData(List<RestaurantDataModel> list) {

        getUsersList(list);

//        homeListAddapters.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v == mBinding.btnBack) {
//            finish();
        } else if (v == mBinding.btnFilter) {
            if (isFilterClicked) {
                isFilterClicked = false;
                mBinding.btnFilter.setBackground(getResources().getDrawable(R.drawable.rounder_circle_light_blue));
                mBinding.filterIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_filter_));
                mBinding.info.setVisibility(View.GONE);
                SetData(nearestRestaurantData);
            } else {
                filterList.clear();
                isFilterClicked = true;
                mBinding.btnFilter.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));
                mBinding.filterIV.setImageDrawable(getResources().getDrawable(R.drawable.filter_white));
                ShowFilterDialog();
            }
        }
    }

    private void ShowFilterDialog() {
        RelativeLayout btnOneStar, btnTwoStar, btnThreeStar, btnFourStar, btnFiveStar;
        RelativeLayout btnCrowdy, btnRoomy, btnEmpty, btnApply;
        RelativeLayout btnGlobeype, btnBarType, btnCoffeeType, btnGuestType, btnRestType;
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.SheetDialog);
        View view = getLayoutInflater().inflate(R.layout.filter_bottomsheet_dialog, null);
        bottomSheetDialog.setContentView(view);
        btnOneStar = view.findViewById(R.id.btnOneStar);
        btnTwoStar = view.findViewById(R.id.btnTwoStar);
        btnThreeStar = view.findViewById(R.id.btnThreeStar);
        btnFourStar = view.findViewById(R.id.btnFourStar);
        btnFiveStar = view.findViewById(R.id.btnFiveStar);
        btnCrowdy = view.findViewById(R.id.btnCrowdy);
        btnRoomy = view.findViewById(R.id.btnRoomy);
        btnEmpty = view.findViewById(R.id.btnEmpty);

        btnGlobeype = view.findViewById(R.id.btnGlobeType);
        btnBarType = view.findViewById(R.id.btnBarType);
        btnCoffeeType = view.findViewById(R.id.btnCoffeeType);
        btnGuestType = view.findViewById(R.id.btnGuestType);
        btnRestType = view.findViewById(R.id.btnRestType);
        btnApply = view.findViewById(R.id.btnApply);

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validation(bottomSheetDialog);
            }
        });

        //clicks
        //clicks
        btnOneStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedFilterStar.equalsIgnoreCase("1")) {

                    btnOneStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                    selectedFilterStar = "";

                } else {

                    btnOneStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));
                    selectedFilterStar = "1";

                }


                btnTwoStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnThreeStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnFourStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnFiveStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
            }
        });

        btnTwoStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedFilterStar.equalsIgnoreCase("2")) {

                    btnTwoStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                    selectedFilterStar = "";

                } else {

                    btnTwoStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));
                    selectedFilterStar = "2";

                }


                btnOneStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnThreeStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnFourStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnFiveStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
            }
        });

        btnThreeStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedFilterStar.equalsIgnoreCase("3")) {

                    btnThreeStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                    selectedFilterStar = "";

                } else {

                    btnThreeStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));
                    selectedFilterStar = "3";

                }


                btnOneStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnTwoStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnFourStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnFiveStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
            }
        });

        btnFourStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnOneStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnTwoStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnThreeStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));

                if (selectedFilterStar.equalsIgnoreCase("4")) {
                    selectedFilterStar = "";

                    btnFourStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));

                } else {

                    btnFourStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));
                    selectedFilterStar = "4";

                }


                btnFiveStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
            }
        });

        btnFiveStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedFilterStar.equalsIgnoreCase("5")) {

                    btnFiveStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                    selectedFilterStar = "";

                } else {

                    btnFiveStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));
                    selectedFilterStar = "5";

                }


                btnOneStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnTwoStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnThreeStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnFourStar.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
            }
        });

        btnCrowdy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedFilterOccupation.equalsIgnoreCase("2")) {

                    btnCrowdy.setBackground(null);
                    selectedFilterOccupation = "";

                } else {

                    btnCrowdy.setBackground(getResources().getDrawable(R.drawable.rounder_circle_golden_border));
                    selectedFilterOccupation = "2";

                }


                btnRoomy.setBackground(null);
                btnEmpty.setBackground(null);
            }
        });

        btnRoomy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (selectedFilterOccupation.equalsIgnoreCase("0")) {

                    btnRoomy.setBackground(null);
                    selectedFilterOccupation = "";

                } else {

                    btnRoomy.setBackground(getResources().getDrawable(R.drawable.rounder_circle_golden_border));
                    selectedFilterOccupation = "0";

                }
                btnCrowdy.setBackground(null);
                btnEmpty.setBackground(null);
            }
        });

        btnEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedFilterOccupation.equalsIgnoreCase("-2")) {

                    selectedFilterOccupation = "";

                    btnEmpty.setBackground(null);

                } else {

                    selectedFilterOccupation = "-2";

                    btnEmpty.setBackground(getResources().getDrawable(R.drawable.rounder_circle_golden_border));

                }


                btnCrowdy.setBackground(null);
                btnRoomy.setBackground(null);
            }
        });

        btnGlobeype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedFilterPlaces.equalsIgnoreCase(getString(R.string.nightClub))) {

                    selectedFilterPlaces = "";

                    btnGlobeype.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));

                } else {

                    btnGlobeype.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));


                    selectedFilterPlaces = getString(R.string.nightClub);

                }

                btnBarType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnCoffeeType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnGuestType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnRestType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
            }
        });

        btnBarType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedFilterPlaces.equalsIgnoreCase(getString(R.string.bar))) {

                    selectedFilterPlaces = "";

                    btnBarType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));

                } else {

                    btnBarType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));


                    selectedFilterPlaces = getString(R.string.bar);

                }


                btnGlobeype.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnCoffeeType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnGuestType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnRestType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
            }
        });

        btnCoffeeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (selectedFilterPlaces.equalsIgnoreCase(getString(R.string.coffeeShop))) {

                    selectedFilterPlaces = "";

                    btnCoffeeType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));

                } else {

                    btnCoffeeType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));


                    selectedFilterPlaces = getString(R.string.coffeeShop);

                }

                btnGlobeype.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnBarType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnGuestType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnRestType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
            }
        });

        btnGuestType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (selectedFilterPlaces.equalsIgnoreCase(getActivity().getString(R.string.club))) {

                    selectedFilterPlaces = "";

                    btnGuestType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));

                } else {

                    btnGuestType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));


                    selectedFilterPlaces = getActivity().getString(R.string.club);
                }
                btnGlobeype.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnBarType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnCoffeeType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnRestType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
            }
        });

        btnRestType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedFilterPlaces.equalsIgnoreCase(getActivity().getString(R.string.restaurant))) {

                    selectedFilterPlaces = "";

                    btnRestType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));

                } else {

                    btnRestType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));


                    selectedFilterPlaces = getActivity().getString(R.string.restaurant);
                }
                btnGlobeype.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnBarType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnCoffeeType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));
                btnGuestType.setBackground(getResources().getDrawable(R.drawable.rounder_circle_dark_blue));

            }
        });
        bottomSheetDialog.show();
    }

    private void Validation(BottomSheetDialog bottomSheetDialog) {
        if (!selectedFilterStar.equals("")) {
            if (!selectedFilterOccupation.equals("")) {
                if (!selectedFilterPlaces.equals("")) {
                    bottomSheetDialog.dismiss();
                    FilterRestaurants();
                } else {
                    UIUpdate.GetUIUpdate(getActivity()).ShowToastMessage(getString(R.string.select_type));
                }
            } else {
                UIUpdate.GetUIUpdate(getActivity()).ShowToastMessage(getString(R.string.select_occupation));
            }
        } else {
            UIUpdate.GetUIUpdate(getActivity()).ShowToastMessage(getString(R.string.select_star));
        }
    }

    private void FilterRestaurants() {
        if (nearestRestaurantData.size() > 0) {
            filterList.clear();
            for (int i = 0; i < nearestRestaurantData.size(); i++) {
                RestaurantDataModel restaurantDataModels = nearestRestaurantData.get(i);
                String intensity = restaurantDataModels.GetIntensity(GetCurrentDay(), GetCurrentHours());
                String finalIntensity = GetFinalIntensity(intensity);
                if (restaurantDataModels.getBusinessTotalRating().equals(selectedFilterStar) &&
                        finalIntensity.equals(selectedFilterOccupation) && restaurantDataModels.getBusinessType().equals(selectedFilterPlaces)) {
                    filterList.add(restaurantDataModels);
                }
            }

            if (filterList.size() > 0) {
                mBinding.info.setVisibility(View.GONE);
                SetData(filterList);
            } else {
                SetData(filterList);
                mBinding.info.setVisibility(View.VISIBLE);
            }
        }
    }

    public String GetFinalIntensity(String intensity) {
        if (intensity.equals("-2") || intensity.equals("-1")) {
            return "-2";
        } else if (intensity.equals("0")) {
            return "0";
        } else if (intensity.equals("1") || intensity.equals("2")) {
            return "2";
        } else {
            return "-2";
        }
    }

    private int GetCurrentDay() {
        int currentDayNumber;
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        Log.e("xcz", dayOfTheWeek);
        if (dayOfTheWeek.equals("Monday")) {
            currentDayNumber = 0;
        } else if (dayOfTheWeek.equals("Tuesday")) {
            currentDayNumber = 1;
        } else if (dayOfTheWeek.equals("Wednesday")) {
            currentDayNumber = 2;
        } else if (dayOfTheWeek.equals("Thursday")) {
            currentDayNumber = 3;
        } else if (dayOfTheWeek.equals("Friday")) {
            currentDayNumber = 4;
        } else if (dayOfTheWeek.equals("Saturday")) {
            currentDayNumber = 5;
        } else {
            currentDayNumber = 6;
        }
        return currentDayNumber;
    }

    private int GetCurrentHours() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime().getHours();
    }
}