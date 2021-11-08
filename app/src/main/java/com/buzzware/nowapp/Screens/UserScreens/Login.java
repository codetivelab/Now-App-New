package com.buzzware.nowapp.Screens.UserScreens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.buzzware.nowapp.Constants.Constant;
import com.buzzware.nowapp.FirebaseRequests.FirebaseRequests;
import com.buzzware.nowapp.FirebaseRequests.Interfaces.LoginResponseCallback;
import com.buzzware.nowapp.Permissions.Permissions;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Screens.BuisnessScreens.BuisnessApplicationStartUpScreen;
import com.buzzware.nowapp.Screens.BuisnessScreens.BuisnessHome;
import com.buzzware.nowapp.Sessions.UserSessions;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.buzzware.nowapp.Constants.Constant;
import com.buzzware.nowapp.Facebook.FacebookHelper;
import com.buzzware.nowapp.Facebook.FacebookResponse;
import com.buzzware.nowapp.Facebook.FacebookUser;
import com.buzzware.nowapp.FirebaseRequests.FirebaseRequests;
import com.buzzware.nowapp.FirebaseRequests.Interfaces.LoginResponseCallback;
import com.buzzware.nowapp.FirebaseRequests.Interfaces.SignupResponseCallback;
import com.buzzware.nowapp.Models.BuisnessSignupModel;
import com.buzzware.nowapp.Models.NormalUserModel;
import com.buzzware.nowapp.Permissions.Permissions;
import com.buzzware.nowapp.R;
import com.buzzware.nowapp.Screens.BuisnessScreens.BuisnessApplicationStartUpScreen;
import com.buzzware.nowapp.Screens.BuisnessScreens.BuisnessHome;
import com.buzzware.nowapp.Sessions.UserSessions;
import com.buzzware.nowapp.UIUpdates.UIUpdate;
import com.buzzware.nowapp.databinding.ActivityLoginBinding;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity implements View.OnClickListener {
    Context context;
    ActivityLoginBinding mBinding;
    Permissions permissions;
    FirebaseFirestore firebaseFirestore;
    CollectionReference collectionReference;
    //facebook
    CallbackManager callbackManager;
    FacebookHelper facebookHelper;
    //google sign in
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        try {
            Init();
            setListener();
            printHashKey(Login.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        createRequest();
        setupFacebook();
    }

    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("keyHash", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("keyHash", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("keyHash", "printHashKey()", e);
        }
    }

    private void setListener() {
        mBinding.googleLoginRL.setOnClickListener(v -> googleSgnIn());
        mBinding.facebookLoginRL.setOnClickListener(v ->
        {
            facebookHelper.performSignIn(Login.this);
        });
    }

    private void createRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupFacebook() {
        FacebookSdk.setApplicationId(getResources().getString(R.string.facebook_app_id));
        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();
        facebookHelper = new FacebookHelper(response, "id,name", this);
    }

    FacebookResponse response = new FacebookResponse() {
        @Override
        public void onFbSignTnFail() {
           // Toast.makeText(Login.this, "Fb Signin Failed", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFbSignInSuccess() {
        }

        @Override
        public void onFbSignOut() {
            Toast.makeText(Login.this, "Sign out Successful", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFbProfileRecieved(FacebookUser facebookUser) {
            Toast.makeText(Login.this, "onFbProfile Received", Toast.LENGTH_LONG).show();
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            facebookUser.token = accessToken;
            accessToken.getDeclinedPermissions();
            handleFacebookAccessToken(facebookUser);
        }
    };

    private void handleFacebookAccessToken(final FacebookUser user) {

        //showProgressDialog(Login.this);
        AuthCredential credential = FacebookAuthProvider.getCredential(user.token.getToken());
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseAuth mAuth= FirebaseAuth.getInstance();
//                            rootNode= FirebaseDatabase.getInstance();
//                            mDatabase=rootNode.getReference("users");
//                            String currentID = mAuth.getCurrentUser().getUid();
//                            User userNew=new User(user.name,user.email,"00000000");
//                            mDatabase.child(currentID).setValue(userNew);
//                            finish();
//                            Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
//                            startActivity(intent);
//                            Toast.makeText(Login.this, "Successfully Login With Facebook!", Toast.LENGTH_LONG).show();


                            NormalUserModel normalUserModel = new NormalUserModel();
                            normalUserModel.setUserEmail(user.email);
                            normalUserModel.setUserFirstName(user.name);
                            normalUserModel.setUserLastName("");
                            normalUserModel.setUserPassword("");
                            normalUserModel.setUserImageUrl(user.profilePic);
                            normalUserModel.setUserToken("");
                            normalUserModel.setUserPhoneNumber("");
                            normalUserModel.setUserType("N");

                            UserSessions.GetUserSession().setFirebaseUserID(mAuth.getCurrentUser().getUid(), context);
                            UserSessions.GetUserSession().setUserEmail(user.email, context);
                            UserSessions.GetUserSession().setUserIMAGE(user.profilePic, context);
                            UserSessions.GetUserSession().setUserFirstNAME(user.name, context);
                            UserSessions.GetUserSession().setUserLastNAME("", context);
                            UserSessions.GetUserSession().setUserNumber("", context);
                            UserSessions.GetUserSession().setUserType("N", context);

                            firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).set(normalUserModel);
                            Intent intent = new Intent(Login.this, Home.class);
                            startActivity(intent);



                        } else {
                            Toast.makeText(Login.this, "Try Again With Facebook" + task.getException(), Toast.LENGTH_LONG).show();
                            Log.d("errorFB", "onComplete: "+task.getException());
                        }
                    }
                });
    }
    private void googleSgnIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void Init() {
        context = Login.this;
        firebaseFirestore = FirebaseFirestore.getInstance();
        permissions = new Permissions(context);
        mBinding.register.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!UserSessions.GetUserSession().getFirebaseUserID(context).equals("")) {
            if (UserSessions.GetUserSession().getUserType(context).equals(Constant.GetConstant().getBuisnessUser())) {
                Intent intent = new Intent(context, BuisnessHome.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                ((Activity) context).finish();
            } else {
                Intent intent = new Intent(context, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        }
    }

    public void Login(View view) {
        Validation();
    }

    private void Validation() {
        if (!TextUtils.isEmpty(mBinding.etEmail.getText().toString())) {
            if (!TextUtils.isEmpty(mBinding.etPassword.getText().toString())) {
                if (Constant.GetConstant().getNetworkInfo(context)) {
                    LoginUser();
                } else {
                    UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.no_internet));
                }
            } else {
                UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.password_req));
            }
        } else {
            UIUpdate.GetUIUpdate(context).ShowToastMessage(getString(R.string.email_req));
        }
    }

    private void LoginUser() {
        FirebaseRequests.GetFirebaseRequests(context).LoginUser(mBinding.etEmail.getText().toString(), mBinding.etPassword.getText().toString(), true, context, callback);
    }

    LoginResponseCallback callback = new LoginResponseCallback() {
        @Override
        public void onResponse(boolean isError, String type) {
            if (!isError) {
                if (type.equals(Constant.GetConstant().getBuisnessUser())) {
                    Intent intent = new Intent(context, BuisnessHome.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                } else {
                    CheckPermission();
                }
            }
        }
    };

    private void CheckPermission() {
        if (permissions.isLocationPermissionGranted()) {
            Intent intent = new Intent(Login.this, Home.class);
            startActivity(intent);
        } else {
            RequestLocationPermisson();
        }
    }

    private void RequestLocationPermisson() {
        Dexter.withActivity(this).withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    Intent intent = new Intent(context, Home.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                    ((Activity) context).finish();
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

    public void Register() {
        Intent intent = new Intent(Login.this, Signup.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v == mBinding.register) {
            Register();
        }
    }

    public void StartBuisnessApplication(View view) {
        Intent intent = new Intent(Login.this, BuisnessApplicationStartUpScreen.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            facebookHelper.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //Toast.makeText(LoginActivity.this,account.getEmail(), Toast.LENGTH_LONG).show();
            Log.w("GoogleUserInfo", "info=" + account.getEmail());
            try {
                firebaseAuthWithGoogle(account.getIdToken(), account);
            } catch (Exception e) {
                Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } catch (ApiException e) {
            Log.w("ErrorDataResult", "signInResult:failed code=" + e.getStatusCode() + "" + completedTask.getException());
            Toast.makeText(Login.this, "Try Again With Google#" + e.getMessage() + "" + completedTask.getException(), Toast.LENGTH_LONG).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken, GoogleSignInAccount account) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            NormalUserModel normalUserModel = new NormalUserModel();
                            normalUserModel.setUserEmail(account.getEmail());
                            normalUserModel.setUserFirstName(account.getDisplayName());
                            normalUserModel.setUserLastName("");
                            normalUserModel.setUserPassword("");
                            normalUserModel.setUserImageUrl(account.getPhotoUrl().toString());
                            normalUserModel.setUserToken("");
                            normalUserModel.setUserPhoneNumber("");
                            normalUserModel.setUserType("N");

                            UserSessions.GetUserSession().setFirebaseUserID(mAuth.getCurrentUser().getUid(), context);
                            UserSessions.GetUserSession().setUserEmail(account.getEmail(), context);
                            UserSessions.GetUserSession().setUserIMAGE(account.getPhotoUrl().toString(), context);
                            UserSessions.GetUserSession().setUserFirstNAME(account.getDisplayName(), context);
                            UserSessions.GetUserSession().setUserLastNAME("", context);
                            UserSessions.GetUserSession().setUserNumber("", context);
                            UserSessions.GetUserSession().setUserType("N", context);

                            firebaseFirestore.collection("Users").document(user.getUid()).set(normalUserModel);
                            Intent intent = new Intent(Login.this, Home.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Login.this, "Try Again With Google" + task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                        // ...
                    }
                });
    }

    SignupResponseCallback callback1 = new SignupResponseCallback() {
        @Override
        public void onResponse(boolean isError, String message) {
            if (!isError) {
                finish();
            }
        }
    };
}









