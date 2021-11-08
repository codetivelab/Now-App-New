package com.buzzware.nowapp.Screens.BuisnessScreens;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.buzzware.nowapp.Models.BusinessModel;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Sessions.UserSessions;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.ActivityBuisnessInfoBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class BuisnessInfo extends AppCompatActivity {

    ActivityBuisnessInfoBinding binding;
    List<String> businessTyprList = new ArrayList<String>();

    StorageReference storageReference;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;

    BusinessModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= ActivityBuisnessInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getDataFromPreferences();

        setSpinner();
        setListener();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UIUpdate.destroy();
    }


    private void getDataFromPreferences() {

        model = UserSessions.userSessions.getBusinessModel(this);
    }
    private void setListener() {
        binding.backIcon.setOnClickListener(v->{
            finish();
        });
        binding.btnSave.setOnClickListener(v->{
            firebaseFirestore.collection("BusinessData").document(mAuth.getCurrentUser().getUid()).update("businessType", binding.businessTypeSpinner.getSelectedItem().toString());
            firebaseFirestore.collection("BusinessData").document(mAuth.getCurrentUser().getUid()).update("businessNumber", binding.businessPhoneNumberET.getText().toString());

            model.businessType = binding.businessTypeSpinner.getSelectedItem().toString();
            model.businessNumber = binding.businessPhoneNumberET.getText().toString();

            UserSessions.GetUserSession().setBusiness(model, BuisnessInfo.this);

            Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show();
        });
    }

    private void setSpinner() {


        businessTyprList.add("Club");
        businessTyprList.add("Restaurant");

        ArrayAdapter<String> arrayAdapterTpye = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, businessTyprList) {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTextSize(16);
                ((TextView) v).setGravity(Gravity.CENTER);
                ((TextView) v).setTextColor(
                        getResources().getColorStateList(R.color.text_gray)
                );

                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setGravity(Gravity.CENTER);

                return v;
            }
        };
        binding.businessTypeSpinner.setDropDownHorizontalOffset(10);
        binding.businessTypeSpinner.setDropDownVerticalOffset(10);
        binding.businessTypeSpinner.setAdapter(arrayAdapterTpye);

        setViews();

    }

    private void setViews() {

        mAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        firebaseFirestore = FirebaseFirestore.getInstance();

        binding.businessNameTV.setText(model.getBusinessName());
        binding.businessEmailTV.setText(UserSessions.GetUserSession().getUserEmail(this));
        binding.businessPhoneNumberET.setText(model.getBusinessNumber());
        binding.businessTypeSpinner.setSelection(businessTyprList.indexOf(model.getBusinessType()));
    }


}