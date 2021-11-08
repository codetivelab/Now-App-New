package com.buzzware.nowapp.Fragments.GeneralFragments.OnBoardingFragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzware.nowapp.R;
import com.buzzware.nowapp.databinding.FragmentSecondScreenBinding;

public class SecondScreen extends Fragment implements View.OnClickListener{

    FragmentSecondScreenBinding mBinding;

    public SecondScreen() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_second_screen, container, false);

        //clicks
        mBinding.btnBack.setOnClickListener(this);
        mBinding.btnNext.setOnClickListener(this);
        return mBinding.getRoot();
    }

    @Override
    public void onClick(View v) {
        if(v == mBinding.btnBack)
        {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.onBoardingContainer, new FirstScreen()).commit();
        }else if(v == mBinding.btnNext)
        {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.onBoardingContainer, new ThirdScreen()).commit();
        }
    }
}