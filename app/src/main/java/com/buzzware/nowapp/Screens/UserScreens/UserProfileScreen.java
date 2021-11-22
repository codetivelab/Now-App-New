package com.buzzware.nowapp.Screens.UserScreens;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.nowapp.Models.NormalUserModel;
import com.buzzware.nowapp.Permissions.Permissions;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Screens.BuisnessScreens.BuisnessInfo;
import com.buzzware.nowapp.Screens.BuisnessScreens.ChangeBuinessPassword;
import com.buzzware.nowapp.Screens.BuisnessScreens.ChangeBuisnessProfilePhoto;
import com.buzzware.nowapp.Sessions.UserSessions;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.ActivityUserProfileScreenBinding;
import com.buzzware.nowapp.databinding.UploadImageBottomDialogBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
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
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.buzzware.nowapp.Libraries.utils.UIUtils.getContext;

public class UserProfileScreen extends AppCompatActivity {

    ActivityUserProfileScreenBinding binding;
    StorageReference storageReference;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;

    Uri imageUri = null;
    String currentPhotoPath;
    FirebaseFirestore db;

    Permissions permissions;
    public static final int ACCESS_CAMERA = 101;
    public static final int ACCESS_Gallery = 102;

    NormalUserModel normalUserModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserProfileScreenBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        firebaseFirestore = FirebaseFirestore.getInstance();
        setListener();

        getUserData();
    }

    private void setListener() {
        binding.btnSave.setOnClickListener(v -> {

            if (normalUserModel == null)
                return;

            normalUserModel.setUserFirstName(binding.nameET.getText().toString());
            normalUserModel.setUserLastName("");
            normalUserModel.setUserPhoneNumber(binding.phoneEt.getText().toString());
            db.collection("Users").document(mAuth.getCurrentUser().getUid()).set(normalUserModel);

            finish();

            Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show();
        });
        binding.changePasswordRL.setOnClickListener(v -> {

            startActivity(new Intent(UserProfileScreen.this, ChangeBuinessPassword.class));

        });

        binding.backIV.setOnClickListener(view -> finish());
        binding.profileIV.setOnClickListener(view -> ShowImageUploadDailog());
    }

    private void ShowImageUploadDailog() {
        UploadImageBottomDialogBinding binding;
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.SheetDialog);
        binding = UploadImageBottomDialogBinding.inflate(LayoutInflater.from(this));
        bottomSheetDialog.setContentView(binding.getRoot());
        binding.btnTakePhoto.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            CheckPermission(true);
        });

        binding.btnChoosePhoto.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            CheckPermission(false);
        });
        bottomSheetDialog.show();
    }

    private void CheckPermission(boolean isTakePhoto) {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    if (isTakePhoto) {
                        dispatchTakePictureIntent();
                    } else {
                        OpenGallery();
                    }
                } else {
                    UIUpdate.GetUIUpdate(UserProfileScreen.this).ShowToastMessage(getString(R.string.allow_permission));
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();

//        if (permissions.isStoragePermissionGranted() && permissions.isCameraPermissionGranted()) {
//            if (isTakePhoto) {
//                OpenCamera();
//            } else {
//                OpenGallery();
//            }
//        } else {
//            RequestPermission(isTakePhoto);
//        }
    }

    private void getUserData() {

//        if (isOnline()) {
        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    normalUserModel = task.getResult().toObject(NormalUserModel.class);
                    if (normalUserModel == null)
                        return;
                    binding.nameET.setText(normalUserModel.getUserFirstName() + " " + normalUserModel.getUserLastName());
                    binding.emailET.setText(normalUserModel.getUserEmail());
                    binding.phoneEt.setText(normalUserModel.getUserPhoneNumber());
                    if (normalUserModel.getUserImageUrl() != null && !normalUserModel.getUserImageUrl().isEmpty())
                        Picasso.with(getContext()).load(normalUserModel.getUserImageUrl()).placeholder(R.drawable.dummy_post_image).into(binding.profileIV);


                });
    }
//    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        dispatchTakePictureLauncher.launch(takePictureIntent);
    }

    @Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }


    ActivityResultLauncher<Intent> dispatchTakePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
//                        File imageFile = new File(currentPhotoPath);
                        imageUri = getImageUri(UserProfileScreen.this, photo);

                        Glide.with(UserProfileScreen.this).load(imageUri)
                                .apply(new RequestOptions().centerCrop()).into(binding.profileIV);
//                        binding.coverPhotoIV.setBit

                        UploadImage();
//                        mBinding.licenceIV.setImageURI(Uri.fromFile(imageFile));
//
//                        pic1Path = currentPhotoPath;
//
//                        FirstTabFragment.buisnessSignupModel.setBuisnessLicenceImage(Uri.fromFile(imageFile));
                    }
                }
            });


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, new Date().toString(), null);
        return Uri.parse(path);
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
        if (requestCode == ACCESS_Gallery && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            binding.profileIV.setImageURI(uri);
            imageUri = uri;
            UploadImage();
        }
    }

    private void RequestPermission(boolean isTakePhoto) {
        Dexter.withActivity(UserProfileScreen.this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    if (isTakePhoto) {
                        OpenCamera();
                    } else {
                        OpenGallery();
                    }
                } else {
                    UIUpdate.GetUIUpdate(UserProfileScreen.this).ShowToastMessage(getString(R.string.allow_permission));
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }


    private void UploadImage() {
        binding.profileIV.setImageURI(imageUri);
        ProgressDialog progressDialog = new ProgressDialog(UserProfileScreen.this);
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
                    firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).update("userImageUrl", uri1.toString());
                    UserSessions.GetUserSession().setUserIMAGE(uri1.toString(), UserProfileScreen.this);
                    getUserData();
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



