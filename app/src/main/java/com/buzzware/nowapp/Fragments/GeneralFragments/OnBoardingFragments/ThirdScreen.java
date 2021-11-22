package com.buzzware.nowapp.Fragments.GeneralFragments.OnBoardingFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Screens.UserScreens.Login;
import com.buzzware.nowapp.databinding.FragmentThirdScreenBinding;

public class ThirdScreen extends Fragment implements View.OnClickListener{

    FragmentThirdScreenBinding mBinding;
    public ThirdScreen() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_third_screen, container, false);

        //clicks
        mBinding.btnBack.setOnClickListener(this);
        mBinding.btnNext.setOnClickListener(this);
        return mBinding.getRoot();
    }

    @Override
    public void onClick(View v) {
        if(v == mBinding.btnBack)
        {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.onBoardingContainer, new SecondScreen()).commit();
        }else if(v == mBinding.btnNext)
        {
            Intent intent= new Intent(getContext(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }
}