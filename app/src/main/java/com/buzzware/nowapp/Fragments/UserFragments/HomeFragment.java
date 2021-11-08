package com.buzzware.nowapp.Fragments.UserFragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.buzzware.nowapp.Addapters.HomeSearchListAddapters;
import com.buzzware.nowapp.Addapters.HomeTopListAddapters;
import com.buzzware.nowapp.BottomSheets.HomeListBottomDialogFragment;
import com.buzzware.nowapp.EventMessages.OpenSearchFragment;
import com.buzzware.nowapp.FirebaseRequests.FirebaseRequests;
import com.buzzware.nowapp.FirebaseRequests.Interfaces.RestaurantResponseCallback;
import com.buzzware.nowapp.Models.BusinessModel;
import com.buzzware.nowapp.Models.HomeListModel;
import com.buzzware.nowapp.Models.RestaurantDataModel;
import com.buzzware.nowapp.Permissions.Permissions;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Screens.UserScreens.SearchActivity;
import com.buzzware.nowapp.Sessions.UserSessions;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.FragmentHomeBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

public class HomeFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, View.OnClickListener {

    FragmentHomeBinding mBinding;
    public static GoogleMap mMap;
    Context context;
    public static Marker marker;
    public LatLng dummyLatLang = new LatLng(41.140630, -74.032660);
    public Dialog myDialog;
    public static List<RestaurantDataModel> restaurantDataModelList, nearestRestaurantData;
    Permissions permissions;
    int REQUEST_CHECK_SETTINGS = 100;
    GoogleApiClient mGoogleApiClient;
    public LocationManager locationManager;
    public LocationListener locationListener;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    double latitude = 0, longitude = 0;
    String selectedFilterStar = "", selectedFilterOccupation = "", selectedFilterPlaces = "";
    boolean isFilterClicked = false;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        try {
            Init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBinding.getRoot();
    }

    private void Init() {
        context = getContext();
        restaurantDataModelList = new ArrayList<>();
        nearestRestaurantData = new ArrayList<>();
        permissions = new Permissions(context);
        myDialog = new Dialog(context);
        if (mBinding.homeMapView != null) {
            mBinding.homeMapView.onCreate(null);
            mBinding.homeMapView.onResume();
            mBinding.homeMapView.getMapAsync(this);
        }

        //clicks
        mBinding.list.setOnClickListener(this);
        mBinding.searchLay.setOnClickListener(this);
        mBinding.btnCancel.setOnClickListener(this);
        mBinding.btnFilter.setOnClickListener(this);
        mBinding.currentLocationDialog.setOnClickListener(this);
        mBinding.profileLay.setOnClickListener(this);
    }

    RestaurantResponseCallback restaurantResponseCallback = new RestaurantResponseCallback() {
        @Override
        public void onResponse(List<RestaurantDataModel> list, boolean isError, String message) {
            if (!isError) {
                Log.e("sadsc", list.size() + "");
                UIUpdate.GetUIUpdate(context).DismissProgressDialog();
                restaurantDataModelList = list;
                GetNearestRestaurant();
            } else {
                UIUpdate.GetUIUpdate(context).DismissProgressDialog();
                UIUpdate.GetUIUpdate(context).ShowToastMessage(message);
            }
        }
    };

    private void GetNearestRestaurant() {
        for (int i = 0; i < restaurantDataModelList.size(); i++) {
            RestaurantDataModel restaurantDataModel = restaurantDataModelList.get(i);
            double diatanceBetween = distance(latitude, longitude, Double.parseDouble(restaurantDataModel.getBusinessLatitude()), Double.parseDouble(restaurantDataModel.getBusinessLongitude()));
            if (diatanceBetween <= 7) { /// 7 miles = 1.6 * 7= 11.2 KM
                nearestRestaurantData.add(restaurantDataModel);
            }
        }
        ShowMarkers();
    }

    private void ShowMarkers() {
        if (nearestRestaurantData.size() > 0) {
            for (int i = 0; i < nearestRestaurantData.size(); i++) {
                RestaurantDataModel restaurantDataModel = nearestRestaurantData.get(i);
                LatLng latLng = new LatLng(Double.parseDouble(restaurantDataModel.getBusinessLatitude()), Double.parseDouble(restaurantDataModel.getBusinessLongitude()));
                marker = mMap.addMarker(new MarkerOptions().position(latLng).title(restaurantDataModel.getBusinessName()).icon(BitmapFromVector(context, R.drawable.dummy_marker)));
            }
        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) { ///distance in miles
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style));

        //get rest data
        FirebaseRequests.GetFirebaseRequests(context).GetRestaurant(restaurantResponseCallback, context);

        ///getCurrent location
        CheckPermission();
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(20, 20, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void ShowRatingDialog() {
        RelativeLayout btnCancel;
        myDialog.setContentView(R.layout.home_rating_dialog_lay);
        btnCancel = myDialog.findViewById(R.id.btnCancel);
        myDialog.setCancelable(true);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

        //click
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
    }

    private void ShowListDialog() {
        HomeListBottomDialogFragment homeListBottomDialogFragment = HomeListBottomDialogFragment.newInstance();
        homeListBottomDialogFragment.show(getActivity().getSupportFragmentManager(), "List");
    }

    @Override
    public void onClick(View v) {
        if (v == mBinding.list) {
            ShowListDialog();
        } else if (v == mBinding.searchLay) {

            EventBus.getDefault().post(new OpenSearchFragment(null, "isFromFilter"));

        } else if (v == mBinding.btnCancel) {
            mBinding.searchHistoryIcon.setImageResource(R.drawable.ic_baseline_search_24);
            mBinding.searchHistoryLay.setVisibility(View.GONE);
        } else if (v == mBinding.btnFilter) {
            if (isFilterClicked) {
                isFilterClicked = false;
                mBinding.btnFilter.setBackground(getResources().getDrawable(R.drawable.rounder_circle_light_blue));
                mBinding.filterIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_filter_));
            } else {
                isFilterClicked = true;
                mBinding.btnFilter.setBackground(getResources().getDrawable(R.drawable.rounder_circle_pink));
                mBinding.filterIV.setImageDrawable(getResources().getDrawable(R.drawable.filter_white));
                ShowFilterDialog();
            }
        } else if (v == mBinding.currentLocationDialog) {
            ShowCurrentLocationDialog();
        } else if (v == mBinding.profileLay) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).addToBackStack("profile").commit();
        }
    }

    private void ShowCurrentLocationDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.SheetDialog);
        View view = getLayoutInflater().inflate(R.layout.home_location_bottomsheet_dialog, (RelativeLayout) getActivity().findViewById(R.id.container));
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    public List<HomeListModel> GetDummyListData() {
        List<HomeListModel> listModels = new ArrayList<>();
        listModels.add(new HomeListModel());
        listModels.add(new HomeListModel());
        listModels.add(new HomeListModel());
        listModels.add(new HomeListModel());
        listModels.add(new HomeListModel());
        listModels.add(new HomeListModel());
        listModels.add(new HomeListModel());
        listModels.add(new HomeListModel());
        return listModels;
    }

    private void ShowFilterDialog() {
        RelativeLayout btnOneStar, btnTwoStar, btnThreeStar, btnFourStar, btnFiveStar;
        RelativeLayout btnCrowdy, btnRoomy, btnEmpty, btnApply;
        RelativeLayout btnGlobeype, btnBarType, btnCoffeeType, btnGuestType, btnRestType;
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.SheetDialog);
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
        btnOneStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFilterStar = "1";
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
                selectedFilterStar = "2";
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
                selectedFilterStar = "3";
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
                selectedFilterStar = "4";
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
                selectedFilterStar = "5";
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
                selectedFilterOccupation = "2";
                btnCrowdy.setBackground(getResources().getDrawable(R.drawable.rounder_circle_golden_border));
                btnRoomy.setBackground(null);
                btnEmpty.setBackground(null);
            }
        });

        btnRoomy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFilterOccupation = "0";
                btnCrowdy.setBackground(null);
                btnRoomy.setBackground(getResources().getDrawable(R.drawable.rounder_circle_golden_border));
                btnEmpty.setBackground(null);
            }
        });

        btnEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFilterOccupation = "-2";
                btnCrowdy.setBackground(null);
                btnRoomy.setBackground(null);
                btnEmpty.setBackground(getResources().getDrawable(R.drawable.rounder_circle_golden_border));
            }
        });

        btnGlobeype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFilterPlaces = getString(R.string.nightClub);
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
                selectedFilterPlaces = getString(R.string.bar);
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
                selectedFilterPlaces = getString(R.string.coffeeShop);
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
                selectedFilterPlaces = context.getString(R.string.club);
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
                selectedFilterPlaces = getString(R.string.restaurant);
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
        if (!selectedFilterStar.equals("")) {
            if (!selectedFilterOccupation.equals("")) {
                if (!selectedFilterPlaces.equals("")) {
                    bottomSheetDialog.dismiss();
                    FilterRestaurants();
                } else {
                    UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.select_type));
                }
            } else {
                UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.select_occupation));
            }
        } else {
            UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.select_star));
        }
    }

    private void FilterRestaurants() {
        if (HomeFragment.nearestRestaurantData.size() > 0) {
            List<RestaurantDataModel> filterList = new ArrayList<>();
            for (int i = 0; i < HomeFragment.nearestRestaurantData.size(); i++) {
                RestaurantDataModel restaurantDataModels = HomeFragment.nearestRestaurantData.get(i);
                String intensity = restaurantDataModels.GetIntensity(GetCurrentDay(), GetCurrentHours());
                String finalIntensity = GetFinalIntensity(intensity);
                if (restaurantDataModels.getBusinessTotalRating().equals(selectedFilterStar) &&
                        finalIntensity.equals(selectedFilterOccupation) && restaurantDataModels.getBusinessType().equals(selectedFilterPlaces)) {
                    filterList.add(restaurantDataModels);
                }
            }

            if (filterList.size() > 0) {

                EventBus.getDefault().post(new OpenSearchFragment(new Gson().toJson(filterList), "true"));
                
            } else {
                UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.restaurant_not_avail));
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

    ///location handling
    private void CheckPermission() {
//        if (permissions.isLocationPermissionGranted()) {
//            CreateLocationRequest();
//        } else {
            RequestPermission();
//        }
    }

    private void RequestPermission() {
        Dexter.withActivity(getActivity()).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    CreateLocationRequest();
                } else {
                    UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.allow_permission));
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    protected void CreateLocationRequest() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000 * 2);
        mLocationRequest.setFastestInterval(1000 * 1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                // final LocationSettingsStates = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            status.startResolutionForResult(
                                    getActivity(),
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                getLocation();
            } else if (resultCode == RESULT_CANCELED) {
                UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.need_location));
            }
        }
    }

    public void getLocation() {
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
                if (mMap != null) {
                    marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Your Location").icon(BitmapFromVector(context, R.drawable.dummy_marker)));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
                    ConvertLatLangToAddress(latitude, longitude);
                    locationManager.removeUpdates(locationListener);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, locationListener);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void ConvertLatLangToAddress(double latitude, double longitude) {

        try {
            Geocoder geocoder = new Geocoder(getActivity());
            List<Address> addresses;
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                String placeName = addresses.get(0).getAddressLine(0);
                mBinding.myLocationAddress.setText(placeName);

                BusinessModel model = UserSessions.GetUserSession().getBusinessModel(getActivity());

                if (addresses.get(0).getAdminArea() != null) {

                    if (model != null) {

                        model.businessCity = addresses.get(0).getAdminArea();

                        UserSessions.GetUserSession().setBusiness(model, getActivity());

                    }
                }

            } else {
                mBinding.myLocationAddress.setText(getString(R.string.unknownAddress));
            }
        } catch (IOException e) {
            e.printStackTrace();
            mBinding.myLocationAddress.setText(getString(R.string.unknownAddress));
        }
    }
}