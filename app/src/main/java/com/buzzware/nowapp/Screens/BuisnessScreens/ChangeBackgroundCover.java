package com.buzzware.nowapp.Screens.BuisnessScreens;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.buzzware.nowapp.Models.BusinessModel;
import com.buzzware.nowapp.Permissions.Permissions;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Sessions.UserSessions;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.ActivityChangeBackgroundCoverBinding;
import com.buzzware.nowapp.databinding.UploadImageBottomDialogBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.UUID;

public class ChangeBackgroundCover extends AppCompatActivity {

    ActivityChangeBackgroundCoverBinding binding;
    Permissions permissions;
    public static final int ACCESS_CAMERA = 101;
    public static final int ACCESS_Gallery = 102;

    StorageReference storageReference;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;

    Uri imageUri = null;

    BusinessModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChangeBackgroundCoverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        permissions = new Permissions(ChangeBackgroundCover.this);

        getDataFromPreferences();
        init();
        setViews();
        setListener();

    }

    private void getDataFromPreferences() {

        model = UserSessions.GetUserSession().getBusinessModel(this);
    }


    private void init() {

        mAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        firebaseFirestore = FirebaseFirestore.getInstance();

    }
    private void setViews() {

        if (model.businessBackgroundImage != null) {

            Picasso.with(ChangeBackgroundCover.this).load(model.businessBackgroundImage).fit().into(binding.coverPhotoIV, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    binding.coverPhotoIV.setImageResource(R.drawable.no_image_placeholder);
                }
            });
        }


    }
    private void setListener() {
        binding.btnUploadImage.setOnClickListener(v -> {
            imageUri = null;
            ShowImageUploadDailog();
        });
        binding.backIcon.setOnClickListener(v -> {
            imageUri = null;
            finish();
        });
        binding.btnSave.setOnClickListener(v -> {
            if (imageUri != null) {
                UploadImage();
            }
        });
    }

    private void ShowImageUploadDailog() {
        UploadImageBottomDialogBinding binding;
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ChangeBackgroundCover.this, R.style.SheetDialog);
        binding = UploadImageBottomDialogBinding.inflate(LayoutInflater.from(ChangeBackgroundCover.this));
        bottomSheetDialog.setContentView(binding.getRoot());
        binding.btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                CheckPermission(true);
            }
        });

        binding.btnChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                CheckPermission(false);
            }
        });
        bottomSheetDialog.show();
    }

    private void CheckPermission(boolean isTakePhoto) {
        if (permissions.isStoragePermissionGranted() && permissions.isCameraPermissionGranted()) {
            if (isTakePhoto) {
                OpenCamera();
            } else {
                OpenGallery();
            }
        } else {
            RequestPermission(isTakePhoto);
        }
    }

    private void OpenCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, ACCESS_CAMERA);
    }

    private void OpenGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), ACCESS_Gallery);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACCESS_CAMERA && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            binding.coverPhotoIV.setImageURI(uri);
            imageUri = uri;
        } else if (requestCode == ACCESS_Gallery && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            binding.coverPhotoIV.setImageURI(uri);
            imageUri = uri;
        }
    }

    private void RequestPermission(boolean isTakePhoto) {
        Dexter.withActivity(ChangeBackgroundCover.this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    if (isTakePhoto) {
                        OpenCamera();
                    } else {
                        OpenGallery();
                    }
                } else {
                    UIUpdate.GetUIUpdate(ChangeBackgroundCover.this).ShowToastMessage(getString(R.string.allow_permission));
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }


    private void UploadImage() {
        ProgressDialog progressDialog = new ProgressDialog(ChangeBackgroundCover.this);
        progressDialog.setTitle("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String randomkey = UUID.randomUUID().toString();
        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("userThumbnail/" + randomkey);
        reference.
                putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            progressDialog.dismiss();
            //Toast.makeText(this, "Save Successfully!", Toast.LENGTH_SHORT).show();
            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri1) {
                    //photoFirbaseURI = uri1;
                    firebaseFirestore.collection("BusinessData").document(mAuth.getCurrentUser().getUid()).update("businessBackgroundImage", uri1.toString());

                    model.businessBackgroundImage = uri1.toString();

                    UserSessions.GetUserSession().setBusiness(model, ChangeBackgroundCover.this);

                    imageUri = null;
                }
            });
        }).addOnFailureListener(e -> {

        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                int progress = (int) (100.0
                        * taskSnapshot.getBytesTransferred()
                        / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage(
                        "Uploaded "
                                + progress + "%");
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        UIUpdate.destroy();
    }

}