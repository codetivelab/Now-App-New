package com.buzzware.nowapp.FirebaseRequests;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.buzzware.nowapp.Constants.Constant;
import com.buzzware.nowapp.FirebaseRequests.Interfaces.LoginResponseCallback;
import com.buzzware.nowapp.FirebaseRequests.Interfaces.RestaurantResponseCallback;
import com.buzzware.nowapp.FirebaseRequests.Interfaces.SignupResponseCallback;
import com.buzzware.nowapp.FirebaseRequests.Interfaces.UploadVideoResponseCallback;
import com.buzzware.nowapp.Models.BuisnessSignupModel;
import com.buzzware.nowapp.Models.BusinessModel;
import com.buzzware.nowapp.Models.PostsModel;
import com.buzzware.nowapp.Models.RestaurantDataModel;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Screens.UserScreens.post.Post;
import com.buzzware.nowapp.Sessions.UserSessions;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Sajeel on 12/24/2018.
 */

public class FirebaseRequests {
    public static FirebaseRequests firebaseRequests;
    Context context;
    FirebaseAuth mAuth;
    StorageReference storageReference;
    FirebaseFirestore firebaseFirestore;

    public static FirebaseRequests GetFirebaseRequests(Context context) {
        if (firebaseRequests == null) {
            firebaseRequests = new FirebaseRequests(context);
        }
        return firebaseRequests;
    }

    public FirebaseRequests(Context context) {
        this.context = context;
        ///init firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void SignUpUser(BuisnessSignupModel buisnessSignupModel, Context context, SignupResponseCallback callback, String type) {
        UIUpdate.GetUIUpdate(context).setProgressDialog("", context.getResources().getString(R.string.please_wait), context);
        mAuth.createUserWithEmailAndPassword(buisnessSignupModel.getBuisessEmail(), buisnessSignupModel.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    SendVarificationEmail(buisnessSignupModel, context, callback, type);
                } else {
                    UIUpdate.GetUIUpdate(context).DismissProgressDialog();
                    UIUpdate.GetUIUpdate(context).ShowToastMessage(task.getException().getLocalizedMessage());
                }
            }
        });
    }

    public void UploadVideo(UploadVideoResponseCallback callback, Uri imageHoldUri, String locationName, String latitude, String longitude, String thumbnail, Context context) {

        final StorageReference mChildStorage = storageReference.child("UserVideos").child(imageHoldUri.getLastPathSegment());

        mChildStorage.putFile(imageHoldUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                mChildStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        final Uri videoUrl = uri;

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.UploadVideoResponseListener(true, e.getMessage());
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                callback.UploadVideoResponseListener(true, context.getString(R.string.upload_failed));
            }
        });
    }

    public interface UploadFileCallback {

        void successfullyUploaded(String message, String errorMessage);
    }

    public void UploadVideo(UploadFileCallback callback, Uri imageHoldUri) {

        final StorageReference mChildStorage = storageReference.child("UserVideos").child(imageHoldUri.getLastPathSegment());

        mChildStorage.putFile(imageHoldUri).addOnSuccessListener(
                taskSnapshot -> mChildStorage.getDownloadUrl().addOnSuccessListener(uri -> {

                    final Uri videoUrl = uri;

                    callback.successfullyUploaded(videoUrl.toString(), null);
//                        firebaseUploadBitmap(callback, uri, thumbnail);
                })).addOnFailureListener(e -> log(e, callback))
                .addOnCanceledListener(() -> callback.successfullyUploaded(null, "Uploading Cancelled"));
    }

    private void log(Exception e, UploadFileCallback callback) {
        e.getLocalizedMessage();
        callback.successfullyUploaded(null, e.getLocalizedMessage());
    }


//    public void firebaseUploadBitmap(UploadFileCallback callback, Uri imageHoldUri) {
//
//        final StorageReference mChildStorage = storageReference.child("Images").child(imageHoldUri.getLastPathSegment());
//
//        mChildStorage.putFile(imageHoldUri).addOnSuccessListener(
//                taskSnapshot -> mChildStorage.getDownloadUrl().addOnSuccessListener(uri -> {
//
//                    final Uri videoUrl = uri;
//
//                    callback.successfullyUploaded(videoUrl.toString(), null);
////                        firebaseUploadBitmap(callback, uri, thumbnail);
//                })).addOnFailureListener(e -> callback.successfullyUploaded(null, e.getLocalizedMessage()))
//                .addOnCanceledListener(() -> callback.successfullyUploaded(null, "Uploading Cancelled"));
//    }

    public void firebaseUploadBitmap(UploadFileCallback callback, Bitmap thumbnail) {


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        StorageReference imageRef = storageReference.child("userThumbnail/" + ts);

        Task<Uri> urlTask = imageRef.putBytes(data).continueWithTask(task -> {

            if (!task.isSuccessful())
                callback.successfullyUploaded(null, task.getException().getLocalizedMessage());
            // Continue with the task to get the download URL
            return imageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                String uri = downloadUri.toString();
                //sendMessageWithFile(uri);
                callback.successfullyUploaded(uri.toString(), null);
            } else {
                callback.successfullyUploaded(null, task.getException().getLocalizedMessage());
            }
            // progressBar.setVisibility(View.GONE);
        });


    }

    private void SendVarificationEmail(BuisnessSignupModel buisnessSignupModel, Context context, SignupResponseCallback callback, String type) {
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        if (type.equals(Constant.GetConstant().getBuisnessUser())) {
                            UIUpdate.GetUIUpdate(context).setProgressDialog("", "Uploading", context);

                            UploadImagesToStorage(buisnessSignupModel, context, firebaseUser, callback);
                        } else {
                            SendDataToUser(buisnessSignupModel, context, firebaseUser, callback);
                        }
                    } else {
                        UIUpdate.GetUIUpdate(context).ShowToastMessage(task.getException().getLocalizedMessage());
                    }
                }
            });
        }
    }

    public void addPost(Post post) {

        DocumentReference documentReferenceUser = firebaseFirestore.collection("UserPosts").document();

        documentReferenceUser.set(post);
    }

    public void updatePost(Post post) {

        DocumentReference documentReferenceUser = firebaseFirestore.collection("UserPosts").document(post.postId);

        documentReferenceUser.set(post);
    }

    public void updatePost(PostsModel post) {

        DocumentReference documentReferenceUser = firebaseFirestore.collection("UserPosts").document(post.postId);

        documentReferenceUser.set(post);
    }

    private void SendDataToUser(BuisnessSignupModel buisnessSignupModel, Context context, FirebaseUser firebaseUser, SignupResponseCallback callback) {
        Map<String, String> userData = new HashMap<>();
        userData.put(Constant.GetConstant().getUserFirstNameDocument(), buisnessSignupModel.getApplicantFirstName());
        userData.put(Constant.GetConstant().getUserLastNameDocument(), buisnessSignupModel.getApplicantLastName());
        userData.put(Constant.GetConstant().getUserPhoneNumberDocument(), buisnessSignupModel.getApplicantNumber());
        userData.put(Constant.GetConstant().getUserEmailDocument(), buisnessSignupModel.getBuisessEmail());
        userData.put(Constant.GetConstant().getUserImageURLDocument(), Constant.GetConstant().getNullString());
        userData.put(Constant.GetConstant().getUserPasswordDocument(), buisnessSignupModel.getPassword());
        userData.put(Constant.GetConstant().getUserTokenDocument(), Constant.GetConstant().getNullString());
        userData.put(Constant.GetConstant().getUserTypeDocument(), Constant.GetConstant().getNormalUser());
        userData.put("userStatus","pending");
        DocumentReference documentReferenceUser = firebaseFirestore.collection(Constant.GetConstant().getUsersCollection()).document(firebaseUser.getUid());
        documentReferenceUser.set(userData);

        UIUpdate.GetUIUpdate(context).DismissProgressDialog();
        UIUpdate.GetUIUpdate(context).ShowToastMessage(context.getResources().getString(R.string.verification_email_sent));
        mAuth.signOut();
        callback.onResponse(false, Constant.GetConstant().getNullString());
    }

    private void UploadImagesToStorage(BuisnessSignupModel buisnessSignupModel, Context context, FirebaseUser firebaseUser, SignupResponseCallback callback) {
        String folderName = "";
        final List<String> imagesURL = new ArrayList<>();
        Uri currentUri = null;
        for (int i = 0; i < 3; i++) {
            int j = i;
            if (j == 0) {
                folderName = Constant.GetConstant().getBuisnessLicense();
                currentUri = buisnessSignupModel.getBuisnessLicenceImage();
            } else if (j == 1) {
                folderName = Constant.GetConstant().getBuisnessLogo();
                currentUri = buisnessSignupModel.getBuisnessLogo();
            } else if (j == 2) {
                folderName = Constant.GetConstant().getBusinessBackgroundImage();
                currentUri = buisnessSignupModel.getBuisnessBackgroundImages();
            }
            final StorageReference mChildStorage = storageReference.child(folderName).child(currentUri.getLastPathSegment());
            mChildStorage.putFile(currentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mChildStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final Uri imageUrl = uri;
                            if (j == 0) {
                                imagesURL.add(imageUrl.toString());
                            } else if (j == 1) {
                                imagesURL.add(imageUrl.toString());
                            } else if (j == 2) {
                                imagesURL.add(imageUrl.toString());
                            }

                            if (j == 2) {
                                SendDataToUserCollection(buisnessSignupModel, context, firebaseUser, imagesURL, callback);
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (j == 0) {
                        imagesURL.add(Constant.GetConstant().getNullString());
                    } else if (j == 1) {
                        imagesURL.add(Constant.GetConstant().getNullString());
                    } else if (j == 2) {
                        imagesURL.add(Constant.GetConstant().getNullString());
                    }
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    if (j == 0) {
                        imagesURL.add(Constant.GetConstant().getNullString());
                    } else if (j == 1) {
                        imagesURL.add(Constant.GetConstant().getNullString());
                    } else if (j == 2) {
                        imagesURL.add(Constant.GetConstant().getNullString());
                    }
                }
            });
        }
    }

    private void SendDataToUserCollection(BuisnessSignupModel buisnessSignupModel, Context context, FirebaseUser firebaseUser, List<String> imagesURL, SignupResponseCallback callback) {
        Map<String, String> userData = new HashMap<>();
        userData.put(Constant.GetConstant().getUserFirstNameDocument(), buisnessSignupModel.getApplicantFirstName());
        userData.put(Constant.GetConstant().getUserLastNameDocument(), buisnessSignupModel.getApplicantLastName());
        userData.put(Constant.GetConstant().getUserPhoneNumberDocument(), buisnessSignupModel.getApplicantNumber());
        userData.put(Constant.GetConstant().getUserEmailDocument(), buisnessSignupModel.getBuisessEmail());
        userData.put(Constant.GetConstant().getUserImageURLDocument(), Constant.GetConstant().getNullString());
        userData.put(Constant.GetConstant().getUserPasswordDocument(), buisnessSignupModel.getPassword());
        userData.put(Constant.GetConstant().getUserTokenDocument(), Constant.GetConstant().getNullString());
        userData.put(Constant.GetConstant().getUserTypeDocument(), Constant.GetConstant().getBuisnessUser());

        Map<String, String> businessData = new HashMap<>();
        businessData.put(Constant.GetConstant().getBuisnessAddressDocument(), buisnessSignupModel.getBuisnessLocation());
        businessData.put(Constant.GetConstant().getBusinessNameDocument(), buisnessSignupModel.getBuisnessName());
        businessData.put(Constant.GetConstant().getBuisnessTypeDocument(), buisnessSignupModel.getBuisnessType());
        businessData.put(Constant.GetConstant().getBuisnessNumberDocument(), buisnessSignupModel.getBuisnessNumber());
        businessData.put(Constant.GetConstant().getBuisnessCityDocument(), buisnessSignupModel.getBuisnessCity());
        businessData.put(Constant.GetConstant().getBuisnessStateDocument(), buisnessSignupModel.getBuisnessState());
        businessData.put(Constant.GetConstant().getBuisnessZipCodeDocument(), buisnessSignupModel.getBuisnessZipcode());
        businessData.put(Constant.GetConstant().getBuisnessLatitudeDocument(), buisnessSignupModel.getBuisnessLatitude());
        businessData.put(Constant.GetConstant().getBuisnessLongitudeDocument(), buisnessSignupModel.getBuisnessLongitude());
        businessData.put(Constant.GetConstant().getBuisnessLogo(), imagesURL.get(1));
        businessData.put(Constant.GetConstant().getBuisnessLiscenceImageDocument(), imagesURL.get(0));
        businessData.put(Constant.GetConstant().getBuisnessBackgroundImageDocument(), imagesURL.get(2));
        businessData.put(Constant.GetConstant().getBuisnessVenueAddressDocument(), buisnessSignupModel.getVenueAddress());
        businessData.put(Constant.GetConstant().getBuisnessResponseDocument(), buisnessSignupModel.getBuisnessResponse());
        businessData.put(Constant.GetConstant().getBuisnessTotalRatingDocument(), Constant.GetConstant().getStartBuisnessRating());

        DocumentReference documentReferenceUser = firebaseFirestore.collection(Constant.GetConstant().getUsersCollection()).document(firebaseUser.getUid());
        DocumentReference documentReferenceBusinessData = firebaseFirestore.collection(Constant.GetConstant().getBuisnessDataCollection()).document(firebaseUser.getUid());
        documentReferenceBusinessData.set(businessData);
        documentReferenceUser.set(userData);
        UIUpdate.GetUIUpdate(context).DismissProgressDialog();
        UIUpdate.GetUIUpdate(context).ShowToastMessage(context.getResources().getString(R.string.verification_email_sent));
        mAuth.signOut();
        callback.onResponse(false, Constant.GetConstant().getNullString());
    }

    public void LoginUser(String email, String password, boolean isRemember, Context context, LoginResponseCallback callback) {
        UIUpdate.GetUIUpdate(context).setProgressDialog("", context.getResources().getString(R.string.please_wait), context);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    UIUpdate.GetUIUpdate(context).DismissProgressDialog();
                    CheckEmailVarification(isRemember, context, callback);
                } else {
                    UIUpdate.GetUIUpdate(context).DismissProgressDialog();
                    UIUpdate.GetUIUpdate(context).ShowToastMessage(task.getException().getLocalizedMessage());
                }
            }
        });
    }

    private void CheckEmailVarification(boolean isRemember, Context context, LoginResponseCallback callback) {
        final FirebaseUser firebaseUser = mAuth.getInstance().getCurrentUser();

//        TODO Check email verification
        Boolean emailFlag = true;

        if (emailFlag) {
            UIUpdate.GetUIUpdate(context).DismissProgressDialog();
            GetUserType(isRemember, context, callback);
        } else {
            UIUpdate.GetUIUpdate(context).DismissProgressDialog();
            UIUpdate.GetUIUpdate(context).ShowToastMessage(context.getResources().getString(R.string.email_not_verify));
            mAuth.signOut();
        }
    }

    public void GetUserType(boolean isRemember, Context context, LoginResponseCallback callback) {

        String userId = mAuth.getCurrentUser().getUid();

        DocumentReference documentReferenceUser = firebaseFirestore.collection(Constant.GetConstant().getUsersCollection()).document(userId);
        documentReferenceUser.addSnapshotListener(((Activity) context), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                UIUpdate.GetUIUpdate(context).DismissProgressDialog();
                if (documentSnapshot.getData() != null) {

                    String fName = documentSnapshot.get(Constant.GetConstant().getUserFirstNameDocument()).toString();
                    String lName = documentSnapshot.get(Constant.GetConstant().getUserLastNameDocument()).toString();
                    String email = documentSnapshot.get(Constant.GetConstant().getUserEmailDocument()).toString();
                    String imageURL = "";
//                    if(Constant.GetConstant().getUserImageURLDocument()!=null){
//                        imageURL= documentSnapshot.get(Constant.GetConstant().getUserImageURLDocument()).toString();
//                    }
                    String phoneNumber = documentSnapshot.get(Constant.GetConstant().getUserPhoneNumberDocument()).toString();
                    String type = documentSnapshot.get(Constant.GetConstant().getUserTypeDocument()).toString();
                    if (type.equals(Constant.GetConstant().getBuisnessUser())) {
                        GetBuisnessDetail(isRemember, fName, lName, email, imageURL, phoneNumber, type, context, callback);
                    } else {
                        SetNormalUserSession(isRemember, fName, lName, email, imageURL, phoneNumber, type, context, callback);
                    }
                } else {
                    UIUpdate.GetUIUpdate(context).DismissProgressDialog();
                    UIUpdate.GetUIUpdate(context).ShowToastMessage(context.getResources().getString(R.string.user_not_exist));
                }
            }
        });
    }

    private void SetNormalUserSession(boolean isRemember, String fName, String lName, String email, String imageURL, String phoneNumber, String type, Context context, LoginResponseCallback callback) {
        UserSessions.GetUserSession().setFirebaseUserID(mAuth.getCurrentUser().getUid(), context);
        UserSessions.GetUserSession().setUserEmail(email, context);
        UserSessions.GetUserSession().setUserIMAGE(imageURL, context);
        UserSessions.GetUserSession().setUserFirstNAME(fName, context);
        UserSessions.GetUserSession().setUserLastNAME(lName, context);
        UserSessions.GetUserSession().setUserNumber(phoneNumber, context);
        UserSessions.GetUserSession().setUserType(type, context);
        if (isRemember) {
            UserSessions.GetUserSession().setIsRemember(true, context);
        } else {
            UserSessions.GetUserSession().setIsRemember(false, context);
        }

        UIUpdate.GetUIUpdate(context).DismissProgressDialog();
        UIUpdate.GetUIUpdate(context).ShowToastMessage(context.getResources().getString(R.string.login_successfully));
        callback.onResponse(false, type);
    }

    private void GetBuisnessDetail(boolean isRemember, String fName, String lName, String email, String imageURL, String phoneNumber, String type, Context context, LoginResponseCallback callback) {
        DocumentReference documentReferenceBuisnessUser = firebaseFirestore.collection(Constant.GetConstant().getBuisnessDataCollection()).document(mAuth.getCurrentUser().getUid());
        documentReferenceBuisnessUser.addSnapshotListener(((Activity) context), (documentSnapshot, error) -> {
            UIUpdate.GetUIUpdate(context).DismissProgressDialog();
            if (documentSnapshot != null) {

                BusinessModel businessModel = documentSnapshot.toObject(BusinessModel.class);

                SetBuisessUserSession(isRemember, fName, lName, email, imageURL, phoneNumber, type, businessModel, callback);
            } else {
                UIUpdate.GetUIUpdate(context).DismissProgressDialog();
                UIUpdate.GetUIUpdate(context).ShowToastMessage(context.getResources().getString(R.string.user_not_exist));
            }
        });
    }

    public interface SnapshotCallback {

        void onSnapshotReceived(DocumentSnapshot snapshot, FirebaseFirestoreException error);
    }

    public void getBusinessData(SnapshotCallback callback, String id) {
        DocumentReference documentReferenceBuisnessUser = firebaseFirestore.collection(Constant.GetConstant().getBuisnessDataCollection()).document(id);
        documentReferenceBuisnessUser.addSnapshotListener((documentSnapshot, error) -> callback.onSnapshotReceived(documentSnapshot, error));
    }

    private void SetBuisessUserSession(boolean isRemember, String fName, String lName, String email, String imageURL, String phoneNumber, String type
            , BusinessModel businessModel, LoginResponseCallback callback) {
        UserSessions.GetUserSession().setFirebaseUserID(mAuth.getCurrentUser().getUid(), context);
        UserSessions.GetUserSession().setUserEmail(email, context);
        UserSessions.GetUserSession().setUserIMAGE(imageURL, context);
        UserSessions.GetUserSession().setUserFirstNAME(fName, context);
        UserSessions.GetUserSession().setUserLastNAME(lName, context);
        UserSessions.GetUserSession().setUserNumber(phoneNumber, context);
        UserSessions.GetUserSession().setUserType(type, context);
        if (isRemember) {
            UserSessions.GetUserSession().setIsRemember(true, context);
        } else {
            UserSessions.GetUserSession().setIsRemember(false, context);
        }

        UserSessions.GetUserSession().setBusiness(businessModel, context);
        UIUpdate.GetUIUpdate(context).DismissProgressDialog();
        UIUpdate.GetUIUpdate(context).ShowToastMessage(context.getResources().getString(R.string.login_successfully));
        callback.onResponse(false, type);
    }

    public void GetRestaurant(RestaurantResponseCallback callback, Context context) {
        UIUpdate.GetUIUpdate(context).setProgressDialog("", context.getResources().getString(R.string.please_wait), context);
        firebaseFirestore.collection(Constant.GetConstant().getBuisnessDataCollection()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                UIUpdate.GetUIUpdate(context).DismissProgressDialog();
                List<RestaurantDataModel> list = new ArrayList<>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = document.getId();
                        RestaurantDataModel restaurantDataModel = document.toObject(RestaurantDataModel.class);
                        restaurantDataModel.setId(id);
                        list.add(restaurantDataModel);
                    }
                    callback.onResponse(list, false, Constant.GetConstant().getNullString());
                } else {
                    callback.onResponse(list, true, context.getString(R.string.no_data_avail));
                }
            }
        });

    }

//    public void UpdateProfileWithImage(Uri imageHoldUri, String firstName, String lastName)
//    {
//        UIUpdate.GetUIUpdate(context).setProgressDialog("", context.getResources().getString(R.string.please_wait));
//        final StorageReference mChildStorage= storageReference.child("Profile_Images").child(imageHoldUri.getLastPathSegment());
//        mChildStorage.putFile(imageHoldUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                mChildStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        final Uri imageUrl = uri;
//                        UpdateFirestoreData(firstName, lastName, imageUrl.toString());
//                    }
//                });
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                UIUpdate.GetUIUpdate(context).DismissProgressDialog();
//                UIUpdate.GetUIUpdate(context).ShowToastMessage(e.getMessage());
//            }
//        }).addOnCanceledListener(new OnCanceledListener() {
//            @Override
//            public void onCanceled() {
//                UIUpdate.GetUIUpdate(context).DismissProgressDialog();
//                UIUpdate.GetUIUpdate(context).ShowToastMessage(context.getString(R.string.failed_profile_update));
//            }
//        });
//    }
//
//    private void UpdateFirestoreData(String firstName, String lastName, String imageUrl) {
//        Map<String, Object> userData= new HashMap<>();
//        userData.put(Constant.GetConstant().getUserFirstNameDocument(), firstName);
//        userData.put(Constant.GetConstant().getUserLastNameDocument(), lastName);
//        userData.put(Constant.GetConstant().getUserImageURLDocument(), imageUrl);
//
//        DocumentReference documentReferenceUser= firebaseFirestore.collection(Constant.GetConstant().getUserCollection()).document(UserSessions.GetUserSession().getFirebaseUserID(context));
//        documentReferenceUser.update(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful())
//                {
//                    UpdateSesion(firstName, lastName, imageUrl);
//                    Log.e("sdhbm", imageUrl);
//                    UIUpdate.GetUIUpdate(context).DismissProgressDialog();
//                    UIUpdate.GetUIUpdate(context).ShowToastMessage(context.getResources().getString(R.string.update_profile_success));
//                }else
//                {
//                    UIUpdate.GetUIUpdate(context).DismissProgressDialog();
//                    UIUpdate.GetUIUpdate(context).ShowToastMessage(context.getResources().getString(R.string.update_profile_fail));
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                UIUpdate.GetUIUpdate(context).DismissProgressDialog();
//                UIUpdate.GetUIUpdate(context).ShowToastMessage(e.getMessage());
//            }
//        });
//    }
//
//    private void UpdateSesion(String firstName, String lastName, String imageUrl) {
//        UserSessions.GetUserSession().setUserFirstNAME(firstName, context);
//        UserSessions.GetUserSession().setUserLastNAME(lastName, context);
//        UserSessions.GetUserSession().setUserIMAGE(imageUrl, context);
//    }
//
//    private void UpdateSesionUserName(String firstName, String lastName) {
//        UserSessions.GetUserSession().setUserFirstNAME(firstName, context);
//        UserSessions.GetUserSession().setUserLastNAME(lastName, context);
//    }
//
//    public void UpdateProfileWithoutimage(String firstName, String lastName){
//        UIUpdate.GetUIUpdate(context).setProgressDialog("", context.getResources().getString(R.string.please_wait));
//        Map<String, Object> userData= new HashMap<>();
//        userData.put(Constant.GetConstant().getUserFirstNameDocument(), firstName);
//        userData.put(Constant.GetConstant().getUserLastNameDocument(), lastName);
//
//        DocumentReference documentReferenceUser= firebaseFirestore.collection(Constant.GetConstant().getUserCollection()).document(UserSessions.GetUserSession().getFirebaseUserID(context));
//        documentReferenceUser.update(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful())
//                {
//                    UpdateSesionUserName(firstName, lastName);
//                    UIUpdate.GetUIUpdate(context).DismissProgressDialog();
//                    UIUpdate.GetUIUpdate(context).ShowToastMessage(context.getResources().getString(R.string.update_profile_success));
//                }else
//                {
//                    UIUpdate.GetUIUpdate(context).DismissProgressDialog();
//                    UIUpdate.GetUIUpdate(context).ShowToastMessage(context.getResources().getString(R.string.update_profile_fail));
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                UIUpdate.GetUIUpdate(context).DismissProgressDialog();
//                UIUpdate.GetUIUpdate(context).ShowToastMessage(e.getMessage());
//            }
//        });
//    }
//
//    public void SendResetPasswordEmail(String email)
//    {
//        UIUpdate.GetUIUpdate(context).setProgressDialog("", context.getResources().getString(R.string.please_wait));
//        mAuth.sendPasswordResetEmail(email)
//                .addOnCompleteListener(new OnCompleteListener() {
//                    @Override
//                    public void onComplete(@NonNull Task task) {
//                        if (task.isSuccessful()) {
//                            UIUpdate.GetUIUpdate(context).DismissProgressDialog();
//                            UIUpdate.GetUIUpdate(context).ShowToastMessage(context.getString(R.string.successfully_send_reset_email));
//                            ((Activity) context).finish();
//                        } else {
//                            UIUpdate.GetUIUpdate(context).DismissProgressDialog();
//                            UIUpdate.GetUIUpdate(context).ShowToastMessage(context.getString(R.string.fail_send_reset_email));
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                UIUpdate.GetUIUpdate(context).DismissProgressDialog();
//                UIUpdate.GetUIUpdate(context).ShowToastMessage(e.getMessage());
//            }
//        });
//    }
}
