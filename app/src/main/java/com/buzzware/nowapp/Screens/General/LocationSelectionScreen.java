package com.buzzware.nowapp.Screens.General;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.buzzware.nowapp.Fragments.BuisnessFragments.Application.FirstTabFragment;
import com.buzzware.nowapp.Fragments.BuisnessFragments.Application.SecondTabFragment;
import com.buzzware.nowapp.NetworkRequests.NetworkRequests;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.ActivityLocationSelectionScreenBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

public class LocationSelectionScreen extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener{

    ActivityLocationSelectionScreenBinding binding;
    public static GoogleMap mMap;
    Context context;
    public static Marker marker;
    public LocationManager locationManager;
    public LocationListener locationListener;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_location_selection_screen);
        try{
            Init();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void Init() {
        context= LocationSelectionScreen.this;
        if (binding.homeMapView != null) {
            binding.homeMapView.onCreate(null);
            binding.homeMapView.onResume();
            binding.homeMapView.getMapAsync(this);
        }

        //click
        binding.btbBack.setOnClickListener(this);
        binding.btnSelect.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == binding.btbBack){
            finish();
        }else if(v == binding.btnSelect){
            finish();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(LocationSelectionScreen.this, R.raw.map_style));
        getLocation();

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                final LatLng latLng= mMap.getCameraPosition().target;
                ConvertLatLangToAddress(latLng.latitude, latLng.longitude);
            }
        });
    }

    public void getLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LocationSelectionScreen.this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng yourLatLang= new LatLng(location.getLatitude(), location.getLongitude());
                ConvertLatLangToAddress(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(yourLatLang, 17));
                if(locationManager != null){
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

    private void ConvertLatLangToAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(LocationSelectionScreen.this);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if(addresses.size() > 0) {
                SecondTabFragment.latitude= latitude;
                SecondTabFragment.longitude= longitude;
                SecondTabFragment.locationName= addresses.get(0).getAddressLine(0);
                SecondTabFragment.cityName= addresses.get(0).getLocality();
                SecondTabFragment.stateName= addresses.get(0).getAdminArea();
                SecondTabFragment.throughFare= addresses.get(0).getThoroughfare();
                if(addresses.get(0).getPostalCode() != null) {
                    SecondTabFragment.zipCode = addresses.get(0).getPostalCode();
                }
                binding.selectedAddress.setText(addresses.get(0).getAddressLine(0));
                Log.e("czxc", addresses.get(0).toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            binding.selectedAddress.setText(getString(R.string.unknown));
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UIUpdate.destroy();
    }

}