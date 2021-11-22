package com.buzzware.nowapp.BottomSheets;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.buzzware.nowapp.Addapters.NearbyPlacesAdapter;
import com.buzzware.nowapp.Addapters.RestaurantsPicturesAdapter;
import com.buzzware.nowapp.Libraries.libactivities.VideoCameraActivity;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Screens.UserScreens.Home;
import com.buzzware.nowapp.databinding.FragmentBottomsheetParentBinding;
import com.buzzware.nowapp.databinding.FragmentPlacesBottomsheetBinding;
import com.buzzware.nowapp.response.NearbyPlacesResponse;
import com.buzzware.nowapp.retrofit.Controller;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import im.delight.android.location.SimpleLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.buzzware.nowapp.Constants.Constant.GOOGLE_PLACES_API_KEY;

public class PlacesDialogListFragment extends BottomSheetDialogFragment {

    public static String TAG = PlacesDialogListFragment.class.getSimpleName();

    private BottomSheetBehavior sheetBehavior;

    NearbyPlacesResponse nearbyPlacesResponse;

    SimpleLocation location;

    FragmentBottomsheetParentBinding binding;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        binding = FragmentBottomsheetParentBinding.inflate(inflater, container, false);

        setSheetBehaviour();

        Dexter.withActivity(getActivity()).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {

                    checkLocationAndGetData();

                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                token.continuePermissionRequest();

            }
        }).check();


        return binding.getRoot();
    }

    private void checkLocationAndGetData() {

        location = new SimpleLocation(getContext());

        if (location.hasLocationEnabled()) {

            location.beginUpdates();

            getPlaces();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void getPlaces() {

        String url = "/maps/api/place/nearbysearch/json?location=" + location.getLatitude() + "," + location.getLongitude() + "&radius=1500&type=restaurant&key=AIzaSyDO-ypkG16_MoqMW0DbDErsEVJo8ikMojM";
        Controller.getApi().getPlaces(url, "asdasd")
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (location != null)
                            location.endUpdates();
                        location = null;

                        Gson gson = new Gson();

                        if (response.body() != null && response.isSuccessful()) {

                            nearbyPlacesResponse = gson.fromJson(response.body(), NearbyPlacesResponse.class);

                            if (nearbyPlacesResponse.getResults() != null)

                                setAdapter(nearbyPlacesResponse);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        if (location != null)
                            location.endUpdates();
                        location = null;

                    }
                });

    }

    private void setAdapter(NearbyPlacesResponse nearbyPlacesResponse) {
//        binding.include.latestPlacesRV.setVisibility(View.GONE);
        binding.include.listRestaurantsRV.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        binding.include.latestPlacesRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.include.listRestaurantsRV.setAdapter(new RestaurantsPicturesAdapter(getActivity(), nearbyPlacesResponse.getResults()));
        binding.include.latestPlacesRV.setAdapter(new NearbyPlacesAdapter(getActivity(), nearbyPlacesResponse.getResults()));

//        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }
    private void setSheetBehaviour() {

        sheetBehavior = BottomSheetBehavior.from(binding.include.bottomSheetLayout);

        sheetBehavior.setDraggable(true);

        sheetBehavior.setHideable(false);

        sheetBehavior.setPeekHeight(new Float(pxFromDp(getActivity(),180)).intValue());

//        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//
//
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//
//
//                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
//
////                    binding.include.latestRestaurantsRV.setVisibility(View.GONE);
////
////                    binding.include.latestPlacesRV.setVisibility(View.VISIBLE);
//
//                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                } else {
//
////                    binding.include.latestPlacesRV.setVisibility(View.GONE);
////
////                    binding.include.latestRestaurantsRV.setVisibility(View.VISIBLE);
//
//                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                }
//            }
//        });
    }
    public float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
