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
import com.buzzware.nowapp.databinding.FragmentHomeBinding;
import com.buzzware.nowapp.databinding.FragmentThirdTabBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ThirdTabFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    FragmentThirdTabBinding mBinding;
    public static GoogleMap mMap;
    Context context;
    public static Marker marker;
    public LatLng dummyLatLang= new LatLng(37.819927, -122.478256);

    public ThirdTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_third_tab, container, false);

        Init();

        return mBinding.getRoot();
    }

    private void Init() {
        context= getContext();
        if (mBinding.locationSelectionMapView != null) {
            mBinding.locationSelectionMapView.onCreate(null);
            mBinding.locationSelectionMapView.onResume();
            mBinding.locationSelectionMapView.getMapAsync(this);
        }

        ///clicks
        mBinding.btnNext.setOnClickListener(this);
        mBinding.btnBack.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        boolean result= mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style));
        SetData();
    }

    private void SetData() {
        LatLng latLng= new LatLng(Double.parseDouble(FirstTabFragment.buisnessSignupModel.getBuisnessLatitude()), Double.parseDouble(FirstTabFragment.buisnessSignupModel.getBuisnessLongitude()));
        ///move to dummy location
        marker = mMap.addMarker(new MarkerOptions().position(latLng).title(FirstTabFragment.buisnessSignupModel.getBuisnessName()).icon(BitmapFromVector(context, R.drawable.dummy_marker)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0F));
        mBinding.buisnessName.setText(FirstTabFragment.buisnessSignupModel.getBuisnessName());
        mBinding.locationName.setText(FirstTabFragment.buisnessSignupModel.getBuisnessLocation());
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
    public void onClick(View v) {
        if(v == mBinding.btnNext)
        {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.buisnessApplicationContaner, new FourthTabFragment()).addToBackStack("thirdTab").commit();
        }else if(v == mBinding.btnBack)
        {
            getActivity().onBackPressed();
        }
    }
}