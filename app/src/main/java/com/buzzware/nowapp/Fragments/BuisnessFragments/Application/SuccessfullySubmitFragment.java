package com.buzzware.nowapp.Fragments.BuisnessFragments.Application;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzware.nowapp.Fragments.BuisnessFragments.Application.AfterVerification.B11Fragment;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.databinding.FragmentSuccessfullySubmitBinding;

public class SuccessfullySubmitFragment extends Fragment implements View.OnClickListener{

    FragmentSuccessfullySubmitBinding mBinding;

    public SuccessfullySubmitFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_successfully_submit, container, false);

        ///click
        mBinding.btnOk.setOnClickListener(this);
        return mBinding.getRoot();
    }

    @Override
    public void onClick(View v) {
        if(v == mBinding.btnOk)
        {
            getActivity().finish();
        }
    }
}