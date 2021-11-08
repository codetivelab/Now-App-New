package com.buzzware.nowapp.Fragments.BuisnessFragments.Application;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzware.nowapp.Constants.Constant;
import com.buzzware.nowapp.NetworkRequests.Interfaces.NetWorkRequestsCallBack;
import com.buzzware.nowapp.NetworkRequests.NetworkRequests;
import com.buzzware.nowapp.Permissions.Permissions;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Screens.General.LocationSelectionScreen;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.FragmentSecondTabBinding;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.michaelbel.bottomsheet.annotation.New;

import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class SecondTabFragment extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    FragmentSecondTabBinding mBinding;
    Context context;
    GoogleApiClient mGoogleApiClient;
    int REQUEST_CHECK_SETTINGS = 100;
    Permissions permissions;
    public static double latitude=0, longitude=0;
    String venueAddress= "";
    public static String locationName="", stateName, cityName="", zipCode="", throughFare= "";

    public SecondTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_second_tab, container, false);
        Init();
        ///clicks
        mBinding.btnNext.setOnClickListener(this);
        mBinding.btnBack.setOnClickListener(this);
        mBinding.addressLay.setOnClickListener(this);
        return mBinding.getRoot();
    }

    private void Init() {
        context= getContext();
        permissions= new Permissions(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!locationName.equals("") && latitude != 0 && longitude != 0){
            mBinding.selectedAddress.setText(locationName);
            mBinding.selectedCity.setText(cityName);
            mBinding.selectedState.setText(stateName);
            if(zipCode != null){
                mBinding.zipCodeET.setText(zipCode);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v == mBinding.btnNext)
        {
            Validation();
        }else if(v == mBinding.btnBack)
        {
            getActivity().onBackPressed();
        }else if(v == mBinding.addressLay){
            CheckPermission();
        }
    }

    private void Validation() {
        if(!locationName.equals("") && !cityName.equals("") && !stateName.equals("")){
            if(!TextUtils.isEmpty(mBinding.zipCodeET.getText().toString())){
                if(Constant.GetConstant().getNetworkInfo(context)){
                    CheckBuisnessAvailability();
                }else{
                    UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.no_internet));
                }
            }else{
                UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.zip_code_req));
            }
        }else{
            UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.select_location));
        }
    }

    private void CheckBuisnessAvailability() {
        Log.e("zxczc", locationName);
        String[] separated = locationName.split("\\,");
        if(separated.length >= 1){
            String[] separatedSpace = separated[1].split("\\ ");
            if(separatedSpace.length > 1){
                String venueName= FirstTabFragment.buisnessSignupModel.getBuisnessName();
                venueAddress= separatedSpace[1]+", "+cityName;
                Log.e("zxczc", venueAddress);
                NetworkRequests.GetNetworkRequests(context).GetVenueDetail(callBack, venueName, venueAddress, context);
            }
        }
    }

    NetWorkRequestsCallBack callBack= new NetWorkRequestsCallBack() {
        @Override
        public void ResponseListener(String response, boolean isError) {
            UIUpdate.GetUIUpdate(context).DismissProgressDialog();
            if(!isError){
                ReadResponse(response);
            }else{
                UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.error_occur));
            }
        }
    };

    private void ReadResponse(String response) {
        try {
            JSONObject jsonObject= new JSONObject(response);
            String status= jsonObject.getString(Constant.GetConstant().getStatus());
            if(status.equals(getString(R.string.OK))){
                JSONObject jsonObjectVenuInfo= jsonObject.getJSONObject(Constant.GetConstant().getVenue_info());
                String venueName= jsonObjectVenuInfo.getString(Constant.GetConstant().getVenue_name());
                String venueAddress= jsonObjectVenuInfo.getString(Constant.GetConstant().getVenue_address());
                SetDataToModel(venueName, venueAddress, response);
            }else{
                UIUpdate.GetUIUpdate(context).ShowAlertDialog(getString(R.string.business_availability), getString(R.string.business_avail_message), context);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            UIUpdate.GetUIUpdate(context).ShowToastMessage(e.getMessage());
        }
    }

    private void SetDataToModel(String venueName, String venueAddress, String response) {
        FirstTabFragment.buisnessSignupModel.setBuisnessLocation(venueAddress);
        FirstTabFragment.buisnessSignupModel.setBuisnessLatitude(String.valueOf(latitude));
        FirstTabFragment.buisnessSignupModel.setBuisnessLongitude(String.valueOf(longitude));
        FirstTabFragment.buisnessSignupModel.setBuisnessState(stateName);
        FirstTabFragment.buisnessSignupModel.setBuisnessCity(cityName);
        FirstTabFragment.buisnessSignupModel.setBuisnessZipcode(zipCode);
        FirstTabFragment.buisnessSignupModel.setVenueAddress(this.venueAddress);
        FirstTabFragment.buisnessSignupModel.setBuisnessResponse(response);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.buisnessApplicationContaner, new ThirdTabFragment()).addToBackStack("thirdTab").commit();
    }

    private void CheckPermission() {
        if(permissions.isLocationPermissionGranted()){
            CreateLocationRequest();
        }else{
            RequestPermission();
        }
    }

    private void CreateLocationRequest() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000*2);
        mLocationRequest.setFastestInterval(1000*1);
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
                        Intent intent= new Intent(context, LocationSelectionScreen.class);
                        startActivity(intent);
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
        if(requestCode == REQUEST_CHECK_SETTINGS)
        {
            if(resultCode == RESULT_OK)
            {
                Intent intent= new Intent(context, LocationSelectionScreen.class);
                startActivity(intent);
            }else if(resultCode == RESULT_CANCELED)
            {
                UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.need_location));
            }
        }
    }

    private void RequestPermission() {
        Dexter.withActivity(getActivity()).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(report.areAllPermissionsGranted()){
                    CreateLocationRequest();
                }else{
                    UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.allow_permission));
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
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
}