package com.buzzware.nowapp.Fragments.BuisnessFragments.Application.BuisnessDashBoard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.buzzware.nowapp.Models.BusinessModel;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Screens.BuisnessScreens.BuisnessInfo;
import com.buzzware.nowapp.Screens.BuisnessScreens.ChangeBackgroundCover;
import com.buzzware.nowapp.Screens.BuisnessScreens.ChangeBuinessPassword;
import com.buzzware.nowapp.Screens.BuisnessScreens.ChangeBuisnessProfilePhoto;
import com.buzzware.nowapp.Screens.UserScreens.Login;
import com.buzzware.nowapp.Sessions.UserSessions;
import com.buzzware.nowapp.databinding.FragmentBuisnessSettingsBinding;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class BuisnessSettingsFragment extends Fragment implements View.OnClickListener {

    FragmentBuisnessSettingsBinding mBinding;
    User user;

    BusinessModel businessModel;

    public BuisnessSettingsFragment() {

        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buisness_settings, container, false);

        businessModel = UserSessions.GetUserSession().getBusinessModel(getActivity());

        mBinding.basicInfo.setOnClickListener(this);
        mBinding.changePassword.setOnClickListener(this);
        mBinding.changeProfilePhoto.setOnClickListener(this);
        mBinding.changeBackgroundCover.setOnClickListener(this);
        mBinding.btnLogout.setOnClickListener(this::onClick);

        setData();

        return mBinding.getRoot();
    }

    private void setData() {

        loadBackgroundImage();

        loadUserImage();
    }

    private void loadBackgroundImage() {

        String backgroundImage = businessModel.businessBackgroundImage;

        if (backgroundImage == null || backgroundImage.isEmpty())
            return;

        Picasso.with(getActivity()).load(backgroundImage).into(mBinding.coverPhotoIV, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
                mBinding.coverPhotoIV.setImageResource(R.drawable.no_image_placeholder);
            }
        });
    }

    private void loadUserImage() {

        String userImage = UserSessions.GetUserSession().getUserIMAGE(getActivity());

        if (userImage == null || userImage.isEmpty())
            return;

        Picasso.with(getActivity()).load(UserSessions.GetUserSession().getUserIMAGE(getContext())).fit().into(mBinding.userIV, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
                mBinding.userIV.setImageResource(R.drawable.no_image_placeholder);
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == mBinding.basicInfo) {
            startActivity(new Intent(getContext(), BuisnessInfo.class));
        } else if (v == mBinding.changePassword) {
            startActivity(new Intent(getContext(), ChangeBuinessPassword.class));
        } else if (v == mBinding.changeProfilePhoto) {
            startActivity(new Intent(getContext(), ChangeBuisnessProfilePhoto.class));
        } else if (v == mBinding.changeBackgroundCover) {
            startActivity(new Intent(getContext(), ChangeBackgroundCover.class));
        } else if (v == mBinding.btnLogout) {
            LogoutUser();
        }
    }

    private void LogoutUser() {
        UserSessions.GetUserSession().setFirebaseUserID("", getContext());
        UserSessions.GetUserSession().setUserType("", getContext());
        Intent intent = new Intent(getContext(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}