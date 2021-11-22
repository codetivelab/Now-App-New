package com.buzzware.nowapp.Fragments.BuisnessFragments.Application;

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
import com.buzzware.nowapp.Fragments.BuisnessFragments.Application.AfterVerification.B11Fragment;
import com.buzzware.nowapp.Models.BuisnessSignupModel;
import com.buzzware.nowapp.Permissions.Permissions;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Screens.UserScreens.UserProfileScreen;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.FragmentFourthTabBinding;
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

public class FourthTabFragment extends Fragment implements View.OnClickListener {

    FragmentFourthTabBinding mBinding;
    public static final int ACCESS_CAMERA = 101;
    public static final int ACCESS_Gallery = 102;
    Permissions permissions;
    Context context;
    String pic1Path;
    BuisnessSignupModel buisnessSignupModel;

    public FourthTabFragment(BuisnessSignupModel buisnessSignupModel) {
        // Required empty public constructor
        this.buisnessSignupModel = buisnessSignupModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_fourth_tab, container, false);

        Init();

        //click

        mBinding.btnBack.setOnClickListener(this);
        mBinding.btnNext.setOnClickListener(this);
        mBinding.btnUploadImage.setOnClickListener(this);

        return mBinding.getRoot();
    }

    String currentPhotoPath;


//
//    private void showPopup() {
//        final CharSequence[] items = {"Take Photo", "Choose from Library",
//                "Cancel"};
//
//        TextView title = new TextView(getActivity());
//        title.setText("Add Photo!");
//        title.setBackgroundColor(Color.BLACK);
//        title.setPadding(10, 15, 15, 10);
//        title.setGravity(Gravity.CENTER);
//        title.setTextColor(Color.WHITE);
//        title.setTextSize(22);
//        AlertDialog.Builder builder = new AlertDialog.Builder(
//                getActivity());
//        builder.setCustomTitle(title);
//        // builder.setTitle("Add Photo!");
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int item) {
//                if (items[item].equals("Take Photo")) {
//                    dispatchTakePictureIntent();
//                } else if (items[item].equals("Choose from Library")) {
//                    pickImageFromGallery();
//                } else if (items[item].equals("Cancel")) {
//                    dialog.dismiss();
//                }
//            }
//        });
//        builder.show();
//    }

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

    private void Init() {
        context = getContext();
        permissions = new Permissions(context);
    }

    @Override
    public void onClick(View v) {
        if (v == mBinding.btnBack) {
            getActivity().onBackPressed();
        } else if (v == mBinding.btnNext) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.buisnessApplicationContaner, new B11Fragment(buisnessSignupModel)).addToBackStack("successFullySubmit").commit();
        } else if (v == mBinding.btnUploadImage) {
            ShowImageUploadDailog();
        }
    }


    private void pickImageFromGallery() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        someActivityResultLauncher.launch(intent
        );
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
                        pic1Path = picturePath;
                        mBinding.licenceIV.setImageURI(Uri.fromFile(file));
                        buisnessSignupModel.setBuisnessLicenceImage(Uri.fromFile(file));
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
//                        File imageFile = new File(currentPhotoPath);
                        Uri imageUri = getImageUri(getActivity(), photo);

                        Glide.with(getActivity()).load(imageUri)
                                .apply(new RequestOptions().centerCrop()).into(mBinding.licenceIV);

                        buisnessSignupModel.setBuisnessLicenceImage(imageUri);
                    }
                }
            });

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, new Date().toString(), null);
        return Uri.parse(path);
    }

    private void Validation() {
        if (buisnessSignupModel.getBuisnessLicenceImage() != null) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.buisnessApplicationContaner, new B11Fragment(buisnessSignupModel)).addToBackStack("b11").commit();
        } else {
            UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.licence_req));
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
//        if (permissions.isStoragePermissionGranted() && permissions.isCameraPermissionGranted()) {

        RequestPermission(isTakePhoto);
//        }
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

        if (buisnessSignupModel.getBuisnessLicenceImage() != null) {

            Glide.with(getActivity()).load(buisnessSignupModel.getBuisnessLicenceImage())
                    .apply(new RequestOptions().centerCrop()).into(mBinding.licenceIV);

        }


    }
}