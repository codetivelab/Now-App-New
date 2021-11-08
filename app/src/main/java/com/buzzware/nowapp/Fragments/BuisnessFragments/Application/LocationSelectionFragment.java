package com.buzzware.nowapp.Fragments.BuisnessFragments.Application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzware.nowapp.R;
import com.buzzware.nowapp.databinding.FragmentLocationSelectionBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationSelectionFragment extends Fragment implements OnMapReadyCallback {

    FragmentLocationSelectionBinding mBinding;
    public static GoogleMap mMap;
    Context context;
    public static Marker marker;
    public LatLng dummyLatLang= new LatLng(37.819927, -122.478256);

    public LocationSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_location_selection, container, false);
        try{

        }catch (Exception e)
        {
            e.printStackTrace();
        }Init();
        return mBinding.getRoot();
    }

    private void Init() {
        context= getContext();
        if (mBinding.locationSelectionMapView != null) {
            mBinding.locationSelectionMapView.onCreate(null);
            mBinding.locationSelectionMapView.onResume();
            mBinding.locationSelectionMapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        boolean result= mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style));

        ///move to dummy location
        marker = mMap.addMarker(new MarkerOptions().position(dummyLatLang).title("Golden Gate Bridge").icon(BitmapFromVector(context, R.drawable.dummy_marker)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dummyLatLang, 16.0F));
    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(20, 20, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}