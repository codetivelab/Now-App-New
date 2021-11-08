package com.buzzware.nowapp.BottomSheets;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.nowapp.Addapters.HomeListAddapters;
import com.buzzware.nowapp.FirebaseRequests.FirebaseRequests;
import com.buzzware.nowapp.FirebaseRequests.Interfaces.RestaurantResponseCallback;
import com.buzzware.nowapp.Fragments.UserFragments.HomeFragment;
import com.buzzware.nowapp.Models.HomeListModel;
import com.buzzware.nowapp.Models.RestaurantDataModel;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

import im.delight.android.location.SimpleLocation;

public class HomeListBottomDialogFragment extends BottomSheetDialogFragment {

    RecyclerView rv_list;
    ProgressBar progressBar;
    TextView tvInfo;

    double latitude = 0, longitude = 0;

    private SimpleLocation location;

    List<RestaurantDataModel> restaurantDataModelList, nearestRestaurantData;

    public static HomeListBottomDialogFragment newInstance() {
        return new HomeListBottomDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                setupFullHeight(bottomSheetDialog);
            }
        });
        return  dialog;
    }

    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels - 150;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);
        View view = inflater.inflate(R.layout.home_list_bottom_sheet_lay, container, false);
        rv_list= view.findViewById(R.id.rvList);
        progressBar= view.findViewById(R.id.progressBar);
        tvInfo= view.findViewById(R.id.infoTV);
        rv_list.setLayoutManager(new LinearLayoutManager(getContext()));

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

        return view;
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



    private void SetData(List<RestaurantDataModel> list) {
        HomeListAddapters homeListAddapters = new HomeListAddapters(getActivity(), list, () -> dismiss());
        rv_list.setAdapter(homeListAddapters);
        homeListAddapters.notifyDataSetChanged();
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
        SetData(nearestRestaurantData);
    }
}
