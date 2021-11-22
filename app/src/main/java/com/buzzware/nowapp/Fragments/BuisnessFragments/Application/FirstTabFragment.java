package com.buzzware.nowapp.Fragments.BuisnessFragments.Application;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.buzzware.nowapp.Addapters.BuisnessTypeListAddapters;
import com.buzzware.nowapp.Constants.Constant;
import com.buzzware.nowapp.Models.BuisnessSignupModel;
import com.buzzware.nowapp.Models.SelectionModel;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.FragmentFirstTabBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstTabFragment extends Fragment implements View.OnClickListener {

    FragmentFirstTabBinding mBinding;
    Context context;
    public BuisnessSignupModel buisnessSignupModel;
    BottomSheetDialog bottomSheetDialog;

    public FirstTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_first_tab, container, false);
        context = getContext();
        buisnessSignupModel = new BuisnessSignupModel();
        mBinding.OpenTypeSpinner.setOnClickListener(this);
        mBinding.btnNext.setOnClickListener(this);
        return mBinding.getRoot();
    }

    @Override
    public void onClick(View v) {

        if (v == mBinding.OpenTypeSpinner) {

            ShowTypeListDialog();

        } else if (v == mBinding.btnNext) {

            Validation();

        }
    }

    private void Validation() {
        if (!TextUtils.isEmpty(mBinding.buisnessNameET.getText().toString())) {
            if (buisnessSignupModel.getBuisnessType() != null) {
                if (!TextUtils.isEmpty(mBinding.buisnessEmailET.getText().toString())) {
                    if (!TextUtils.isEmpty(mBinding.buisnessPhoneNumberET.getText().toString())) {
                        if (!TextUtils.isEmpty(mBinding.firstNameET.getText().toString())) {
                            if (!TextUtils.isEmpty(mBinding.lastNameET.getText().toString())) {
                                if (!TextUtils.isEmpty(mBinding.applicantPhoneNumberET.getText().toString())) {
                                    if (!TextUtils.isEmpty(mBinding.passwordET.getText().toString())) {
                                        SetDataToModel();
                                    } else {
                                        UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.password_req));
                                    }
                                } else {
                                    UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.applicant_number_req));
                                }
                            } else {
                                UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.last_name_req));
                            }
                        } else {
                            UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.first_name_req));
                        }
                    } else {
                        UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.buisness_number_req));
                    }
                } else {
                    UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.buisness_email_req));
                }
            } else {
                UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.buisness_type_req));
            }
        } else {
            UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.buisness_name_req));
        }
    }

    private void SetDataToModel() {
        buisnessSignupModel.setBuisnessName(mBinding.buisnessNameET.getText().toString());
        buisnessSignupModel.setBuisessEmail(mBinding.buisnessEmailET.getText().toString());
        buisnessSignupModel.setBuisnessNumber(mBinding.buisnessPhoneNumberET.getText().toString());
        buisnessSignupModel.setApplicantFirstName(mBinding.firstNameET.getText().toString());
        buisnessSignupModel.setApplicantLastName(mBinding.lastNameET.getText().toString());
        buisnessSignupModel.setApplicantNumber(mBinding.applicantPhoneNumberET.getText().toString());
        buisnessSignupModel.setPassword(mBinding.passwordET.getText().toString());
        CallSecondTabFragment();
    }

    private void CallSecondTabFragment() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.buisnessApplicationContaner, new SecondTabFragment(buisnessSignupModel)).addToBackStack("secondTab").commit();
    }

    private void ShowTypeListDialog() {
        RecyclerView recyclerView;

        bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.SheetDialog);

        View view = getLayoutInflater().inflate(R.layout.buisness_type_list_dialog_lay, (LinearLayout) getActivity().findViewById(R.id.container));

        recyclerView = view.findViewById(R.id.rv_buisness_types);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        BuisnessTypeListAddapters buisnessTypeListAddapters = new BuisnessTypeListAddapters(getContext(), Constant.GetConstant().GetBuisnessTypes(context), clickListener);

        recyclerView.setAdapter(buisnessTypeListAddapters);

        buisnessTypeListAddapters.notifyDataSetChanged();
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    BuisnessTypeListAddapters.ClickListener clickListener = new BuisnessTypeListAddapters.ClickListener() {
        @Override
        public void OnClick(SelectionModel selectionModel) {
            buisnessSignupModel.setBuisnessType(selectionModel.getName());
            mBinding.businessTypeTV.setText(selectionModel.getName());
            if (bottomSheetDialog != null)
                bottomSheetDialog.dismiss();
        }
    };
}