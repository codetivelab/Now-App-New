package com.buzzware.nowapp.Fragments.BuisnessFragments.Application.AfterVerification;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.buzzware.nowapp.Fragments.BuisnessFragments.Application.FirstTabFragment;
import com.buzzware.nowapp.Models.BuisnessSignupModel;
import com.buzzware.nowapp.Permissions.Permissions;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Screens.UserScreens.UserProfileScreen;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.FragmentB11Binding;
import com.buzzware.nowapp.databinding.UploadImageBottomDialogBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class B11Fragment extends Fragment implements View.OnClickListener {

    FragmentB11Binding mBinding;
    public static final int ACCESS_CAMERA = 101;
    public static final int ACCESS_Gallery = 102;
    Permissions permissions;
    Context context;

    String currentPhotoPath;

//    String pic1Path;

    BuisnessSignupModel buisnessSignupModel;

    public B11Fragment(BuisnessSignupModel buisnessSignupModel) {
        // Required empty public constructor
        this.buisnessSignupModel = buisnessSignupModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_b11, container, false);
        context = getContext();
        permissions = new Permissions(context);
        mBinding.btnUploadImage.setOnClickListener(this);
        mBinding.btnNext.setOnClickListener(this::onClick);
        mBinding.btnBack.setOnClickListener(this::onClick);
        return mBinding.getRoot();
    }

    @Override
    public void onClick(View v) {
        if (v == mBinding.btnUploadImage) {
            ShowImageUploadDailog();
        } else if (v == mBinding.btnNext) {
            Validation();
        } else if (v == mBinding.btnBack) {
            getActivity().onBackPressed();
        }
    }

    private void Validation() {
        if (buisnessSignupModel.getBuisnessLogo() != null) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.buisnessApplicationContaner, new B12Fragment(buisnessSignupModel)).addToBackStack("b12").commit();
        } else {
            UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.log_req));
        }
    }

    private void ShowImageUploadDailog() {
        UploadImageBottomDialogBinding binding;
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.SheetDialog);
        binding = UploadImageBottomDialogBinding.inflate(LayoutInflater.from(getContext()));
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

        RequestPermission(isTakePhoto);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Uri selectedImage = data.getData();
                        String[] filePath = {MediaStore.Images.Media.DATA};
                        Cursor c = getActivity().getContentResolver().query(
                                selectedImage, filePath, null, null, null);
                        c.moveToFirst();
                        int columnIndex = c.getColumnIndex(filePath[0]);
                        String picturePath = c.getString(columnIndex);
                        c.close();

                        File file = new File(picturePath);
//                        pic1Path = picturePath;
                        mBinding.logoIV.setImageURI(Uri.fromFile(file));

                        buisnessSignupModel.setBuisnessLogo(Uri.fromFile(file));
                    }
                }
            });

    ActivityResultLauncher<Intent> dispatchTakePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        Bitmap photo = (Bitmap) result.getData().getExtras().get("data");

                        Uri imageUri = getImageUri(getActivity(), photo);

                        buisnessSignupModel.setBuisnessLogo(imageUri);
                        Glide.with(getActivity()).load(imageUri)
                                .apply(new RequestOptions().centerCrop()).into(mBinding.logoIV);
                    }

                }
            });

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, new Date().toString(), null);
        return Uri.parse(path);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        someActivityResultLauncher.launch(intent
        );
    }

    private void RequestPermission(boolean isTakePhoto) {
        Dexter.withActivity(getActivity()).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    if (isTakePhoto) {
                        dispatchTakePictureIntent();
                    } else {
                        pickImageFromGallery();
                    }
                } else {
                    UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.allow_permission));
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(buisnessSignupModel.getBuisnessLogo() != null) {

            Glide.with(getActivity()).load(buisnessSignupModel.getBuisnessLogo())
                    .apply(new RequestOptions().centerCrop()).into(mBinding.logoIV);

        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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

}