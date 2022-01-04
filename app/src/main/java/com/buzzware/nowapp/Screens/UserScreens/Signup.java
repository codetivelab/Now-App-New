package com.buzzware.nowapp.Screens.UserScreens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.buzzware.nowapp.Constants.Constant;
import com.buzzware.nowapp.FieldChecker.FieldChecker;
import com.buzzware.nowapp.FirebaseRequests.FirebaseRequests;
import com.buzzware.nowapp.FirebaseRequests.Interfaces.SignupResponseCallback;
import com.buzzware.nowapp.Models.BuisnessSignupModel;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.ActivitySignupBinding;

public class Signup extends AppCompatActivity implements View.OnClickListener{

    Context context;
    ActivitySignupBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_signup);
        try{
            Init();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void Init() {
        context= this;
        mBinding.backIcon.setOnClickListener(this::onClick);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void Register(View view) {
        Validation();
    }

    private void Validation() {
        if(!TextUtils.isEmpty(mBinding.fnameEt.getText().toString())){
            if(!TextUtils.isEmpty(mBinding.lnameEt.getText().toString())) {
                if (!TextUtils.isEmpty(mBinding.emailEt.getText().toString())) {
                    if (!TextUtils.isEmpty(mBinding.phoneEt.getText().toString())) {
                        if (FieldChecker.GetFieldChecker().isValidEmail(mBinding.emailEt)) {
                            if (!TextUtils.isEmpty(mBinding.passwordEt.getText().toString())) {
                                if (Constant.GetConstant().getNetworkInfo(context)) {
                                    SignupUser();
                                } else {
                                    UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.no_internet));
                                }
                            } else {
                                UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.password_req));
                            }
                        } else {
                            UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.invalid_email));
                        }
                    } else {
                        UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.phone_req));
                    }
                } else {
                    UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.email_req));
                }
            }else{
                UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.lname_req));
            }
        }else{
            UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.fname_req));
        }
    }

    private void SignupUser() {
        BuisnessSignupModel buisnessSignupModel= new BuisnessSignupModel();

        buisnessSignupModel.setApplicantFirstName(mBinding.fnameEt.getText().toString());
        buisnessSignupModel.setApplicantLastName(mBinding.lnameEt.getText().toString());
        buisnessSignupModel.setBuisessEmail(mBinding.emailEt.getText().toString());
        buisnessSignupModel.setPassword(mBinding.passwordEt.getText().toString());
        buisnessSignupModel.setApplicantNumber(mBinding.phoneEt.getText().toString());

        FirebaseRequests.GetFirebaseRequests(context).SignUpUser(buisnessSignupModel, context, callback, Constant.GetConstant().getNormalUser());
    }

    SignupResponseCallback callback= new SignupResponseCallback() {
        @Override
        public void onResponse(boolean isError, String message) {
            if(!isError){
                finish();
            }
        }
    };

    @Override
    public void onClick(View v) {
        if(v == mBinding.backIcon)
        {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UIUpdate.destroy();
    }

}