package com.buzzware.nowapp.Fragments.UserFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Screens.UserScreens.Login;
import com.buzzware.nowapp.Sessions.UserSessions;
import com.buzzware.nowapp.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment implements View.OnClickListener{

    FragmentSettingsBinding mBinding;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        try{
            Init();
        }catch (Exception e){
            e.printStackTrace();
        }
        return mBinding.getRoot();
    }

    private void Init() {
        mBinding.btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == mBinding.btnLogout){
            LogoutUser();
        }
    }

    private void LogoutUser() {
        UserSessions.GetUserSession().setFirebaseUserID("", getContext());
        UserSessions.GetUserSession().setUserType("", getContext());
        Intent intent= new Intent(getContext(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}