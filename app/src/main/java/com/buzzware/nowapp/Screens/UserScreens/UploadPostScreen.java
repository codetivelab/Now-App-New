package com.buzzware.nowapp.Screens.UserScreens;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.buzzware.nowapp.FirebaseRequests.FirebaseRequests;
import com.buzzware.nowapp.FirebaseRequests.Interfaces.RestaurantResponseCallback;
import com.buzzware.nowapp.Models.RestaurantDataModel;
import com.buzzware.nowapp.Models.UserSpinnerModel;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Screens.BuisnessScreens.BuisnessHome;
import com.buzzware.nowapp.Screens.General.BaseActivity;
import com.buzzware.nowapp.Screens.UserScreens.post.Post;
import com.buzzware.nowapp.Sessions.UserSessions;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.ActivityUploadPostScreenBinding;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UploadPostScreen extends BaseActivity {

    ActivityUploadPostScreenBinding binding;

    List<UserSpinnerModel> userList = new ArrayList<>();
    List<UserSpinnerModel> businessList = new ArrayList<>();

    String filePath;
    Bitmap bmThumbnail;

    FirebaseFirestore db;

    String userType;

    List<RestaurantDataModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUploadPostScreenBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        UIUpdate.destroy();
        getExtrasFromIntent();
        setVisibilities();
        setThumbnail();
        getUserAndBusinessList();
        setListeners();
    }

    private void setVisibilities() {
        userType = UserSessions.GetUserSession().getUserType(this);

        if (userType != null && userType.equalsIgnoreCase("b")) {
            binding.containerUserSpinnerRL.setVisibility(View.GONE);
            binding.containerBusinessSpinnerRL.setVisibility(View.GONE);
            binding.userRating.setVisibility(View.GONE);
            binding.ratingTV.setVisibility(View.GONE);
        }
        else
        {
            binding.containerUserSpinnerRL.setVisibility(View.GONE);
        }
    }

    private void setListeners() {

        binding.addPostBtn.setOnClickListener(v -> validateAndUpload());

    }

    public void getRestaurants() {

        FirebaseRequests.GetFirebaseRequests(this
        ).GetRestaurant(restaurantResponseCallback, this);

    }


    RestaurantResponseCallback restaurantResponseCallback = new RestaurantResponseCallback() {
        @Override
        public void onResponse(List<RestaurantDataModel> datalist, boolean isError, String message) {
            if (!isError) {

                list = datalist;
                setSpinner();

            } else {
                UIUpdate.GetUIUpdate(UploadPostScreen.this).DismissProgressDialog();
                UIUpdate.GetUIUpdate(UploadPostScreen.this).ShowToastMessage(message);
            }
        }
    };
    private void validateAndUpload() {

        if (validate()) {

            Post post = new Post();
            int businessPosition = binding.businessSpinner.getSelectedItemPosition();
            int usersPosition = binding.userSpinner.getSelectedItemPosition();

            String tagBusinessId = "N/A";
            String tagUserId = "N/A";
            String tagUserName = "N/A";

            if (userType != null && !userType.equalsIgnoreCase("b")) {

                if (businessPosition > 0) {
                    tagBusinessId = list.get(businessPosition).getId();
                }

                if (usersPosition > 0) {
                    tagUserId = userList.get(usersPosition).getUserID();
                    tagUserName = userList.get(usersPosition).getUserName();
                }
            }

            post.tagBusinessId = tagBusinessId;
            post.tagUserID = tagUserId;
            post.userPostComment = binding.captionField.getText().toString();
            post.userRating = "" + binding.userRating.getRating();
            post.tagUserName = tagUserName;

            File file = new File(filePath);

            String name = file.getAbsolutePath();

            UIUpdate.GetUIUpdate(this).setProgressDialog("Uploading", "Please Wait", this);

            FirebaseRequests.GetFirebaseRequests(this).UploadVideo((message, errorMessage) -> {

                if (message != null) {

                    post.userVideoUrl = message;

                    uploadThumbnail(post);

                } else {

                    UIUpdate.GetUIUpdate(this).DismissProgressDialog();

                    UIUpdate.GetUIUpdate(this).AlertDialog("Alert", errorMessage);
                }

            }, Uri.fromFile(file));
        }
    }

    private void uploadThumbnail(Post post) {

        Date date = new Date();

        FirebaseRequests.GetFirebaseRequests(this).firebaseUploadBitmap((message, errorMessage) -> {

            if (message != null) {

                post.userPostThumbnail = message;

                post.createdAt = String.valueOf(date.getTime());

                post.UserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                post.userImage = UserSessions.GetUserSession().getUserIMAGE(this);

                FirebaseRequests.GetFirebaseRequests(this).addPost(post);

                UIUpdate.GetUIUpdate(this).ShowToastMessage("Successfully Added");

                if (userType != null && userType.equalsIgnoreCase("b")) {

                    moveToBusinessScreen();

                } else {

                    finish();

                }

            } else {
                UIUpdate.GetUIUpdate(this).DismissProgressDialog();

                UIUpdate.GetUIUpdate(UploadPostScreen.this).AlertDialog("Alert", errorMessage);
            }

        }, bmThumbnail);

    }

    private void moveToBusinessScreen()
    {
        Intent i = new Intent(this, BuisnessHome.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }


    public static File bitmapToFile(Context context, Bitmap bitmap, String fileNameToSave) { // File name like "image.png"
        //create a file to write bitmap data
        File file = null;
        try {
            file = new File(Environment.getExternalStorageDirectory() + File.separator + fileNameToSave);
            file.createNewFile();

//Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos); // YOU can also save it in JPEG
            byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return file; // it will return null
        }
    }


    private boolean validate() {

        Boolean isValid = true;

        if (binding.captionField.getText().toString().isEmpty()) {

            UIUpdate.GetUIUpdate(this).AlertDialog("Alert", "Caption Required");

            return false;
        }

        return isValid;
    }

    private void getExtrasFromIntent() {

        Bundle b = getIntent().getExtras();

        if (b.getString("path") != null) {
            filePath = b.getString("path");
        }
    }

    private void setThumbnail() {


        bmThumbnail = ThumbnailUtils.createVideoThumbnail(filePath,
                MediaStore.Video.Thumbnails.MINI_KIND);

        if (bmThumbnail != null) {

            binding.thumbnailIV.setImageBitmap(bmThumbnail);

        } else {

            binding.thumbnailIV.setImageResource(R.drawable.camera_dummy);
            Log.d("VideoAdapter", "video thumbnail not found");

        }
    }

    private void getUserAndBusinessList() {

        db = FirebaseFirestore.getInstance();

        if (isOnline()) {

            db.collection("Users")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            populateBusinessList(task);
                            getRestaurants();
                        } else {

                            Log.w("TAG", "Error getting documents.", task.getException());

                        }
                    });
        }
    }

    private void populateBusinessList(Task<QuerySnapshot> task) {

        businessList.clear();
        userList.clear();

        businessList.add(new UserSpinnerModel("N/A", ""));
        userList.add(new UserSpinnerModel("N/A", ""));

        for (QueryDocumentSnapshot document : task.getResult()) {

            UserSpinnerModel userSpinnerModel = document.toObject(UserSpinnerModel.class);
            userSpinnerModel.setUserID(document.getId());
//            if (!userSpinnerModel.getUserID().equals(UserSessions.GetUserSession().getFirebaseUserID(UploadPostScreen.this))) {

            if (userSpinnerModel.getUserType().equals("B")) {

                businessList.add(userSpinnerModel);

            } else {

                userList.add(userSpinnerModel);

            }

        }

    }

    private void setSpinner() {
//        ArrayAdapter<UserSpinnerModel> adapter = new ArrayAdapter<UserSpinnerModel>(this,
//                android.R.layout.simple_spinner_item, userList) {
//            public View getView(int position, View convertView, ViewGroup parent) {
//                View v = super.getView(position, convertView, parent);
//
//                ((TextView) v).setTextSize(16);
//                ((TextView) v).setGravity(Gravity.LEFT);
//                ((TextView) v).setTextColor(
//                        getResources().getColorStateList(R.color.text_gray)
//                );
//
//
//                return v;
//            }
//
//            public View getDropDownView(int position, View convertView, ViewGroup parent) {
//                View v = super.getDropDownView(position, convertView, parent);
//                ((TextView) v).setGravity(Gravity.LEFT);
//
//                return v;
//            }
//        };
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        binding.userSpinner.setAdapter(adapter);

        ArrayAdapter<RestaurantDataModel> adapter;
        adapter = new ArrayAdapter<RestaurantDataModel>(this,
                android.R.layout.simple_spinner_item, list) {
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTextSize(16);

                ((TextView) v).setGravity(Gravity.LEFT);

                ((TextView) v).setTextColor(getResources().getColorStateList(R.color.text_gray));

                ((TextView) v).setText(list.get(position).getBusinessName());

                return v;

            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {

                View v = super.getDropDownView(position, convertView, parent);

                ((TextView) v).setGravity(Gravity.LEFT);

                ((TextView) v).setText(list.get(position).getBusinessName());

                return v;

            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.businessSpinner.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UIUpdate.destroy();
    }

}