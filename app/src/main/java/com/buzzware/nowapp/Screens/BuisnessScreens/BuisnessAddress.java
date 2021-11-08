package com.buzzware.nowapp.Screens.BuisnessScreens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.buzzware.nowapp.R;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.ActivityBuisnessAddressBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class BuisnessAddress extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener{

    ActivityBuisnessAddressBinding mBinding;
    public static GoogleMap mMap;
    Context context;
    public static Marker marker;
    public LatLng dummyLatLang= new LatLng(37.819927, -122.478256);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_buisness_address);
        context= this;
        if (mBinding.locationSelectionMapView != null) {
            mBinding.locationSelectionMapView.onCreate(null);
            mBinding.locationSelectionMapView.onResume();
            mBinding.locationSelectionMapView.getMapAsync(this);
        }

        ///clicks
        mBinding.btnChangeLocation.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        boolean result= mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style));

        ///move to dummy location
        marker = mMap.addMarker(new MarkerOptions().position(dummyLatLang).title("Golden Gate Bridge").icon(BitmapFromVector(context, R.drawable.dummy_marker)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dummyLatLang, 12.0F));
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(20, 20, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UIUpdate.destroy();
    }

    @Override
    public void onClick(View v) {
        if(v == mBinding.btnChangeLocation)
        {
            startActivity(new Intent(this, ChangeBuisnessLocation.class));
        }
    }
}